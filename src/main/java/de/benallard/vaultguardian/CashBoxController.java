package de.benallard.vaultguardian;

import de.benallard.vaultguardian.commands.*;
import de.benallard.vaultguardian.events.CashBoxEvent;
import de.benallard.vaultguardian.events.MoneyCounted;
import de.benallard.vaultguardian.events.MoneyRefilled;
import de.benallard.vaultguardian.events.ReceiptReceived;
import org.occurrent.application.converter.CloudEventConverter;
import org.occurrent.application.service.blocking.ApplicationService;
import org.occurrent.eventstore.api.blocking.EventStore;
import org.springframework.web.bind.annotation.*;

import static org.occurrent.application.composition.command.CommandConversion.toStreamCommand;

@RestController
@RequestMapping("/cashbox")
public class CashBoxController {
    private final String streamId = "cashbox";


    private final CashBoxDecider decider;

    private final ApplicationService<CashBoxEvent> applicationService;

    private final EventStore eventStore;
    private final CloudEventConverter<CashBoxEvent> eventConverter;

    public CashBoxController(
            CashBoxDecider decider,
            ApplicationService<CashBoxEvent> applicationService, EventStore eventStore, CloudEventConverter<CashBoxEvent> eventConverter) {
        this.decider = decider;
        this.applicationService = applicationService;
        this.eventStore = eventStore;
        this.eventConverter = eventConverter;
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
        return eventStore.read(streamId)
                .events()
                .map(eventConverter::toDomainEvent)
                .reduce( CashBoxState.initial(), (state, event) -> state.apply(event), (s1, s2) -> s2)
                .boxAmount();
    }

    private void process(CashBoxCommand cmd) {
        applicationService.execute(streamId,
                toStreamCommand(events -> decider.decideOnEventsAndReturnEvents(events, cmd)));
    }
}
