package de.benallard.vaultguardian.commands;

public record AddReceipt(
        double amount,
        String description
) implements CashBoxCommand {
}
