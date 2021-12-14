package org.otpr11.itassetmanagementapp.db.model;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;

class OperatingSystemTest {
  private final GlobalDAO dao = GlobalDAO.getInstance(true);
  private OperatingSystem operatingSystemUnderTest;

  @BeforeEach
  void setUp() {
    operatingSystemUnderTest = new OperatingSystem("Windows 10", "21H2", "1195");
    save();
  }

  @AfterEach
  void tearDown() {
    dao.operatingSystems.delete(operatingSystemUnderTest);
  }

  @Test
  void testToPrettyString() {
    assertEquals("Windows 10 21H2 (1195)", operatingSystemUnderTest.toPrettyString());
  }

  @Test
  void testToString() {
    assertEquals(
        "OperatingSystem(id=1, name=Windows 10, version=21H2, buildNumber=1195)",
        operatingSystemUnderTest.toString());
  }

  @Test
  void testGet() {
    assertEquals(operatingSystemUnderTest, get());
  }

  @Test
  void testGetAll() {
    assertThat(dao.operatingSystems.getAll(), hasItem(operatingSystemUnderTest));
  }

  @Test
  void testDelete() {
    dao.locations.delete(operatingSystemUnderTest);
    assertNull(get());
  }

  private OperatingSystem get() {
    return dao.operatingSystems.get(operatingSystemUnderTest.getId());
  }

  private void save() {
    dao.operatingSystems.save(operatingSystemUnderTest);
  }
}
