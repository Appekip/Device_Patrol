package org.otpr11.itassetmanagementapp.db.dao;

import java.io.Serializable;
import java.util.List;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DAO;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.model.User;

/**
 * {@link User} DAO implementation.
 *
 * <p>Methods in this class are proxy methods for {@link GlobalDAO} with merely some added
 * operation-specific logging; the actual business logic resides in the aforementioned class.
 */
@Log4j2
public class UserDAO extends DAO {
  private final GlobalDAO dao;

  protected UserDAO(GlobalDAO globalDao) {
    super(globalDao);
    dao = globalDao;
  }

  @Override
  public User get(@NonNull Serializable id) {
    try {
      return dao.get(User.class, id);
    } catch (Exception e) {
      log.error("Could not get user {}:", id, e);
      return null;
    }
  }

  @Override
  public List<User> getAll() {
    try {
      return dao.getAll(User.class);
    } catch (Exception e) {
      log.error("Could not get users:", e);
      return null;
    }
  }

  @Override
  public <T extends DTO> boolean save(@NotNull T dto) {
    try {
      log.trace("Saving user {}.", dto);
      dao.saveOrUpdate(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not save user {}:", dto, e);
      return false;
    }
  }

  @Override
  public <T extends DTO> boolean delete(@NotNull T dto) {
    try {
      log.trace("Deleting user {}.", dto);
      dao.delete(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not delete user {}:", dto, e);
      return false;
    }
  }
}
