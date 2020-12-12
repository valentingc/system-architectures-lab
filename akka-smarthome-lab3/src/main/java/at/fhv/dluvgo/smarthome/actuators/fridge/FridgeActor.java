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
import at.fhv.dluvgo.smarthome.actuators.fridge.sensor.FridgeItemCountSensor;
import at.fhv.dluvgo.smarthome.actuators.fridge.sensor.ItemCountChangedMessage;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class FridgeActor {
    private static final float MAX_WEIGHT = 10.00f; // Weight measured in kg
    private static final int MAX_ITEMS = 10;

    public static Behavior<Message> create() {
        return DefaultFridgeBehavior.create(new LinkedList<>(), new LinkedList<>());
    }

    private static List<Product> copyFridgeProducts(List<Product> products) {
        return new LinkedList<>(products);
    }

    private static boolean isProductEmpty(List<Product> products, Product product) {
        float amountLeft = 0;
        for (Product p : products) {
            if (p.name.equals(product.name)) {
                amountLeft += 1;
            }
        }
        return amountLeft < 1.0f;

    }

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

    public static final class FullFridgeBehavior extends AbstractBehavior<Message> {
        private final List<Product> products;
        private List<OrderProcessorActor.OrderReceipt> historicalOrders;

        public FullFridgeBehavior(
            ActorContext<Message> context,
            List<Product> products,
            List<OrderProcessorActor.OrderReceipt> historicalOrders
        ) {
            super(context);
            this.products = new LinkedList<>(products);
            this.historicalOrders = historicalOrders;
            getContext().getLog().info("Switching Fridge Behavior: Full");
        }

        public static Behavior<Message> create(
            List<Product> products,
            List<OrderProcessorActor.OrderReceipt> historicalOrders
        ) {
            return Behaviors.setup(
                ctx -> new FullFridgeBehavior(ctx, products, historicalOrders));
        }

        @Override
        public Receive<Message> createReceive() {
            return newReceiveBuilder()
                .onMessage(RequestStoredProductsMessage.class, this::getStoredProducts)
                .onMessage(ConsumeProductMessage.class, this::onConsumeProduct)
                .onMessage(RequestOrderHistoryMessage.class, this::onRequestOrderHistory)
                .onMessage(OrderProductMessage.class, this::onOrderProduct)
                .build();
        }

        private Behavior<Message> getStoredProducts(RequestStoredProductsMessage msg) {
            List<Product> productsCopy = copyFridgeProducts(this.products);
            msg.replyTo.tell(new ResponseStoredProductsMessage(productsCopy));
            return this;
        }

        private Behavior<Message> onConsumeProduct(ConsumeProductMessage msg) {
            if (!products.contains(msg.getProduct())) {
                getContext().getLog().info("Product is not in fridge, are you kidding me?: {}",
                    msg.getProduct().name);
                return this;
            }
            return DefaultFridgeBehavior.switchFromFullConsumed(products, msg, historicalOrders);
        }

        private Behavior<Message> onRequestOrderHistory(RequestOrderHistoryMessage msg) {
            getContext().getLog().info("Got a request for order history");

            List<OrderProcessorActor.OrderReceipt> orderHistory =
                new LinkedList<>(this.historicalOrders);
            msg.getReplyTo().tell(new ResponseOrderHistoryMessage(orderHistory));

            return this;
        }

        private Behavior<Message> onOrderProduct(OrderProductMessage msg) {
            getContext().getLog()
                .info("You should probably eat something first, your fridge is full! ;-)");
            return this;
        }

    }

    public static final class DefaultFridgeBehavior extends AbstractBehavior<Message> {
        private final List<Product> products;
        private final ActorRef<Message> orderProcessor;
        private final List<OrderProcessorActor.OrderReceipt> historicalOrders;

        private DefaultFridgeBehavior(
            ActorContext<Message> context,
            List<Product> products,
            List<OrderProcessorActor.OrderReceipt> historicalOrders
        ) {
            super(context);
            this.products = new LinkedList<>(products);
            this.historicalOrders = historicalOrders;

            orderProcessor = getContext().spawn(
                OrderProcessorActor.create(getContext().getSelf(), MAX_WEIGHT, MAX_ITEMS),
                "order-processor-" + new Random().nextInt() // :shrug: :see_no_evil: :exploding_head:
            );

            getContext().getLog().info("Switching Fridge Behavior: Default");
        }

        public static Behavior<Message> switchFromFullConsumed(
            List<Product> products,
            ConsumeProductMessage msg,
            List<OrderProcessorActor.OrderReceipt> historicalOrders
        ) {
            return Behaviors
                .setup(ctx -> new DefaultFridgeBehavior(ctx, products, historicalOrders).onConsumeProduct(msg));
        }


        public static Behavior<Message> create(
            List<Product> products,
            List<OrderProcessorActor.OrderReceipt> historicalOrders
        ) {
            return Behaviors
                .setup(ctx -> new DefaultFridgeBehavior(ctx, products, historicalOrders));
        }

        @Override
        public Receive<Message> createReceive() {
            return newReceiveBuilder()
                .onMessage(RequestStoredProductsMessage.class, this::onGetStoredProducts)
                .onMessage(ConsumeProductMessage.class, this::onConsumeProduct)
                .onMessage(OrderProductMessage.class, this::onOrderProduct)
                .onMessage(ProductOrderedUnsuccessfullyMessage.class,
                    this::onProductOrderedUnsuccessfully)
                .onMessage(
                    ProductOrderedSuccessfullyMessage.class,
                    this::onProductOrderedSuccessfully
                )
                .onMessage(RequestOrderHistoryMessage.class, this::onRequestOrderHistory)
                .build();
        }

        private Behavior<Message> onGetStoredProducts(RequestStoredProductsMessage msg) {
            List<Product> productsCopy = copyFridgeProducts(this.products);
            msg.replyTo.tell(new ResponseStoredProductsMessage(productsCopy));
            return this;
        }

        private Behavior<Message> onProductOrderedUnsuccessfully(
            ProductOrderedUnsuccessfullyMessage msg) {
            getContext().getLog().info(
                "Could not order product: {}, Reason: {}",
                msg.getProduct().name,
                msg.getReason()
            );
            return FridgeActor.FullFridgeBehavior
                .create(products, historicalOrders);
        }

        private Behavior<Message> onProductOrderedSuccessfully(
            ProductOrderedSuccessfullyMessage msg
        ) {
            this.products.add(msg.getProduct());
            this.historicalOrders.add(msg.getReceipt());
            getContext().getLog().info("Product was ordered and restocked: {}, New Fridge Amount: "
                    + "{}/{}",
                msg.getProduct().name,
                this.products.size(),
                MAX_ITEMS
            );

            return this;
        }

        private Behavior<Message> onRequestOrderHistory(RequestOrderHistoryMessage msg) {
            getContext().getLog().info("Got a request for order history");

            List<OrderProcessorActor.OrderReceipt> orderHistory =
                new LinkedList<>(this.historicalOrders);
            msg.getReplyTo().tell(new ResponseOrderHistoryMessage(orderHistory));

            return this;
        }

        private Behavior<Message> onOrderProduct(OrderProductMessage msg) {
            Product product = msg.getProductToOrder();

            List<Product> productsCopy = copyFridgeProducts(this.products);

            orderProcessor.tell(
                new OrderProductMessage(product, msg.getOriginalSender(), getContext().getSelf(),
                    productsCopy)
            );
            return this;
        }

        private Behavior<Message> onConsumeProduct(ConsumeProductMessage msg) {
            Product product = msg.getProduct();

            if (!products.contains(msg.getProduct())) {
                getContext().getLog().info("Product is not in fridge, are you kidding me?: {}",
                    msg.getProduct().name);
                return this;
            }
            getContext().getLog().info("Consumed product from fridge: {}", msg.getProduct().name);

            products.remove(product);

            // re-order if now empty
            if (FridgeActor.isProductEmpty(products, product)) {
                getContext().getLog().info(
                    "Product {} is running out. Need to re-order",
                    product.name
                );

                // we let the original sender of the consume message know that we have re-ordered the product
                // so he knows what we're doing :-)
                return this.onOrderProduct(
                    new OrderProductMessage(
                        product,
                        msg.getReplyTo(),
                        getContext().getSelf(),
                        this.products
                    )
                );
            }

            return this;
        }

    }
}
