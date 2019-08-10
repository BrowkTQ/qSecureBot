package me.Browk.qSecureBot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    public Connection connection;
    public String url;
    public String username;
    public String password;
    public String TABLE;
    public String INSERT;
    public String SELECT;
    public String SELECT2;
    public String DELETE;

    public MySQL(final String tableprefix, final String host, final String port, final String database, final String username, final String password) {
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        this.username = username;
        this.password = password;
        this.TABLE = "CREATE TABLE IF NOT EXISTS " + tableprefix + "_sesiune (nume VARCHAR(255), statut_sesiune VARCHAR(255), pin VARCHAR(255), discordID VARCHAR(255))";
        this.INSERT = "INSERT INTO " + tableprefix + "_sesiune (nume, statut_sesiune, discordID) VALUES(?, ?, ?)";
        this.SELECT = "SELECT * FROM " + tableprefix + "_sesiune";
        this.SELECT2 = "SELECT discordID FROM " + tableprefix + "_sesiune WHERE nume=?";
        this.DELETE = "DELETE FROM " + tableprefix + "_sesiune WHERE nume=?";
    }

    public void close() {
        try {
            this.getConnection().close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void connect() {
        try {
            this.connection = DriverManager.getConnection(this.url, this.username, this.password);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.connection == null || !this.connection.isValid(5)) {
            this.connect();
        }
        return this.connection;
    }

    public void setupTable() {
        try {
            this.getConnection().createStatement().executeUpdate(this.TABLE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
