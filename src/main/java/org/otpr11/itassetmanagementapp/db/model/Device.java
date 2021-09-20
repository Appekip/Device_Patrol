package org.otpr11.itassetmanagementapp.db.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.model.configuration.Configuration;

/** Represents a device. */
@Entity
@Table(name = "runs")
@ToString()
@AllArgsConstructor
@NoArgsConstructor
public class Device extends DTO {
  @Id
  @Getter
  @Setter
  @NotNull
  @Column(nullable = false, length = 64, name = "device_id")
  private String id;

  @Getter @Setter @Column private String nickname;

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

  @Getter @Setter @ManyToOne private User user;
  @Getter @Setter @NotNull @ManyToOne private Configuration configuration;
  @Getter @Setter @NotNull @ManyToOne private Status status;
  @Getter @Setter @ManyToOne private Location location;
  // TODO: Leaving ^ nullable for now, but that might have to change if we want to mandate a loc

  @Getter
  @Setter
  @NotNull
  @ManyToMany(cascade = {CascadeType.ALL})
  @Exclude
  @JoinTable(
      name = "runs",
      joinColumns = {@JoinColumn(name = "device_id")},
      inverseJoinColumns = {@JoinColumn(name = "os_id")})
  private List<OperatingSystem> operatingSystems;
}
