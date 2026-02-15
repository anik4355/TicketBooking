package com.ticket_booking.IRCTC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ticket_booking.IRCTC.entities.Train;
import com.ticket_booking.IRCTC.entities.User;
import com.ticket_booking.IRCTC.services.TrainService;
import com.ticket_booking.IRCTC.services.UserBookingService;
import com.ticket_booking.IRCTC.util.UserServiceUtil;

@SpringBootApplication
public class IrctcApplication {

	public static void main(String[] args) throws IOException {
		// SpringApplication.run(IrctcApplication.class, args);
		System.out.println("Running Train Booking System");
		Scanner sc = new Scanner(System.in);
		int option  = 0;
		UserBookingService userBookingService;
		try {
			userBookingService = new UserBookingService();
		} catch (Exception ex) {
			System.out.println("There is something wrong.");
			return ;
		}
		Train selectTrainForBooking = null;
		String stationSource = null;
		String stationDestination = null;
		while(option != 7){
			System.out.println("Choose your option : ");
			System.out.println("1. Sign Up");
			System.out.println("2. Login");
			System.out.println("3. Fetch Booking");
			System.out.println("4. Search Train");
			System.out.println("5. Book a Seat");
			System.out.println("6. Cancle my Booking");
			System.out.println("7. Exit from Service");
			option  = sc.nextInt();
			
			switch(option){
				case 1 : 
					System.out.println();
					System.out.print("Enter your name :");
					String nameToSignup = sc.next();
					System.out.print("Enter your password :");
					String passwordToSignup = sc.next();
					User userToSignup = new User(nameToSignup, passwordToSignup,UserServiceUtil.hashPassword(passwordToSignup), new ArrayList<>(), UUID.randomUUID().toString() );
					userBookingService.signUp(userToSignup);
					break;

				case 2:
					System.out.println("Enter the username to Login");
					String nameToLogin = sc.next();

					System.out.println("Enter the password to login");
					String passwordToLogin = sc.next();

					User userToLogin = new User(
						nameToLogin,
						passwordToLogin,
						null,
						new ArrayList<>(),
						""
					);

					userBookingService = new UserBookingService(userToLogin);

					if (!userBookingService.loginUser()) {
						System.out.println("Login failed!");
					}
					break;

					
				case 3:
					if (userBookingService.getUser() == null) {
						System.out.println("Please login first.");
						break;
					}
					System.out.println("Feetching your booking");
					userBookingService.fetchBooking();
					break;



				case 4:
					System.out.println("Enter train source :");
                	stationSource = sc.next();
					System.out.println("Enter train destionation source :");
					stationDestination = sc.next();
					List<Train> trains = userBookingService.getTrains(stationSource, stationDestination);
					int i =1;
					for(Train train : trains){
						System.out.println( train.getTrainInfo());
						train.seatDetails();
						for(Map.Entry<String, String> entry : train.getStationTimes().entrySet()){
							System.out.println("station" + entry.getKey() + "time :" + entry.getValue());
						}
					}
					System.out.println("Select train for booking :");
					selectTrainForBooking = trains.get(sc.nextInt());
					List<List<Integer>> availableSeats = userBookingService.featchSeats(selectTrainForBooking);
					for (int r = 0; r < availableSeats.size(); r++) {
						for (int c = 0; c < availableSeats.get(r).size(); c++) {
							System.out.print(availableSeats.get(r).get(c) + " ");
						}
						System.out.println();
					}
					break;

				case 5:
					if (selectTrainForBooking == null) {
						System.out.println("Please search and select a train first.");
						break;
					}
					TrainService trainService = new TrainService();
						selectTrainForBooking =
							trainService.getTrainById(selectTrainForBooking.getTrainId());

					List<List<Integer>> seats =
						userBookingService.featchSeats(selectTrainForBooking);

					for (int r = 0; r < seats.size(); r++) {
						for (int c = 0; c < seats.get(r).size(); c++) {
							System.out.print(seats.get(r).get(c) + " ");
						}
						System.out.println();
					}

					System.out.print("Enter row number: ");
					int row = sc.nextInt();

					System.out.print("Enter seat number: ");
					int seat = sc.nextInt();

					boolean booked = userBookingService.bookTrainSeat(selectTrainForBooking, row, seat,stationSource,stationDestination);

					if (booked) {
						System.out.println("Seat booked successfully!");
					} else {
						System.out.println("Seat already booked or invalid.");
					}
					break;

				case 6:
					System.out.println("Enter your ticket id to cancle :");
					String ticket = sc.next();
					userBookingService.cancelBooking(ticket);
					break;
				default:
					break;
			}
			
		}
	}
	
	
}
