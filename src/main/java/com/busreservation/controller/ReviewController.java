package com.busreservation.controller;

import com.busreservation.model.Review;
import com.busreservation.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/bus/{busId}")
    public List<Review> getReviews(@PathVariable Integer busId) {
        return reviewRepository.findByBus_BusId(busId);
    }

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return reviewRepository.save(review);
    }
}
