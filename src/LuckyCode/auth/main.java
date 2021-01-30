package LuckyCode.auth;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

public class main extends JavaPlugin {
    public void onEnable() {
        File file = new File(getDataFolder()
                + File.separator + "config.yml");
        if(!file.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("LuckyAuth").getDataFolder() + File.separator + "config.yml"));
        params = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("LuckyAuth").getDataFolder() + File.separator + "config.yml"));
        Bukkit.getPluginManager().registerEvents(new events(), this);
        getCommand("register").setExecutor(new register());
        getCommand("luckyauth").setExecutor(new LuckyAuth());
        getCommand("luckyauth").setExecutor(new changepassword());
        getCommand("luckyauth").setExecutor(new code());
        getCommand("luckyauth").setExecutor(new login());

        for (Player p : Bukkit.getOnlinePlayers()) {
            authorization(p);
        }
    }
    public static FileConfiguration config;
    public static FileConfiguration params;

    public static boolean isRegister(String player){
        player = player.toLowerCase();
        if(params.getString("users." + player) != null)return true;
        return false;
    }
    public static void setPassword(String player, String password){
        player = player.toLowerCase();
        params.set("users." + player + ".password", password);
    }
    public static boolean checkPassword(String player, String password){
        player = player.toLowerCase();
        if(params.getString("users." + player + ".password").equals(password))return true;
        return false;
    }
    public static String getAdress(String player){
        player = player.toLowerCase();
        return params.getString("users." + player + ".ip");
    }
    public static HashMap<Player, Boolean> autorize;
    public static HashMap<Player, String> code;

    public static void authorization(Player p){
        if(config.getBoolean("settengs.session")){
            String ip = p.getAddress().getHostName().toString().replace("/", "");
            //if(getAdress())
        }
    }


}
