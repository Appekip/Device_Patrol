package org.otpr11.itassetmanagementapp.interfaces;

import org.otpr11.itassetmanagementapp.constants.DatabaseEventType;
import org.otpr11.itassetmanagementapp.db.core.DTO;

public interface DatabaseEventListener {
  void onDatabaseEvent(DatabaseEventType eventType, DTO entity);
}
