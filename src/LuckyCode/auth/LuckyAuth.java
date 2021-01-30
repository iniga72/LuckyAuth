package LuckyCode.auth;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LuckyAuth extends JavaPlugin implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player p = (Player) commandSender;
            if(p.hasPermission("luckyauth.reload")){
                p.sendMessage("хуй сбаки");
                return true;
            }
        }
        File file = new File(getDataFolder()
                + File.separator + "config.yml");
        if(!file.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        main.config = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("LuckyAuth").getDataFolder() + File.separator + "config.yml"));
        commandSender.sendMessage("§aReaload complited");
        return true;
    }
}
