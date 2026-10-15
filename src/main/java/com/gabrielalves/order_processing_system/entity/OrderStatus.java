package com.gabrielalves.order_processing_system.entity;

/**
 * Ciclo de vida do pedido: CREATED -> PROCESSING -> COMPLETED, com cancelamento permitido
 * enquanto o pedido não foi concluído. COMPLETED e CANCELLED são estados finais.
 */
public enum OrderStatus {
    CREATED,
    PROCESSING,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case CREATED -> next == PROCESSING || next == CANCELLED;
            case PROCESSING -> next == COMPLETED || next == CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
