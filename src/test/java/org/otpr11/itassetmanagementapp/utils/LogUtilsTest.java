package org.otpr11.itassetmanagementapp.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

class LogUtilsTest {
  private static Level oldLevel;

  @AfterAll
  static void tearDown() {
    Configurator.setLevel(LogUtils.LOGGER_NAME, oldLevel);
  }

  @Test
  void testConfigureLogger() {
    oldLevel = LogManager.getLogger(LogUtils.LOGGER_NAME).getLevel();
    LogUtils.configureLogger();
    assertEquals(Level.TRACE, LogManager.getLogger(LogUtils.LOGGER_NAME).getLevel());
  }
}
