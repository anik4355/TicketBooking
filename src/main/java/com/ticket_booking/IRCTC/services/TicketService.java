package com.ticket_booking.IRCTC.services;
import java.util.List;
import java.util.UUID;

import com.ticket_booking.IRCTC.entities.Ticket;

import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

import tools.jackson.core.type.TypeReference;

import java.util.ArrayList;

import com.ticket_booking.IRCTC.entities.Train;
import com.ticket_booking.IRCTC.entities.User;
public class TicketService {
    private Ticket ticket;
    private final List<Ticket> ticketList;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ticket_path = "src\\main\\java\\com\\ticket_booking\\IRCTC\\localDB\\tickets.json";


    public TicketService() throws IOException{
        this.ticketList = loadTicket();
    }
    public List<Ticket> loadTicket() throws IOException{
        File tickets  = new File(ticket_path);
        if(tickets.exists() && tickets.length() > 0){
            return objectMapper.readValue(tickets, new TypeReference<List<Ticket>>() {});
        }else{
            return new ArrayList<>();
        }
    }

    public Ticket generateTicket(Train train, int row, int seat, String source, String destination, User user) throws IOException{
        String randomDate = java.time.LocalDate.now()
        .plusDays(new java.util.Random().nextInt(30) + 1)
        .toString();

        Ticket newTicket = new Ticket(
            UUID.randomUUID().toString(),
            user.getUserId(),
            source,
            destination,
            randomDate,
            train,
            row,
            seat
        );

        ticketList.add(newTicket);
        saveTicketListToFile();
        return newTicket;

    }
    public void saveTicketListToFile() throws IOException{
        File ticketFile = new File(ticket_path);
            objectMapper.writeValue(ticketFile,ticketList);
        }
    public boolean removeTicket(String ticketId) throws IOException {

        boolean removed = ticketList.removeIf(
                ticket -> ticket.getTicketId().equalsIgnoreCase(ticketId)
        );

        if (removed) {
            saveTicketListToFile();
            System.out.println("Ticket removed from database.");
        } else {
            System.out.println("Ticket not found in database.");
        }
        return removed;

    }
}
