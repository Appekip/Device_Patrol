package org.otpr11.itassetmanagementapp.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Arrays;
import lombok.Getter;
import lombok.val;
import org.otpr11.itassetmanagementapp.Main;

/** Configuration manager. Access to the config is provided via {@code Config.getConfig()}. */
public abstract class Config {
  @Getter private static Dotenv config;

  public static void load() {
    // Because we're using JavaFX, we need to adhere to the `resources` folder; hence
    // some path parsing magic is needed to figure out where the .env file is

    String configDir = "";
    val configPath = Main.class.getResource("config/.env");

    // If resource exists, parse the folder out; if it doesn't exist, dotenv will crash us out,
    // so no need to throw a strop about that
    if (configPath != null) {
      val parts = configPath.toString().split("/");
      val directory = Arrays.copyOfRange(parts, 0, parts.length - 1);
      configDir = String.join("/", directory);
    }

    // Load configuration
    config = Dotenv.configure().directory(configDir).load();
  }
}
