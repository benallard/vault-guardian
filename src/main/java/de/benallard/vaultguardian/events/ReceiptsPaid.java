package de.benallard.vaultguardian.events;

public record ReceiptsPaid(
        double totalAmount
) implements CashBoxEvent {
}
