package LuckyCode.auth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class code implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length != 1) return true;
        if (!(sender instanceof Player))return true;
        Player p = (Player) sender;
        if(main.autorize.containsKey(p) && main.autorize.get(p) ==2){
            String code = main.db.getCode(p.getName());
            if(code.equals(args[0])){
                main.db.setCode(p.getName(), null);
                main.autorize.put(p, 1);
                main.db.setAdress(p.getName(), p.getAddress().getHostName().replace("/", ""));
                for(String s : main.config.getStringList("messages.succesfull.code")) {
                    s = s.replace("&","§");
                    sender.sendMessage(s);
                }
            }
        }
        return true;
    }
}
