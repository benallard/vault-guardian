package de.benallard.vaultguardian;

import de.benallard.vaultguardian.events.CashBoxCreated;
import de.benallard.vaultguardian.events.CashBoxEvent;
import io.cloudevents.CloudEvent;
import org.occurrent.application.converter.CloudEventConverter;
import org.occurrent.dsl.query.blocking.DomainEventQueries;
import org.occurrent.eventstore.api.blocking.EventStore;
import org.occurrent.eventstore.api.blocking.EventStoreQueries;
import org.occurrent.filter.Filter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class CashboxQueryService {

    private final EventStore eventStore;
    private final CloudEventConverter<CashBoxEvent> eventConverter;
    private final DomainEventQueries<CashBoxEvent> domainEventQueries;

    public CashboxQueryService(
            EventStore eventStore,
            CloudEventConverter<CashBoxEvent> eventConverter,
            DomainEventQueries<CashBoxEvent> domainEventQueries) {
        this.eventStore = eventStore;
        this.eventConverter = eventConverter;
        this.domainEventQueries = domainEventQueries;
    }

    public CashBoxReadModel getReadModel(String cashboxId) {
        CashBoxState state = loadState(cashboxId, eventStore);
        return new CashBoxReadModel(
                state.toPayAmount(),
                state.inventoryAmount() != 0,
                state.discrepancyAmount());
    }

    private CashBoxState loadState(String streamId, EventStore eventStore) {

        Stream<CloudEvent> events = eventStore.read(streamId).events();

        return events.map(eventConverter::toDomainEvent)
                .reduce(CashBoxState.initial(), CashBoxState::apply, (s1, s2) -> s2);
    }

    public Collection<UUID> listBoxes(){
        return domainEventQueries.query(CashBoxCreated.class)
                .map(CashBoxCreated::boxId)
                .toList();
    }

    public record CashBoxReadModel(
            double toPayAmount,
            boolean countingInProgress,
            double discrepancyAmount) {
    }
}
