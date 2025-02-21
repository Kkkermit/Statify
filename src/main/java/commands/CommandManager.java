package commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import commands.impl.HelpCommand;
import commands.impl.PingCommand;
import commands.interfaces.ICommand;
import utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {
    private final Map<String, ICommand> commands;
    private final List<CommandData> commandData;

    public CommandManager() {
        this.commands = new HashMap<>();
        this.commandData = new ArrayList<>();
        registerDefaultCommands();
    }

    private void registerDefaultCommands() {
        registerCommand(new PingCommand());
        registerCommand(new HelpCommand(this));
        LogUtil.info("Default commands registered");
    }

    public void registerCommand(ICommand command) {
        commands.put(command.getName().toLowerCase(), command);
        commandData.add(Commands.slash(command.getName().toLowerCase(), command.getDescription()));
        LogUtil.debug("Registered command: " + command.getName());
    }

    public void registerCommands(JDA jda) {
        try {
            jda.updateCommands().addCommands(commandData).queue(
                success -> LogUtil.info("Successfully registered " + commands.size() + " slash commands"),
                error -> LogUtil.error("Failed to register slash commands", error)
            );
        } catch (Exception e) {
            LogUtil.error("Error registering commands", e);
        }
    }

    public ICommand getCommand(String name) {
        return commands.get(name);
    }

    public Map<String, ICommand> getCommands() {
        return commands;
    }

    public boolean hasCommand(String name) {
        return commands.containsKey(name);
    }

    public void removeCommand(String name) {
        if (commands.remove(name) != null) {
            LogUtil.debug("Removed command: " + name);
        }
    }

    public void clearCommands() {
        commands.clear();
        commandData.clear();
        LogUtil.info("All commands cleared");
    }
}