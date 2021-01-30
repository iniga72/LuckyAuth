package LuckyCode.auth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        return true;
    }
}