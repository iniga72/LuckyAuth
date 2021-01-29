package LuckyCode.auth;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class main extends JavaPlugin {
    public void onEnable() {
        File file = new File(getDataFolder()
                + File.separator + "config.yml");
        if(!file.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("LuckyAuth").getDataFolder() + File.separator + "config.yml"));
        Bukkit.getPluginManager().registerEvents(new events(), this);
        getCommand("register").setExecutor(new register());
    }
    public static FileConfiguration config;
}
