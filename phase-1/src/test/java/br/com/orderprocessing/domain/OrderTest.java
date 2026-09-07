package br.com.orderprocessing.domain;

import java.util.UUID;

import br.com.orderprocessing.exception.InvalidOrderStatusException;

public class OrderTest {

    public static void main(String[] args) {
        shouldStartWithCreatedStatus();
        shouldTransitionFromCreatedToProcessing();
        shouldTransitionFromProcessingToCompleted();
        shouldTransitionFromCreatedToCancelled();
        shouldTransitionFromProcessingToCancelled();
        shouldRejectCancellationOfCompletedOrder();
        shouldRejectProcessingOfCompletedOrder();
        shouldRejectProcessingOfCancelledOrder();

        System.out.println("All Order tests passed.");
    }

    private static void shouldStartWithCreatedStatus() {
        Order order = newOrder();

        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    private static void shouldTransitionFromCreatedToProcessing() {
        Order order = newOrder();

        order.process();

        assertEquals(OrderStatus.PROCESSING, order.getStatus());
    }

    private static void shouldTransitionFromProcessingToCompleted() {
        Order order = newOrder();
        order.process();

        order.complete();

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    private static void shouldTransitionFromCreatedToCancelled() {
        Order order = newOrder();

        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    private static void shouldTransitionFromProcessingToCancelled() {
        Order order = newOrder();
        order.process();

        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    private static void shouldRejectCancellationOfCompletedOrder() {
        Order order = completedOrder();

        assertThrows(InvalidOrderStatusException.class, order::cancel);
    }

    private static void shouldRejectProcessingOfCompletedOrder() {
        Order order = completedOrder();

        assertThrows(InvalidOrderStatusException.class, order::process);
    }

    private static void shouldRejectProcessingOfCancelledOrder() {
        Order order = newOrder();
        order.cancel();

        assertThrows(InvalidOrderStatusException.class, order::process);
    }

    private static Order completedOrder() {
        Order order = newOrder();
        order.process();
        order.complete();
        return order;
    }

    private static Order newOrder() {
        Customer customer = new Customer(
            UUID.randomUUID(),
            "Gabriel",
            "gabriel@email.com"
        );

        return new Order(UUID.randomUUID(), customer);
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
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
