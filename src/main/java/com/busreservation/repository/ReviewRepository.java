package com.busreservation.repository;

import com.busreservation.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByBus_BusId(Integer busId);
}
