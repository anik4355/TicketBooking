package com.ticket_booking.IRCTC.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
// import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import com.ticket_booking.IRCTC.entities.Ticket;
import com.ticket_booking.IRCTC.entities.Train;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

public class TrainService {
    private Train train;
    private List<Train> trainList;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAIN_PATH = "src\\main\\java\\com\\ticket_booking\\IRCTC\\localDB\\trains.json";

    public TrainService() throws IOException {
        this.trainList = loadTrain();
    }

    public TrainService(Train train) throws IOException {
        this.train = train;
        this.trainList = loadTrain();
    }

    public List<Train> loadTrain() throws IOException {
        File trains = new File(TRAIN_PATH);

        if (trains.exists() && trains.length() > 0) {
            return objectMapper.readValue(trains, new TypeReference<List<Train>>() {});
        }
        return new java.util.ArrayList<>();
    }


    public List<Train> SearchTrains(String source, String destination) throws IndexOutOfBoundsException{
        List<Train> trains = trainList.stream().filter(train ->
            validTrain(train, source, destination)).collect(java.util.stream.Collectors.toList());
            return trains;
    }
    public boolean validTrain(Train train, String source, String destination){
        List<String> stationsOrder = train.getStations();
        int sourceIndex = IntStream.range(0, stationsOrder.size())
            .filter(i -> stationsOrder.get(i).equalsIgnoreCase(source))
            .findFirst()
            .orElse(-1);

        int destinationIndex = IntStream.range(0, stationsOrder.size())
            .filter(i -> stationsOrder.get(i).equalsIgnoreCase(destination))
            .findFirst()
            .orElse(-1);

        
        return destinationIndex != -1 && sourceIndex != -1 && sourceIndex < destinationIndex;
    }
    public void addTrain(Train newTrain) throws IOException {
        Optional<Train> existingTrain = trainList.stream()
                .filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId()))
                .findFirst();

        if (existingTrain.isPresent()) {
            updateTrain(newTrain);
        } else {
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }
    public void updateTrain(Train updatedTrain) throws IOException {
        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                .findFirst();

        if (index.isPresent()) {
            trainList.set(index.getAsInt(), updatedTrain);
            saveTrainListToFile();
        } else {
            addTrain(updatedTrain);
        }
    }

    private void saveTrainListToFile() throws IOException {
        objectMapper.writeValue(new File(TRAIN_PATH), trainList);
    }
    public void seatAvailable(String ticketId) {
        try {
            TicketService ticketService = new TicketService();
            List<Ticket> tickets = ticketService.loadTicket();

        
            Optional<Ticket> ticketOptional = tickets.stream()
                    .filter(t -> t.getTicketId().equalsIgnoreCase(ticketId))
                    .findFirst();

            if (ticketOptional.isEmpty()) {
                System.out.println("Ticket not found.");
                return;
            }

            Ticket ticket = ticketOptional.get();

            Optional<Train> trainOptional = trainList.stream()
                    .filter(t -> t.getTrainId()
                    .equalsIgnoreCase(ticket.getTrain().getTrainId()))
                    .findFirst();

            if (trainOptional.isEmpty()) {
                System.out.println("Train not found.");
                return;
            }

            Train train = trainOptional.get();
            List<List<Integer>> seats = train.getSeats();
            seats.get(ticket.getRowNumber()).set(ticket.getSeatNumber(), 0);

            train.setSeats(seats);
            updateTrain(train);

            System.out.println("Seat successfully made available.");

        } catch (IOException e) {
            System.out.println("Error while cancelling seat.");
        }
    }
    public Train getTrainById(String trainId) throws IOException {
        return trainList.stream()
                .filter(t -> t.getTrainId().equalsIgnoreCase(trainId))
                .findFirst()
                .orElse(null);
    }



    
}
