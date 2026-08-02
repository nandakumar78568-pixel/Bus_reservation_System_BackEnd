package com.busreservation.controller;

import com.busreservation.dto.AdminBusScheduleRequest;
import com.busreservation.model.Bus;
import com.busreservation.model.Route;
import com.busreservation.model.Schedule;
import com.busreservation.model.Seat;
import com.busreservation.repository.BusRepository;
import com.busreservation.repository.RouteRepository;
import com.busreservation.repository.ScheduleRepository;
import com.busreservation.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/buses")
public class AdminController {

    private static final int DEFAULT_SEATS_PER_BUS = 40;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SeatRepository seatRepository;

    @GetMapping
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    // Kept as-is for backward compatibility (bus only, no schedule/seats).
    @PostMapping
    public Bus addBus(@RequestBody Bus bus) {
        return busRepository.save(bus);
    }

    // Preferred endpoint for the Admin Dashboard "Add Bus" form:
    // creates the Bus, finds-or-creates the Route, creates the Schedule,
    // and seeds seats for the new bus in one call.
    @PostMapping("/full")
    public ResponseEntity<?> addBusWithSchedule(@RequestBody AdminBusScheduleRequest request) {
        if (request.getBusNumber() == null || request.getBusNumber().isBlank()) {
            return ResponseEntity.badRequest().body("busNumber is required");
        }
        if (request.getSource() == null || request.getSource().isBlank()
                || request.getDestination() == null || request.getDestination().isBlank()) {
            return ResponseEntity.badRequest().body("source and destination are required");
        }
        if (request.getDepartureTime() == null || request.getArrivalTime() == null) {
            return ResponseEntity.badRequest().body("departureTime and arrivalTime are required");
        }
        if (request.getFare() == null || request.getFare() <= 0) {
            return ResponseEntity.badRequest().body("fare must be greater than zero");
        }

        Bus.BusType busType;
        try {
            busType = Bus.BusType.valueOf(request.getBusType());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid busType. Must be one of AC, Non_AC, Sleeper, Semi_Sleeper");
        }

        LocalDateTime departureTime;
        LocalDateTime arrivalTime;
        try {
            departureTime = LocalDateTime.parse(request.getDepartureTime());
            arrivalTime = LocalDateTime.parse(request.getArrivalTime());
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("departureTime/arrivalTime must look like yyyy-MM-ddTHH:mm");
        }
        if (!arrivalTime.isAfter(departureTime)) {
            return ResponseEntity.badRequest().body("arrivalTime must be after departureTime");
        }

        int totalSeats = request.getTotalSeats() != null ? request.getTotalSeats() : DEFAULT_SEATS_PER_BUS;

        // 1. Bus
        Bus bus = new Bus();
        bus.setBusNumber(request.getBusNumber());
        bus.setBusType(busType);
        bus.setTotalSeats(totalSeats);
        bus.setOperatorName(request.getOperatorName());
        Bus savedBus = busRepository.save(bus);

        // 2. Route (reuse if source+destination already exists)
        Route route = routeRepository
                .findBySourceAndDestination(request.getSource(), request.getDestination())
                .orElseGet(() -> {
                    Route r = new Route();
                    r.setSource(request.getSource());
                    r.setDestination(request.getDestination());
                    return routeRepository.save(r);
                });

        // 3. Schedule
        Schedule schedule = new Schedule();
        schedule.setBus(savedBus);
        schedule.setRoute(route);
        schedule.setDepartureTime(departureTime);
        schedule.setArrivalTime(arrivalTime);
        schedule.setFare(request.getFare());
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // 4. Seats — without this, seat selection would show an empty bus
        // until the next app restart (SeatSeeder only runs on startup).
        for (int i = 1; i <= totalSeats; i++) {
            Seat seat = new Seat();
            seat.setBus(savedBus);
            seat.setSeatNumber("S" + i);
            seat.setSeatType(i % 2 == 0 ? Seat.SeatType.Aisle : Seat.SeatType.Window);
            seatRepository.save(seat);
        }

        return ResponseEntity.ok(savedSchedule);
    }

    @DeleteMapping("/{busId}")
    public void deleteBus(@PathVariable Integer busId) {
        busRepository.deleteById(busId);
    }
}