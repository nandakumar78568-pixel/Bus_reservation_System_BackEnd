package com.busreservation.controller;

import com.busreservation.model.Booking;
import com.busreservation.model.Schedule;
import com.busreservation.model.Seat;
import com.busreservation.repository.BookingRepository;
import com.busreservation.repository.ScheduleRepository;
import com.busreservation.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/{scheduleId}")
    public List<Map<String, Object>> getSeats(@PathVariable Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        List<Seat> seats = seatRepository.findByBus_BusId(schedule.getBus().getBusId());
        List<Booking> confirmedBookings = bookingRepository.findBySchedule_ScheduleId(scheduleId);

        return seats.stream().map(seat -> {
            Map<String, Object> map = new HashMap<>();
            map.put("seat_id", seat.getSeatId());
            map.put("seat_number", seat.getSeatNumber());
            boolean booked = confirmedBookings.stream()
                    .anyMatch(b -> b.getSeat().getSeatId().equals(seat.getSeatId())
                            && b.getStatus() == Booking.Status.Confirmed);
            map.put("booked", booked);
            return map;
        }).collect(Collectors.toList());
    }
}
