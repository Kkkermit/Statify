package utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.awt.Color;
import java.time.Instant;

public class EmbedUtil {
    private static final Color SUCCESS_COLOR = Color.GREEN;
    private static final Color ERROR_COLOR = Color.RED;
    private static final Color INFO_COLOR = new Color(0, 149, 255);

    public static MessageEmbed createSuccessEmbed(String title, String description) {
        return new EmbedBuilder()
                .setColor(SUCCESS_COLOR)
                .setTitle(title)
                .setDescription(description)
                .setTimestamp(Instant.now())
                .build();
    }

    public static MessageEmbed createErrorEmbed(String title, String description) {
        return new EmbedBuilder()
                .setColor(ERROR_COLOR)
                .setTitle(title)
                .setDescription(description)
                .setTimestamp(Instant.now())
                .build();
    }

    public static MessageEmbed createInfoEmbed(String title, String description) {
        return new EmbedBuilder()
                .setColor(INFO_COLOR)
                .setTitle(title)
                .setDescription(description)
                .setTimestamp(Instant.now())
                .build();
    }

    public static EmbedBuilder createBaseEmbed() {
        return new EmbedBuilder()
                .setColor(INFO_COLOR)
                .setTimestamp(Instant.now());
    }

    public static MessageEmbed createHelpEmbed(String commandName, String description, String usage) {
        return new EmbedBuilder()
                .setColor(INFO_COLOR)
                .setTitle("Command: " + commandName)
                .addField("Description", description, false)
                .addField("Usage", usage, false)
                .setTimestamp(Instant.now())
                .build();
    }
}