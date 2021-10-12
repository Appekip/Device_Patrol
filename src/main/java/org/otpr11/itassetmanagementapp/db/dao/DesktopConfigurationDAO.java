package org.otpr11.itassetmanagementapp.db.dao;

import java.io.Serializable;
import java.util.List;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DAO;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.model.configuration.DesktopConfiguration;

/**
 * {@link org.otpr11.itassetmanagementapp.db.model.configuration.DesktopConfiguration} DAO
 * implementation.
 *
 * <p>Methods in this class are proxy methods for {@link GlobalDAO} with merely some added
 * operation-specific logging; the actual business logic resides in the aforementioned class.
 */
@Log4j2
public class DesktopConfigurationDAO extends DAO {
  private final GlobalDAO dao;

  protected DesktopConfigurationDAO(GlobalDAO globalDao) {
    super(globalDao);
    dao = globalDao;
  }

  @Override
  public DesktopConfiguration get(@NonNull Serializable id) {
    try {
      return dao.get(DesktopConfiguration.class, id);
    } catch (Exception e) {
      log.error("Could not get desktop configuration {}:", id, e);
      return null;
    }
  }

  @Override
  public List<DesktopConfiguration> getAll() {
    try {
      return dao.getAll(DesktopConfiguration.class);
    } catch (Exception e) {
      log.error("Could not get desktop configurations:", e);
      return null;
    }
  }

  @Override
  public <T extends DTO> boolean save(@NotNull T dto) {
    try {
      log.trace("Saving desktop configuration {}.", dto);
      dao.saveOrUpdate(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not save desktop configuration {}:", dto, e);
      return false;
    }
  }

  @Override
  public <T extends DTO> boolean delete(@NotNull T dto) {
    try {
      log.trace("Deleting desktop configuration {}.", dto);
      dao.delete(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not delete desktop configuration {}:", dto, e);
      return false;
    }
  }
}
