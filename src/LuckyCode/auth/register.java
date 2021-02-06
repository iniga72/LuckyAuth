package LuckyCode.auth;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;

public class register implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Вы не являетесь игроком!");
            return true;
        }
        if(main.isRegister(sender.getName())){
           sender.sendMessage(main.config.getString("messages.isregister").replace("§", "&"));
           return true;
        }

        Player p = (Player) sender;
        if(main.autorize.containsKey(p) && main.autorize.get(p)){
            sender.sendMessage(main.config.getString("messages.autorize").replace("§", "&"));
            return true;
        }
        if (args.length != 2) return  true;
        String passone = args[0];
        String passtwo = args[1];
        if(!passone.equals(passtwo)){
            sender.sendMessage(main.config.getString("messages.noverify").replace("§", "&"));
            return true;
        }

        for(String s : main.config.getStringList("messages.succesfull")) {
            s = s.replace("&","§");
            sender.sendMessage(s);
        }
        main.setPassword(p.getName(), passone, p.getAddress().getAddress().toString().replace("/", ""));
        return true;
    }
}