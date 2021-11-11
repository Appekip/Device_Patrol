package org.otpr11.itassetmanagementapp.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.otpr11.itassetmanagementapp.interfaces.PrettyStringifiable;

class StringUtilsTest {
  private static class TestStringifiable implements PrettyStringifiable {

    @Override
    public String toPrettyString() {
      return "pretty string";
    }
  }

  @Test
  void testJoinPrettyStrings() {
    TestStringifiable[] objects = {new TestStringifiable(), new TestStringifiable()};
    assertEquals("pretty string, pretty string", StringUtils.joinPrettyStrings(List.of(objects)));
  }
}
