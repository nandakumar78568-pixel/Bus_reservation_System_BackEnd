package com.busreservation.repository;

import com.busreservation.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Integer> {
    List<Seat> findByBus_BusId(Integer busId);
}
