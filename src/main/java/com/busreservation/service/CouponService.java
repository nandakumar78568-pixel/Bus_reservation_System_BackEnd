package com.busreservation.service;

import com.busreservation.dto.CouponApplyResponse;
import com.busreservation.model.Coupon;
import com.busreservation.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    /**
     * Validates a coupon against a fare and returns the discount to apply.
     * Never throws — an invalid/expired/ineligible coupon is a normal
     * outcome (valid=false + reason), not a server error. Shared by the
     * preview endpoint (CouponController) and the real booking flow
     * (BookingController), so the discount math only lives in one place.
     */
    public CouponApplyResponse evaluate(String code, Double fare) {
        if (code == null || code.isBlank()) {
            return new CouponApplyResponse(false, code, 0.0, fare, "Enter a coupon code");
        }
        if (fare == null || fare <= 0) {
            return new CouponApplyResponse(false, code, 0.0, fare, "Fare must be greater than zero");
        }

        Optional<Coupon> found = couponRepository.findByCodeIgnoreCase(code.trim());
        if (found.isEmpty()) {
            return new CouponApplyResponse(false, code, 0.0, fare, "Invalid coupon code");
        }

        Coupon coupon = found.get();

        if (coupon.getActive() != null && !coupon.getActive()) {
            return new CouponApplyResponse(false, code, 0.0, fare, "This coupon is no longer active");
        }
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDate.now())) {
            return new CouponApplyResponse(false, code, 0.0, fare, "This coupon has expired");
        }
        if (coupon.getMinFare() != null && fare < coupon.getMinFare()) {
            return new CouponApplyResponse(false, code, 0.0, fare,
                    "Minimum fare of \u20B9" + coupon.getMinFare() + " required for this coupon");
        }

        double discountAmount;
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            discountAmount = fare * (coupon.getDiscountValue() / 100.0);
            if (coupon.getMaxDiscount() != null) {
                discountAmount = Math.min(discountAmount, coupon.getMaxDiscount());
            }
        } else {
            discountAmount = coupon.getDiscountValue();
        }

        discountAmount = Math.min(discountAmount, fare); // never discount more than the fare
        discountAmount = Math.round(discountAmount * 100.0) / 100.0;
        double finalFare = Math.round((fare - discountAmount) * 100.0) / 100.0;

        return new CouponApplyResponse(true, coupon.getCode(), discountAmount, finalFare,
                "Coupon applied! You saved \u20B9" + discountAmount);
    }
}