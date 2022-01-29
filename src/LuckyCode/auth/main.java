package LuckyCode.auth;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class main extends JavaPlugin implements Runnable{
    public static HashMap<Player, Integer> autorize;
    public static HashMap<Player, String> loc;
    public static HashMap<Player, String> connect;
    public static FileConfiguration config;
    public static Map <Player, Inventory> inventory;
    public static Database db;
    public static VkApi api;
    private Map<Integer, String> holders3;
    public VkApi getApi() {
        return api;

    }

    public main(){
        autorize = new HashMap<>();
        loc = new HashMap<>();
        connect = new HashMap<>();
        holders3 = new HashMap<>();
        inventory = new HashMap<>();
    }
    static  String pln = "";
    public static void load(){

        File file = new File(Bukkit.getPluginManager().getPlugin(pln).getDataFolder() + File.separator + "config.yml");
        config = YamlConfiguration.loadConfiguration(file);
        autorize.clear();
        loc.clear();
        connect.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            authorization(p);
        }
        //s
    }

    public void onEnable() {
        pln = getDescription().getName();
        File file = new File(Bukkit.getPluginManager().getPlugin(getDescription().getName()).getDataFolder() + File.separator + "config.yml");
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
        getCommand("vkauth").setExecutor(new reload());
        getCommand("changepassword").setExecutor(new changepassword());
        getCommand("code").setExecutor(new code());
        getCommand("login").setExecutor(new login());
            String id = config.getString("settengs.group_token");
            String token = config.getString("settengs.group_id");

            if(id == null ||token == null ||id.equalsIgnoreCase("group_token")||token.equalsIgnoreCase("id")) {
                Bukkit.getLogger().warning("Необходимо указать верный айди и токен группы");
                return;
            }

            api = new VkApi(this, 20, id, token){
                @Override
                protected void receiveMessage(ReceivedMessage message) throws NoSuchAlgorithmException, IOException {
                    String peer = message.getPeer();
                    if(message.getMsg().toLowerCase().startsWith(config.getString("vk.events.connect"))) connect(message, peer);
                    if(message.getMsg().toLowerCase().startsWith(config.getString("vk.events.disconnect"))) disconnect(message, peer);
                    if(message.getMsg().toLowerCase().equalsIgnoreCase(config.getString("vk.events.recover"))) recover(peer);
                    if(message.getMsg().toLowerCase().equalsIgnoreCase(config.getString("vk.keyboard.buttons.accept")))keyboardaccept(peer);
                    if(message.getMsg().toLowerCase().equalsIgnoreCase(config.getString("vk.keyboard.buttons.decline")))keyboarddecline(peer);
                }
            };


    }

    public void keyboarddecline(String id){
        String name = db.getName(id);
        if(name == null){
            String msg = config.getString("vk.connect.haventconnect");
            api.sendMessage(msg, id, "");
            return;
        }
        if(!main.config.getBoolean("settengs.keyboard")){
            api.sendMessage(config.getString("vk.haventkeyboard"), id, "");
            return;
        }
        Player p = Bukkit.getPlayer(name);
        if(p == null){
            api.sendMessage(config.getString("vk.haventaccept"), id, "");
            return;
        }
        if(autorize.containsKey(p) && autorize.get(p) == 2){
            Bukkit.getScheduler().runTask(this, new Runnable() {
                public void run() {
                    p.kickPlayer(config.getString("messages.wrongaccept").replace("&", "§"));
                }
            });
            api.sendMessage(config.getString("vk.badly"), id, "");
        }else {
            api.sendMessage(config.getString("vk.haventaccept"), id, "");
        }
    }
    public void keyboardaccept(String id){
        String name = db.getName(id);
        if(name == null){
            String msg = config.getString("vk.connect.haventconnect");
            api.sendMessage(msg, id, "");
            return;
        }
        if(!main.config.getBoolean("settengs.keyboard")){
            api.sendMessage(config.getString("vk.haventkeyboard"), id, "");
            return;
        }
        Player p = Bukkit.getPlayer(name);
        if(p == null){
            api.sendMessage(config.getString("vk.haventaccept"), id, "");
            return;
        }
        if(autorize.containsKey(p) && autorize.get(p) == 2){
            api.sendMessage(config.getString("vk.good"), id, "");
            autorize.put(p, 1);
            main.db.setAdress(p.getName(), p.getAddress().getAddress().getHostAddress().replace("/", ""));
            for(String s : main.config.getStringList("messages.succesfull.code")) {
                s = s.replace("&","§");
                p.sendMessage(s);
            }
        }else {
            api.sendMessage(config.getString("vk.haventaccept"), id, "");
        }
    }
    public void recover(String id) throws NoSuchAlgorithmException, IOException {
        String name = db.getName(id);
        if(name == null){
            String msg = config.getString("vk.connect.haventconnect");
            api.sendMessage(msg, id, "");
            return;
        }
        String pass = RandomStringUtils.randomAlphabetic(config.getInt("settengs.length.max"));
        Player p = Bukkit.getPlayer(name);
        main.db.setAdress(name, null);
        db.setPassword(name,pass);
        api.sendMessage(config.getString("vk.recover").replace("$pass", pass), id, "");
        if(p != null){
            main.autorize.remove(p);
            main.loc.remove(p);
            main.connect.remove(p);
        }

    }


    public void connect(VkApi.ReceivedMessage message, String id){
        if(message.getMsg().split(" ").length != 2){
            String help = config.getString("vk.connect.help");
            api.sendMessage(help,id, "");
            return;
        }
        access(message.getMsg().split(" ")[1], id, message.getUser().getFirstName() + " "+ message.getUser().getLastName());
    }
    public void disconnect(VkApi.ReceivedMessage message, String id){
        String name = db.getName(id);
        if(name == null){
            String msg = config.getString("vk.connect.haventconnect");
            api.sendMessage(msg, id, "");
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
        api.sendMessage(msg, id, "");
    }

    public static boolean isRegister(String player){
        player = player.toLowerCase();
        String end = db.isRegister(player);
        if(end == null ||end == "")return false;
        return true;
    }
    public static void sendCode(String player, String vk){
        String code = RandomStringUtils.randomAlphabetic(7);
        try {
            if(Bukkit.getPluginManager().getPlugin("ProtocolMeneger").isEnabled()){
                hmain.bukkitmain.key(player, code);
            }
        }catch (Exception ignored){

        }
        api.sendMessage(config.getString("vk.send").replace("$code", code),vk, "");
        db.setCode(player, code);
    }
    public static void sendkeyboard(String vk, String ip, String player){
        String msg = config.getString("vk.keyboard.message").replace("$ip", ip).replace("$name", player);
        String buttonAccept = config.getString("vk.keyboard.buttons.accept");
        String buttonDecline = config.getString("vk.keyboard.buttons.decline");
        String buttons = "{\"buttons\":[[{\"action\":{\"type\":\"text\",\"label\":\""+buttonAccept+"\",\"payload\":\"\"},\"color\":\"positive\"}],[{\"action\":{\"type\":\"text\",\"label\":\""+buttonDecline+"\",\"payload\":\"\"},\"color\":\"negative\"}]],\"inline\":true}";
        api.sendMessage(msg,vk, buttons);
    }
    public void access(String player, String id, String names) {
        String name = db.getName(id);
        if(name != null){
            String msg = config.getString("vk.connect.haveconnect").replace("$player", name);
            api.sendMessage(msg, id, "");
            return;
        }
        Player p = Bukkit.getPlayer(player);
        if(p == null){
            String msg = config.getString("vk.online");
            api.sendMessage(msg, id, "");
            return;
        }
        if(main.autorize.containsKey(p) && main.autorize.get(p) ==1) {
            String msg = config.getString("vk.connect.send");
            api.sendMessage(msg, id, "");
            connect.put(p, id);
            String accept = config.getString("vk.access.accept").replace("&", "§");
            String decline = config.getString("vk.access.decline").replace("&", "§");
            String text = config.getString("vk.access.text").replace("$name", names);
            accept = " " + ",{\"text\":\" " + accept + "\",\"clickEvent\":"
                    + "{\"action\":\"run_command\",\"value\":\"/luckyauth accept" + "\"},"
                    + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                    + "{\"text\":\"\",\"extra\":[{\"text\":\" " + accept + "\",\"color\":\"gray\"}]}}}";
            decline = " " + ",{\"text\":\" " + decline + "\",\"clickEvent\":"
                    + "{\"action\":\"run_command\",\"value\":\"/luckyauth decline" + "\"},"
                    + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                    + "{\"text\":\"\",\"extra\":[{\"text\":\" " + decline + "\",\"color\":\"gray\"}]}}}";
            String CHAT_FORMAT = "[\"\",{\"text\":\"" + text + "\"}" + accept + decline + "]";
            p.spigot().sendMessage(new TextComponent(ComponentSerializer.parse(CHAT_FORMAT.replace("&", "§"))));
        }else{
            String msg = config.getString("vk.connect.auth");
            api.sendMessage(msg, id, "");
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
                }
                else if(autorize.get(p) == 2){
                    if(main.config.getBoolean("settengs.keyboard")){
                        for(String s : main.config.getStringList("notyfications.accept")) {
                            s = s.replace("&","§");
                            p.sendMessage(s);
                        }
                    }else{
                        if(main.config.getBoolean("settengs.keyboard")){
                            for(String s : main.config.getStringList("notyfications.accept")) {
                                s = s.replace("&","§");
                                p.sendMessage(s);
                            }
                        }else {
                            for(String s : main.config.getStringList("notyfications.code")) {
                                s = s.replace("&","§");
                                p.sendMessage(s);
                            }
                        }

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
                String ip = p.getAddress().getAddress().getHostAddress().replace("/", "");
                if(db.getAdress(p.getName()) != null  && db.getAdress(p.getName()).equalsIgnoreCase(ip)) {
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
