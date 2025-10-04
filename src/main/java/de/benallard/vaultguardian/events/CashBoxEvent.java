package de.benallard.vaultguardian.events;

public sealed interface CashBoxEvent
        permits CashBoxCreated, CountingFinalized, MoneyCounted, MoneyRefilled, ReceiptReceived, ReceiptsPaid, SaldoAdjusted {
}

