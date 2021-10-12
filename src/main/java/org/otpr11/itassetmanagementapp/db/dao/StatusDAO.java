package org.otpr11.itassetmanagementapp.db.dao;

import java.io.Serializable;
import java.util.List;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DAO;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.model.Status;

/**
 * {@link org.otpr11.itassetmanagementapp.db.model.Status} DAO implementation.
 *
 * <p>Methods in this class are proxy methods for {@link GlobalDAO} with merely some added
 * operation-specific logging; the actual business logic resides in the aforementioned class.
 */
@Log4j2
public class StatusDAO extends DAO {
  private final GlobalDAO dao;

  protected StatusDAO(GlobalDAO globalDao) {
    super(globalDao);
    dao = globalDao;
  }

  @Override
  public Status get(@NonNull Serializable id) {
    try {
      return dao.get(Status.class, id);
    } catch (Exception e) {
      log.error("Could not get status {}:", id, e);
      return null;
    }
  }

  @Override
  public List<Status> getAll() {
    try {
      return dao.getAll(Status.class);
    } catch (Exception e) {
      log.error("Could not get statuss:", e);
      return null;
    }
  }

  @Override
  public <T extends DTO> boolean create(@NotNull T dto) {
    try {
      log.trace("Creating status {}.", dto);
      dao.saveOrUpdate(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not create status {}:", dto, e);
      return false;
    }
  }

  @Override
  public <T extends DTO> boolean update(@NotNull T dto) {
    try {
      log.trace("Updating status {}.", dto);
      dao.saveOrUpdate(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not update status {}:", dto, e);
      return false;
    }
  }

  @Override
  public <T extends DTO> boolean delete(@NotNull T dto) {
    try {
      log.trace("Deleting status {}.", dto);
      dao.delete(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not delete status {}:", dto, e);
      return false;
    }
  }
}
