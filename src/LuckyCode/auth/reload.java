package LuckyCode.auth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class reload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length != 1) return true;
        if(args[0].equalsIgnoreCase("reload")){
            if ((sender instanceof Player)){
                Player p = (Player) sender;
                if(!p.hasPermission("luckyauth.reload")){
                    p.sendMessage(main.config.getString("messages.permission").replace("&","§"));
                    return true;
                }
            }
            main.load();
            sender.sendMessage("§aReload complited");

            return true;
        }
        if (!(sender instanceof Player))return true;

        Player p = (Player) sender;
        if(args[0].equals("accept")){
            if(main.connect.containsKey(p)){
                String id = main.connect.get(p);
                String msg = main.config.getString("vk.connect.accept");
                main.api.sendMessage(msg, id);
                p.sendMessage(main.config.getString("messages.vk.accept").replace("&", "§"));
                main.connect.remove(p);
                main.db.setVK(sender.getName(), id);
            }else{
                p.sendMessage(main.config.getString("messages.vk.empty").replace("&", "§"));
            }
            return true;
        }
        if(args[0].equals("decline")){
            if(main.connect.containsKey(p)){
                String id = main.connect.get(p);
                String msg = main.config.getString("vk.connect.decline");
                main.api.sendMessage(msg, id);
                p.sendMessage(main.config.getString("messages.vk.decline").replace("&", "§"));
                main.connect.remove(p);
            }else{
                p.sendMessage(main.config.getString("messages.vk.empty").replace("&", "§"));
            }
            return true;
        }
        //decline
        return true;
    }
}