package org.otpr11.itassetmanagementapp.interfaces;

import org.otpr11.itassetmanagementapp.constants.DatabaseEvent;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.core.DatabaseEventPropagator;

/**
 * When properly registered (as per directives in the documentation of {@link
 * DatabaseEventPropagator}), classes implementing this interface will receive database update
 * events. The complete documentation of this behaviour is in the aforementioned class.
 *
 * @see DatabaseEventPropagator
 */
public interface DatabaseEventListener {
  void onDatabaseEvent(DatabaseEvent event, DTO entity);
}
