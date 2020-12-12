package at.fhv.dluvgo.smarthome.actuators.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ConsumeProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.FridgeMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.OrderProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ProductOrderedSuccessfullyMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.RequestStoredProductsMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ResponseStoredProductsMessage;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class FridgeActor {
    private static final float MAX_WEIGHT = 20.00f; // Weight measured in kg
    private static final int MAX_ITEMS = 20;

    public static Behavior<FridgeMessage> create() {
        return DefaultFridgeBehavior.create(new LinkedList<>());
    }

    private static Behavior<FridgeMessage> copyFridgeProducts(
        RequestStoredProductsMessage msg,
        List<Product> products
    ) {
        List<Product> productsCopy = new LinkedList<>();
        for (Product p : products) {
            productsCopy.add(new Product(p.name, p.weight, p.price));
        }
        msg.replyTo.tell(new ResponseStoredProductsMessage(productsCopy));
        return Behaviors.same();
    }

    public static final class Product {
        public final String name;
        public final float weight;
        public final float price;

        public Product(String name, float weight, float price) {
            this.name = name;
            this.weight = weight;
            this.price = price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Product product = (Product) o;
            return name.toLowerCase().equals(product.name.toLowerCase());
        }

        @Override
        public int hashCode() {
            return Objects.hash(name.toLowerCase());
        }
    }

    public static final class FullFridgeBehavior extends AbstractBehavior<FridgeMessage> {
        // TODO: change this to a map, because one product can be added multiple times
        // so we need a mapping between product <-> amount
        private List<Product> products;

        public FullFridgeBehavior(ActorContext<FridgeMessage> context, List<Product> products) {
            super(context);
            this.products = new LinkedList<>(products);
            getContext().getLog().info("Switching Fridge Behavior: Full");
        }

        public static Behavior<FridgeMessage> create(List<Product> products) {
            return Behaviors.setup(ctx -> new FullFridgeBehavior(ctx, products));
        }

        @Override
        public Receive<FridgeMessage> createReceive() {
            return newReceiveBuilder()
                .onMessage(RequestStoredProductsMessage.class, this::getStoredProducts)
                .build();
        }

        private Behavior<FridgeMessage> getStoredProducts(RequestStoredProductsMessage msg) {
            return copyFridgeProducts(msg, this.products);
        }
    }

    public static final class DefaultFridgeBehavior extends AbstractBehavior<FridgeMessage> {
        private List<Product> products;

        private DefaultFridgeBehavior(ActorContext<FridgeMessage> context, List<Product> products) {
            super(context);
            this.products = new LinkedList<>(products);
            getContext().getLog().info("Switching Fridge Behavior: Default");
        }

        public static Behavior<FridgeMessage> create(List<Product> products) {
            return Behaviors.setup(ctx -> new DefaultFridgeBehavior(ctx, products));
        }

        @Override
        public Receive<FridgeMessage> createReceive() {
            return newReceiveBuilder()
                .onMessage(RequestStoredProductsMessage.class, this::onGetStoredProducts)
                .onMessage(ConsumeProductMessage.class, this::onConsumeProduct)
                .onMessage(OrderProductMessage.class, this::onOrderProduct)
                .onMessage(
                    ProductOrderedSuccessfullyMessage.class,
                    this::onProductOrderedSuccessfully
                )
                .build();
        }

        private Behavior<FridgeMessage> onGetStoredProducts(RequestStoredProductsMessage msg) {
            return copyFridgeProducts(msg, this.products);
        }

        private Behavior<FridgeMessage> onProductOrderedSuccessfully(
            ProductOrderedSuccessfullyMessage msg
        ) {
            getContext().getLog().info("Product was ordered and restocked: {}",
                msg.getProduct().name);
            products.add(msg.getProduct());
            return Behaviors.same();
        }

        private Behavior<FridgeMessage> onOrderProduct(OrderProductMessage msg) {
            Product product = msg.getProduct();


            // TODO: immutable products!
            ActorRef<FridgeMessage> orderProcessor = getContext().spawn(
                OrderProcessorActor.create(getContext().getSelf(), products, MAX_WEIGHT, MAX_ITEMS),
                "order-processor"
            );
            orderProcessor.tell(new OrderProductMessage(msg.getProduct(), getContext().getSelf()));
            return Behaviors.same();

        }

        private Behavior<FridgeMessage> onConsumeProduct(ConsumeProductMessage msg) {
            Product product = msg.getProduct();

            // calculate how many products of this type are left
            float amountLeft = 0;
            for (Product p : products) {
                if (p.name.equals(product.name)) {
                    amountLeft += 1;
                }
            }
            products.remove(product);

            // re-order if now empty
            if (amountLeft < 1.0f) {
                getContext().getLog().info(
                    "Product {} is running out. Need to re-order",
                    product.name
                );
            }
            return Behaviors.same();
        }

    }
}
