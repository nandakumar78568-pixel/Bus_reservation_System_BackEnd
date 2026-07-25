package com.busreservation.repository;

import com.busreservation.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findBySchedule_ScheduleId(Integer scheduleId);
    List<Booking> findByUser_UserId(Integer userId);
    boolean existsBySchedule_ScheduleIdAndSeat_SeatIdAndStatus(Integer scheduleId, Integer seatId, Booking.Status status);
}
