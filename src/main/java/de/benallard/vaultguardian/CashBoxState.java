package de.benallard.vaultguardian;

import de.benallard.vaultguardian.events.CashBoxEvent;
import de.benallard.vaultguardian.events.MoneyCounted;
import de.benallard.vaultguardian.events.MoneyRefilled;
import de.benallard.vaultguardian.events.ReceiptReceived;

public record CashBoxState(double expectedBalance, double lastCountedAmount) {
    public static CashBoxState initial() {
        return new CashBoxState(0, 0);
    }

    public CashBoxState apply(CashBoxEvent event) {
        return switch (event) {
            case ReceiptReceived r -> new CashBoxState(expectedBalance - r.amount(), lastCountedAmount);
            case MoneyRefilled r -> new CashBoxState(expectedBalance + r.amount(), lastCountedAmount);
            case MoneyCounted c -> new CashBoxState(expectedBalance, c.physicalAmount());
            default -> this;
        };
    }
}