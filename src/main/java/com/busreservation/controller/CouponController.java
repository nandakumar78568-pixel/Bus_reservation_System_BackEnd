package com.busreservation.controller;

import com.busreservation.dto.CouponApplyRequest;
import com.busreservation.dto.CouponApplyResponse;
import com.busreservation.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    // Lets the frontend preview a discount before the user actually submits
    // the booking. No auth required — same trust level as browsing fares.
    @PostMapping("/apply")
    public ResponseEntity<CouponApplyResponse> apply(@RequestBody CouponApplyRequest request) {
        return ResponseEntity.ok(couponService.evaluate(request.getCode(), request.getFare()));
    }
}