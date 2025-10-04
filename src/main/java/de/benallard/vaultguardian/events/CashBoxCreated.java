package de.benallard.vaultguardian.events;

public record CashBoxCreated(
        double initialAmount
) implements CashBoxEvent {
}
