package com.busreservation.repository;

import com.busreservation.model.SeatLock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatLockRepository extends JpaRepository<SeatLock, Integer> {
    Optional<SeatLock> findBySchedule_ScheduleIdAndSeat_SeatId(Integer scheduleId, Integer seatId);
    List<SeatLock> findByExpiresAtBefore(LocalDateTime time);
}
