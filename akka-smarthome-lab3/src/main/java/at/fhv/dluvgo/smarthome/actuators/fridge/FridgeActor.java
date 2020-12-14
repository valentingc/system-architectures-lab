package at.fhv.dluvgo.smarthome.actuators.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ConsumeProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.OrderProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ProductOrderedSuccessfullyMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ProductOrderedUnsuccessfullyMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.RequestOrderHistoryMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.RequestStoredProductsMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ResponseOrderHistoryMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ResponseStoredProductsMessage;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class FridgeActor {
    public static final float MAX_WEIGHT = 10.00f; // Weight measured in kg
    public static final int MAX_ITEMS = 10;

    public enum Product {
        MILK("Milk", 1, 1.50f),
        BUTTER("Butter", 0.25f, 0.99f),
        YOGHURT("Yoghurt", 0.2f, 0.79f),
        WHITE_WHINE("White Wine", 0.71f, 4.99f),
        MILCH_SCHNITTE("Milchschnitte", 0.3f, 1.99f);

        public final String name;
        public final float weight;
        public final float price;

        Product(String name, float weight, float price) {
            this.name = name;
            this.weight = weight;
            this.price = price;
        }
    }

    public static Behavior<Message> create() {
        return DefaultFridgeBehavior.create(new LinkedList<>(), new LinkedList<>());
    }

    /**
     * The {@link #DefaultFridgeBehavior(ActorContext, List, List)} is responsible for when the
     * fridge is in a default state (not full). It allows for all functionalities to be used.
     */
    public static final class DefaultFridgeBehavior extends AbstractBehavior<Message> {
        private final List<Product> products;
        private final ActorRef<Message> orderProcessor;
        private final List<OrderProcessorActor.OrderReceipt> historicalOrders;

        public static Behavior<Message> create(
            List<Product> products,
            List<OrderProcessorActor.OrderReceipt> historicalOrders
        ) {
            return Behaviors.setup(ctx -> new DefaultFridgeBehavior(
                ctx,
                products,
                historicalOrders
            ));
        }

        public static Behavior<Message> switchFromFullToDefault(
            List<Product> products,
            ConsumeProductMessage msg,
            List<OrderProcessorActor.OrderReceipt> historicalOrders
        ) {
            return Behaviors.setup(ctx ->
                new DefaultFridgeBehavior(ctx, products, historicalOrders).onConsumeProduct(msg)
            );
        }

        private DefaultFridgeBehavior(
            ActorContext<Message> context,
            List<Product> products,
            List<OrderProcessorActor.OrderReceipt> historicalOrders
        ) {
            super(context);
            this.products = new LinkedList<>(products);
            this.historicalOrders = historicalOrders;

            orderProcessor = getContext().spawn(
                OrderProcessorActor.create(getContext().getSelf()),
                "order-processor-" + new Random().nextInt() // :shrug: :see_no_evil:
            );

            getContext().getLog().info("Fridge behaviour now is: Default");
        }

        @Override
        public Receive<Message> createReceive() {
            return newReceiveBuilder()
                .onMessage(ConsumeProductMessage.class, this::onConsumeProduct)
                .onMessage(OrderProductMessage.class, this::onOrderProduct)
                .onMessage(
                    ProductOrderedSuccessfullyMessage.class,
                    this::onProductOrderedSuccessfully
                )
                .onMessage(
                    ProductOrderedUnsuccessfullyMessage.class,
                    this::onProductOrderedUnsuccessfully
                )
                .onMessage(RequestOrderHistoryMessage.class, this::onOrderHistoryRequest)
                .onMessage(RequestStoredProductsMessage.class, this::onStoredProductsRequest)
                .build();
        }

        private Behavior<Message> onStoredProductsRequest(RequestStoredProductsMessage msg) {
            List<Product> productsCopy = copyFridgeProducts(this.products);
            msg.replyTo.tell(new ResponseStoredProductsMessage(productsCopy));
            return this;
        }

        private Behavior<Message> onProductOrderedSuccessfully(
            ProductOrderedSuccessfullyMessage msg
        ) {
            this.products.add(msg.getProduct());
            this.historicalOrders.add(msg.getReceipt());
            getContext().getLog().info(
                "Product was ordered and restocked: {}, New Fridge Amount: {}/{}",
                msg.getProduct().name,
                this.products.size(),
                MAX_ITEMS
            );

            return this;
        }

        private Behavior<Message> onProductOrderedUnsuccessfully(
            ProductOrderedUnsuccessfullyMessage msg
        ) {
            getContext().getLog().info(
                "Could not order product: {}, Reason: {}",
                msg.getProduct().name,
                msg.getReason()
            );
            return FridgeActor.FullFridgeBehavior.create(products, historicalOrders);
        }

        private Behavior<Message> onOrderHistoryRequest(RequestOrderHistoryMessage msg) {
            getContext().getLog().info("Got a request for order history");

            List<OrderProcessorActor.OrderReceipt> orderHistory = new LinkedList<>(
                this.historicalOrders
            );
            msg.getReplyTo().tell(new ResponseOrderHistoryMessage(orderHistory));

            return this;
        }

        private Behavior<Message> onOrderProduct(OrderProductMessage msg) {
            Product product = msg.getProductToOrder();
            List<Product> productsCopy = copyFridgeProducts(this.products);

            orderProcessor.tell(new OrderProductMessage(
                product,
                msg.getOriginalSender(),
                getContext().getSelf(),
                productsCopy
            ));

            return this;
        }

        private Behavior<Message> onConsumeProduct(ConsumeProductMessage msg) {
            Product product = msg.getProduct();

            if (!products.contains(msg.getProduct())) {
                getContext().getLog().info(
                    "{} is not in fridge, are you kidding me?",
                    msg.getProduct().name
                );
                return this;
            }
            getContext().getLog().info("Consumed product from fridge: {}", msg.getProduct().name);

            products.remove(product);

            // Re-order product if it's now empty
            if (FridgeActor.isProductRunningOut(products, product)) {
                getContext().getLog().info(
                    "Product {} is running out. Need to re-order",
                    product.name
                );

                // We let the original sender of the consume message know that we have re-ordered
                // the product. Being as transparent as possible.. :)
                return this.onOrderProduct(new OrderProductMessage(
                    product,
                    msg.getReplyTo(),
                    getContext().getSelf(),
                    this.products
                ));
            }

            return this;
        }
    }

    /**
     * The {@link #FullFridgeBehavior(ActorContext, List, List)} is responsible for when the fridge
     * is full (either because of weight or item count limits). The behaviour prohibits ordering new
     * products, while still allowing the other functionalities to be used.
     */
    public static final class FullFridgeBehavior extends AbstractBehavior<Message> {
        private final List<Product> products;
        private final List<OrderProcessorActor.OrderReceipt> historicalOrders;

        public static Behavior<Message> create(
            List<Product> products,
            List<OrderProcessorActor.OrderReceipt> historicalOrders
        ) {
            return Behaviors.setup(ctx -> new FullFridgeBehavior(ctx, products, historicalOrders));
        }

        private FullFridgeBehavior(
            ActorContext<Message> context,
            List<Product> products,
            List<OrderProcessorActor.OrderReceipt> historicalOrders
        ) {
            super(context);
            this.products = new LinkedList<>(products);
            this.historicalOrders = historicalOrders;
            getContext().getLog().info("Fridge behaviour now is: Full");
        }

        @Override
        public Receive<Message> createReceive() {
            return newReceiveBuilder()
                .onMessage(ConsumeProductMessage.class, this::onConsumeProduct)
                .onMessage(OrderProductMessage.class, this::onOrderProduct)
                .onMessage(RequestOrderHistoryMessage.class, this::onOrderHistoryRequest)
                .onMessage(RequestStoredProductsMessage.class, this::onStoredProductsRequest)
                .build();
        }

        private Behavior<Message> onConsumeProduct(ConsumeProductMessage msg) {
            if (!products.contains(msg.getProduct())) {
                getContext().getLog().info(
                    "{} is not in the fridge, are you kidding me?",
                    msg.getProduct().name
                );
                return this;
            }
            return DefaultFridgeBehavior.switchFromFullToDefault(products, msg, historicalOrders);
        }

        private Behavior<Message> onOrderProduct(OrderProductMessage msg) {
            getContext().getLog().info(
                "You should probably eat something first, your fridge is full! ;-)"
            );
            return this;
        }

        private Behavior<Message> onOrderHistoryRequest(RequestOrderHistoryMessage msg) {
            getContext().getLog().debug("Got a request for order history");

            List<OrderProcessorActor.OrderReceipt> orderHistory = new LinkedList<>(
                this.historicalOrders
            );
            msg.getReplyTo().tell(new ResponseOrderHistoryMessage(orderHistory));

            return this;
        }

        private Behavior<Message> onStoredProductsRequest(RequestStoredProductsMessage msg) {
            List<Product> productsCopy = copyFridgeProducts(this.products);
            msg.replyTo.tell(new ResponseStoredProductsMessage(productsCopy));
            return this;
        }
    }

    /**
     * "Copies" the currently stored products into a new {@link LinkedList}. It's technically
     * not a real copy, because {@link Product} is an {@code enum} and therefore has no real
     * instances
     *
     * @param products The products to copy
     *
     * @return New LinkedList containing the products to copy
     */
    private static List<Product> copyFridgeProducts(List<Product> products) {
        return new LinkedList<>(products);
    }

    /**
     * Determines whether a product is running out (meaning there are 0 left).
     *
     * @param products The current list of stored products
     * @param product  The product that might have ran out
     *
     * @return True if product has ran out, false otherwise
     */
    private static boolean isProductRunningOut(List<Product> products, Product product) {
        int amountLeft = 0;
        for (Product p : products) {
            if (p.name.equals(product.name)) {
                amountLeft += 1;
            }
        }
        return amountLeft < 1;
    }
}
