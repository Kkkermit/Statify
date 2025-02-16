package commands.handlers;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import commands.interfaces.ICommand;
import utils.LogUtil;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler extends ListenerAdapter {
    private final Map<String, ICommand> commands;

    public CommandHandler() {
        this.commands = new HashMap<>();
    }

    public void registerCommand(ICommand command) {
        commands.put(command.getName(), command);
        LogUtil.debug("Registered command: " + command.getName());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        
        try {
            if (commands.containsKey(commandName)) {
                ICommand command = commands.get(commandName);
                command.execute(event);
                LogUtil.debug("Executed command: " + commandName);
            } else {
                event.reply("Unknown command!").setEphemeral(true).queue();
                LogUtil.warn("Unknown command attempted: " + commandName);
            }
        } catch (Exception e) {
            event.reply("An error occurred while executing the command!").setEphemeral(true).queue();
            LogUtil.error("Error executing command: " + commandName, e);
        }
    }

    public Map<String, ICommand> getCommands() {
        return commands;
    }
}