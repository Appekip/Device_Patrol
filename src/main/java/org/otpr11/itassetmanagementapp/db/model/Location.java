package org.otpr11.itassetmanagementapp.db.model;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.core.DatabaseEventPropagator;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;

/** Represents the physical location of a {@link Device}. */
@Entity
@Table(name = "locations")
@ToString
@EntityListeners({DatabaseEventPropagator.class})
@NoArgsConstructor
public class Location extends DTO {
  @Getter(AccessLevel.PACKAGE)
  @OneToMany(fetch = FetchType.EAGER)
  @Exclude
  private final List<Device> devices = new ArrayList<>();

  @Id
  @Setter
  @Getter
  @NotNull
  @Column(nullable = false, length = 64, name = "location_id")
  private String id;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false)
  private String nickname;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false)
  private String address;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false, name = "zip_code")
  private String zipCode; // Not a number in all locales

  public Location(
      @NotNull String id,
      @NotNull String nickname,
      @NotNull String address,
      @NotNull String zipCode) {
    this.id = id;
    this.nickname = nickname;
    this.address = address;
    this.zipCode = zipCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Location location = (Location) o;
    return Objects.equal(nickname, location.nickname)
        && Objects.equal(address, location.address)
        && Objects.equal(zipCode, location.zipCode);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @PreRemove
  private void preRemove() {
    val dao = GlobalDAO.getInstance();

    for (val device : devices) {
      device.setLocation(null);
      dao.devices.save(device);
    }
  }
}
