package org.otpr11.itassetmanagementapp.db.model.configuration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.otpr11.itassetmanagementapp.constants.DeviceType;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.core.DatabaseEventPropagator;

/**
 * Represents the hardware configuration of a {@link
 * org.otpr11.itassetmanagementapp.db.model.Device}. The getDeviceType() function can be used to
 * determine the type of the device.
 */
@Entity
@Table(name = "configurations")
@ToString
@EntityListeners({DatabaseEventPropagator.class})
@NoArgsConstructor
public class Configuration extends DTO {
  @Id
  @Getter
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Getter
  @JoinColumn(name = "desktop_configuration_id")
  @ManyToOne
  private DesktopConfiguration desktopConfiguration;

  @Getter
  @JoinColumn(name = "laptop_configuration_id")
  @ManyToOne
  private LaptopConfiguration laptopConfiguration;

  @Getter
  @Column(name = "device_type")
  private DeviceType deviceType;

  /**
   * Sets this configuration up as a desktop. This also implicitly sets {@link
   * Configuration#deviceType} to {@link DeviceType#DESKTOP}.
   *
   * @param cfg {@link DesktopConfiguration}
   */
  public void setDesktopConfiguration(DesktopConfiguration cfg) {
    this.desktopConfiguration = cfg;
    this.deviceType = DeviceType.DESKTOP;
  }

  /**
   * Sets this configuration up as a laptop. This also implicitly sets {@link
   * Configuration#deviceType} to {@link DeviceType#LAPTOP}.
   *
   * @param cfg {@link LaptopConfiguration}
   */
  public void setLaptopConfiguration(LaptopConfiguration cfg) {
    this.laptopConfiguration = cfg;
    this.deviceType = DeviceType.LAPTOP;
  }

  /**
   * Creates a desktop configuration. (Proxies {@link
   * Configuration#setDesktopConfiguration(DesktopConfiguration)}.)
   *
   * @param cfg {@link DesktopConfiguration}
   */
  public Configuration(DesktopConfiguration cfg) {
    setDesktopConfiguration(cfg);
  }

  /**
   * Creates a laptop configuration. (Proxies {@link
   * Configuration#setLaptopConfiguration(LaptopConfiguration)}.)
   *
   * @param cfg {@link LaptopConfiguration}
   */
  public Configuration(LaptopConfiguration cfg) {
    setLaptopConfiguration(cfg);
  }
}
