package org.otpr11.itassetmanagementapp.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.val;
import org.junit.jupiter.api.Test;

class ConfigTest {
  @Test
  void testVariableLoading() {
    val config = Config.getConfig();
    assertEquals("TEST_VALUE", config.get("TEST_VARIABLE"));
  }
}
