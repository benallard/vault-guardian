package de.benallard.vaultguardian.events;

public record MoneyCounted(
        double physicalAmount
) implements CashBoxEvent {
}
