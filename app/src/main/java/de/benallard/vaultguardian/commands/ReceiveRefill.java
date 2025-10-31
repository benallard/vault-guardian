package de.benallard.vaultguardian.commands;

public record ReceiveRefill(
        double amount
) implements CashBoxCommand {
}
