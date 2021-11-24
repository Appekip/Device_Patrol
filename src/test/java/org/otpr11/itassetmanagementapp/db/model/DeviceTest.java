package org.otpr11.itassetmanagementapp.db.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.List;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class DeviceTest {

  @Mock private User mockUser;
  @Mock private LaptopConfiguration mockLaptopConfiguration;
  @Mock private Status mockStatus;
  @Mock private Location mockLocation;

  private Device deviceUnderTest;

  @BeforeEach
  void setUp() {
    openMocks(this);
    val mockConfiguration = new Configuration();
    mockConfiguration.setLaptopConfiguration(mockLaptopConfiguration);
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
        "Device(id=id, nickname=nickname, manufacturer=manufacturer, modelID=modelID, modelName=modelName, modelYear=modelYear, macAddress=macAddress, user=mockUser, configuration=Configuration(id=0, desktopConfiguration=null, laptopConfiguration=mockLaptopConfiguration, deviceType=LAPTOP), location=mockLocation, status=mockStatus, operatingSystems=[OperatingSystem(id=0, name=name, version=version, buildNumber=buildNumber)])",
        deviceUnderTest.toString());
  }
}
