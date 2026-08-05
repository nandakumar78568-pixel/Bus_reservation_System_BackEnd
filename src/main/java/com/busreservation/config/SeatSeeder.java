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

    // Change this to however many seats you want per bus
    private static final int TARGET_SEATS_PER_BUS = 40;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Override
    public void run(String... args) {
        List<Bus> buses = busRepository.findAll();

        for (Bus bus : buses) {
            List<Seat> existingSeats = seatRepository.findByBus_BusId(bus.getBusId());
            int currentCount = existingSeats.size();

            if (currentCount >= TARGET_SEATS_PER_BUS) {
                continue; // already has enough seats
            }

            int nextNumber = currentCount + 1;

            for (int i = currentCount; i < TARGET_SEATS_PER_BUS; i++) {
                Seat seat = new Seat();
                seat.setBus(bus);
                seat.setSeatNumber("S" + nextNumber);
                // Same seat-type rule as the admin "add bus" flow, so
                // top-up seats render correctly too (seater/sleeper mix
                // for Semi_Sleeper, all-sleeper for Sleeper, etc).
                seat.setSeatType(AdminController.determineSeatType(bus.getBusType(), nextNumber, TARGET_SEATS_PER_BUS));
                seatRepository.save(seat);
                nextNumber++;
            }

            System.out.println("Added " + (TARGET_SEATS_PER_BUS - currentCount)
                    + " seats to bus " + bus.getBusNumber());
        }
    }
}