package de.benallard.occurent.eventstore.spring.data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CloudEventRepository extends JpaRepository<CloudEventEntity, UUID>,
        JpaSpecificationExecutor<CloudEventEntity> {
    List<CloudEventEntity> findByStream(StreamEntity stream, Pageable aPageable);
}
