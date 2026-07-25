package com.busreservation.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "BoardingPoints")
public class BoardingPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pointId;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    private String pointName;

    @Enumerated(EnumType.STRING)
    private PointType pointType;

    private LocalTime pointTime;

    public enum PointType { Boarding, Dropping }

    public Integer getPointId() { return pointId; }
    public void setPointId(Integer pointId) { this.pointId = pointId; }
    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }
    public String getPointName() { return pointName; }
    public void setPointName(String pointName) { this.pointName = pointName; }
    public PointType getPointType() { return pointType; }
    public void setPointType(PointType pointType) { this.pointType = pointType; }
    public LocalTime getPointTime() { return pointTime; }
    public void setPointTime(LocalTime pointTime) { this.pointTime = pointTime; }
}
