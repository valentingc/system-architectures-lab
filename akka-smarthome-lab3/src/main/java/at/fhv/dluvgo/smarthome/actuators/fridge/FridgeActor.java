package at.fhv.dluvgo.smarthome.actuators.fridge;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.AddProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ConsumeProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.FridgeMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.RequestStoredProductsMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ResponseStoredProductsMessage;
import java.util.LinkedList;
import java.util.List;

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
                .onMessage(AddProductMessage.class, this::onAddProduct)
                .onMessage(RequestStoredProductsMessage.class, this::onGetStoredProducts)
                .onMessage(ConsumeProductMessage.class, this::onConsumeProduct)
                .build();
        }

        private Behavior<FridgeMessage> onGetStoredProducts(RequestStoredProductsMessage msg) {
            return copyFridgeProducts(msg, this.products);
        }
        private Behavior<FridgeMessage> onConsumeProduct(ConsumeProductMessage msg) {
            Product product = msg.getProduct();
            // TODO: remove product from products
            // check if empty, if empty -> create order if possible
            float amountLeft = 0;
            for (Product p : products) {
                if (p.name.equals(product.name)) {
                    amountLeft += 1;
                }
            }
            if (amountLeft < 1.0f) {
                getContext().getLog().info(
                    "Product {} is running out. Need to re-order",
                    product.name
                );
            }
            return Behaviors.same();
        }

        private Behavior<FridgeMessage> onAddProduct(AddProductMessage msg) {
            Product product = msg.getProduct();

            if ((products.size() + 1) > MAX_ITEMS) {
                // TODO - max items reached
                getContext().getLog().info("Fridge is now full (max_items)");
                return FullFridgeBehavior.create(products);
            } else if ((calculateTotalWeight() + product.weight) > MAX_WEIGHT) {
                // TODO - max weight reached
                getContext().getLog().info("Fridge is now full (max_weight)");
                return FullFridgeBehavior.create(products);
            }

            products.add(product);
            return Behaviors.same();
        }

        private float calculateTotalWeight() {
            float weight = 0.0f;
            for (Product product : products) {
                weight += product.weight;
            }

            return weight;
        }
    }
}
