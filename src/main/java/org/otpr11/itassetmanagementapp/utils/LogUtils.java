package org.otpr11.itassetmanagementapp.utils;

import lombok.val;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.otpr11.itassetmanagementapp.config.Config;

/** Generic logging utilities. */
public abstract class LogUtils {

  /** Pre-configures the global log4j logger. */
  public static void configureLogger() {
    val config = Config.getConfig();
    val level = Level.valueOf(config.get("LOG_LEVEL".toUpperCase()));
    Configurator.setLevel("org.otpr11", level);
  }
}
