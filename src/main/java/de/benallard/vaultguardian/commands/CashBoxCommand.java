package de.benallard.vaultguardian.commands;

public sealed interface CashBoxCommand
        permits AddReceipt, AdjustSaldo, CountMoney, CreateCashBox, FinalizeCounting, PayReceipts, ReceiveRefill {
}
