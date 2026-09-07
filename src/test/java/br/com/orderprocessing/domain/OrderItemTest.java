package br.com.orderprocessing.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemTest {

    public static void main(String[] args) {
        shouldCalculateSubtotalCorrectly();
        shouldRejectZeroQuantity();
        shouldRejectNegativeQuantity();
        shouldRejectNullProduct();

        System.out.println("All OrderItem tests passed.");
    }

    private static void shouldCalculateSubtotalCorrectly() {
        Product product = productWithPrice("19.90");

        OrderItem orderItem = new OrderItem(product, 3);

        assertBigDecimalEquals(new BigDecimal("59.70"), orderItem.getSubtotal());
    }

    private static void shouldRejectZeroQuantity() {
        Product product = productWithPrice("19.90");

        assertThrows(IllegalArgumentException.class, () -> new OrderItem(product, 0));
    }

    private static void shouldRejectNegativeQuantity() {
        Product product = productWithPrice("19.90");

        assertThrows(IllegalArgumentException.class, () -> new OrderItem(product, -1));
    }

    private static void shouldRejectNullProduct() {
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(null, 1));
    }

    private static Product productWithPrice(String price) {
        return new Product(
            UUID.randomUUID(),
            "Notebook",
            new BigDecimal(price),
            10
        );
    }

    private static void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
        if (expected.compareTo(actual) != 0) {
            throw new AssertionError("Expected " + expected + ", but was " + actual);
        }
    }

    private static void assertThrows(
        Class<? extends Throwable> expectedException,
        Runnable action
    ) {
        try {
            action.run();
        } catch (Throwable actualException) {
            if (expectedException.isInstance(actualException)) {
                return;
            }

            throw new AssertionError(
                "Expected " + expectedException.getSimpleName()
                    + ", but was " + actualException.getClass().getSimpleName(),
                actualException
            );
        }

        throw new AssertionError(
            "Expected " + expectedException.getSimpleName() + " to be thrown"
        );
    }
}
