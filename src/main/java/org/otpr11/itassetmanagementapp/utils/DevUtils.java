package org.otpr11.itassetmanagementapp.utils;

import java.util.ArrayList;
import lombok.val;
import org.otpr11.itassetmanagementapp.constants.DeviceStatus;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.DesktopConfiguration;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.db.model.LaptopConfiguration;
import org.otpr11.itassetmanagementapp.db.model.Location;
import org.otpr11.itassetmanagementapp.db.model.OperatingSystem;
import org.otpr11.itassetmanagementapp.db.model.Status;
import org.otpr11.itassetmanagementapp.db.model.User;

/** Generic development utilities. */
public abstract class DevUtils {
  private static final GlobalDAO dao = GlobalDAO.getInstance();

  /**
   * Generates test data into the database. Useful for development and debugging without having to
   * manually create devices.
   */
  public static void generateTestData() {
    // Create demo desktops
    val desktopCfg1 =
        dao.configurations.saveDesktop(
            new DesktopConfiguration(
                "AMD Ryzen 9 3900X", "NVIDIA Tesla V100", "64 GB 3200 MHz", "4 TB"));
    dao.configurations.saveDesktop(
        new DesktopConfiguration(
            "Intel i9-9900K", "AMD Radeon Pro 6900X", "32 GB 3200 MHz", "8 TB"));

    // Create demo laptops
    val laptopCfg1 =
        dao.configurations.saveLaptop(
            new LaptopConfiguration(
                "Intel Core i7-1185G7", "AMD Radeon Pro 5300M", "16 GB 3200 MHz", "2 TB", 16));
    dao.configurations.saveLaptop(
        new LaptopConfiguration(
            "AMD Ryzen 9 5900HX", "NVIDIA GTX 1060 Ti", "32 GB 3200 MHz", "4 TB", 17));

    val os1 = new OperatingSystem("Windows", "10", "19043.1266");
    val os2 = new OperatingSystem("Ubuntu Linux", "20.10", "kernel 5.1.4");
    val os3 = new OperatingSystem("macOS Big Sur", "11.5", "20G40");
    dao.operatingSystems.save(os1);
    dao.operatingSystems.save(os2);
    dao.operatingSystems.save(os3);
    val osList = new ArrayList<OperatingSystem>();
    osList.add(os1);
    osList.add(os2);

    val loc = new Location("office", "office", "Yliopistonkatu 4, Helsinki", "00100");
    dao.locations.save(loc);

    val user = new User("john", "John", "Doe", "+35844123456", "john.doe@company.com");
    dao.users.save(user);

    val status = new Status(DeviceStatus.IN_USE.toString());

    for (int i = 0; i < 10; i++) {
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
              i % 2 == 0 ? laptopCfg1 : desktopCfg1, // Randomise
              status,
              loc,
              osList);
      dao.devices.save(device);
    }
  }
}
