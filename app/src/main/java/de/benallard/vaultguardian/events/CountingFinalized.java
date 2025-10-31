package de.benallard.vaultguardian.events;

public record CountingFinalized(
    double countedSum,
    double expectedSum
) implements CashBoxEvent {
}
