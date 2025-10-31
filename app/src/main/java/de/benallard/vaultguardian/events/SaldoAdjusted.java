package de.benallard.vaultguardian.events;
/**
 * Event indicating that the saldo has been adjusted.
 *
 * Not sure what the real use case is, but it might be useful for logging purposes.
 */
public record SaldoAdjusted(
) implements CashBoxEvent {
}
