package org.otpr11.itassetmanagementapp.db.dao;

import java.io.Serializable;
import java.util.List;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DAO;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.model.Location;

/**
 * {@link Location} DAO implementation.
 *
 * <p>Methods in this class are proxy methods for {@link GlobalDAO} with merely some added
 * operation-specific logging; the actual business logic resides in the aforementioned class.
 */
@Log4j2
public class LocationDAO extends DAO {
  private final GlobalDAO dao;

  protected LocationDAO(GlobalDAO globalDao) {
    super(globalDao);
    dao = globalDao;
  }

  @Override
  public Location get(@NonNull Serializable id) {
    try {
      return dao.get(Location.class, id);
    } catch (Exception e) {
      log.error("Could not get location {}:", id, e);
      return null;
    }
  }

  @Override
  public List<Location> getAll() {
    try {
      return dao.getAll(Location.class);
    } catch (Exception e) {
      log.error("Could not get locations:", e);
      return null;
    }
  }

  @Override
  public <T extends DTO> boolean create(@NotNull T dto) {
    try {
      log.trace("Creating location {}.", dto);
      dao.saveOrUpdate(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not create location {}:", dto, e);
      return false;
    }
  }

  @Override
  public <T extends DTO> boolean update(@NotNull T dto) {
    try {
      log.trace("Updating location {}.", dto);
      dao.saveOrUpdate(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not update location {}:", dto, e);
      return false;
    }
  }

  @Override
  public <T extends DTO> boolean delete(@NotNull T dto) {
    try {
      log.trace("Deleting location {}.", dto);
      dao.delete(dto);
      return true;
    } catch (Exception e) {
      log.error("Could not delete location {}:", dto, e);
      return false;
    }
  }
}
