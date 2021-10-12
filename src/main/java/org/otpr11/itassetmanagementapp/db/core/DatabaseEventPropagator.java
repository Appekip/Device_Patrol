package org.otpr11.itassetmanagementapp.db.core;

import java.util.ArrayList;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import org.otpr11.itassetmanagementapp.constants.DatabaseEventType;

public class DatabaseEventPropagator {
  private static final ArrayList<DatabaseEventListener> listeners = new ArrayList<>();

  public static void addListener(DatabaseEventListener listenerClass) {
    listeners.add(listenerClass);
  }

  private static void propagate(DatabaseEventType event, DTO entity) {
    listeners.forEach(listener -> listener.onDatabaseEvent(event, entity));
  }

  @PrePersist
  public static void prePersist(Object o) {
    propagate(DatabaseEventType.PRE_PERSIST, (DTO) o);
  }

  @PostPersist
  public static void postPersist(Object o) {
    propagate(DatabaseEventType.POST_PERSIST, (DTO) o);
  }

  @PreRemove
  public static void preRemove(Object o) {
    propagate(DatabaseEventType.PRE_REMOVE, (DTO) o);
  }

  @PostRemove
  public static void postRemove(Object o) {
    propagate(DatabaseEventType.POST_REMOVE, (DTO) o);
  }

  @PreUpdate
  public static void preUpdate(Object o) {
    propagate(DatabaseEventType.PRE_UPDATE, (DTO) o);
  }

  @PostUpdate
  public static void postUpdate(Object o) {
    propagate(DatabaseEventType.POST_UPDATE, (DTO) o);
  }

  @PostLoad
  public static void postLoad(Object o) {
    propagate(DatabaseEventType.POST_LOAD, (DTO) o);
  }
}
