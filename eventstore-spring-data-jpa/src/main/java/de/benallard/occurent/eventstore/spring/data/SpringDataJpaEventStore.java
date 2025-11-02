package de.benallard.occurent.eventstore.spring.data;

import io.cloudevents.CloudEvent;
import jakarta.transaction.Transactional;
import org.occurrent.condition.Condition;
import org.occurrent.eventstore.api.*;
import org.occurrent.eventstore.api.blocking.EventStore;
import org.occurrent.eventstore.api.blocking.EventStoreQueries;
import org.occurrent.eventstore.api.blocking.EventStream;
import org.occurrent.filter.Filter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * A JPA-based implementation of the {@link EventStore} interface.
 */
@Component
public class SpringDataJpaEventStore implements EventStore, EventStoreQueries {

    private final CloudEventRepository itsCloudEventRepository;
    private final StreamRepository itsStreamRepository;
    private final CloudEventMapper itsCloudEventMapper;

    public SpringDataJpaEventStore(
            CloudEventRepository itsCloudEventRepository,
            StreamRepository itsStreamRepository,
            CloudEventMapper itsCloudEventMapper) {
        this.itsCloudEventRepository = itsCloudEventRepository;
        this.itsStreamRepository = itsStreamRepository;
        this.itsCloudEventMapper = itsCloudEventMapper;
    }

    @Override
    @Transactional
    public WriteResult write(String streamId, WriteCondition writeCondition, Stream<CloudEvent> events) {
        // Fetch the stream Version.
        StreamEntity stream = itsStreamRepository.getByName(streamId)
                .orElseGet(() -> {
                    StreamEntity newStream = new StreamEntity();
                    newStream.setName(streamId);
                    newStream.setVersion(0L);
                    return itsStreamRepository.save(newStream);
                });
        long oldVersion = stream.getVersion();
        // Evaluate the condition
        if (!evaluateCondition(writeCondition, oldVersion)) {
            throw new WriteConditionNotFulfilledException(
                    streamId,
                    stream.getVersion(),
                    writeCondition,
                    String.format("%s was not fulfilled. Expected version %s but was %s.",
                            WriteCondition.class.getSimpleName(),
                            writeCondition,
                            stream.getVersion()));
        }

        // enrich the events with stream information
        AtomicLong counter = new AtomicLong(stream.getVersion());
        List<CloudEventEntity> newEvents = events
                .map(e -> {
                    var res = itsCloudEventMapper.toEntity(e);
                    res.setStream(stream);
                    res.setStreamPosition(counter.incrementAndGet());
                    return res;
                })
                .toList();
        // Update the stream version
        stream.setVersion(counter.get());
        itsStreamRepository.save(stream);
        // Persist the events
        itsCloudEventRepository.saveAll(newEvents);
        return new WriteResult(streamId, oldVersion, stream.getVersion());
    }

    private boolean evaluateCondition(WriteCondition aCondition, long aVersion) {
        if (aCondition.isAnyStreamVersion()) {
            return true;
        }

        if (!(aCondition instanceof WriteCondition.StreamVersionWriteCondition(Condition<Long> condition))) {
            return false;
        }

        return LongConditionEvaluator.evaluate(condition, aVersion);
    }

    @Override
    public boolean exists(String streamId) {
        // Better check if events are there ?
        return itsStreamRepository.existsByName(streamId);
    }

    @Override
    @Transactional
    public EventStream<CloudEvent> read(String streamId, int skip, int limit) {
        StreamEntity stream = itsStreamRepository.getByName(streamId)
                .orElseGet(() -> {
                    StreamEntity newStream = new StreamEntity();
                    newStream.setName(streamId);
                    newStream.setVersion(0L);
                    return itsStreamRepository.save(newStream);
                });
        List<? extends CloudEvent> events = itsCloudEventRepository.findByStream(
                stream,
                new OffsetBasedPageRequest(
                        skip,
                        limit,
                        Sort.by(
                                Sort.Direction.ASC,
                                "streamPosition")));
        return new EventStreamImpl(
                stream.getName(),
                stream.getVersion(),
                (Stream<CloudEvent>) events.stream());
    }

    @Override
    public WriteResult write(String streamId, Stream<CloudEvent> events) {
        return write(streamId, WriteCondition.anyStreamVersion(), events);
    }

    /*
     * Following query methods are not implemented yet.
     */

    @Override
    public Stream<CloudEvent> query(Filter filter, int skip, int limit, SortBy sortBy) {
        return Stream.empty();
    }

    @Override
    public long count(Filter filter) {
        return 0;
    }

    @Override
    public boolean exists(Filter filter) {
        return false;
    }
}
