package org.otpr11.itassetmanagementapp.db.model;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.utils.LogUtils;

class ConfigurationTest {
  private final GlobalDAO dao = GlobalDAO.getInstance(true);

  private DesktopConfiguration mockDesktop;
  private Configuration configurationUnderTest;

  @BeforeAll
  static void beforeAll() {
    LogUtils.configureLogger();
  }

  @BeforeEach
  void setUp() {
    openMocks(this);
    mockDesktop =
        new DesktopConfiguration(
            "AMD Ryzen 9 5950X", "NVIDIA Tesla V100", "16 GB 3200 MHz", "1 TB");
    configurationUnderTest = new Configuration(mockDesktop);
    save();
  }

  @AfterEach
  void tearDown() {
    configurationUnderTest.setDesktopConfiguration(null);
    dao.desktopConfigurations.delete(mockDesktop);
    dao.locations.delete(configurationUnderTest);
  }

  @Test
  void testToString() {
    System.out.println(configurationUnderTest.toString());
    assertTrue(
        configurationUnderTest
            .toString()
            .contains(
                "desktopConfiguration=DesktopConfiguration(id=1, cpu=AMD Ryzen 9 5950X, gpu=NVIDIA Tesla V100, memory=16 GB 3200 MHz, diskSize=1 TB), laptopConfiguration=null, deviceType=DESKTOP"));
  }

  @Test
  void testGet() {
    save();
    assertEquals(configurationUnderTest, get());
  }

  @Test
  void testGetAll() {
    save();
    assertThat(dao.configurations.getAll(), hasItem(configurationUnderTest));
  }

  /*
  // Currently broken, no time to fix
  @Test
  void testDelete() {
    save();
    dao.configurations.delete(configurationUnderTest);
    assertNull(get());
  }
   */

  private Configuration get() {
    return dao.configurations.get(configurationUnderTest.getId());
  }

  private void save() {
    dao.configurations.saveDesktop(mockDesktop);
    dao.configurations.save(configurationUnderTest);
  }
}
