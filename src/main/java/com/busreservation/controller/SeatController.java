package com.busreservation.controller;

import com.busreservation.model.Booking;
import com.busreservation.model.Schedule;
import com.busreservation.model.Seat;
import com.busreservation.model.SeatLock;
import com.busreservation.repository.BookingRepository;
import com.busreservation.repository.ScheduleRepository;
import com.busreservation.repository.SeatRepository;
import com.busreservation.repository.SeatLockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired private SeatRepository seatRepository;
    @Autowired private ScheduleRepository scheduleRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private SeatLockRepository seatLockRepository;

    @GetMapping("/{scheduleId}")
    public List<Map<String, Object>> getSeats(@PathVariable Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        List<Seat> seats = seatRepository.findByBus_BusId(schedule.getBus().getBusId());
        List<Booking> confirmedBookings = bookingRepository.findBySchedule_ScheduleId(scheduleId);
        List<SeatLock> activeLocks = seatLockRepository
                .findBySchedule_ScheduleIdAndExpiresAtAfter(scheduleId, LocalDateTime.now());

        return seats.stream().map(seat -> {
            Map<String, Object> map = new HashMap<>();
            map.put("seat_id", seat.getSeatId());
            map.put("seat_number", seat.getSeatNumber());

            boolean booked = confirmedBookings.stream()
                    .anyMatch(b -> b.getSeat().getSeatId().equals(seat.getSeatId())
                            && b.getStatus() == Booking.Status.Confirmed);
            map.put("booked", booked);

            boolean locked = activeLocks.stream()
                    .anyMatch(l -> l.getSeat().getSeatId().equals(seat.getSeatId()));
            map.put("locked", locked);

            return map;
        }).collect(Collectors.toList());
    }
    
    
}