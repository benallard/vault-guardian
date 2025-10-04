package de.benallard.vaultguardian.commands;

public record CountMoney(
        double amount
) implements CashBoxCommand {
}
