package org.otpr11.itassetmanagementapp.db.core;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.ArrayList;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.otpr11.itassetmanagementapp.config.Config;
import org.otpr11.itassetmanagementapp.constants.DatabaseEvent;
import org.otpr11.itassetmanagementapp.interfaces.DatabaseEventListener;

/**
 * This class listens to all database events from all {@link DTO}s and propagates them to registered
 * listeners. Listeners are registered by calling {@link DatabaseEventPropagator#addListener}.
 * Classes can implement the {@link DatabaseEventListener} interface, after which they can declare
 * an <code>onDatabaseEvent()</code> method.
 *
 * <p>Please note: Due to limitations of Java, merely implementing the interface does not guarantee
 * event delivery; classes still have to call <code>DatabaseEventPropagator.addListener(this)</code>
 * , should they wish to actually receive events. This behaviour is subject to change in the future,
 * as there are plans to turn {@link DatabaseEventListener} into an annotation instead.
 */
@Log4j2
public class DatabaseEventPropagator {
  private static final Dotenv config = Config.getConfig();

  private static final ArrayList<DatabaseEventListener> listeners = new ArrayList<>();

  public static void addListener(DatabaseEventListener listenerClass) {
    listeners.add(listenerClass);
  }

  private static void propagate(DatabaseEvent event, DTO entity) {
    log(event, entity);
    listeners.forEach(listener -> listener.onDatabaseEvent(event, entity));
  }

  private static void log(DatabaseEvent event, DTO entity) {
    val logSome = Boolean.parseBoolean(config.get("LOG_HIBERNATE_EVENT_TRIGGERS"));
    val logAll = Boolean.parseBoolean(config.get("LOG_ALL_HIBERNATE_EVENT_TRIGGERS"));

    if (logSome && event != DatabaseEvent.POST_LOAD) {
      logMessage(event, entity);
    } else if (logSome && logAll) {
      logMessage(event, entity);
    }
  }

  private static void logMessage(DatabaseEvent event, DTO entity) {
    log.trace("[{}] {}", event, entity);
  }

  @PrePersist
  public static void prePersist(Object o) {
    propagate(DatabaseEvent.PRE_PERSIST, (DTO) o);
  }

  @PostPersist
  public static void postPersist(Object o) {
    propagate(DatabaseEvent.POST_PERSIST, (DTO) o);
  }

  @PreRemove
  public static void preRemove(Object o) {
    propagate(DatabaseEvent.PRE_REMOVE, (DTO) o);
  }

  @PostRemove
  public static void postRemove(Object o) {
    propagate(DatabaseEvent.POST_REMOVE, (DTO) o);
  }

  @PreUpdate
  public static void preUpdate(Object o) {
    propagate(DatabaseEvent.PRE_UPDATE, (DTO) o);
  }

  @PostUpdate
  public static void postUpdate(Object o) {
    propagate(DatabaseEvent.POST_UPDATE, (DTO) o);
  }

  @PostLoad
  public static void postLoad(Object o) {
    propagate(DatabaseEvent.POST_LOAD, (DTO) o);
  }
}
