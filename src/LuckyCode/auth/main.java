package LuckyCode.auth;

import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
    public void onEnable() {
        getCommand("register").setExecutor(new register());
    }
}
