package org.otpr11.itassetmanagementapp.db.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LaptopConfigurationTest {

  private LaptopConfiguration laptopConfigurationUnderTest;

  @BeforeEach
  void setUp() {
    laptopConfigurationUnderTest =
        new LaptopConfiguration(
            "AMD Ryzen 9 5900HX", "NVIDIA GTX 1660 Ti", "16 GB 3200 MHz", "1 TB", 17);
  }

  @Test
  void testToPrettyString() {
    assertEquals(
        "17\", AMD Ryzen 9 5900HX, NVIDIA GTX 1660 Ti, 16 GB 3200 MHz RAM, 1 TB disk",
        laptopConfigurationUnderTest.toPrettyString());
  }

  @Test
  void testToString() {
    assertEquals(
        "LaptopConfiguration(id=0, cpu=AMD Ryzen 9 5900HX, gpu=NVIDIA GTX 1660 Ti, memory=16 GB 3200 MHz, diskSize=1 TB, screenSize=17)",
        laptopConfigurationUnderTest.toString());
  }
}
