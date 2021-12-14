package org.otpr11.itassetmanagementapp.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.otpr11.itassetmanagementapp.db.model.Configuration;
import org.otpr11.itassetmanagementapp.db.model.DesktopConfiguration;
import org.otpr11.itassetmanagementapp.db.model.LaptopConfiguration;
import org.otpr11.itassetmanagementapp.interfaces.PrettyStringifiable;

class StringUtilsTest {
  @Test
  void testGetFullExceptionStack() {
    val e = new Exception();
    assertTrue(StringUtils.getFullExceptionStack(e).contains("java.lang.Exception"));
  }

  @Test
  void testGetPrettyHWConfig() {
    val cfg = new Configuration();
    val desktop = new DesktopConfiguration("test", "test", "test", "test");
    val laptop = new LaptopConfiguration("test", "test", "test", "test", 0);
    cfg.setDesktopConfiguration(desktop);
    assertEquals(StringUtils.getPrettyHWConfig(cfg), "test, test, test RAM, test disk");
    cfg.setLaptopConfiguration(laptop);
    assertEquals(StringUtils.getPrettyHWConfig(cfg), "0\", test, test, test RAM, test disk");
  }

  @Test
  void testJoinPrettyStrings() {
    TestStringifiable[] objects = {new TestStringifiable(), new TestStringifiable()};
    assertEquals("pretty string, pretty string", StringUtils.joinPrettyStrings(List.of(objects)));
  }

  private static class TestStringifiable implements PrettyStringifiable {

    @Override
    public String toPrettyString() {
      return "pretty string";
    }
  }
}
