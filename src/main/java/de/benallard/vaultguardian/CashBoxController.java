package de.benallard.vaultguardian;

import de.benallard.vaultguardian.events.MoneyCounted;
import de.benallard.vaultguardian.events.MoneyRefilled;
import de.benallard.vaultguardian.events.ReceiptReceived;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cashbox")
public class CashBoxController {

    // Temporary in-memory state
    private CashBoxState state = CashBoxState.initial();
    private final CashBoxDecider decider = new CashBoxDecider();

    @PostMapping("/receipt")
    public void receipt(@RequestBody ReceiptReceived cmd) {
        decider.decide(state, cmd).forEach(e -> state = state.apply(e));
    }

    @PostMapping("/refill")
    public void refill(@RequestBody MoneyRefilled cmd) {
        decider.decide(state, cmd).forEach(e -> state = state.apply(e));
    }

    @PostMapping("/count")
    public void count(@RequestBody MoneyCounted cmd) {
        decider.decide(state, cmd).forEach(e -> state = state.apply(e));
    }

    @GetMapping("/balance")
    public double balance() {
        return state.expectedBalance();
    }
}
