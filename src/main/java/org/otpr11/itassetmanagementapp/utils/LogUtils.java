package org.otpr11.itassetmanagementapp.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.otpr11.itassetmanagementapp.config.Config;

/** Generic logging utilities. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class LogUtils {
  public static final String LOGGER_NAME = "org.otpr11";

  /** Pre-configures the global log4j logger. */
  public static void configureLogger() {
    val config = Config.getConfig();
    val level = Level.valueOf(config.get("LOG_LEVEL".toUpperCase()));
    Configurator.setLevel(LOGGER_NAME, level);
  }
}
