package me.Browk.qSecureBot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;

public class Main {

    static String token = "NDk1Njk1ODgxOTcxMTcxMzM4.DqDULg.mMhpDx1jT04Cit84tc9BwUSBpfM";
    public static Main instance;
    public static MySQL mysql;

    public static Main getInstance() {
        return instance;
    }

    public static void main(String[] Args) {
        JDABuilder builder = new JDABuilder(AccountType.BOT);

        builder.setToken(token);
        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        builder.setGame(Game.streaming("play.mc-ro.ro", "www.minecraft-romania.ro/forum"));
        loadStartDatabase();
        addListeners(builder);

        try {
            JDA jda = builder.buildBlocking();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void addListeners(JDABuilder builder) {
        builder.addEventListener(new MessageListener());
    }

    public static void loadStartDatabase() {
        if (mysql != null) {
            mysql = null;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        System.out.println("Se realizeaza conexiunea la baza de date!");
        final long currentTimeMillis = System.currentTimeMillis();
        (mysql = new MySQL("qSecure", "5.9.143.111", "3306", "browksecurity1", "browksecurity1", "Hfhgj@#$tfghsajh1tyg")).setupTable();
        System.out.println("Conexiunea la baza de date a fost realizata cu succes! A durat " + (System.currentTimeMillis() - currentTimeMillis) + "ms!");
    }
}
