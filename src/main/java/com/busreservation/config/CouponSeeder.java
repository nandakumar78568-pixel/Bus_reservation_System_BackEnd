package com.busreservation.config;

import com.busreservation.model.Coupon;
import com.busreservation.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CouponSeeder implements CommandLineRunner {

    @Autowired
    private CouponRepository couponRepository;

    @Override
    public void run(String... args) {
        seedIfMissing("GOFIRST", Coupon.DiscountType.FLAT, 150.0, null, null);
        seedIfMissing("WEEKEND75", Coupon.DiscountType.FLAT, 75.0, null, null);
        seedIfMissing("SLEEPER10", Coupon.DiscountType.PERCENTAGE, 10.0, null, 200.0);
    }

    private void seedIfMissing(String code, Coupon.DiscountType type, Double value, Double minFare, Double maxDiscount) {
        if (couponRepository.findByCodeIgnoreCase(code).isPresent()) return;

        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setDiscountType(type);
        coupon.setDiscountValue(value);
        coupon.setMinFare(minFare);
        coupon.setMaxDiscount(maxDiscount);
        coupon.setActive(true);
        couponRepository.save(coupon);
        System.out.println("Seeded coupon: " + code);
    }
}