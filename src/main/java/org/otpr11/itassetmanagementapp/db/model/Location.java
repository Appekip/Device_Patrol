package org.otpr11.itassetmanagementapp.db.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.core.DatabaseEventPropagator;

/** Represents the physical location of a {@link Device}. */
@Entity
@Table(name = "locations")
@ToString
@EntityListeners({DatabaseEventPropagator.class})
@AllArgsConstructor
@NoArgsConstructor
public class Location extends DTO {
  @Id
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

  @OneToMany @Exclude private List<Device> devices;

  @PreRemove
  private void preRemove() {
    System.out.println(devices);

    for (val device : devices) {
      device.setLocation(null);
    }
  }
}
