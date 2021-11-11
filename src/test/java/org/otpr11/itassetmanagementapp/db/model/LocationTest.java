package org.otpr11.itassetmanagementapp.db.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocationTest {

  private Location locationUnderTest;

  @BeforeEach
  void setUp() {
    locationUnderTest = new Location("id", "nickname", "address", "zipCode");
  }

  @Test
  void testToString() {
    assertEquals(
        "Location(id=id, nickname=nickname, address=address, zipCode=zipCode)",
        locationUnderTest.toString());
  }
}
