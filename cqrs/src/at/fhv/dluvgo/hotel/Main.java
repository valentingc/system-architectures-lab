package at.fhv.dluvgo.hotel;

import at.fhv.dluvgo.hotel.read.RunRead;
import at.fhv.dluvgo.hotel.read.cqrs.query.GetBookingsQuery;
import at.fhv.dluvgo.hotel.read.cqrs.query.GetFreeRoomsQuery;
import at.fhv.dluvgo.hotel.read.domain.BookableRoom;
import at.fhv.dluvgo.hotel.read.domain.Booking;
import at.fhv.dluvgo.hotel.write.RunWrite;
import at.fhv.dluvgo.hotel.write.cqrs.command.BookRoomCommand;
import at.fhv.dluvgo.hotel.write.cqrs.command.CancelBookingCommand;
import at.fhv.dluvgo.hotel.write.cqrs.command.CreateRoomCommand;
import at.fhv.dluvgo.hotel.write.repository.EventStore;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class Main implements Runnable {
    private final RunWrite runWrite;
    private final RunRead runRead;

    public Main(RunWrite runWrite, RunRead runRead) {
        this.runWrite = runWrite;
        this.runRead = runRead;
    }

    public static void main(String[] args) {

        RunWrite wt = new RunWrite();
        Thread wtThread = new Thread(wt);

        RunRead rr = new RunRead();
        Thread rrThread = new Thread(rr);

        EventStore eventStore = wt.getEventStore();
        eventStore.subscribe(rr.getBookingProjector());
        eventStore.subscribe(rr.getRoomProjector());

        Main m = new Main(wt, rr);
        Thread mainThread = new Thread(m);

        wtThread.start();
        rrThread.start();
        mainThread.start();
    }

    @Override
    public void run() {
        // Rooms created by hand
        runWrite.runCommand(new CreateRoomCommand(UUID.randomUUID(), 1));
        runWrite.runCommand(new CreateRoomCommand(UUID.randomUUID(), 1));
        runWrite.runCommand(new CreateRoomCommand(UUID.randomUUID(), 2));
        runWrite.runCommand(new CreateRoomCommand(UUID.randomUUID(), 2));
        runWrite.runCommand(new CreateRoomCommand(UUID.randomUUID(), 2));
        runWrite.runCommand(new CreateRoomCommand(UUID.randomUUID(), 2));
        runWrite.runCommand(new CreateRoomCommand(UUID.randomUUID(), 3));
        runWrite.runCommand(new CreateRoomCommand(UUID.randomUUID(), 4));
        runWrite.runCommand(new CreateRoomCommand(UUID.randomUUID(), 4));
        runWrite.runCommand(new CreateRoomCommand(UUID.randomUUID(), 5)); // 10 rooms

        try {
            this.handleCliInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCliInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        MenuState state = MenuState.MAIN_MENU;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        while (true) {
            while (state.equals(MenuState.MAIN_MENU)) {
                System.out.println("Welcome to the CQRS with ES HotelSystem by Valentin & Dominic");
                System.out.println(
                    "General Checkout latest 11am, Checkin earliest 2pm, no exceptions"
                );
                System.out.println("1) List of free rooms");
                System.out.println("2) List of bookings");
                System.out.println("3) Book a room");
                System.out.println("4) Cancel booking");
                System.out.println("0) Quit the system");

                String input = reader.readLine();
                switch (input) {
                    case "1":
                        state = MenuState.FREE_ROOMS;
                        break;
                    case "2":
                        state = MenuState.BOOKINGS;
                        break;
                    case "3":
                        state = MenuState.BOOK;
                        break;
                    case "4":
                        state = MenuState.CANCEL;
                        break;
                    case "0":
                    case "quit":
                        System.exit(0);
                    default:
                        System.err.println("[CLI] Unknown command");
                        break;
                }
            }

            while (state.equals(MenuState.FREE_ROOMS)) {
                try {
                    System.out.println("$ Enter start date (e.g. 07-12-2021 14:00)");
                    LocalDateTime start = LocalDateTime.parse(reader.readLine(), formatter);
                    System.out.println("$ Enter end date (e.g. 09-12-2021 11:00)");
                    LocalDateTime end = LocalDateTime.parse(reader.readLine(), formatter);
                    System.out.println("$ Enter room capacity");
                    int capacity = Integer.parseInt(reader.readLine());

                    System.out.println("------------------------");
                    List<BookableRoom> result = this.runRead.runQuery(
                        new GetFreeRoomsQuery(start, end, capacity)
                    );
                    System.out.printf("## Found %s bookable rooms%n", result.size());
                    for (BookableRoom br : result) {
                        System.out.println(br);
                        System.out.println("------------------------");
                    }
                } catch (Exception e) {
                    System.err.println(
                        "[CLI] Something went wrong - see exception. Returning to main menu"
                    );
                    e.printStackTrace();
                } finally {
                    state = MenuState.MAIN_MENU;
                }
            }
            while (state.equals(MenuState.BOOKINGS)) {
                try {
                    System.out.println("$ Enter start date (e.g. 07-12-2021 14:00)");
                    LocalDateTime start = LocalDateTime.parse(reader.readLine(), formatter);
                    System.out.println("$ Enter end date (e.g. 09-12-2021 11:00)");
                    LocalDateTime end = LocalDateTime.parse(reader.readLine(), formatter);

                    System.out.println("------------------------");
                    List<Booking> result = this.runRead.runQuery(new GetBookingsQuery(start, end));
                    System.out.printf("## Found %s bookings%n", result.size());
                    for (Booking b : result) {
                        System.out.println(b);
                        System.out.println("------------------------");
                    }
                } catch (Exception e) {
                    System.err.println(
                        "[CLI] Something went wrong - see exception. Returning to main menu"
                    );
                    e.printStackTrace();
                } finally {
                    state = MenuState.MAIN_MENU;
                }
            }

            while (state.equals(MenuState.BOOK)) {
                try {
                    System.out.println(
                        "$ Enter room number (e.g. 375f4797-558a-4d23-a965-44553c807ea1)"
                    );
                    UUID roomNumber = UUID.fromString(reader.readLine());
                    System.out.println("$ Enter start date (e.g. 07-12-2021 14:00)");
                    LocalDateTime start = LocalDateTime.parse(reader.readLine(), formatter);
                    System.out.println("$ Enter end date (e.g. 09-12-2021 11:00)");
                    LocalDateTime end = LocalDateTime.parse(reader.readLine(), formatter);
                    System.out.println("$ Enter required capacity");
                    int capacity = Integer.parseInt(reader.readLine());
                    System.out.println("$ Enter name");
                    String name = reader.readLine();

                    this.runWrite.runCommand(
                        new BookRoomCommand(
                            roomNumber,
                            start,
                            end,
                            name,
                            capacity
                        ));
                    System.out.println("[CLI] Command sent successfully");
                } catch (Exception e) {
                    System.err.println(
                        "[CLI] Something went wrong - see exception. Returning to main menu"
                    );
                    e.printStackTrace();
                } finally {
                    state = MenuState.MAIN_MENU;
                }
            }

            while (state.equals(MenuState.CANCEL)) {
                try {
                    this.runWrite.runCommand(new CancelBookingCommand(
                        UUID.fromString(reader.readLine())
                    ));
                    System.out.println("[CLI] Command sent successfully");
                } catch (IllegalArgumentException e) {
                    System.err.println(
                        "[CLI] Something went wrong - see exception. Returning to main menu"
                    );
                    e.printStackTrace();
                } finally {
                    state = MenuState.MAIN_MENU;
                }
            }
        }
    }

    private enum MenuState {
        MAIN_MENU,
        FREE_ROOMS,
        BOOKINGS,
        CANCEL,
        BOOK
    }
}
