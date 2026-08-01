package com.busreservation.dto;

public class CouponApplyResponse {
    private boolean valid;
    private String code;
    private Double discountAmount;
    private Double finalFare;
    private String message;

    public CouponApplyResponse() {}

    public CouponApplyResponse(boolean valid, String code, Double discountAmount, Double finalFare, String message) {
        this.valid = valid;
        this.code = code;
        this.discountAmount = discountAmount;
        this.finalFare = finalFare;
        this.message = message;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }
    public Double getFinalFare() { return finalFare; }
    public void setFinalFare(Double finalFare) { this.finalFare = finalFare; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}