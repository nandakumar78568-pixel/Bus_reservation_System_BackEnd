package com.busreservation.controller;

import com.busreservation.model.*;
import com.busreservation.repository.*;
import com.busreservation.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/seat-locks")
public class SeatLockController {

    @Autowired private SeatLockRepository seatLockRepository;
    @Autowired private ScheduleRepository scheduleRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> lockSeat(@RequestParam Integer scheduleId, @RequestParam Integer seatId,
                                        @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Integer userId = jwtUtil.extractUserId(token);

        Optional<SeatLock> existing = seatLockRepository.findBySchedule_ScheduleIdAndSeat_SeatId(scheduleId, seatId);

        if (existing.isPresent() && existing.get().getExpiresAt().isAfter(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Seat is currently locked by another user");
        }

        SeatLock lock = existing.orElse(new SeatLock());
        lock.setSchedule(scheduleRepository.findById(scheduleId).orElseThrow());
        lock.setSeat(seatRepository.findById(seatId).orElseThrow());
        lock.setUser(userRepository.findById(userId).orElseThrow());
        lock.setLockedAt(LocalDateTime.now());
        lock.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        seatLockRepository.save(lock);
        return ResponseEntity.ok("Seat locked for 5 minutes");
    }

    // Runs every 60 seconds to clear expired locks
    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000)
    public void clearExpiredLocks() {
        seatLockRepository.findByExpiresAtBefore(LocalDateTime.now())
                .forEach(seatLockRepository::delete);
    }
}
