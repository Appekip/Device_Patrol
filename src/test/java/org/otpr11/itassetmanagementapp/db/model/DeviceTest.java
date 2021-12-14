package org.otpr11.itassetmanagementapp.db.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.List;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.utils.LogUtils;

class DeviceTest {
  private final GlobalDAO dao = GlobalDAO.getInstance();
  @Mock private User mockUser;
  @Mock private LaptopConfiguration mockLaptopConfiguration;
  @Mock private Status mockStatus;
  @Mock private Location mockLocation;
  private Configuration mockConfiguration;
  private Device deviceUnderTest;

  @BeforeAll
  static void beforeAll() {
    LogUtils.configureLogger();
  }

  @BeforeEach
  void setUp() {
    openMocks(this);
    mockConfiguration = new Configuration();
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

  @SuppressWarnings({"ConstantConditions", "EqualsWithItself", "SimplifiableAssertion"})
  @Test
  void testEquals() {
    assertTrue(deviceUnderTest.equals(deviceUnderTest));
    assertFalse(deviceUnderTest.equals(null));
    val test =
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
    assertTrue(deviceUnderTest.equals(test));
  }
}
