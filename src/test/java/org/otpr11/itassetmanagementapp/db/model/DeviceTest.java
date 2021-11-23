package org.otpr11.itassetmanagementapp.db.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class DeviceTest {

  @Mock private User mockUser;
  @Mock private Configuration mockConfiguration;
  @Mock private Status mockStatus;
  @Mock private Location mockLocation;

  private Device deviceUnderTest;

  @BeforeEach
  void setUp() {
    initMocks(this);
    deviceUnderTest =
        new Device(
            "id",
            "nickname",
            "manufacturer",
            "modelID",
            "modelName",
            "modelYear",
            "macAddress",
            mockUser,
            mockConfiguration,
            mockStatus,
            mockLocation,
            List.of(new OperatingSystem("name", "version", "buildNumber")));
  }

  @Test
  void testToString() {
    assertEquals(
        "Device(id=id, nickname=nickname, manufacturer=manufacturer, modelID=modelID, modelName=modelName, modelYear=modelYear, macAddress=macAddress, user=mockUser, configuration=mockConfiguration, status=mockStatus, location=mockLocation)",
        deviceUnderTest.toString());
  }
}
