package com.busreservation.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Buses")
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer busId;

    @Column(unique = true)
    private String busNumber;

    @Enumerated(EnumType.STRING)
    private BusType busType;

    private Integer totalSeats;
    private String operatorName;

    public enum BusType { AC, Non_AC, Sleeper, Semi_Sleeper }

    public Integer getBusId() { return busId; }
    public void setBusId(Integer busId) { this.busId = busId; }
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    public BusType getBusType() { return busType; }
    public void setBusType(BusType busType) { this.busType = busType; }
    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
}
