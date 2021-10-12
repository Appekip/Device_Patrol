package org.otpr11.itassetmanagementapp.utils;

import java.security.InvalidParameterException;
import lombok.val;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.config.Config;

public abstract class LogUtils {

  /**
   * Pre-configures the global log4j logger.
   */
  public static void configureLogger () {
    val config = Config.getConfig();
    val level = parseLogLevel(config.get("LOG_LEVEL"));
    Configurator.setLevel("org.otpr11", level);
  }

  /**
   * Parses a text-form log level into a log4j {@link Level}.
   * @param level String representation of a {@link Level}.
   * @return {@link Level}
   */
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
