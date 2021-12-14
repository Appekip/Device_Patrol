package org.otpr11.itassetmanagementapp.db.model;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.utils.LogUtils;

class DesktopConfigurationTest {
  private final GlobalDAO dao = GlobalDAO.getInstance(true);
  private DesktopConfiguration desktopConfigurationUnderTest;

  @BeforeAll
  static void beforeAll() {
    LogUtils.configureLogger();
  }

  @BeforeEach
  void setUp() {
    desktopConfigurationUnderTest =
        new DesktopConfiguration(
            "AMD Ryzen 9 5950X", "NVIDIA Tesla V100", "16 GB 3200 MHz", "1 TB");
    save();
  }

  @AfterEach
  void tearDown() {
    dao.desktopConfigurations.delete(desktopConfigurationUnderTest);
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
        "DesktopConfiguration(id=1, cpu=AMD Ryzen 9 5950X, gpu=NVIDIA Tesla V100, memory=16 GB 3200 MHz, diskSize=1 TB)",
        desktopConfigurationUnderTest.toString());
  }

  @Test
  void testGet() {
    assertEquals(desktopConfigurationUnderTest, get());
  }

  @Test
  void testGetAll() {
    assertThat(dao.desktopConfigurations.getAll(), hasItem(desktopConfigurationUnderTest));
  }

  @Test
  void testDelete() {
    dao.locations.delete(desktopConfigurationUnderTest);
    assertNull(get());
  }

  private DesktopConfiguration get() {
    return dao.desktopConfigurations.get(desktopConfigurationUnderTest.getId());
  }

  private void save() {
    dao.desktopConfigurations.save(desktopConfigurationUnderTest);
  }
}
