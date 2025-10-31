package de.benallard.occurent.eventstore.jpa;

import io.cloudevents.CloudEvent;
import org.occurrent.eventstore.api.WriteCondition;
import org.occurrent.eventstore.api.WriteResult;
import org.occurrent.eventstore.api.blocking.EventStore;
import org.occurrent.eventstore.api.blocking.EventStream;

import java.util.stream.Stream;

public class JpaEventStore implements EventStore {
    @Override
    public WriteResult write(String s, WriteCondition writeCondition, Stream<CloudEvent> stream) {
        return null;
    }

    @Override
    public boolean exists(String s) {
        return false;
    }

    @Override
    public EventStream<CloudEvent> read(String s, int i, int i1) {
        return null;
    }

    @Override
    public WriteResult write(String s, Stream<CloudEvent> stream) {
        return null;
    }
}
