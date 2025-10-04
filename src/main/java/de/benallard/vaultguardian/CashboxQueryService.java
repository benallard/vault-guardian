package de.benallard.vaultguardian;

import de.benallard.vaultguardian.events.CashBoxEvent;
import io.cloudevents.CloudEvent;
import org.occurrent.application.converter.CloudEventConverter;
import org.occurrent.eventstore.api.blocking.EventStore;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CashboxQueryService {

    private final EventStore eventStore;
    private final CloudEventConverter<CashBoxEvent> eventConverter;

    public CashboxQueryService(EventStore eventStore, CloudEventConverter<CashBoxEvent> eventConverter) {
        this.eventStore = eventStore;
        this.eventConverter = eventConverter;
    }

    public double getSaldo(String cashboxId) {
        CashBoxState state = loadState(cashboxId, eventStore);
        return state.boxAmount();
    }

    private CashBoxState loadState(String streamId, EventStore eventStore) {

        Stream<CloudEvent> events = eventStore.read(streamId).events();

        return events.map(eventConverter::toDomainEvent)
              .reduce(CashBoxState.initial(), CashBoxState::apply, (s1, s2) -> s2);
    }
}
