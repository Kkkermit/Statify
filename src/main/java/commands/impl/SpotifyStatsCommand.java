package commands.impl;

import commands.interfaces.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.SpotifyUtil;
import utils.LogUtil;

import java.awt.Color;
import java.util.Map;

public class SpotifyStatsCommand implements ICommand {
    private final SpotifyUtil spotifyUtil;

    public SpotifyStatsCommand(SpotifyUtil spotifyUtil) {
        this.spotifyUtil = spotifyUtil;
    }

    @Override
    public String getName() {
        return "spotify";
    }

    @Override
    public String getDescription() {
        return "View your Spotify statistics";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        try {
            Map<String, Object> stats = spotifyUtil.getUserStats(userId);
            
            if (stats.isEmpty()) {
                String authUrl = spotifyUtil.getAuthorizationUrl(userId);
                event.reply("Please authorize the bot to access your Spotify data: " + authUrl)
                     .setEphemeral(true)
                     .queue();
                return;
            }

            EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Spotify Statistics for " + stats.get("displayName"))
                .setColor(Color.GREEN)
                .addField("Followers", String.valueOf(stats.get("followers")), true)
                .addField("Profile", (String) stats.get("spotifyUrl"), false)
                .setFooter("Stats provided by Spotify", "https://spotify.com/favicon.ico");

            event.replyEmbeds(embed.build()).queue();
            
        } catch (Exception e) {
            LogUtil.error("Error executing Spotify stats command", e);
            event.reply("An error occurred while fetching your Spotify stats!").setEphemeral(true).queue();
        }
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        String userId = event.getAuthor().getId();
        try {
            Map<String, Object> stats = spotifyUtil.getUserStats(userId);
            
            if (stats.isEmpty()) {
                String authUrl = spotifyUtil.getAuthorizationUrl(userId);
                event.getChannel().sendMessage("Please authorize the bot to access your Spotify data: " + authUrl).queue();
                return;
            }

            EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Spotify Statistics for " + stats.get("displayName"))
                .setColor(Color.GREEN)
                .addField("Followers", String.valueOf(stats.get("followers")), true)
                .addField("Profile", (String) stats.get("spotifyUrl"), false)
                .setFooter("Stats provided by Spotify", "https://spotify.com/favicon.ico");

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            
        } catch (Exception e) {
            LogUtil.error("Error executing Spotify stats command", e);
            event.getChannel().sendMessage("An error occurred while fetching your Spotify stats!").queue();
        }
    }
}
