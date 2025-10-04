package de.benallard.vaultguardian;

import de.benallard.vaultguardian.commands.*;
import de.benallard.vaultguardian.events.CashBoxEvent;
import org.occurrent.application.service.blocking.ApplicationService;
import org.springframework.web.bind.annotation.*;

import static org.occurrent.application.composition.command.CommandConversion.toStreamCommand;

@RestController
@RequestMapping("/cashbox")
public class CashBoxController {
    private final String streamId = "cashbox";


    private final CashBoxDecider decider;
    private final ApplicationService<CashBoxEvent> applicationService;

    private final CashboxQueryService queryService;

    public CashBoxController(
            CashBoxDecider decider,
            ApplicationService<CashBoxEvent> applicationService,
            CashboxQueryService queryService) {
        this.decider = decider;
        this.applicationService = applicationService;
        this.queryService = queryService;
    }

    @PostMapping("/receipt")
    public void receipt(@RequestBody AddReceipt cmd) {
        process(cmd);
    }


    @PostMapping("/refill")
    public void refill(@RequestBody ReceiveRefill cmd) {
        process(cmd);
    }

    @PostMapping("/count")
    public void count(@RequestBody CountMoney cmd) {
        process(cmd);
    }

    @PostMapping("/done-counting")
    public void doneCounting() {
        process(new FinalizeCounting());
    }

    @PostMapping("/pay")
    public void pay() {
        process(new PayReceipts());
    }

    @GetMapping("/balance")
    public double balance() {
        return queryService.getSaldo(streamId);
    }

    private void process(CashBoxCommand cmd) {
        applicationService.execute(streamId,
                toStreamCommand(events -> decider.decideOnEventsAndReturnEvents(events, cmd)));
    }
}
