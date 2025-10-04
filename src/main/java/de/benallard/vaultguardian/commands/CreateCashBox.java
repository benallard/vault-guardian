package de.benallard.vaultguardian.commands;

public record CreateCashBox(
        double initialAmount
) implements CashBoxCommand {
}
