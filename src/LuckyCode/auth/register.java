package LuckyCode.auth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.NoSuchAlgorithmException;

public class register implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        if(main.isRegister(sender.getName())){
           sender.sendMessage(main.config.getString("messages.isregister").replace("&", "§"));
           return true;
        }

        Player p = (Player) sender;
        if(main.autorize.containsKey(p) && main.autorize.get(p) != 0){
            sender.sendMessage(main.config.getString("messages.autorize").replace("&", "§").replace("&", "§"));
            return true;
        }
        if (args.length != 2){
            sender.sendMessage(main.config.getString("help.register").replace("&", "§").replace("&", "§"));
            return  true;}
        String passone = args[0];
        String passtwo = args[1];
        if(!passone.equals(passtwo)){
            sender.sendMessage(main.config.getString("messages.noverify").replace("&", "§"));
            return true;
        }
        int min = main.config.getInt("settengs.length.min");
        int max = main.config.getInt("settengs.length.max");
        if(passone.length() < min){
            p.sendMessage(main.config.getString("messages.minpassword").replace("$count", min + ""));
            return true;
        }
        if(passone.length() > max){
            p.sendMessage(main.config.getString("messages.maxpassword").replace("$count", min + ""));
            return true;
        }
        for(String s : main.config.getStringList("messages.succesfull.register")) {
            s = s.replace("&","§");
            s = s.replace("$pass",args[0]);
            sender.sendMessage(s);
        }
        for(String s : main.config.getStringList("messages.reference")) {
            s = s.replace("$player",sender.getName());
            s = s.replace("&","§");
            sender.sendMessage(s);
        }
        try {
            main.db.newPlayer(p.getName() , passone, p.getAddress().getAddress().toString().replace("/", ""));
        } catch (NoSuchAlgorithmException ignored) {

        }
        main.autorize.put(p, 1);
        return true;
    }
}