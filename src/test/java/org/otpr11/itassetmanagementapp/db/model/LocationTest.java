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

class LocationTest {
  private final GlobalDAO dao = GlobalDAO.getInstance();
  private Location locationUnderTest;

  @BeforeAll
  static void beforeAll() {
    LogUtils.configureLogger();
  }

  @BeforeEach
  void setUp() {
    locationUnderTest = new Location("id", "nickname", "address", "zipCode");
    save();
  }

  @AfterEach
  void tearDown() {
    dao.locations.delete(locationUnderTest);
  }

  @Test
  void testToString() {
    assertEquals(
        "Location(id=id, nickname=nickname, address=address, zipCode=zipCode)",
        locationUnderTest.toString());
  }

  @Test
  void testGet() {
    assertEquals(locationUnderTest, get());
  }

  @Test
  void testGetAll() {
    assertThat(dao.locations.getAll(), hasItem(locationUnderTest));
  }

  @Test
  void testDelete() {
    dao.locations.delete(locationUnderTest);
    assertNull(get());
  }

  private Location get() {
    return dao.locations.get(locationUnderTest.getId());
  }

  private void save() {
    dao.locations.save(locationUnderTest);
  }
}
