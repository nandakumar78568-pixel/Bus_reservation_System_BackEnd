package com.busreservation.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Bookings", uniqueConstraints = @UniqueConstraint(columnNames = {"schedule_id", "seat_id"}))
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "boarding_point_id")
    private BoardingPoint boardingPoint;

    @ManyToOne
    @JoinColumn(name = "dropping_point_id")
    private BoardingPoint droppingPoint;

    private LocalDate journeyDate;

    private LocalDateTime bookingDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Status status = Status.Pending;

    public enum Status { Confirmed, Cancelled, Pending }

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }
    public Seat getSeat() { return seat; }
    public void setSeat(Seat seat) { this.seat = seat; }
    public BoardingPoint getBoardingPoint() { return boardingPoint; }
    public void setBoardingPoint(BoardingPoint boardingPoint) { this.boardingPoint = boardingPoint; }
    public BoardingPoint getDroppingPoint() { return droppingPoint; }
    public void setDroppingPoint(BoardingPoint droppingPoint) { this.droppingPoint = droppingPoint; }
    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}