package de.benallard.vaultguardian.events;

public sealed interface CashBoxEvent
        permits
        ReceiptReceived,
        MoneyRefilled,
        ReceiptsPaid,
        MoneyCounted,
        CountingFinalized,
        SaldoAdjusted {
}

