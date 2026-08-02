ackage com.busreservation.dto;
 
public class AdminBusScheduleRequest {
    private String busNumber;
    private String busType;
    private Integer totalSeats;
    private String operatorName;
    private String source;
    private String destination;
    private String departureTime; // "yyyy-MM-ddTHH:mm" (matches <input type="datetime-local">)
    private String arrivalTime;
    private Double fare;
 
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    public String getBusType() { return busType; }
    public void setBusType(String busType) { this.busType = busType; }
    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public Double getFare() { return fare; }
    public void setFare(Double fare) { this.fare = fare; }
}
 