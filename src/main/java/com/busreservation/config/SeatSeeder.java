package com.busreservation.config;

import com.busreservation.controller.AdminController;
import com.busreservation.model.Bus;
import com.busreservation.model.Seat;
import com.busreservation.repository.BusRepository;
import com.busreservation.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeatSeeder implements CommandLineRunner {

    // Fallback only used when a bus has no totalSeats value set.
    private static final int DEFAULT_SEATS_PER_BUS = 40;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Override
    public void run(String... args) {
        List<Bus> buses = busRepository.findAll();

        for (Bus bus : buses) {
            int target = (bus.getTotalSeats() != null && bus.getTotalSeats() > 0)
                    ? bus.getTotalSeats()
                    : DEFAULT_SEATS_PER_BUS;

            List<Seat> existingSeats = seatRepository.findByBus_BusId(bus.getBusId());
            int currentCount = existingSeats.size();

            // --- Step 1: top up seat COUNT if this bus has fewer than target ---
            if (currentCount < target) {
                int nextNumber = currentCount + 1;
                for (int i = currentCount; i < target; i++) {
                    Seat seat = new Seat();
                    seat.setBus(bus);
                    seat.setSeatNumber("S" + nextNumber);
                    seat.setSeatType(AdminController.determineSeatType(bus.getBusType(), nextNumber, target));
                    seatRepository.save(seat);
                    nextNumber++;
                }
                System.out.println("Added " + (target - currentCount) + " seats to bus " + bus.getBusNumber());
            }

            // --- Step 2: repair seat_type for ALL of this bus's seats ---
            // Fixes seats that were seeded incorrectly (e.g. via raw SQL
            // scripts) or computed against a stale totalSeats value, so a
            // Semi_Sleeper/Sleeper bus always ends up with a correct split
            // no matter how its seats originally got created.
            List<Seat> allSeats = seatRepository.findByBus_BusId(bus.getBusId());
            allSeats.sort((a, b) -> a.getSeatId().compareTo(b.getSeatId()));

            int position = 1;
            int fixedCount = 0;
            for (Seat seat : allSeats) {
                Seat.SeatType correctType = AdminController.determineSeatType(bus.getBusType(), position, allSeats.size());
                if (seat.getSeatType() != correctType) {
                    seat.setSeatType(correctType);
                    seatRepository.save(seat);
                    fixedCount++;
                }
                position++;
            }
            if (fixedCount > 0) {
                System.out.println("Repaired seat_type on " + fixedCount + " seat(s) for bus " + bus.getBusNumber());
            }
        }
    }
}