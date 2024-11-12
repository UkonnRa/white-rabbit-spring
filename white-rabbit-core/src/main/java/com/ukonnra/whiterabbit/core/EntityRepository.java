package com.ukonnra.whiterabbit.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

public interface EntityRepository<E extends AbstractEntity<V>, Q extends Query, V extends Event> {
  Logger LOG = LoggerFactory.getLogger(EntityRepository.class);

  EventRepository getEventRepository();

  String getAggregateType();

  Class<V> getEventClass();

  E getDefaultEntity(UUID id);

  Set<E> findAll(@Nullable final Q query, @Nullable final Integer size);

  Set<E> findAllByIds(final Collection<UUID> ids);

  default Optional<E> findOne(final Q query) {
    return this.findAll(query, 1).stream().findFirst();
  }

  default Optional<E> findById(final UUID id) {
    return this.findAllByIds(Set.of(id)).stream().findFirst();
  }

  default Set<E> findAll(final Q query) {
    return this.findAll(query, null);
  }

  default Set<E> findAll() {
    return this.findAll(null, null);
  }

  // For snapshots
  void saveAll(final Collection<E> entities);

  @Transactional
  default void refreshSnapshots(@Nullable Collection<UUID> ids) {
    if (ids != null && ids.isEmpty()) {
      return;
    }

    final Set<E> entities;
    final List<V> events;

    if (ids == null) {
      entities = this.findAll();
      events = this.getEventRepository().findAll(this.getAggregateType(), this.getEventClass());
    } else {
      entities = this.findAllByIds(ids);
      events =
          this.getEventRepository()
              .findAll(this.getAggregateType(), ids, null, this.getEventClass());
    }

    final var entityMap =
        entities.stream().collect(Collectors.toMap(AbstractEntity::getId, Function.identity()));

    final var allIds =
        Stream.concat(entities.stream().map(AbstractEntity::getId), events.stream().map(Event::id))
            .collect(Collectors.toSet());

    final var results = new HashSet<E>();
    for (final var id : allIds) {
      final var entity = entityMap.getOrDefault(id, this.getDefaultEntity(id));

      final var futureEvents =
          events.stream()
              .filter(e -> e.id().equals(entity.getId()) && e.version() > entity.getVersion())
              .toList();
      entity.handleEvents(futureEvents);
      results.add(entity);
    }

    this.saveAll(results);
  }
}
