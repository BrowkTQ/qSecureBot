package me.Browk.qSecureBot;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(!event.getAuthor().getId().equalsIgnoreCase("481862479560048640") && !event.getAuthor().getId().equalsIgnoreCase("144482844880666625")) {
            return;
        }
        if(!event.getMessage().getContentRaw().startsWith("%secure")) {
            return;
        }
        if(event.getMessage().getContentRaw().startsWith("%secure list")) {
            listCmd(event.getTextChannel(), event.getAuthor(), event.getJDA());
            return;
        } else if(event.getMessage().getContentRaw().startsWith("%secure add")) {
            String[] l = event.getMessage().getContentRaw().split(" ");
            if(l.length == 4) {
                if(event.getMessage().getMentionedUsers().size() != 1) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl() + "?size=256");
                    embed.setTitle("Eroare!");
                    embed.setDescription("» Nu ai mentionat un cont de Discord valid pentru a finaliza actiunea!");
                    embed.setFooter("play.minecraft-romania.ro | Browk_", event.getJDA().getSelfUser().getAvatarUrl());
                    embed.setColor(Color.RED);
                    event.getTextChannel().sendMessage(embed.build()).queue();
                    return;
                }
                addCmd(event.getTextChannel(), event.getAuthor(), event.getJDA(), l[2], event.getMessage().getMentionedUsers().get(0).getId());
                return;
            }
        } else if(event.getMessage().getContentRaw().startsWith("%secure remove")) {
            String[] l = event.getMessage().getContentRaw().split(" ");
            if(l.length == 3) {
                deleteCmd(event.getTextChannel(), event.getAuthor(), event.getJDA(), l[2]);
                return;
            }
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl() + "?size=256");
            embed.setTitle("Eroare!");
            embed.setDescription("» Comanda nu a fost gasita! Lista de comenzi:\n  -> %secure list\n  -> %secure remove <nume>\n  -> %secure add <numeMc> <mentionDiscord>");
            embed.setFooter("play.minecraft-romania.ro | Browk_", event.getJDA().getSelfUser().getAvatarUrl());
            embed.setColor(Color.RED);
            event.getTextChannel().sendMessage(embed.build()).queue();
            return;
        }
    }

    private void listCmd(MessageChannel channel, User user, JDA jda) {
        String list = "";
        String list2 = "";
        try {
            PreparedStatement preparedStatement = Main.mysql.getConnection().prepareStatement(Main.mysql.SELECT);
            ResultSet localResultSet = preparedStatement.executeQuery();
            while(localResultSet.next()) {
                list += "\n" + localResultSet.getString(1) + "\n";
                list2 += "\n<@" + localResultSet.getString(4) + ">\n";
            }
            localResultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(user.getName(), null, user.getAvatarUrl() + "?size=256");
            embed.setTitle("Eroare!");
            embed.setDescription("» A avut loc o eroare in timpul conectarii la baza de date!");
            embed.setFooter("play.minecraft-romania.ro | Browk_", jda.getSelfUser().getAvatarUrl());
            embed.setColor(Color.RED);
            channel.sendMessage(embed.build()).queue();
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(user.getName(), null, user.getAvatarUrl() + "?size=256");
        embed.setTitle("Executat!");
        if(list.equalsIgnoreCase("")) {
            list = "\nnimeni";
            embed.setDescription("» Lista persoanelor care au activat confirmarea prin discord:`" + list + "`");
        }
        embed.setDescription("» Lista persoanelor care au activat confirmarea prin discord:");
        embed.addField("» Minecraft Name:", "`" + list + "`", true);
        embed.addField("» Discord ID:", list2, true);
        embed.setFooter("play.minecraft-romania.ro | Browk_", jda.getSelfUser().getAvatarUrl());
        embed.setColor(Color.GREEN);
        channel.sendMessage(embed.build()).queue();
    }

    private void deleteCmd(MessageChannel channel, User user, JDA jda, String playername) {
        try {
            PreparedStatement preparedStatement = Main.mysql.getConnection().prepareStatement(Main.mysql.SELECT2);
            preparedStatement.setString(1, playername);
            ResultSet localResultSet = preparedStatement.executeQuery();
            if(localResultSet.next()) {
                PreparedStatement preparedStatement2 = Main.mysql.getConnection().prepareStatement(Main.mysql.DELETE);
                preparedStatement2.setString(1, playername);
                preparedStatement2.execute();
                preparedStatement2.close();
            } else {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor(user.getName(), null, user.getAvatarUrl() + "?size=256");
                embed.setTitle("Eroare!");
                embed.setDescription("» Nu exista niciun jucator cu numele `" + playername + "` in baza de date!");
                embed.setFooter("play.minecraft-romania.ro | Browk_", jda.getSelfUser().getAvatarUrl());
                embed.setColor(Color.RED);
                channel.sendMessage(embed.build()).queue();
                localResultSet.close();
                preparedStatement.close();
                return;
            }
            localResultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(user.getName(), null, user.getAvatarUrl() + "?size=256");
            embed.setTitle("Eroare!");
            embed.setDescription("» A avut loc o eroare in timpul conectarii la baza de date!");
            embed.setFooter("play.minecraft-romania.ro | Browk_", jda.getSelfUser().getAvatarUrl());
            embed.setColor(Color.RED);
            channel.sendMessage(embed.build()).queue();
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(user.getName(), null, user.getAvatarUrl() + "?size=256");
        embed.setTitle("Executat!");
        embed.setDescription("» A fost sters utilizatorul `" + playername + "`!");
        embed.setFooter("play.minecraft-romania.ro | Browk_", jda.getSelfUser().getAvatarUrl());
        embed.setColor(Color.GREEN);
        channel.sendMessage(embed.build()).queue();
    }

    private void addCmd(MessageChannel channel, User user, JDA jda, String playername, String discordID) {
        if (jda.getGuildById(369226969583321088L).getMemberById(discordID) == null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(user.getName(), null, user.getAvatarUrl() + "?size=256");
            embed.setTitle("Eroare!");
            embed.setDescription("» Nu exista un cont de Discord in aceasta conferinta, care sa aiba ID-ul specificat!");
            embed.setFooter("play.minecraft-romania.ro | Browk_", jda.getSelfUser().getAvatarUrl());
            embed.setColor(Color.RED);
            channel.sendMessage(embed.build()).queue();
        }
        try {
            PreparedStatement preparedStatement = Main.mysql.getConnection().prepareStatement(Main.mysql.SELECT2);
            preparedStatement.setString(1, playername);
            ResultSet localResultSet = preparedStatement.executeQuery();
            if(!localResultSet.next()) {
                PreparedStatement preparedStatement2 = Main.mysql.getConnection().prepareStatement(Main.mysql.INSERT);
                preparedStatement2.setString(1, playername);
                preparedStatement2.setString(2, "neconectat");
                preparedStatement2.setString(3, discordID);
                preparedStatement2.execute();
                preparedStatement2.close();
            } else {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor(user.getName(), null, user.getAvatarUrl() + "?size=256");
                embed.setTitle("Eroare!");
                embed.setDescription("» Exista deja un jucator cu numele `" + playername + "` in baza de date!");
                embed.setFooter("play.minecraft-romania.ro | Browk_", jda.getSelfUser().getAvatarUrl());
                embed.setColor(Color.RED);
                channel.sendMessage(embed.build()).queue();
                localResultSet.close();
                preparedStatement.close();
                return;
            }
            localResultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(user.getName(), null, user.getAvatarUrl() + "?size=256");
            embed.setTitle("Eroare!");
            embed.setDescription("» A avut loc o eroare in timpul conectarii la baza de date!");
            embed.setFooter("play.minecraft-romania.ro | Browk_", jda.getSelfUser().getAvatarUrl());
            embed.setColor(Color.RED);
            channel.sendMessage(embed.build()).queue();
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(user.getName(), null, user.getAvatarUrl() + "?size=256");
        embed.setTitle("Executat!");
        embed.setDescription("» A fost adaugat utilizatorul `" + playername + "` cu Discord user-ul <@" + discordID + ">!");
        embed.setFooter("play.minecraft-romania.ro | Browk_", jda.getSelfUser().getAvatarUrl());
        embed.setColor(Color.GREEN);
        channel.sendMessage(embed.build()).queue();
    }

}
