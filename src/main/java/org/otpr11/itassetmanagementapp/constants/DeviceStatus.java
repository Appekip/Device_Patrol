package org.otpr11.itassetmanagementapp.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DeviceStatus {
  VACANT("Vacant"),
  IN_USE("In use"),
  BROKEN("Broken"),
  IN_REPAIR("In repair");

  @Getter
  private final String prettyName;

  public static DeviceStatus fromString (String value) {
    return switch (value) {
      case "VACANT" -> VACANT;
      case "IN_USE" -> IN_USE;
      case "BROKEN" -> BROKEN;
      case "IN_REPAIR" -> IN_REPAIR;
      default -> throw new IllegalStateException("Unknown device status %s".formatted(value));
    };
  }
}
