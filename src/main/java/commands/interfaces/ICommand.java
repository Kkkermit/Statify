package commands.interfaces;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ICommand {
    String getName();
    String getDescription();
    void execute(SlashCommandInteractionEvent event);
    void execute(MessageReceivedEvent event);
}