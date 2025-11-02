package de.benallard.occurent.eventstore.spring.data;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.data.BytesCloudEventData;
import jakarta.persistence.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "cloud_event",
        indexes = {
                @Index(name = "IDX_cloud_event_stream", columnList = "stream_id"),
                @Index(name = "IDX_cloud_event_stream_position", columnList = "stream_position"),
        })
public class CloudEventEntity implements CloudEvent {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "spec_version", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private SpecVersion specVersion;

    @ManyToOne
    @JoinColumn(name = "stream_id", nullable = false, updatable = false)
    private StreamEntity stream;

    @Column(name = "stream_position", nullable = false, updatable = false)
    private Long streamPosition;

    @Column(name = "source", nullable = false, updatable = false)
    private URI source;

    @Column(name = "subject", updatable = false)
    private String subject;

    @Column(name = "type", nullable = false, updatable = false)
    private String type;

    @Column(name = "time", updatable = false)
    private OffsetDateTime time;

    @Column(name = "data_content_type", updatable = false)
    private String dataContentType;

    @Column(name = "data_schema", updatable = false)
    private URI dataSchema;

    @Column(name = "data", updatable = false)
    private byte[] data;

    @OneToMany(mappedBy = "cloudEvent")
    @MapKey(name = "attributeName")
    private Map<String, CloudEventAttributeEntity> attributes;


    @Override
    public CloudEventData getData() {
        return BytesCloudEventData.wrap(data);
    }

    public void setData(byte[] aData) {
        data = aData;
    }

    @Override
    public SpecVersion getSpecVersion() {
        return specVersion;
    }

    public void setSpecVersion(SpecVersion aSpecVersion) {
        specVersion = aSpecVersion;
    }

    @Override
    public String getId() {
        return id.toString();
    }

    public void setId(UUID aId) {
        id = aId;
    }

    public StreamEntity getStream() {
        return stream;
    }

    public void setStream(StreamEntity stream) {
        this.stream = stream;
    }

    public Long getStreamPosition() {
        return streamPosition;
    }

    public void setStreamPosition(Long streamPosition) {
        this.streamPosition = streamPosition;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String aType) {
        type = aType;
    }

    @Override
    public URI getSource() {
        return source;
    }

    public void setSource(URI aSource) {
        source = aSource;
    }

    @Override
    public String getDataContentType() {
        return dataContentType;
    }

    public void setDataContentType(String aDataContentType) {
        dataContentType = aDataContentType;
    }

    @Override
    public URI getDataSchema() {
        return dataSchema;
    }

    public void setDataSchema(URI aDataSchema) {
        dataSchema = aDataSchema;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    public void setSubject(String aSubject) {
        subject = aSubject;
    }

    @Override
    public OffsetDateTime getTime() {
        return time;
    }

    public void setTime(OffsetDateTime aTime) {
        time = aTime;
    }

    @Override
    public Object getAttribute(String s) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Object getExtension(String s) {
        return null;
    }

    @Override
    public Set<String> getExtensionNames() {
        return Set.of();
    }
}
