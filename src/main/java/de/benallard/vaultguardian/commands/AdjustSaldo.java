package de.benallard.vaultguardian.commands;

public record AdjustSaldo(
        double amount,
        String reason
) implements CashBoxCommand {
}
