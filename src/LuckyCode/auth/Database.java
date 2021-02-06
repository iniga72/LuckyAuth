package LuckyCode.auth;

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
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE nick = '"+player+"';");
            rs = ps.executeQuery();
            String end = "";
            while(rs.next()){
                end = rs.getInt("id") + "";
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

    public void newPlayer(String realname, String password, String ip) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            String name = realname.toLowerCase();
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO "+table+" (id, nick, ip, realname, password, date, code, vk) VALUES (NULL, '"+name +"', '"+ip+"', '"+realname+"', '"+password+"', '"+System.currentTimeMillis() + "', NULL, NULL)"); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.

            ps.executeUpdate();
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

