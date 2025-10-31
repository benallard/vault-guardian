package de.benallard.vaultguardian.events;

import java.util.UUID;

public record CashBoxCreated(
        UUID boxId,
        double initialAmount
) implements CashBoxEvent {
}
