package com.busreservation.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Coupons")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer couponId;

    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Double discountValue;   // percentage points (e.g. 10) or a flat rupee amount
    private Double minFare;         // nullable — minimum subtotal required to use this coupon
    private Double maxDiscount;     // nullable — cap for PERCENTAGE coupons
    private LocalDate expiryDate;   // nullable — never expires if null
    private Boolean active = true;

    public enum DiscountType { PERCENTAGE, FLAT }

    public Integer getCouponId() { return couponId; }
    public void setCouponId(Integer couponId) { this.couponId = couponId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(DiscountType discountType) { this.discountType = discountType; }
    public Double getDiscountValue() { return discountValue; }
    public void setDiscountValue(Double discountValue) { this.discountValue = discountValue; }
    public Double getMinFare() { return minFare; }
    public void setMinFare(Double minFare) { this.minFare = minFare; }
    public Double getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(Double maxDiscount) { this.maxDiscount = maxDiscount; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}