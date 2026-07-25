package com.busreservation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Cancellations")
public class Cancellation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cancellationId;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private LocalDateTime cancelledAt = LocalDateTime.now();
    private String reason;
    private Double refundAmount;

    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus = RefundStatus.Pending;

    public enum RefundStatus { Pending, Processed, Rejected }

    public Integer getCancellationId() { return cancellationId; }
    public void setCancellationId(Integer cancellationId) { this.cancellationId = cancellationId; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }
    public RefundStatus getRefundStatus() { return refundStatus; }
    public void setRefundStatus(RefundStatus refundStatus) { this.refundStatus = refundStatus; }
}
