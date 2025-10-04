package de.benallard.vaultguardian;

import de.benallard.vaultguardian.events.*;

public record CashBoxState(
    double boxAmount,
    double toPayAmount,
    double inventoryAmount
) {
    public static CashBoxState initial() {
        return new CashBoxState(0,0,0);
    }

    /**
     * Here, we MUST apply the event.
     * The @{see CashBoxDecider} is responsible to ensure that the event is valid in the current state.
     */
    public CashBoxState apply(CashBoxEvent event) {
        return switch (event) {
            case ReceiptReceived received -> new CashBoxState(boxAmount, toPayAmount + received.amount(), 0);
            case MoneyRefilled refill -> new CashBoxState(boxAmount + refill.amount(), toPayAmount, 0);
            case MoneyCounted count -> new CashBoxState(boxAmount, toPayAmount, inventoryAmount + count.physicalAmount());
            case CountingFinalized _ -> new CashBoxState(boxAmount, toPayAmount, 0);
            case ReceiptsPaid paid -> new CashBoxState(boxAmount - paid.totalAmount(), toPayAmount - paid.totalAmount(), 0);
            case SaldoAdjusted _ -> this;
        };
    }
}