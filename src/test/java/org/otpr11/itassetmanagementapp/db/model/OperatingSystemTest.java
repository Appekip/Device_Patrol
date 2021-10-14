package org.otpr11.itassetmanagementapp.db.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OperatingSystemTest {

  private OperatingSystem operatingSystemUnderTest;

  @BeforeEach
  void setUp() {
    operatingSystemUnderTest = new OperatingSystem("Windows 10", "21H2", "1195");
  }

  @Test
  void testToPrettyString() {
    assertEquals("Windows 10 21H2 (1195)", operatingSystemUnderTest.toPrettyString());
  }

  @Test
  void testToString() {
    assertEquals(
        "OperatingSystem(id=0, name=Windows 10, version=21H2, buildNumber=1195)",
        operatingSystemUnderTest.toString());
  }
}
