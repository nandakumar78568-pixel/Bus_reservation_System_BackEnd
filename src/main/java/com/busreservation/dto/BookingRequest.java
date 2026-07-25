package com.busreservation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class BookingRequest {
    private Integer scheduleId;
    private Integer boardingPointId;
    private Integer droppingPointId;
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
    public List<PassengerDTO> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerDTO> passengers) { this.passengers = passengers; }
}