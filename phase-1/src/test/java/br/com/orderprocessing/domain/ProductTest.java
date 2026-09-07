package br.com.orderprocessing;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.orderprocessing.domain.Product;
import br.com.orderprocessing.exception.InsufficientStockException;

public class ProductTest {

    public static void main(String[] args) {
        shouldIncreaseStockWhenQuantityIsValid();
        shouldRejectZeroOrNegativeQuantityWhenIncreasingStock();
        shouldDecreaseStockWhenQuantityIsAvailable();
        shouldAllowStockToReachZero();
        shouldRejectZeroOrNegativeQuantity();
        shouldRejectQuantityGreaterThanAvailableStock();

        System.out.println("All Product tests passed.");
    }

    private static void shouldIncreaseStockWhenQuantityIsValid() {
        Product product = productWithStock(10);

        product.increaseStock(5);

        assertEquals(15, product.getStock());
    }

    private static void shouldRejectZeroOrNegativeQuantityWhenIncreasingStock() {
        Product product = productWithStock(10);

        assertThrows(IllegalArgumentException.class, () -> product.increaseStock(0));
        assertThrows(IllegalArgumentException.class, () -> product.increaseStock(-1));
    }

    private static void shouldDecreaseStockWhenQuantityIsAvailable() {
        Product product = productWithStock(10);

        product.decreaseStock(3);

        assertEquals(7, product.getStock());
    }

    private static void shouldAllowStockToReachZero() {
        Product product = productWithStock(1);

        product.decreaseStock(1);

        assertEquals(0, product.getStock());
    }

    private static void shouldRejectZeroOrNegativeQuantity() {
        Product product = productWithStock(10);

        assertThrows(IllegalArgumentException.class, () -> product.decreaseStock(0));
        assertThrows(IllegalArgumentException.class, () -> product.decreaseStock(-1));
    }

    private static void shouldRejectQuantityGreaterThanAvailableStock() {
        Product product = productWithStock(10);

        assertThrows(InsufficientStockException.class, () -> product.decreaseStock(11));
    }

    private static Product productWithStock(int stock) {
        return new Product(
                UUID.randomUUID(),
                "Notebook",
                new BigDecimal("2999.90"),
                stock);
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + ", but was " + actual);
        }
    }

    private static void assertThrows(
            Class<? extends Throwable> expectedException,
            Runnable action) {
        try {
            action.run();
        } catch (Throwable actualException) {
            if (expectedException.isInstance(actualException)) {
                return;
            }

            throw new AssertionError(
                    "Expected " + expectedException.getSimpleName()
                            + ", but was " + actualException.getClass().getSimpleName(),
                    actualException);
        }

        throw new AssertionError(
                "Expected " + expectedException.getSimpleName() + " to be thrown");
    }
}
