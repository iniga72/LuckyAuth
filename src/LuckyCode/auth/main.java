package LuckyCode.auth;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class main extends JavaPlugin implements Runnable{
    public static HashMap<Player, Integer> autorize;
    public static HashMap<Player, String> loc;
    public static HashMap<Player, String> connect;
    public static HashMap<Player, String> code;
    public static FileConfiguration config;
    public static FileConfiguration params;
    public static Database db;
    public static VkApi api;
    public VkApi getApi() {
        return api;

    }
    public main(){
        autorize = new HashMap<Player, Integer>();
        loc = new HashMap<Player, String>();
        connect = new HashMap<Player, String>();
    }
    public static void load(){
        autorize.clear();
        loc.clear();
        connect.clear();
        File file = new File(Bukkit.getPluginManager().getPlugin("LuckyAuth").getDataFolder() + File.separator + "config.yml");

        config = YamlConfiguration.loadConfiguration(file);
        for (Player p : Bukkit.getOnlinePlayers()) {
            authorization(p);
        }
    }

    public void onEnable() {
        File file = new File(Bukkit.getPluginManager().getPlugin("LuckyAuth").getDataFolder() + File.separator + "config.yml");
        if(!file.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }


        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this, 20L, 100L);
        db = new SQLite(this);
        db.load();
        load();
        Bukkit.getPluginManager().registerEvents(new events(), this);
        getCommand("register").setExecutor(new register());
        getCommand("luckyauth").setExecutor(new reload());
        getCommand("changepassword").setExecutor(new changepassword());
        getCommand("code").setExecutor(new code());
        getCommand("login").setExecutor(new login());

        api = new VkApi(this, 20, config.getString("settengs.group_token"), config.getString("settengs.group_id")){
            @Override
            protected void receiveMessage(ReceivedMessage message) throws NoSuchAlgorithmException {
                String peer = message.getPeer();
                if(message.getMsg().toLowerCase().startsWith(config.getString("vk.events.connect"))) connect(message, peer);
                if(message.getMsg().toLowerCase().startsWith(config.getString("vk.events.disconnect"))) disconnect(message, peer);
                if(message.getMsg().toLowerCase().startsWith(config.getString("vk.events.recover"))) recover(message, peer);
            }
        };
    }
    public void recover(VkApi.ReceivedMessage message, String id) throws NoSuchAlgorithmException {
        String name = db.getName(id);
        if(name == null){
            String msg = config.getString("vk.connect.haventconnect");
            api.sendMessage(msg, id);
            return;
        }
        String pass = RandomStringUtils.randomAlphabetic(15);
        Player p = Bukkit.getPlayer(name);
        main.db.setAdress(p.getName(), null);
        if(p != null){
            main.autorize.remove(p);
            main.loc.remove(p);
            main.connect.remove(p);
        }
        db.setPassword(name,pass);
        api.sendMessage(config.getString("vk.recover").replace("$pass", pass), id);
    }
    public void connect(VkApi.ReceivedMessage message, String id){
        if(message.getMsg().split(" ").length != 2){
            String help = config.getString("vk.connect.help");
            api.sendMessage(help,id);
            return;
        }
        access(message.getMsg().split(" ")[1], id);
    }
    public void disconnect(VkApi.ReceivedMessage message, String id){
        String name = db.getName(id);
        if(name == null){
            String msg = config.getString("vk.connect.haventconnect");
            api.sendMessage(msg, id);
            return;
        }
        Player p = Bukkit.getPlayer(name);
        if(p != null){
            for(String s : main.config.getStringList("messages.succesfull.disconnect+")) {
                s = s.replace("&","§");
                p.sendMessage(s);
            }
        }
        db.setVK(name, null);
        String msg = config.getString("vk.disconnect");
        api.sendMessage(msg, id);
    }

    public static boolean isRegister(String player){
        player = player.toLowerCase();
        String end = db.isRegister(player);
        if(end == null ||end == "")return false;
        return true;
    }
    public static void sendCode(String player, String vk){
        String code = RandomStringUtils.randomAlphabetic(7);
        api.sendMessage(config.getString("vk.send").replace("$code", code),vk);
        db.setCode(player, code);
    }
    public void access(String player, String id) {
        String name = db.getName(id);
        if(name != null){
            String msg = config.getString("vk.connect.haveconnect").replace("$player", name);
            api.sendMessage(msg, id);
            return;
        }
        Player p = Bukkit.getPlayer(player);
        if(p == null){
            String msg = config.getString("vk.online");
            api.sendMessage(msg, id);
            return;
        }
        if(main.autorize.containsKey(p) && main.autorize.get(p) ==1) {
            connect.put(p, id);
            String accept = config.getString("vk.access.accept").replace("&", "§");
            String decline = config.getString("vk.access.decline").replace("&", "§");
            String text = config.getString("vk.access.text");
            accept = " " + ",{\"text\":\" " + accept + "\",\"clickEvent\":"
                    + "{\"action\":\"run_command\",\"value\":\"/luckyauth accept" + "\"},"
                    + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                    + "{\"text\":\"\",\"extra\":[{\"text\":\" " + accept + "\",\"color\":\"gray\"}]}}}";
            decline = " " + ",{\"text\":\" " + decline + "\",\"clickEvent\":"
                    + "{\"action\":\"run_command\",\"value\":\"/luckyauth decline" + "\"},"
                    + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                    + "{\"text\":\"\",\"extra\":[{\"text\":\" " + decline + "\",\"color\":\"gray\"}]}}}";
            String CHAT_FORMAT = "[\"\",{\"text\":\"" + text + "\"}" + accept + decline + "]";
            p.sendMessage(new TextComponent(ComponentSerializer.parse(CHAT_FORMAT.replace("&", "§"))));
        }else{
            String msg = config.getString("vk.connect.auth");
            api.sendMessage(msg, id);
        }

    }
    public void run() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            if(main.autorize.containsKey(p)){
                if(autorize.get(p) == 0){
                    if(!main.isRegister(p.getName())){
                        for(String s : main.config.getStringList("notyfications.register")) {
                            s = s.replace("&","§");
                            p.sendMessage(s);
                        }
                    }else {
                        for(String s : main.config.getStringList("notyfications.login")) {
                            s = s.replace("&","§");
                            p.sendMessage(s);
                        }
                    }
                }else if(autorize.get(p) == 2){
                    for(String s : main.config.getStringList("notyfications.code")) {
                        s = s.replace("&","§");
                        p.sendMessage(s);
                    }
                }
            }else{
                authorization(p);
            }
        }
    }
    public static void authorization(Player p){
        if(main.isRegister(p.getName())){
            if(config.getBoolean("settengs.session")){
                String ip = p.getAddress().getHostName().replace("/", "");
                if(db.getAdress(p.getName()).equalsIgnoreCase(ip)) {
                    autorize.put(p, 1);
                    for(String s : main.config.getStringList("messages.succesfull.session")) {
                        s = s.replace("&","§");
                        p.sendMessage(s);
                    }
                }else{
                    autorize.put(p, 0);
                    String loc = p.getLocation().getBlockX() + "_" + p.getLocation().getBlockZ();
                    main.loc.put(p, loc);
                }
            }else {
                autorize.put(p, 0);
                String loc = p.getLocation().getBlockX() + "_" + p.getLocation().getBlockZ();
                main.loc.put(p, loc);
            }
        }else{
            main.autorize.put(p, 0);
        }

    }
}
