package commands.impl;

import commands.interfaces.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import commands.CommandManager;
import java.awt.Color;
import java.util.Map;

public class HelpCommand implements ICommand {
    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows information about available commands";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = createHelpEmbed();
        event.replyEmbeds(embed.build()).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        EmbedBuilder embed = createHelpEmbed();
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private EmbedBuilder createHelpEmbed() {
        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("📚 Available Commands")
            .setColor(Color.BLUE)
            .setDescription("Here are all the available commands:");

        Map<String, ICommand> commands = commandManager.getCommands();
        for (ICommand command : commands.values()) {
            embed.addField("!" + command.getName(), 
                         command.getDescription(), 
                         false);
        }

        return embed;
    }
}