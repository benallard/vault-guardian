package de.benallard.vaultguardian;

import de.benallard.vaultguardian.commands.*;
import de.benallard.vaultguardian.events.CashBoxEvent;
import org.occurrent.application.service.blocking.ApplicationService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

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

    public record InitialBalance(double initialAmount) {
    }

    @PostMapping()
    public UUID create(@RequestBody InitialBalance balance) {
        var boxId = UUID.randomUUID();
        var cmd = new CreateCashBox(boxId, balance.initialAmount);
        process(boxId, cmd);
        return boxId;
    }

    @PostMapping("/{id}/receipt")
    public void receipt(@PathVariable("id") UUID boxId, @RequestBody AddReceipt cmd) {
        process(boxId, cmd);
    }


    @PostMapping("/{id}/refill")
    public void refill(@PathVariable("id") UUID boxId, @RequestBody ReceiveRefill cmd) {
        process(boxId, cmd);
    }

    @PostMapping("/{id}/count")
    public void count(@PathVariable("id") UUID boxId, @RequestBody CountMoney cmd) {
        process(boxId, cmd);
    }

    @PostMapping("/{id}/done-counting")
    public void doneCounting(@PathVariable("id") UUID boxId) {
        process(boxId, new FinalizeCounting());
    }

    @PostMapping("/{id}/reset-discrepancy")
    public void resetDiscrepancy(@PathVariable("id") UUID boxId) {
        process(boxId, new AdjustSaldo());
    }

    @PostMapping("/{id}/pay")
    public void pay(@PathVariable("id") UUID boxId) {
        process(boxId, new PayReceipts());
    }

    private void process(UUID aBoxId, CashBoxCommand cmd) {
        applicationService.execute(streamId + "-" + aBoxId,
                toStreamCommand(events -> decider.decideOnEventsAndReturnEvents(events, cmd)));
    }

    @GetMapping("/{id}")
    public CashboxQueryService.CashBoxReadModel openBalance(@PathVariable("id") UUID boxId) {
        return queryService.getReadModel(streamId + "-" + boxId);
    }

    @GetMapping()
    public Collection<UUID> listCashBoxes() {
        return queryService.listBoxes();
    }
}
