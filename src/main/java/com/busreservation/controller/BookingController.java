package com.busreservation.controller;

import com.busreservation.dto.BookingRequest;
import com.busreservation.model.*;
import com.busreservation.repository.*;
import com.busreservation.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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

    // Creates a booking and returns full structured details the frontend can render directly
    @PostMapping
    @Transactional
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request,
                                             @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Integer userId = jwtUtil.extractUserId(token);

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> bookedSeatNumbers = new ArrayList<>();
        List<Integer> bookingIds = new ArrayList<>();
        double totalFare = 0;

        for (BookingRequest.PassengerDTO p : request.getPassengers()) {
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

            if (request.getBoardingPointId() != null) {
                booking.setBoardingPoint(boardingPointRepository.findById(request.getBoardingPointId()).orElse(null));
            }
            if (request.getDroppingPointId() != null) {
                booking.setDroppingPoint(boardingPointRepository.findById(request.getDroppingPointId()).orElse(null));
            }

            Booking savedBooking = bookingRepository.save(booking);
            bookingIds.add(savedBooking.getBookingId());

            // Save passenger record
            Passenger passenger = new Passenger();
            passenger.setBooking(savedBooking);
            passenger.setSeat(seat);
            passenger.setName(p.getName());
            passenger.setAge(p.getAge());
            passenger.setGender(Passenger.Gender.valueOf(p.getGender()));
            passengerRepository.save(passenger);

            // Record payment
            Payment payment = new Payment();
            payment.setBooking(savedBooking);
            payment.setAmount(schedule.getFare());
            payment.setPaymentMethod(Payment.PaymentMethod.UPI);
            payment.setPaymentStatus(Payment.PaymentStatus.Success);
            paymentRepository.save(payment);

            bookedSeatNumbers.add(seat.getSeatNumber());
            totalFare += schedule.getFare();
        }

        // Build structured response matching what BookingConfirmation.jsx expects
        Map<String, Object> response = new HashMap<>();
        response.put("booking_id", bookingIds.isEmpty() ? null : bookingIds.get(0));
        response.put("bus_number", schedule.getBus().getBusNumber());
        response.put("source", schedule.getRoute().getSource());
        response.put("destination", schedule.getRoute().getDestination());
        response.put("departure_time", schedule.getDepartureTime());
        response.put("seat_numbers", bookedSeatNumbers);
        response.put("total_fare", totalFare);
        response.put("status", "Confirmed");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public List<Booking> getUserBookings(@PathVariable Integer userId) {
        return bookingRepository.findByUser_UserId(userId);
    }

    @PutMapping("/{bookingId}/cancel")
    @Transactional
    public ResponseEntity<?> cancelBooking(@PathVariable Integer bookingId,
                                             @RequestParam(required = false) String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(Booking.Status.Cancelled);
        bookingRepository.save(booking);

        Cancellation cancellation = new Cancellation();
        cancellation.setBooking(booking);
        cancellation.setReason(reason != null ? reason : "User requested cancellation");
        cancellation.setRefundAmount(booking.getSchedule().getFare() * 0.9); // 10% cancellation fee example
        cancellation.setRefundStatus(Cancellation.RefundStatus.Pending);
        cancellationRepository.save(cancellation);

        return ResponseEntity.ok("Booking cancelled, refund pending");
    }
}
