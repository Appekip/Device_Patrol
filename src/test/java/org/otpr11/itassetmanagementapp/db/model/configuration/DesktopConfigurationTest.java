package org.otpr11.itassetmanagementapp.db.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DesktopConfigurationTest {

  private DesktopConfiguration desktopConfigurationUnderTest;

  @BeforeEach
  void setUp() {
    desktopConfigurationUnderTest =
        new DesktopConfiguration(
            "AMD Ryzen 9 5950X", "NVIDIA Tesla V100", "16 GB 3200 MHz", "1 TB");
  }

  @Test
  void testToPrettyString() {
    assertEquals(
        "AMD Ryzen 9 5950X, NVIDIA Tesla V100, 16 GB 3200 MHz RAM, 1 TB disk",
        desktopConfigurationUnderTest.toPrettyString());
  }

  @Test
  void testToString() {
    assertEquals(
        "DesktopConfiguration(id=0, cpu=AMD Ryzen 9 5950X, gpu=NVIDIA Tesla V100, memory=16 GB 3200 MHz, diskSize=1 TB)",
        desktopConfigurationUnderTest.toString());
  }
}
