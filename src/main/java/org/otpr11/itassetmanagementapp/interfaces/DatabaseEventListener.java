package org.otpr11.itassetmanagementapp.interfaces;

import org.otpr11.itassetmanagementapp.constants.DatabaseEvent;
import org.otpr11.itassetmanagementapp.db.core.DTO;

public interface DatabaseEventListener {
  void onDatabaseEvent(DatabaseEvent event, DTO entity);
}
