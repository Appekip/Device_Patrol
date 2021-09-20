package org.otpr11.itassetmanagementapp.db.core;

public class DatabaseQueryException extends Exception {
  public DatabaseQueryException(String message) {
    super(message);
  }

  public DatabaseQueryException(Throwable cause) {
    super(cause);
  }
}
