package com.busreservation.controller;

import com.busreservation.dto.BookingRequest;
import com.busreservation.dto.CouponApplyResponse;
import com.busreservation.model.*;
import com.busreservation.repository.*;
import com.busreservation.security.JwtUtil;
import com.busreservation.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private ScheduleRepository scheduleRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BoardingPointRepository boardingPointRepository;
    @Autowired private PassengerRepository passengerRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private CancellationRepository cancellationRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private CouponService couponService;

    private ResponseEntity<Integer> resolveUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(jwtUtil.extractUserId(token));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request,
                                             @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<Integer> userIdResult = resolveUserId(authHeader);
        if (userIdResult.getStatusCode().isError() || userIdResult.getBody() == null) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }
        Integer userId = userIdResult.getBody();

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getJourneyDate() == null || request.getJourneyDate().isBlank()) {
            return ResponseEntity.badRequest().body("journeyDate is required");
        }
        LocalDate journeyDate;
        try {
            journeyDate = LocalDate.parse(request.getJourneyDate());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid journeyDate format, expected yyyy-MM-dd");
        }

        if (request.getPaymentMethod() == null || request.getPaymentMethod().isBlank()) {
            return ResponseEntity.badRequest().body("paymentMethod is required");
        }
        Payment.PaymentMethod paymentMethod;
        try {
            paymentMethod = Payment.PaymentMethod.valueOf(request.getPaymentMethod());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid paymentMethod. Must be one of UPI, Paytm, DebitCard, CreditCard, NetBanking");
        }

        String maskedCardNumber = null;
        if (paymentMethod == Payment.PaymentMethod.UPI || paymentMethod == Payment.PaymentMethod.Paytm) {
            if (request.getUpiId() == null || request.getUpiId().isBlank()) {
                return ResponseEntity.badRequest().body("upiId is required for UPI/Paytm payments");
            }
            if (!request.getUpiId().matches("^[\\w.\\-]{2,}@[a-zA-Z]{2,}$")) {
                return ResponseEntity.badRequest().body("upiId format looks invalid (expected something like name@bank)");
            }
        } else if (paymentMethod == Payment.PaymentMethod.DebitCard || paymentMethod == Payment.PaymentMethod.CreditCard) {
            String digits = request.getCardNumber() != null ? request.getCardNumber().replaceAll("\\s", "") : "";
            if (digits.length() < 12 || digits.length() > 19 || !digits.matches("\\d+")) {
                return ResponseEntity.badRequest().body("A valid card number is required for card payments");
            }
            maskedCardNumber = "**** **** **** " + digits.substring(digits.length() - 4);
        }

        if (request.getPassengers() == null || request.getPassengers().isEmpty()) {
            return ResponseEntity.badRequest().body("At least one passenger is required");
        }

        // ---- Coupon: validated server-side against the real subtotal.
        // The client's earlier /api/coupons/apply preview is never trusted
        // directly — we recompute here so a stale or tampered code/amount
        // can't slip through.
        double subtotal = schedule.getFare() * request.getPassengers().size();
        double discountAmount = 0;
        String appliedCouponCode = null;

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            CouponApplyResponse couponResult = couponService.evaluate(request.getCouponCode(), subtotal);
            if (!couponResult.isValid()) {
                return ResponseEntity.badRequest().body(couponResult.getMessage());
            }
            discountAmount = couponResult.getDiscountAmount();
            appliedCouponCode = couponResult.getCode();
        }
        double perSeatDiscount = discountAmount / request.getPassengers().size();

        List<String> bookedSeatNumbers = new ArrayList<>();
        List<Integer> bookingIds = new ArrayList<>();
        double totalFare = 0;

        for (BookingRequest.PassengerDTO p : request.getPassengers()) {
            if (p.getSeatId() == null) {
                return ResponseEntity.badRequest().body("seat_id is required for every passenger");
            }
            if (p.getName() == null || p.getName().isBlank()) {
                return ResponseEntity.badRequest().body("name is required for every passenger");
            }
            if (p.getGender() == null) {
                return ResponseEntity.badRequest().body("gender is required for every passenger");
            }

            boolean alreadyBooked = bookingRepository
                    .existsBySchedule_ScheduleIdAndSeat_SeatIdAndStatus(
                            request.getScheduleId(), p.getSeatId(), Booking.Status.Confirmed);

            if (alreadyBooked) {
                return ResponseEntity.badRequest().body("Seat " + p.getSeatId() + " is already booked");
            }

            Seat seat = seatRepository.findById(p.getSeatId())
                    .orElseThrow(() -> new RuntimeException("Seat not found"));

            Booking booking = new Booking();
            booking.setUser(user);
            booking.setSchedule(schedule);
            booking.setSeat(seat);
            booking.setStatus(Booking.Status.Confirmed);
            booking.setJourneyDate(journeyDate);

            if (request.getBoardingPointId() != null) {
                booking.setBoardingPoint(boardingPointRepository.findById(request.getBoardingPointId()).orElse(null));
            }
            if (request.getDroppingPointId() != null) {
                booking.setDroppingPoint(boardingPointRepository.findById(request.getDroppingPointId()).orElse(null));
            }

            Booking savedBooking = bookingRepository.save(booking);
            bookingIds.add(savedBooking.getBookingId());

            Passenger passenger = new Passenger();
            passenger.setBooking(savedBooking);
            passenger.setSeat(seat);
            passenger.setName(p.getName());
            passenger.setAge(p.getAge());
            try {
                passenger.setGender(Passenger.Gender.valueOf(p.getGender()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid gender. Must be Male, Female, or Other");
            }
            passengerRepository.save(passenger);

            double seatFare = Math.round((schedule.getFare() - perSeatDiscount) * 100.0) / 100.0;

            Payment payment = new Payment();
            payment.setBooking(savedBooking);
            payment.setAmount(seatFare);
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentStatus(Payment.PaymentStatus.Success);
            payment.setCouponCode(appliedCouponCode);
            payment.setDiscountAmount(perSeatDiscount);
            if (paymentMethod == Payment.PaymentMethod.UPI || paymentMethod == Payment.PaymentMethod.Paytm) {
                payment.setUpiId(request.getUpiId());
            } else if (paymentMethod == Payment.PaymentMethod.DebitCard || paymentMethod == Payment.PaymentMethod.CreditCard) {
                payment.setMaskedCardNumber(maskedCardNumber);
            }
            paymentRepository.save(payment);

            bookedSeatNumbers.add(seat.getSeatNumber());
            totalFare += seatFare;
        }

        totalFare = Math.round(totalFare * 100.0) / 100.0;

        Map<String, Object> response = new HashMap<>();
        response.put("booking_id", bookingIds.isEmpty() ? null : bookingIds.get(0));
        response.put("bus_number", schedule.getBus().getBusNumber());
        response.put("source", schedule.getRoute().getSource());
        response.put("destination", schedule.getRoute().getDestination());
        response.put("departure_time", schedule.getDepartureTime());
        response.put("journey_date", journeyDate);
        response.put("payment_method", paymentMethod);
        response.put("seat_numbers", bookedSeatNumbers);
        response.put("subtotal", Math.round(subtotal * 100.0) / 100.0);
        response.put("coupon_code", appliedCouponCode);
        response.put("discount_amount", Math.round(discountAmount * 100.0) / 100.0);
        response.put("total_fare", totalFare);
        response.put("status", "Confirmed");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserBookings(@PathVariable Integer userId,
                                               @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<Integer> callerResult = resolveUserId(authHeader);
        if (callerResult.getStatusCode().isError() || callerResult.getBody() == null) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }
        if (!callerResult.getBody().equals(userId)) {
            return ResponseEntity.status(403).body("You may only view your own bookings");
        }
        return ResponseEntity.ok(bookingRepository.findByUser_UserId(userId));
    }

    @PutMapping("/{bookingId}/cancel")
    @Transactional
    public ResponseEntity<?> cancelBooking(@PathVariable Integer bookingId,
                                             @RequestParam(required = false) String reason,
                                             @RequestHeader("Authorization") String authHeader) {
        ResponseEntity<Integer> callerResult = resolveUserId(authHeader);
        if (callerResult.getStatusCode().isError() || callerResult.getBody() == null) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getUserId().equals(callerResult.getBody())) {
            return ResponseEntity.status(403).body("You may only cancel your own bookings");
        }

        if (booking.getStatus() == Booking.Status.Cancelled) {
            return ResponseEntity.badRequest().body("Booking is already cancelled");
        }

        booking.setStatus(Booking.Status.Cancelled);
        bookingRepository.save(booking);

        double refundAmount = booking.getSchedule().getFare() * 0.9;

        Cancellation cancellation = new Cancellation();
        cancellation.setBooking(booking);
        cancellation.setReason(reason != null ? reason : "User requested cancellation");
        cancellation.setRefundAmount(refundAmount);
        cancellation.setRefundStatus(Cancellation.RefundStatus.Pending);
        cancellationRepository.save(cancellation);

        Map<String, Object> response = new HashMap<>();
        response.put("bookingId", booking.getBookingId());
        response.put("status", booking.getStatus());
        response.put("refundAmount", refundAmount);
        response.put("message", "Booking cancelled, refund pending");
        return ResponseEntity.ok(response);
    }
}