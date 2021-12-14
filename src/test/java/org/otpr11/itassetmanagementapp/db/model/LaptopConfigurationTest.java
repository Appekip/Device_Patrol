package org.otpr11.itassetmanagementapp.db.model;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.utils.LogUtils;

class LaptopConfigurationTest {
  private final GlobalDAO dao = GlobalDAO.getInstance(true);
  private LaptopConfiguration laptopConfigurationUnderTest;

  @BeforeAll
  static void beforeAll() {
    LogUtils.configureLogger();
  }

  @BeforeEach
  void setUp() {
    laptopConfigurationUnderTest =
        new LaptopConfiguration(
            "AMD Ryzen 9 5900HX", "NVIDIA GTX 1660 Ti", "16 GB 3200 MHz", "1 TB", 17);
    save();
  }

  @AfterEach
  void tearDown() {
    dao.laptopConfigurations.delete(laptopConfigurationUnderTest);
  }

  @Test
  void testToPrettyString() {
    assertEquals(
        "17\", AMD Ryzen 9 5900HX, NVIDIA GTX 1660 Ti, 16 GB 3200 MHz RAM, 1 TB disk",
        laptopConfigurationUnderTest.toPrettyString());
  }

  @Test
  void testToString() {
    assertTrue(
        laptopConfigurationUnderTest
            .toString()
            .contains(
                "cpu=AMD Ryzen 9 5900HX, gpu=NVIDIA GTX 1660 Ti, memory=16 GB 3200 MHz, diskSize=1 TB, screenSize=17"));
  }

  @Test
  void testGet() {
    assertEquals(laptopConfigurationUnderTest, get());
  }

  @Test
  void testGetAll() {
    assertThat(dao.laptopConfigurations.getAll(), hasItem(laptopConfigurationUnderTest));
  }

  @Test
  void testDelete() {
    dao.locations.delete(laptopConfigurationUnderTest);
    assertNull(get());
  }

  private LaptopConfiguration get() {
    return dao.laptopConfigurations.get(laptopConfigurationUnderTest.getId());
  }

  private void save() {
    dao.laptopConfigurations.save(laptopConfigurationUnderTest);
  }
}
