/**
 * Created by Arcombe on 2017-03-21.
 */

import java.sql.*;
import java.util.*;

/**
 * Database is a class that specifies the interface to the playerDatabase. Uses JDBC.
 */
public class Database {

    /**
     * The database connection.
     */
    private Connection conn;

    /**
     * Create the database interface object. Connection to the
     * database is performed later.
     */
    public Database() {
        conn = null;
    }

    /**
     * Open a connection to the database, using the specified name .
     */
    public boolean openConnection(String filename) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + filename);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Close the connection to the database.
     */
    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the connection to the database has been established
     * @return true if the connection has been established
     */
    public boolean isConnected() {
        return conn != null;
    }

    /**
     *  Hämtar en spelare från databasen.
     *  @return Spelaren ifall han finns i databasen annars null.
     */
    public Player getPlayer(String name) {
        Player found = null;
        try {
            String sql =
                    "select * " +
                            "from   Player " +
                            "where name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                found = new Player(rs);
            }
            return found;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // code to clone our Statement...
        }
        return found;
    }

    /**
     *  Hämtar top 10 spelare i databasen eller så många det finns.
     *  @return En lista med de 10 bästa spelarna eller så många det finns i databasen.
     */
    public List<Player> getTopTenPlayer() {
        List<Player> found = new LinkedList<>();
        try {
            String sql =
                    "select * " +
                            "from   Player " +
                            "order by gamesWon desc, gamesTied desc, gamesLost asc "+
                            "limit 10";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                found.add(new Player(rs));
            }
            return found;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // code to clone our Statement...
        }
        return found;
    }

    /**
     *  Uppdaterar en spelare från databasen.
     *  @return Hur många spelare som påvärkades i databasen.
     */
    public int updatePlayerStats(String name, String type){
        try {
            String sql;
            if (type.equals("gamesWon")){
                sql =
                "update Player " +
                        "set gamesWon = gamesWon + 1 " +
                        "where name = ?";
            } else if(type.equals("gamesLost")){
                sql =
                        "update Player " +
                                "set gamesLost = gamesLost + 1 " +
                                "where name = ?";
            } else {
                sql =
                        "update Player " +
                                "set gamesTied = gamesTied + 1 " +
                                "where name = ?";
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            int n =  ps.executeUpdate();
            return n;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // code to clone our Statement...
        }
        return 0;
    }

    /**
     *  Lägger till en ny spelare i databasen.
     *  @return Antalet spelare som lades till.
     */
    public int newPlayer(String name){
        try {
            String sql =
                    "insert into Player(name, gamesWon, gamesLost, gamesTied) " +
                            "values (?, 0, 0, 0)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            int n =  ps.executeUpdate();
            return n;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // code to clone our Statement...
        }
        return 0;
    }

    /**
     *  Lägger till ett nytt game i databasen.
     *  @return Antalet games som lades till.
     */
    public int newGame(String playerNameRed, String playerNameYellow){
        try {
            String sql =
                    "insert into GameHistory(playerNameRed, playerNameYellow, winner) " +
                            "values (?, ?, null)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, playerNameRed);
            ps.setString(2, playerNameYellow);
            int n =  ps.executeUpdate();
            return n;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // code to clone our Statement...
        }
        return 0;
    }

    public GameHistory getLatestGame(){
        GameHistory game = null;
        try {
            String sql =
                    "select * " +
                            "from   GameHistory " +
                            "order by gameID desc " +
                            "limit 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                game = new GameHistory(rs);
            }
            return game;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // code to clone our Statement...
        }
        return game;
    }

    public int updateGameResult(String result, int gameID){
        try {
            String sql = "update GameHistory " +
                            "set winner = ? " +
                            "where gameID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, result);
            ps.setInt(2,gameID);
            int n =  ps.executeUpdate();
            return n;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // code to clone our Statement...
        }
        return 0;
    }

    public List<GameHistory> getGameHistory(){
        List<GameHistory> found = new LinkedList<>();
        try {
            String sql =
                    "select * " +
                            "from   GameHistory " +
                            "order by gameID desc ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                found.add(new GameHistory(rs));
            }
            return found;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // code to clone our Statement...
        }
        return found;
    }

    public int addMove(int gameID, int moveID, int row, int column){
        try {
            String sql =
                    "insert into Move(gameID, moveID, row, column) " +
                            "values (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, gameID);
            ps.setInt(2, moveID);
            ps.setInt(3, row);
            ps.setInt(4, column);
            int n =  ps.executeUpdate();
            return n;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // code to clone our Statement...
        }
        return 0;
    }

    public List<Move> getMoves(int gameID){
        List<Move> found = new LinkedList<>();
        try {
            String sql =
                    "select * " +
                            "from   Move " +
                            "where gameID = ? " +
                            "order by moveID asc";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                found.add(new Move(rs));
            }
            return found;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // code to clone our Statement...
        }
        return found;
    }


}

class Player{

    public final String name;
    public int gamesWon;
    public int gamesLost;
    public int gamesTied;

    public Player(ResultSet rs) throws SQLException{
        this.name = rs.getString("name");
        this.gamesWon = rs.getInt("gamesWon");
        this.gamesLost = rs.getInt("gamesLost");
        this.gamesTied = rs.getInt("gamesTied");
    }
}

class GameHistory{

    public final int gameID;
    public final String playerNameRed;
    public final String playerNameYellow;
    public final String winner;

    public GameHistory(ResultSet rs) throws  SQLException{
        this.gameID = rs.getInt("gameID");
        this.playerNameRed = rs.getString("playerNameRed");
        this.playerNameYellow = rs.getString("playerNameYellow");
        this.winner = rs.getString("winner");
    }
}

class Move{

    public final int gameID;
    public final int moveID;
    public final int row;
    public final int column;


    public Move(ResultSet rs) throws SQLException{
        this.gameID = rs.getInt("gameID");
        this.moveID = rs.getInt("moveID");
        this.row = rs.getInt("row");
        this.column = rs.getInt("column");
    }
}











