package de.benallard.vaultguardian.commands;

import java.util.UUID;

public record CreateCashBox(
        UUID cashBoxId,
        double initialAmount
) implements CashBoxCommand {
}
