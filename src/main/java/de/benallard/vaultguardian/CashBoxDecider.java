package de.benallard.vaultguardian;

import de.benallard.vaultguardian.events.CashBoxEvent;
import de.benallard.vaultguardian.events.MoneyCounted;
import de.benallard.vaultguardian.events.MoneyRefilled;
import de.benallard.vaultguardian.events.ReceiptReceived;

import java.util.List;

public class CashBoxDecider {

    public List<CashBoxEvent> decide(CashBoxState state, Object command) {
        return switch (command) {
            case ReceiptReceived r -> List.of(r);
            case MoneyRefilled r -> List.of(r);
            case MoneyCounted c -> List.of(c);
            default -> throw new IllegalArgumentException("Unknown command");
        };
    }
}