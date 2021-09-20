package org.otpr11.itassetmanagementapp.db.dao;

import java.io.Serializable;
import java.util.List;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DAO;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.model.OperatingSystem;

/**
 * {@link OperatingSystem} DAO implementation.
 *
 * <p>Methods in this class are proxy methods for {@link GlobalDAO} with merely some added
 * operation-specific logging; the actual business logic resides in the aforementioned class.
 */
@Log4j2
public class OperatingSystemDAO extends DAO {
  private final GlobalDAO dao;

  protected OperatingSystemDAO(GlobalDAO globalDao) {
    super(globalDao);
    dao = globalDao;
  }

  @Override
  public OperatingSystem get(@NonNull Serializable id) {
    try {
      return dao.get(OperatingSystem.class, id);
    } catch (Exception e) {
      log.error("Could not get operating system {}:", id, e);
      return null;
    }
  }

  @Override
  public List<OperatingSystem> getAll() {
    try {
      return dao.getAll(OperatingSystem.class);
    } catch (Exception e) {
      log.error("Could not get operating systems:", e);
      return null;
    }
  }

  @Override
  public <T extends DTO> boolean create(@NotNull T dto) {
    try {
      log.trace("Creating operating system {}.", dto);
      dao.saveOrUpdate(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not create operating system {}:", dto, e);
      return false;
    }
  }

  @Override
  public <T extends DTO> boolean update(@NotNull T dto) {
    try {
      log.trace("Updating operating system {}.", dto);
      dao.saveOrUpdate(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not update operating system {}:", dto, e);
      return false;
    }
  }

  @Override
  public <T extends DTO> boolean delete(@NotNull T dto) {
    try {
      log.trace("Deleting operating system {}.", dto);
      dao.delete(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not delete operating system {}:", dto, e);
      return false;
    }
  }
}
