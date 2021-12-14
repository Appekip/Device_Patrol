package org.otpr11.itassetmanagementapp.db.model;

import com.google.common.base.Objects;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.core.DatabaseEventPropagator;

/** Represents a device. */
@Entity
@Table(name = "devices")
@ToString
@EntityListeners({DatabaseEventPropagator.class})
@NoArgsConstructor
public class Device extends DTO {
  @Id
  @Getter
  @Setter
  @NotNull
  @Column(nullable = false, length = 64, name = "device_id")
  private String id;

  @Getter @Setter @Column @NotNull private String nickname;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false)
  private String manufacturer;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false, name = "model_id")
  private String modelID;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false, name = "model_name")
  private String modelName;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false, name = "model_year")
  private String modelYear;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false, name = "mac_address")
  private String macAddress;

  // Associations

  @Getter @ManyToOne private User user;
  @Getter @ManyToOne private Configuration configuration;
  @Getter @ManyToOne private Location location;
  @Getter @Setter @NotNull @ManyToOne private Status status;

  @Getter
  @NotNull
  @ManyToMany(
      cascade = {CascadeType.PERSIST},
      fetch = FetchType.EAGER)
  private List<OperatingSystem> operatingSystems;

  public Device(
      @NotNull String id,
      @NotNull String nickname,
      @NotNull String manufacturer,
      @NotNull String modelID,
      @NotNull String modelName,
      @NotNull String modelYear,
      @NotNull String macAddress,
      @NotNull User user,
      @NotNull Configuration configuration,
      @NotNull Status status,
      @NotNull Location location,
      @NotNull List<OperatingSystem> operatingSystems) {
    this.id = id;
    this.nickname = nickname;
    this.manufacturer = manufacturer;
    this.modelID = modelID;
    this.modelName = modelName;
    this.modelYear = modelYear;
    this.macAddress = macAddress;
    this.user = user;
    this.configuration = configuration;
    this.status = status;
    this.location = location;
    this.operatingSystems = operatingSystems;

    configuration.getDevices().add(this);

    switch (configuration.getDeviceType()) {
      case DESKTOP -> configuration.getDesktopConfiguration().setConfiguration(configuration);
      case LAPTOP -> configuration.getLaptopConfiguration().setConfiguration(configuration);
    }

    user.getDevices().add(this);
    location.getDevices().add(this);
    operatingSystems.forEach(os -> os.getDevices().add(this));
  }

  public void setConfiguration(Configuration cfg) {
    this.configuration = cfg;

    if (cfg != null) {
      configuration.getDevices().add(this);

      switch (configuration.getDeviceType()) {
        case DESKTOP -> configuration.getDesktopConfiguration().setConfiguration(configuration);
        case LAPTOP -> configuration.getLaptopConfiguration().setConfiguration(configuration);
      }
    }
  }

  public void setUser(User user) {
    this.user = user;

    if (user != null) {
      user.getDevices().add(this);
    }
  }

  public void setLocation(Location location) {
    this.location = location;

    if (location != null) {
      location.getDevices().add(this);
    }
  }

  public void setOperatingSystems(List<OperatingSystem> operatingSystems) {
    this.operatingSystems = operatingSystems;
    operatingSystems.forEach(os -> os.getDevices().add(this));
  }

  @PreRemove
  private void preRemove() {
    if (configuration != null) {
      configuration.getDevices().remove(this);
    }

    if (user != null) {
      user.getDevices().remove(this);
    }

    if (location != null) {
      location.getDevices().remove(this);
    }

    if (operatingSystems.size() > 0) {
      operatingSystems.forEach(os -> os.getDevices().remove(this));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Device device = (Device) o;
    return Objects.equal(nickname, device.nickname)
        && Objects.equal(manufacturer, device.manufacturer)
        && Objects.equal(modelID, device.modelID)
        && Objects.equal(modelName, device.modelName)
        && Objects.equal(modelYear, device.modelYear)
        && Objects.equal(macAddress, device.macAddress)
        && Objects.equal(user, device.user)
        && Objects.equal(configuration, device.configuration)
        && Objects.equal(location, device.location)
        && Objects.equal(status, device.status)
        && Objects.equal(operatingSystems, device.operatingSystems);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
