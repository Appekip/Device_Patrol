package org.otpr11.itassetmanagementapp.db.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatusTest {

  private Status statusUnderTest;

  @BeforeEach
  void setUp() {
    statusUnderTest = new Status("status");
  }

  @Test
  void testToString() {
    assertEquals("status", statusUnderTest.toString());
  }
}
