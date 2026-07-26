package com.busreservation.controller;

import com.busreservation.model.Bus;
import com.busreservation.model.Review;
import com.busreservation.model.User;
import com.busreservation.repository.BusRepository;
import com.busreservation.repository.ReviewRepository;
import com.busreservation.repository.UserRepository;
import com.busreservation.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/bus/{busId}")
    public List<Review> getReviews(@PathVariable Integer busId) {
        return reviewRepository.findByBus_BusId(busId);
    }

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody Review review,
                                        @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        if (review.getBus() == null || review.getBus().getBusId() == null) {
            return ResponseEntity.badRequest().body("busId is required");
        }
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            return ResponseEntity.badRequest().body("rating must be between 1 and 5");
        }

        Integer userId = jwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Bus bus = busRepository.findById(review.getBus().getBusId())
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        review.setUser(user);
        review.setBus(bus);
        review.setReviewId(null); // ensure this is always a create, never overwriting by client-supplied id

        return ResponseEntity.ok(reviewRepository.save(review));
    }
}