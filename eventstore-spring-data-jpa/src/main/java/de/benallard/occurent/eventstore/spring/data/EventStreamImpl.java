package de.benallard.occurent.eventstore.spring.data;

import io.cloudevents.CloudEvent;
import org.occurrent.eventstore.api.blocking.EventStream;

import java.util.stream.Stream;

public record EventStreamImpl(
        String streamId,
        long streamVersion,
        Stream<CloudEvent> events
) implements EventStream<CloudEvent> {
    @Override
    public String id() {
        return "";
    }

    @Override
    public long version() {
        return 0;
    }

    @Override
    public Stream<CloudEvent> events() {
        return Stream.empty();
    }
}
