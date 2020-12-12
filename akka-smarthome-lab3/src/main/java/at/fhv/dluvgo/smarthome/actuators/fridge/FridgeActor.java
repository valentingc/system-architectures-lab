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
import at.fhv.dluvgo.smarthome.actuators.fridge.message.RequestStoredProductsMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ResponseStoredProductsMessage;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class FridgeActor {
    private static final float MAX_WEIGHT = 20.00f; // Weight measured in kg
    private static final int MAX_ITEMS = 5;

    public static Behavior<Message> create() {
        return DefaultFridgeBehavior.create(new LinkedList<>());
    }

    private static List<Product> copyFridgeProducts(List<Product> products) {
        List<Product> productsCopy = new LinkedList<>();
        for (Product p : products) {
            productsCopy.add(new Product(p.name, p.weight, p.price));
        }
        return productsCopy;
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

    public static final class FullFridgeBehavior extends AbstractBehavior<Message> {
        private final List<Product> products;

        public FullFridgeBehavior(ActorContext<Message> context, List<Product> products) {
            super(context);
            this.products = new LinkedList<>(products);
            getContext().getLog().info("Switching Fridge Behavior: Full");
        }

        public static Behavior<Message> create(List<Product> products) {
            return Behaviors.setup(ctx -> new FullFridgeBehavior(ctx, products));
        }

        @Override
        public Receive<Message> createReceive() {
            return newReceiveBuilder()
                .onMessage(RequestStoredProductsMessage.class, this::getStoredProducts)
                .build();
        }

        private Behavior<Message> getStoredProducts(RequestStoredProductsMessage msg) {
            List<Product> productsCopy = copyFridgeProducts(this.products);
            msg.replyTo.tell(new ResponseStoredProductsMessage(productsCopy));
            return Behaviors.same();
        }
    }

    public static final class DefaultFridgeBehavior extends AbstractBehavior<Message> {
        private final List<Product> products;
        private final ActorRef<Message> orderProcessor;

        private DefaultFridgeBehavior(ActorContext<Message> context, List<Product> products) {
            super(context);
            this.products = new LinkedList<>(products);

             orderProcessor = getContext().spawn(
                OrderProcessorActor.create(getContext().getSelf(), MAX_WEIGHT, MAX_ITEMS),
                "order-processor"
            );
            getContext().getLog().info("Switching Fridge Behavior: Default");
        }

        public static Behavior<Message> create(List<Product> products) {
            return Behaviors.setup(ctx -> new DefaultFridgeBehavior(ctx, products));
        }

        @Override
        public Receive<Message> createReceive() {
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

        private Behavior<Message> onGetStoredProducts(RequestStoredProductsMessage msg) {
            List<Product> productsCopy = copyFridgeProducts(this.products);
            msg.replyTo.tell(new ResponseStoredProductsMessage(productsCopy));
            return Behaviors.same();
        }

        private Behavior<Message> onProductOrderedSuccessfully(
            ProductOrderedSuccessfullyMessage msg
        ) {
            this.products.add(msg.getProduct());
            getContext().getLog().info("Product was ordered and restocked: {}, New Fridge Amount: "
                    + "{}/{}",
                msg.getProduct().name,
                this.products.size(),
                MAX_ITEMS
                );
            return Behaviors.same();
        }

        private Behavior<Message> onOrderProduct(OrderProductMessage msg) {
            Product product = msg.getProductToOrder();

            List<Product> productsCopy = copyFridgeProducts(this.products);

            orderProcessor.tell(
                new OrderProductMessage(product, getContext().getSelf(), productsCopy)
            );
            return Behaviors.same();

        }

        private Behavior<Message> onConsumeProduct(ConsumeProductMessage msg) {
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
