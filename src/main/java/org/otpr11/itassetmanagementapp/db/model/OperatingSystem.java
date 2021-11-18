package org.otpr11.itassetmanagementapp.db.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
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
import org.otpr11.itassetmanagementapp.interfaces.PrettyStringifiable;

/** Represents an operating system a {@link Device} is running. */
@Entity
@Table(name = "operating_systems")
@ToString
@EntityListeners({DatabaseEventPropagator.class})
@NoArgsConstructor
public class OperatingSystem extends DTO implements PrettyStringifiable {

  @Id
  @Getter
  @Column(nullable = false, name = "os_id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false)
  private String name;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false)
  private String version;

  @Getter
  @Setter
  @NotNull
  @Column(nullable = false)
  private String buildNumber;
  // ^ is a string because macOS likes to add random letters into build numbers (thanks Apple)

  // Left unused as it's only needed to connect the database tables together
  @ManyToMany @Exclude private List<Device> devices;

  public OperatingSystem(
      @NotNull String name, @NotNull String version, @NotNull String buildNumber) {
    this.name = name;
    this.version = version;
    this.buildNumber = buildNumber;
  }

  public String toPrettyString() {
    return "%s %s (%s)".formatted(name, version, buildNumber);
  }

  @PreRemove
  private void preRemove() {
    System.out.println(devices);

    val dao = GlobalDAO.getInstance();

    for (val device : devices) {
      val operatingSystems = device.getOperatingSystems();
      operatingSystems.remove(this);
      device.setOperatingSystems(operatingSystems);
    }
  }
}
