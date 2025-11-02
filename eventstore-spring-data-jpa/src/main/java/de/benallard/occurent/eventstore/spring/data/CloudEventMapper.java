package de.benallard.occurent.eventstore.spring.data;

import io.cloudevents.CloudEvent;

import java.util.UUID;

public class CloudEventMapper {

    public CloudEventEntity toEntity(CloudEvent aEvent) {
        CloudEventEntity res = new CloudEventEntity();
        res.setId(UUID.fromString(aEvent.getId()));
        res.setSpecVersion(aEvent.getSpecVersion());
        res.setType(aEvent.getType());
        res.setSource(aEvent.getSource());
        res.setDataContentType(aEvent.getDataContentType());
        res.setDataSchema(aEvent.getDataSchema());
        res.setSubject(aEvent.getSubject());
        res.setTime(aEvent.getTime());
        if (aEvent.getData() != null) {
            res.setData(aEvent.getData().toBytes());
        }
        return res;
    }

}
