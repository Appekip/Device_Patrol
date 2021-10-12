package org.otpr11.itassetmanagementapp.utils;

import java.util.ArrayList;
import lombok.val;
import org.otpr11.itassetmanagementapp.constants.DeviceStatus;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.db.model.Location;
import org.otpr11.itassetmanagementapp.db.model.OperatingSystem;
import org.otpr11.itassetmanagementapp.db.model.Status;
import org.otpr11.itassetmanagementapp.db.model.User;
import org.otpr11.itassetmanagementapp.db.model.configuration.DesktopConfiguration;
import org.otpr11.itassetmanagementapp.db.model.configuration.LaptopConfiguration;

public abstract class DevUtils {
  private static final GlobalDAO dao = GlobalDAO.getInstance();

  public static void generateTestData() {
    // Create demo desktops
    val desktopCfg =
        dao.configurations.createDesktop(
            new DesktopConfiguration(
                "AMD Ryzen 9 3900X", "NVIDIA Tesla V100", "64 GB 3200 MHz", "8 TB"));

    // Create demo laptops
    val laptopCfg =
        dao.configurations.createLaptop(
            new LaptopConfiguration(
                "Intel Core i7-1185G7", "NVIDIA GTX 1060 Ti", "32 GB 3200 MHz", "2 TB", 17));

    val os1 = new OperatingSystem("Windows", "10", "19043.1266");
    val os2 = new OperatingSystem("Ubuntu Linux", "20.10", "kernel 5.1.4");
    dao.operatingSystems.save(os1);
    dao.operatingSystems.save(os2);
    val osList = new ArrayList<OperatingSystem>();
    osList.add(os1);
    osList.add(os2);

    for (int i = 0; i < 10; i++) {
      val user = new User("john" + i, "John", "Doe", "+35844123456", "john.doe@company.com");
      dao.users.save(user);

      val status = new Status(DeviceStatus.VACANT.toString());
      val loc = new Location("office" + i, "office" + i, "Yliopistonkatu 4, Helsinki", "00100");
      dao.locations.save(loc);
      val device =
          new Device(
              "dev" + i,
              "device-" + i,
              "Dell",
              "MXCWV",
              "XPS 17",
              "2021",
              "ff:ff:ff:ff:ff:ff",
              user,
              i % 2 == 0 ? laptopCfg : desktopCfg, // Randomise
              status,
              loc,
              osList);
      dao.devices.save(device);
    }
  }
}
