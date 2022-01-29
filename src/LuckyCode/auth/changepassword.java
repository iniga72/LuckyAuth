package LuckyCode.auth;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class changepassword implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            if (args.length == 2){
                //игроку проверка на права
                if(!main.isRegister(args[0])){
                    sender.sendMessage(main.config.getString("messages.playernotfound").replace("&", "§"));
                    return true;
                }
                int min = main.config.getInt("settengs.length.min");
                int max = main.config.getInt("settengs.length.max");
                if(args[1].length() < min){
                    sender.sendMessage(main.config.getString("messages.minpassword").replace("$count", min + "").replace("&", "§"));
                    return true;
                }
                if(args[1].length() > max){
                    sender.sendMessage(main.config.getString("messages.maxpassword").replace("$count", min + "").replace("&", "§"));
                    return true;
                }
                String vk = main.db.getVK(args[0]);
                if(vk != null){
                    main.api.sendMessage(main.config.getString("vk.newpassword").replace("$pass", args[1]), vk, "");
                }
                try {
                    main.db.setPassword(args[0], args[1]);
                    for(String s : main.config.getStringList("messages.succesfull.changepassword")) {
                        s = s.replace("&","§");
                        s = s.replace("$pass",args[1]);
                        sender.sendMessage(s);
                    }
                } catch (NoSuchAlgorithmException | IOException ignored) {

                }
                main.db.setAdress(args[0], null);
                Player p = Bukkit.getPlayer(args[0]);
                if(p != null){
                    main.autorize.remove(p);
                    main.loc.remove(p);
                    main.connect.remove(p);
                }
            }
            return true;
        }

        Player p = (Player) sender;
        if (args.length != 2){
            p.sendMessage(main.config.getString("help.changepassword").replace("&", "§"));
            return true;
        }
        String old = main.db.getPassword(p.getName());
        String pold = "";
        try {
            pold = main.db.ha(args[0]);
        } catch (NoSuchAlgorithmException ignored) {

        }
        if(old.equals(pold)){
            int min = main.config.getInt("settengs.length.min");
            int max = main.config.getInt("settengs.length.max");
            if(args[1].length() < min){
                p.sendMessage(main.config.getString("messages.minpassword").replace("$count", min + "").replace("&", "§"));
                return true;
            }
            if(args[1].length() > max){
                p.sendMessage(main.config.getString("messages.maxpassword").replace("$count", min + "").replace("&", "§"));
                return true;
            }
            String vk = main.db.getVK(p.getName());
            if(vk != null){
                main.api.sendMessage(main.config.getString("vk.newpassword").replace("$pass", args[1]), vk, "");
            }
            for(String s : main.config.getStringList("messages.succesfull.changepassword")) {
                s = s.replace("&","§");
                s = s.replace("$pass",args[1]);
                sender.sendMessage(s);
            }
            try {
                main.db.setPassword(p.getName(), args[1]);
            } catch (NoSuchAlgorithmException | IOException ignored) {

            }
        }else {
            if(p.hasPermission("luckyauth.admin")){
                if(!main.isRegister(args[0])){
                    sender.sendMessage(main.config.getString("messages.playernotfound").replace("&", "§"));
                    return true;
                }
                int min = main.config.getInt("settengs.length.min");
                int max = main.config.getInt("settengs.length.max");
                if(args[1].length() < min){
                    sender.sendMessage(main.config.getString("messages.minpassword").replace("$count", min + "").replace("&", "§"));
                    return true;
                }
                if(args[1].length() > max){
                    sender.sendMessage(main.config.getString("messages.maxpassword").replace("$count", min + "").replace("&", "§"));
                    return true;
                }
                String vk = main.db.getVK(args[0]);
                if(vk != null){
                    main.api.sendMessage(main.config.getString("vk.newpassword").replace("$pass", args[1]), vk, "");
                }
                try {
                    main.db.setPassword(args[0], args[1]);
                    for(String s : main.config.getStringList("messages.succesfull.changepassword")) {
                        s = s.replace("&","§");
                        s = s.replace("$pass",args[1]);
                        sender.sendMessage(s);
                    }
                } catch (NoSuchAlgorithmException | IOException ignored) {

                }
                main.db.setAdress(args[0], null);
                Player p2 = Bukkit.getPlayer(args[0]);
                if(p2 != null){
                    main.autorize.remove(p2);
                    main.loc.remove(p2);
                    main.connect.remove(p2);
                }

                return true;
            }
            p.sendMessage(main.config.getString("messages.oldwrongpasswrd").replace("&", "§"));
        }

        return true;
    }
}
