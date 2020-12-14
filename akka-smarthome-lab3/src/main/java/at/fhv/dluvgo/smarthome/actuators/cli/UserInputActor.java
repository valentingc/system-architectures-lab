package at.fhv.dluvgo.smarthome.actuators.cli;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;
import at.fhv.dluvgo.smarthome.actuators.fridge.OrderProcessorActor;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ConsumeProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.OrderProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ProductOrderedSuccessfullyMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ProductOrderedUnsuccessfullyMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.RequestOrderHistoryMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.RequestStoredProductsMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ResponseOrderHistoryMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ResponseStoredProductsMessage;
import at.fhv.dluvgo.smarthome.actuators.mediastation.message.MediaPlaybackRequestMessage;
import at.fhv.dluvgo.smarthome.actuators.mediastation.message.StopMediaPlaybackRequestMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInputActor extends AbstractBehavior<Message> {
    private enum MenuState {
        MAIN_MENU,
        MOVIE_SUBMENU,
        FRIDGE_SUBMENU,
        FRIDGE_CONSUME_SUBMENU,
        FRIDGE_ORDER_SUBMENU;
    }

    private final ActorRef<Message> fridge;
    private final ActorRef<Message> mediaStation;

    public static Behavior<Message> create(
        ActorRef<Message> fridge,
        ActorRef<Message> mediaStation
    ) {
        return Behaviors.setup(ctx -> new UserInputActor(ctx, fridge, mediaStation));
    }

    private UserInputActor(
        ActorContext<Message> context,
        ActorRef<Message> fridge,
        ActorRef<Message> mediaStation
    ) {
        super(context);
        this.fridge = fridge;
        this.mediaStation = mediaStation;

        listenForCliInput();
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(ResponseStoredProductsMessage.class, this::onStoredProductsResponse)
            .onMessage(ProductOrderedSuccessfullyMessage.class, this::onProductOrderedSuccessfully)
            .onMessage(ProductOrderedUnsuccessfullyMessage.class, this::onProductOrderedUnsuccessfully)
            .onMessage(ResponseOrderHistoryMessage.class, this::onOrderHistory)
            .build();
    }

    private Behavior<Message> onStoredProductsResponse(ResponseStoredProductsMessage msg) {
        getContext().getLog().info("Received response from getStoredProducts (fridge)");

        for (FridgeActor.Product p : msg.getProducts()) {
            getContext().getLog().info("Fridge contains: {}", p.name);
        }

        return Behaviors.same();
    }

    private Behavior<Message> onProductOrderedSuccessfully(ProductOrderedSuccessfullyMessage msg) {
        getContext().getLog().info("Successfully ordered product [{}]", msg.getProduct().name);
        return Behaviors.same();
    }

    private Behavior<Message> onProductOrderedUnsuccessfully(ProductOrderedUnsuccessfullyMessage msg) {
        getContext().getLog().info(
            "Could not order product [{}] because [{}]",
            msg.getProduct().name,
            msg.getReason()
        );
        return Behaviors.same();
    }

    private Behavior<Message> onOrderHistory(ResponseOrderHistoryMessage msg) {
        int i = 0;
        if (msg.getOrderHistory().isEmpty()) {
            getContext().getLog().info("Order history is empty");
            return Behaviors.same();
        }
        for (OrderProcessorActor.OrderReceipt receipt : msg.getOrderHistory()) {
            getContext().getLog().info("Order history #" + i++ + ": [" + receipt + "]");
        }
        return Behaviors.same();
    }

    private void listenForCliInput() {
        new Thread(() -> {
            try {
                handleCliInput();
            } catch (IOException e) {
                getContext().getLog().error("An error occurred while reading the CLI input", e);
            }
        }).start();
    }

    private void handleCliInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        MenuState state = MenuState.MAIN_MENU;

        while (true) {
            while (state.equals(MenuState.MAIN_MENU)) {
                System.out.println("Welcome to the SmartHomeSystem by Valentin & Dominic");
                System.out.println("1) Control media station");
                System.out.println("2) Control fridge");
                System.out.println("0) Quit the system");

                String input = reader.readLine();
                switch (input) {
                    case "1":
                    case "media":
                        state = MenuState.MOVIE_SUBMENU;
                        break;
                    case "2":
                    case "fridge":
                        state = MenuState.FRIDGE_SUBMENU;
                        break;
                    case "0":
                    case "quit":
                        System.exit(0);
                    default:
                        System.err.println("Unknown command");
                        break;
                }
            }

            while (state.equals(MenuState.MOVIE_SUBMENU)) {
                System.out.println("Please choose an option..");
                System.out.println("1) Start a movie playback");
                System.out.println("2) Stop the current movie playback");
                System.out.println("0) Back to main menu");

                String input = reader.readLine();
                switch (input) {
                    case "1":
                    case "start":
                        System.out.println("Please enter the movie title you would like to watch");
                        mediaStation.tell(new MediaPlaybackRequestMessage(reader.readLine()));
                        break;
                    case "2":
                    case "stop":
                        System.out.println("Trying to stop media playback");
                        mediaStation.tell(new StopMediaPlaybackRequestMessage());
                        break;
                    case "0":
                    case "back":
                        state = MenuState.MAIN_MENU;
                        break;
                    default:
                        System.err.println("Unknown command");
                        break;
                }
            }

            while (state.equals(MenuState.FRIDGE_SUBMENU)) {
                System.out.println("Please choose an option..");
                System.out.println("1) Get list of currently stored items");
                System.out.println("2) Consume a product");
                System.out.println("3) Order a product");
                System.out.println("4) Request order history");
                System.out.println("0) Back to main menu");

                String input = reader.readLine();
                switch (input) {
                    case "1":
                    case "stored":
                        fridge.tell(new RequestStoredProductsMessage(getContext().getSelf()));
                        break;
                    case "2":
                    case "consume":
                        state = MenuState.FRIDGE_CONSUME_SUBMENU;
                        break;
                    case "3":
                    case "order":
                        state = MenuState.FRIDGE_ORDER_SUBMENU;
                        break;
                    case "4":
                    case "history":
                        fridge.tell(new RequestOrderHistoryMessage(getContext().getSelf()));
                        break;
                    case "0":
                    case "back":
                        state = MenuState.MAIN_MENU;
                        break;
                    default:
                        System.err.println("Unknown command");
                        break;
                }
            }

            while (state.equals(MenuState.FRIDGE_CONSUME_SUBMENU)
                || state.equals(MenuState.FRIDGE_ORDER_SUBMENU)
            ) {
                if (state.equals(MenuState.FRIDGE_CONSUME_SUBMENU)) {
                    System.out.println("Please choose one product to consume..");
                } else {
                    System.out.println("Please choose one product to order..");
                }
                System.out.println("1) Milk (1kg, 1,50€)");
                System.out.println("2) Butter (0.25kg, 0.99€)");
                System.out.println("3) Yoghurt (0.20kg, 0.79€)");
                System.out.println("4) White Wine (0.71kg, 4.99€)");
                System.out.println("5) Milchschnitte (0.30kg, 1.99€)");
                System.out.println("0) Back to fridge menu");

                FridgeActor.Product product;
                switch (reader.readLine()) {
                    case "1":
                        product = FridgeActor.Product.MILK;
                        break;
                    case "2":
                        product = FridgeActor.Product.BUTTER;
                        break;
                    case "3":
                        product = FridgeActor.Product.YOGHURT;
                        break;
                    case "4":
                        product = FridgeActor.Product.WHITE_WHINE;
                        break;
                    case "5":
                        product = FridgeActor.Product.MILCH_SCHNITTE;
                        break;
                    case "0":
                        state = MenuState.FRIDGE_SUBMENU;
                        continue;
                    default:
                        System.err.println("Unknown product");
                        continue;
                }

                if (state.equals(MenuState.FRIDGE_CONSUME_SUBMENU)) {
                    fridge.tell(new ConsumeProductMessage(product, getContext().getSelf()));
                } else {
                    fridge.tell(new OrderProductMessage(
                        product,
                        getContext().getSelf(),
                        getContext().getSelf(),
                        null
                    ));
                }
            }
        }
    }
}
