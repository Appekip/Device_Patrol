package org.otpr11.itassetmanagementapp.db.model;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;

class UserTest {
  private final GlobalDAO dao = GlobalDAO.getInstance(true);
  private User userUnderTest;

  @BeforeEach
  void setUp() {
    userUnderTest = new User("id", "firstName", "lastName", "phone", "email");
    save();
  }

  @AfterEach
  void tearDown() {
    dao.operatingSystems.delete(userUnderTest);
  }

  @Test
  void testToString() {
    assertEquals(
        "User(id=id, firstName=firstName, lastName=lastName, phone=phone, email=email)",
        userUnderTest.toString());
  }

  @Test
  void testGet() {
    assertEquals(userUnderTest, get());
  }

  @Test
  void testGetAll() {
    assertThat(dao.users.getAll(), hasItem(userUnderTest));
  }

  @Test
  void testDelete() {
    dao.locations.delete(userUnderTest);
    assertNull(get());
  }

  private User get() {
    return dao.users.get(userUnderTest.getId());
  }

  private void save() {
    dao.users.save(userUnderTest);
  }
}
