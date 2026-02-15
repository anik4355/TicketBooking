package com.ticket_booking.IRCTC.entities;
// @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Ticket{

    private String ticketId;

    private String userId;

    private String source;

    private String destination;

    private String dateOfTravel;

    private Train train;

    private int rowNumber;

    private int seatNumber;

    public Ticket(){}

    public Ticket(String ticketId, String userId, String source, String destination, String dateOfTravel, Train train, int rowNumber, int seatNumber){
        this.ticketId = ticketId;
        this.userId = userId;
        this.source = source;
        this.destination = destination;
        this.dateOfTravel = dateOfTravel;
        this.train = train;
        this.seatNumber = seatNumber;
        this.rowNumber = rowNumber;
    }
    public void setSeatNumber(int seatNumber){
        this.seatNumber = seatNumber; 
    }
    public int getSeatNumber(){
        return seatNumber;
    }
    public void setRowNumber(int rowNumber){
        this.rowNumber = rowNumber;
    }
    public int getRowNumber(){
        return rowNumber;
    }
    public String getTicketInfo(){
        return String.format("Ticket ID: %s belongs to User %s from %s to %s on %s", ticketId, userId, source, destination, dateOfTravel);
    }

    public String getTicketId(){
        return ticketId;
    }

    public void setTicketId(String ticketId){
        this.ticketId = ticketId;
    }

    public String getSource(){
        return source;
    }

    public void setSource(String source){
        this.source = source;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getDestination(){
        return destination;
    }

    public void setDestination(String destination){
        this.destination = destination;
    }

    public String getDateOfTravel(){
        return dateOfTravel;
    }

    public void setDateOfTravel(String dateOfTravel){
        this.dateOfTravel = dateOfTravel;
    }

    public Train getTrain(){
        return train;
    }

    public void setTrain(Train train){
        this.train = train;
    }

}