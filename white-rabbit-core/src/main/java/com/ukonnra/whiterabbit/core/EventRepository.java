package com.ukonnra.whiterabbit.core;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public interface EventRepository {
  Logger LOG = LoggerFactory.getLogger(EventRepository.class);

  <E extends Event> List<E> findAll(
      final String aggregateType,
      @Nullable final Collection<UUID> id,
      @Nullable final Integer startVersion,
      Class<E> eventClass);

  default <E extends Event> List<E> findAll(final String aggregateType, Class<E> eventClass) {
    return this.findAll(aggregateType, null, null, eventClass);
  }

  void doSaveAll(final Collection<Event> events);

  default void saveAll(final Collection<Event> events) {
    this.doSaveAll(events);
  }
}
