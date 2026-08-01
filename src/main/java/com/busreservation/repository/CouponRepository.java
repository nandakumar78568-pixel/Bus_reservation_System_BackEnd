package com.busreservation.repository;

import com.busreservation.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    Optional<Coupon> findByCodeIgnoreCase(String code);
}