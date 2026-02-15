package com.ticket_booking.IRCTC.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
// import java.util.ArrayList;  
import java.util.List;
import java.util.Optional;

import com.ticket_booking.IRCTC.entities.Ticket;
import com.ticket_booking.IRCTC.entities.Train;
import com.ticket_booking.IRCTC.entities.User;
import com.ticket_booking.IRCTC.util.UserServiceUtil;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

public class UserBookingService  {
    private User user;
    private List<User> userList;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String USER_PATH = "src\\main\\java\\com\\ticket_booking\\IRCTC\\localDB\\users.json";

    public UserBookingService() throws IOException{
        this.userList = loadUser();
    }

    public UserBookingService(User user) throws IOException {
        this.user = user;
        this.userList = loadUser();
        
    }


    public User getUser(){
        return this.user;
    }



    public List<User> loadUser() throws IOException {
        File users = new File(USER_PATH);

        if (users.exists() && users.length() > 0) {
            return objectMapper.readValue(users, new TypeReference<List<User>>() {});
        }
        return new ArrayList<>();
    }



    public Boolean loginUser() throws IOException{
        Optional<User>  foundUser = userList.stream().filter(user->
            user.getName().equalsIgnoreCase(this.user.getName()) && UserServiceUtil.checkPassword(this.user.getPassword(),user.getHashedPassword())
        ).findFirst();
        if(foundUser.isPresent()){
            this.user = foundUser.get();
            System.out.println(user.getName() + " Have Login Successfully");
        }else{
            System.out.println("Invalid Credentials");
        }
        return  foundUser.isPresent();

    }



    public Boolean signUp(User user){
        try{
            boolean exists = userList.stream()
                .anyMatch(u -> u.getName().equalsIgnoreCase(user.getName()));

                if (exists) {
                    System.out.println("User already exists");
                return false;
                }
            userList.add(user);
            saveUserListToFile();
            return Boolean.TRUE;
        }
        catch(IOException ex){
            return Boolean.FALSE;
        }
    }




    private void saveUserListToFile() throws IOException{
         File userFile = new File(USER_PATH);
         objectMapper.writeValue(userFile, userList);   
    }



    public void fetchBooking() {
        if (this.user == null) {
            System.out.println("Please login first.");
        return;
        }
        this.user.printTickets();
    }




    public List<Train> getTrains(String source, String destination) {
        try{
            TrainService trainService = new TrainService();
            return trainService.SearchTrains(source, destination);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }



    public List<List<Integer>> featchSeats(Train train){
        return train.getSeats();
    }




    public Boolean bookTrainSeat(Train train, int row, int seat ,String source, String destination) {
        try{
            TrainService trainService = new TrainService();
            TicketService ticketService = new TicketService();
            List<List<Integer>> seats = train.getSeats();
            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    Ticket newTicket = ticketService.generateTicket(train, row, seat, source, destination, user);
                    this.user.getTicketsBooked().add(newTicket);
                    trainService.addTrain(train);
                    saveUserListToFile();
                    return true; 
                } else {
                    return false; 
                }
            } else {
                return false; 
            }
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }



    public void cancelBooking(String ticketId) throws IOException{
        
        try {
            
            if (ticketId == null || ticketId.isEmpty()) {
                System.out.println("Ticket ID cannot be null or empty.");
                return;
            }
            boolean belongsToUser = this.user.getTicketsBooked().stream()
                .anyMatch(t -> t.getTicketId().equals(ticketId));

            if (!belongsToUser) {
                System.out.println("You cannot cancel someone else's ticket!");
                return;
            }
            TrainService trainService = new TrainService();
            TicketService ticketService = new TicketService();
            
            trainService.seatAvailable(ticketId);
            ticketService.removeTicket(ticketId);
            this.user.getTicketsBooked()
                .removeIf(ticket -> ticket.getTicketId().equals(ticketId));
            saveUserListToFile();
            System.out.println("Ticket with ID " + ticketId + " has been canceled.");
        } catch (IOException e) {
            System.out.println("Failed to save cancellation.");
        }
        
    }
}
