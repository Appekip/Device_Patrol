package org.otpr11.itassetmanagementapp.db.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

  private User userUnderTest;

  @BeforeEach
  void setUp() {
    userUnderTest = new User("id", "firstName", "lastName", "phone", "email");
  }

  @Test
  void testToString() {
    assertEquals(
        "User(id=id, firstName=firstName, lastName=lastName, phone=phone, email=email)",
        userUnderTest.toString());
  }
}
