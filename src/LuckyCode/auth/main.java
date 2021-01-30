package LuckyCode.auth;

import com.sun.deploy.security.CertStore;
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
        File file = new File(getDataFolder()
                + File.separator + "config.yml");
        if(!file.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(fileconfig);
        file = new File(getDataFolder()
                + File.separator + "params.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        params = YamlConfiguration.loadConfiguration(fileparam);
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
        try {
            params.save(fileparam);
        }
        catch (IOException e2) {
        }
    }
    public static void setAdress(Player p){
        String ip = p.getAddress().getHostName().toString().replace("/", "");
        params.set("users." + p.getName().toLowerCase() + ".adress", ip);
        try {
            params.save(fileparam);
        }
        catch (IOException e2) {
        }
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
