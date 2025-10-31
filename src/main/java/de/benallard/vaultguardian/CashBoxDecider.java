package de.benallard.vaultguardian;

import de.benallard.vaultguardian.commands.*;
import de.benallard.vaultguardian.events.*;
import org.jetbrains.annotations.NotNull;
import org.occurrent.dsl.decider.Decider;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Component
public class CashBoxDecider implements Decider<CashBoxCommand, CashBoxState, CashBoxEvent> {
    @Override
    public CashBoxState initialState() {
        return CashBoxState.initial();
    }

    /**
     * That's the part that may refuse commands based on the current state
     * or produce different events based on the current state.
     */
    @NotNull
    @Override
    public List<CashBoxEvent> decide(@NotNull CashBoxCommand cashBoxCommand, CashBoxState cashBoxState) {
        List<CashBoxEvent> res = new LinkedList<>();
        if (cashBoxState.virgin() && !(cashBoxCommand instanceof CreateCashBox)) {
            // While it might look cool to have the decider create the cash box on the fly,
            // I don't think it's worth it. Better to be explicit.
            // But for demonstration purposes, here is how it would look like:
            // However, as we see, there is no way to return the created box ID to the caller.
            res.add(new CashBoxCreated(UUID.randomUUID(), 0));
            throw new IllegalStateException("Cash box not created yet. Please create it first.");
        }
        res.add(
                switch (cashBoxCommand) {
                    case CreateCashBox create -> {
                        if (!cashBoxState.virgin()) {
                            throw new IllegalStateException("Cash box already created");
                        }
                        yield new CashBoxCreated(create.cashBoxId(), create.initialAmount());
                    }
                    case AddReceipt receipt -> {
                        if (cashBoxState.toPayAmount() + receipt.amount() > 500) {
                            throw new IllegalStateException("Cannot add receipt, to pay amount would exceed 500");
                        }
                        if (cashBoxState.toPayAmount() + receipt.amount() > cashBoxState.boxAmount()) {
                            throw new IllegalStateException("Cannot add receipt, to pay amount would exceed box amount");
                        }
                        yield new ReceiptReceived(receipt.amount(), receipt.description());
                    }
                    case ReceiveRefill refill -> new MoneyRefilled(refill.amount());
                    case PayReceipts _ -> {
                        if (cashBoxState.toPayAmount() <= 0) {
                            throw new IllegalStateException("No receipts to pay");
                        }
                        if (cashBoxState.toPayAmount() > cashBoxState.boxAmount()) {
                            throw new IllegalStateException("Not enough money in the box to pay receipts");
                        }
                        yield new ReceiptsPaid(cashBoxState.toPayAmount());
                    }
                    case CountMoney count -> new MoneyCounted(count.amount());
                    case FinalizeCounting _ -> {
                        if (cashBoxState.inventoryAmount() == 0) {
                            throw new IllegalStateException("No counting in progress");
                        }
                        yield new CountingFinalized(cashBoxState.inventoryAmount(), cashBoxState.boxAmount());
                    }
                    case AdjustSaldo _ -> {
                        if (cashBoxState.inventoryAmount() != 0) {
                            throw new IllegalStateException("Cannot adjust saldo while counting is in progress");
                        }
                        yield new SaldoAdjusted();
                    }
                });
        return res;
    }

    @Override
    public CashBoxState evolve(CashBoxState cashBoxState, @NotNull CashBoxEvent cashBoxEvent) {
        return cashBoxState.apply(cashBoxEvent);
    }

}