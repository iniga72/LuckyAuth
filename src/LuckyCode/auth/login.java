package LuckyCode.auth;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.NoSuchAlgorithmException;

public class login  implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;
        if(!main.isRegister(sender.getName())){
            for(String s : main.config.getStringList("notyfications.register")) {
                s = s.replace("&","§");
                p.sendMessage(s);
            }
            return true;
        }
        if(main.autorize.containsKey(p) && main.autorize.get(p) == 0){
            if(args.length != 1){
                p.sendMessage(main.config.getString("help.login").replace("&", "§"));
                return true;
            }
            try {
                if(!main.db.ha(args[0]).equals(main.db.getPassword(p.getName()))){
                    if(main.config.getBoolean("settengs.wrongpasswrd")){
                        p.kickPlayer(main.config.getString("messages.wrongpasswrd").replace("&", "§"));
                        return true;
                    }
                    p.sendMessage(main.config.getString("messages.wrongpasswrd").replace("&", "§"));
                    return true;
                }
            } catch (NoSuchAlgorithmException ignored) {

            }
            String vk = main.db.getVK(p.getName());
            if(vk == null){
                main.autorize.put(p, 1);
                for(String s : main.config.getStringList("messages.succesfull.login")) {
                    s = s.replace("&","§");
                    sender.sendMessage(s);
                }
                main.db.setAdress(p.getName(), p.getAddress().getHostName().replace("/", ""));
            }else {
                main.autorize.put(p, 2);
                main.sendCode(p.getName(), vk);
                for(String s : main.config.getStringList("notyfications.code")) {
                    s = s.replace("&","§");
                    sender.sendMessage(s);
                }
            }
        }else {
                sender.sendMessage(main.config.getString("messages.autorize").replace("&", "§"));

        }
        return true;
    }
}
