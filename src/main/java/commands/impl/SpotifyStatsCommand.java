package commands.impl;

import commands.interfaces.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
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

            String botIconUrl = event.getJDA().getSelfUser().getAvatarUrl();
            
            if (stats.isEmpty()) {
                String authUrl = spotifyUtil.getAuthorizationUrl(userId);
            
                EmbedBuilder authEmbed = new EmbedBuilder()
                    .setTitle("Spotify Authorization Required")
                    .setDescription("Please click the button below to authorize this bot to access your Spotify data.")
                    .setColor(Color.GREEN)
                    .setFooter("Statsify Bot", botIconUrl);
            
                Button authButton = Button.link(authUrl, "Authorize Spotify");
                
                event.replyEmbeds(authEmbed.build())
                    .addActionRow(authButton)
                    .setEphemeral(true)
                    .queue();
                return;
            }

            EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Spotify Statistics for " + stats.get("displayName"))
                .setColor(Color.GREEN)
                .addField("Followers", String.valueOf(stats.get("followers")), true)
                .addField("Profile", (String) stats.get("spotifyUrl"), false)
                .setFooter("Stats provided by Spotify", botIconUrl);

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

            String botIconUrl = event.getJDA().getSelfUser().getAvatarUrl();
            
            if (stats.isEmpty()) {
                String authUrl = spotifyUtil.getAuthorizationUrl(userId);
    
                EmbedBuilder authEmbed = new EmbedBuilder()
                    .setTitle("Spotify Authorization Required")
                    .setDescription("Please click the button below to authorize this bot to access your Spotify data.")
                    .setColor(Color.GREEN)
                    .setFooter("Statsify Bot", botIconUrl);

                Button authButton = Button.link(authUrl, "Authorize Spotify");
                
                event.getChannel().sendMessageEmbeds(authEmbed.build())
                    .setActionRow(authButton)
                    .queue();
                return;
            }

            EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Spotify Statistics for " + stats.get("displayName"))
                .setColor(Color.GREEN)
                .addField("Followers", String.valueOf(stats.get("followers")), true)
                .addField("Profile", (String) stats.get("spotifyUrl"), false)
                .setFooter("Stats provided by Statsify", botIconUrl);

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            
        } catch (Exception e) {
            LogUtil.error("Error executing Spotify stats command", e);
            event.getChannel().sendMessage("An error occurred while fetching your Spotify stats!").queue();
        }
    }
}
