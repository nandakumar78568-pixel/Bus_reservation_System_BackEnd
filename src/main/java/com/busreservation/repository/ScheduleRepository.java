package com.busreservation.repository;

import com.busreservation.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByRoute_SourceAndRoute_Destination(String source, String destination);
}
