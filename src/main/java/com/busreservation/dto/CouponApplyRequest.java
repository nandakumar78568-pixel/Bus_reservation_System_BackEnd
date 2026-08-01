package com.busreservation.dto;

public class CouponApplyRequest {
    private String code;
    private Double fare;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Double getFare() { return fare; }
    public void setFare(Double fare) { this.fare = fare; }
}