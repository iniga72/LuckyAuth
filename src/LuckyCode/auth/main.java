package LuckyCode.auth;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class main extends JavaPlugin {
    public static File fileconfig = new File(Bukkit.getPluginManager().getPlugin("LuckyAuth").getDataFolder() + File.separator + "config.yml");
    public static File fileparam = new File(Bukkit.getPluginManager().getPlugin("LuckyAuth").getDataFolder() + File.separator + "params.yml");
    public void onEnable() {
        if(!fileconfig.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(fileconfig);
        fileparam = new File(getDataFolder()
                + File.separator + "params.yml");
        if(!fileparam.exists()) {
            try {
                fileparam.createNewFile();
            } catch (IOException ignored) {
            }
        }
        params = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("LuckyAuth").getDataFolder() + File.separator + "params.yml"));
        Bukkit.getPluginManager().registerEvents(new events(), this);
        getCommand("register").setExecutor(new register());
        getCommand("luckyauth").setExecutor(new LuckyAuth());
        getCommand("changepassword").setExecutor(new changepassword());
        getCommand("code").setExecutor(new code());
        getCommand("login").setExecutor(new login());
/*
        for (Player p : Bukkit.getOnlinePlayers()) {
            authorization(p);
        }*/
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
    public static void setAdress(Player p){
        String ip = p.getAddress().getHostName().toString().replace("/", "");
        params.set("users." + p.getName().toLowerCase() + ".adress", ip);
    }
    public static boolean checkPassword(String player, String password){
        player = player.toLowerCase();
        if(params.getString("users." + player + ".password").equals(password))return true;
        return false;
    }
    public static String getAdress(String player){
        player = player.toLowerCase();
        return params.getString("users." + player + ".adress");
    }
    public static HashMap<Player, Boolean> autorize;
    public static HashMap<Player, String> code;

    public static void authorization(Player p){
        if(config.getBoolean("settengs.session ")){
            String ip = p.getAddress().getHostName().toString().replace("/", "");
            if(getAdress(p.getName()).equalsIgnoreCase(ip)){

            }
        }
    }


}
