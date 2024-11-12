package com.ukonnra.whiterabbit.core;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public abstract class AbstractEntity<V extends Event> implements Entity {
  public static final String FIELD_ID = "id";

  public static final int MIN_NAMELY = 2;
  public static final int MAX_NAMELY = 127;
  public static final int MAX_LONG_TEXT = 1023;
  public static final int MAX_TAGS = 15;

  private UUID id;

  private Instant createdDate = Instant.now();

  private int version = -1;

  private @Nullable Instant deletedDate = null;

  public void delete(final Instant timestamp) {
    if (this.deletedDate == null) {
      this.deletedDate = timestamp;
    }
  }

  public void delete() {
    this.delete(Instant.now());
  }

  public final void handleEvents(final Collection<V> events) {
    events.stream().sorted(Comparator.comparing(Event::version)).forEachOrdered(this::handleEvent);
  }

  public final void handleEvent(final V event) {
    if (event.version() == this.version + 1) {
      this.doHandleEvent(event);
      this.version = event.version();
    } else {
      // todo: invalid event version
      throw new UnsupportedOperationException("Invalid event version");
    }
  }

  protected abstract void doHandleEvent(final V event);
}
