package org.otpr11.itassetmanagementapp.constants;

public enum DeviceType {
  LAPTOP,
  DESKTOP;

  public static DeviceType fromString (String value) {
    return switch (value) {
      case "LAPTOP" -> LAPTOP;
      case "DESKTOP" -> DESKTOP;
      default -> throw new IllegalStateException("Unknown device type %s".formatted(value));
    };
  }
}
