package de.benallard.vaultguardian.commands;

public sealed interface CashBoxCommand
        permits
        AddReceipt,
        PayReceipts,
        ReceiveRefill,
        CountMoney,
        FinalizeCounting,
        AdjustSaldo {
}
