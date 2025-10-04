package de.benallard.vaultguardian;

import de.benallard.vaultguardian.events.*;

public record CashBoxState(
    double boxAmount,
    double toPayAmount,
    double inventoryAmount,
    double discrepancyAmount,
    boolean virgin
) {
    public static CashBoxState initial() {
        return new CashBoxState(0,0,0, 0, true);
    }

    /**
     * Here, we MUST apply the event.
     * The @{see CashBoxDecider} is responsible to ensure that the event is valid in the current state.
     */
    public CashBoxState apply(CashBoxEvent event) {
        return switch (event) {
            case CashBoxCreated created -> new CashBoxState(created.initialAmount(), 0, 0, 0, false);
            case ReceiptReceived received -> new CashBoxState(boxAmount, toPayAmount + received.amount(), 0, discrepancyAmount, false);
            case MoneyRefilled refill -> new CashBoxState(boxAmount + refill.amount(), toPayAmount, 0, discrepancyAmount, false);
            case MoneyCounted count -> new CashBoxState(boxAmount, toPayAmount, inventoryAmount + count.physicalAmount(), discrepancyAmount, false);
            case CountingFinalized finalized -> new CashBoxState(boxAmount, toPayAmount, 0, finalized.expectedSum() - finalized.countedSum(), false);
            case ReceiptsPaid paid -> new CashBoxState(boxAmount - paid.totalAmount(), toPayAmount - paid.totalAmount(), 0, discrepancyAmount, false);
            case SaldoAdjusted _ -> new CashBoxState(boxAmount, toPayAmount, 0, 0, false);
        };
    }
}