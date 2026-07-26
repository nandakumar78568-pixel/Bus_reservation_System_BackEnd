package com.busreservation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class BookingRequest {
    private Integer scheduleId;
    private Integer boardingPointId;
    private Integer droppingPointId;
    private String journeyDate;
    private String paymentMethod;
    private String upiId;
    private String cardNumber;
    private List<PassengerDTO> passengers;

    public static class PassengerDTO {
        @JsonProperty("seat_id")
        private Integer seatId;
        private String name;
        private Integer age;
        private String gender;

        public Integer getSeatId() { return seatId; }
        public void setSeatId(Integer seatId) { this.seatId = seatId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
    }

    public Integer getScheduleId() { return scheduleId; }
    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }
    public Integer getBoardingPointId() { return boardingPointId; }
    public void setBoardingPointId(Integer boardingPointId) { this.boardingPointId = boardingPointId; }
    public Integer getDroppingPointId() { return droppingPointId; }
    public void setDroppingPointId(Integer droppingPointId) { this.droppingPointId = droppingPointId; }
    public String getJourneyDate() { return journeyDate; }
    public void setJourneyDate(String journeyDate) { this.journeyDate = journeyDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public List<PassengerDTO> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerDTO> passengers) { this.passengers = passengers; }
}