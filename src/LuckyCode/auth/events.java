package LuckyCode.auth;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;

public class events implements Listener {
    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        main.authorization(p);
        String loc = p.getLocation().getBlockX() + "_" + p.getLocation().getBlockZ();
        main.loc.put(p, loc);
    }
    @EventHandler
    public void leave(PlayerQuitEvent e){
        main.autorize.remove(e.getPlayer());
        main.loc.remove(e.getPlayer());
        main.connect.remove(e.getPlayer());
    }

    @EventHandler
    public void cmd(PlayerCommandPreprocessEvent e){
        if(e.isCancelled())return;
        if(!main.config.getBoolean("settengs.blocked.cmd"))return;
        Player p = e.getPlayer();
        if(main.autorize.containsKey(p) && main.autorize.get(p) == 1)return;
            ArrayList<String> cmd = new ArrayList<>();
            cmd.add("/l");
            cmd.add("/login");
            cmd.add("/reg");
            cmd.add("/register");
            cmd.add("/code");
            if(!cmd.contains(e.getMessage().split(" ")[0].toLowerCase())){
                e.setCancelled(true);
                if(main.isRegister(p.getName())){
                    if(main.autorize.containsKey(p) && main.autorize.get(p) ==2){
                        for(String s : main.config.getStringList("notyfications.code")) {
                            s = s.replace("&","§");
                            p.sendMessage(s);
                        }
                    }
                    if(main.autorize.containsKey(p) && main.autorize.get(p) ==0){
                        for(String s : main.config.getStringList("notyfications.login")) {
                            s = s.replace("&","§");
                            p.sendMessage(s);
                        }
                    }

                }else{
                    for(String s : main.config.getStringList("notyfications.register")) {
                        s = s.replace("&","§");
                        p.sendMessage(s);
                    }
                }
            }

    }
    @EventHandler
    public void chat(AsyncPlayerChatEvent e){
        if(e.isCancelled())return;
        if(!main.config.getBoolean("settengs.blocked.chat"))return;
        Player p = e.getPlayer();
        if(main.autorize.containsKey(p) && main.autorize.get(p) ==1) return;
            e.setCancelled(true);
            if(main.isRegister(p.getName())){
                if(main.autorize.containsKey(p) && main.autorize.get(p) ==2){
                    for(String s : main.config.getStringList("notyfications.code")) {
                        s = s.replace("&","§");
                        p.sendMessage(s);
                    }
                }
                if(main.autorize.containsKey(p) && main.autorize.get(p) ==0){
                    for(String s : main.config.getStringList("notyfications.login")) {
                        s = s.replace("&","§");
                        p.sendMessage(s);
                    }
                }

            }else{
                for(String s : main.config.getStringList("notyfications.register")) {
                    s = s.replace("&","§");
                    p.sendMessage(s);
                }
            }

    }
    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent e){
        if(e.isCancelled())return;
        if(!main.config.getBoolean("settengs.blocked.blockbreak"))return;
        Player p = e.getPlayer();
        if(main.autorize.containsKey(p) && main.autorize.get(p) ==1) return;
        e.setCancelled(true);
    }
    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent e){
        if(e.isCancelled())return;
        if(!main.config.getBoolean("settengs.blocked.damage"))return;
        if(e.getDamager().getType().toString().equals("PLAYER")) {
            Player p = (Player) e.getDamager();
            if(main.autorize.containsKey(p) && main.autorize.get(p) ==1) return;
            e.setCancelled(true);
            return;
        }
        if(e.getEntity().getType().toString().equals("PLAYER")) {
            Player p = (Player) e.getEntity();
            if(main.autorize.containsKey(p) && main.autorize.get(p) ==1) return;
            e.setCancelled(true);
            return;
        }

    }
    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent e){
        if(e.isCancelled())return;
        if(!main.config.getBoolean("settengs.blocked.blockplace"))return;
        Player p = e.getPlayer();
        if(main.autorize.containsKey(p) && main.autorize.get(p) ==1) return;
        e.setCancelled(true);

    }
    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e){

        if(e.isCancelled())return;
        if(!main.config.getBoolean("settengs.blocked.move"))return;
        if(main.loc.containsKey(e.getPlayer()) && main.loc.get(e.getPlayer()).equals(e.getTo().getBlockX() + "_" + e.getTo().getBlockZ())) return;

        Player p = e.getPlayer();

        if(main.autorize.containsKey(p) && main.autorize.get(p) ==1) return;
        e.setCancelled(true);


    }
    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent e){
        if(e.isCancelled())return;
        if(!main.config.getBoolean("settengs.blocked.dropitem"))return;
        Player p = e.getPlayer();
        if(main.autorize.containsKey(p) && main.autorize.get(p) ==1) return;
        e.setCancelled(true);

    }
    @EventHandler
    public void PlayerPickupItemEvent(PlayerPickupItemEvent e){
        if(e.isCancelled())return;
        if(!main.config.getBoolean("settengs.blocked.pickup"))return;
        Player p = e.getPlayer();
        if(main.autorize.containsKey(p) && main.autorize.get(p) ==1) return;
        e.setCancelled(true);

    }
    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent e){
        if(e.isCancelled())return;
        if(!main.config.getBoolean("settengs.blocked.inventoryclick"))return;
        Player p = (Player) e.getWhoClicked();
        if(main.autorize.containsKey(p) && main.autorize.get(p) ==1) return;
        e.setCancelled(true);

    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent e){
        if(e.isCancelled())return;
        Player p = e.getPlayer();
        if(main.autorize.containsKey(p) && main.autorize.get(p) ==1) return;
        e.setCancelled(true);

    }


}
