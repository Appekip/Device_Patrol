package org.otpr11.itassetmanagementapp.db.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.otpr11.itassetmanagementapp.db.model.Configuration;
import org.otpr11.itassetmanagementapp.db.model.DesktopConfiguration;

class ConfigurationTest {

  @Mock private DesktopConfiguration mockCfg;

  private Configuration configurationUnderTest;

  @BeforeEach
  void setUp() {
    initMocks(this);
    configurationUnderTest = new Configuration(mockCfg);
  }

  @Test
  void testToString() {
    assertEquals(
        "Configuration(id=0, desktopConfiguration=mockCfg, laptopConfiguration=null, deviceType=DESKTOP)",
        configurationUnderTest.toString());
  }
}
