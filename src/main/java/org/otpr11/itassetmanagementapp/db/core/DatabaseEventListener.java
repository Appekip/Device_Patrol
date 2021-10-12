package org.otpr11.itassetmanagementapp.db.core;

import org.otpr11.itassetmanagementapp.constants.DatabaseEventType;

public interface DatabaseEventListener {
  void onDatabaseEvent(DatabaseEventType eventType, DTO entity);
}
