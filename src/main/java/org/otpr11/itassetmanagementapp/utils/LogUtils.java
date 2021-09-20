package org.otpr11.itassetmanagementapp.utils;

import java.security.InvalidParameterException;
import lombok.val;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.config.Config;

public abstract class LogUtils {
  public static void configureLogger () {
    val config = Config.getConfig();
    val level = parseLogLevel(config.get("LOG_LEVEL"));
    Configurator.setLevel("org.otpr11", level);
  }

  private static Level parseLogLevel(@NotNull String level) {
    return switch (level.toUpperCase()) {
      case "OFF" -> Level.OFF;
      case "FATAL" -> Level.FATAL;
      case "ERROR" -> Level.ERROR;
      case "WARN" -> Level.WARN;
      case "INFO" -> Level.INFO;
      case "DEBUG" -> Level.DEBUG;
      case "TRACE" -> Level.TRACE;
      case "ALL" -> Level.ALL;
      default ->
        throw new InvalidParameterException("Log level %s does not exist");
    };
  }
}
