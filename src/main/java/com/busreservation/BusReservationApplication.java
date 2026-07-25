package com.busreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BusReservationApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusReservationApplication.class, args);
    }
}
