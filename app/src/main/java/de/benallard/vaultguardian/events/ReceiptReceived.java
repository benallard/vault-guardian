package de.benallard.vaultguardian.events;

public record ReceiptReceived(
        double amount,
        String description
) implements CashBoxEvent {}
