package events.impl;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.LogUtil;
import utils.ConfigUtil;
import commands.CommandManager;

public class MessageEventListener extends ListenerAdapter {
    private final String prefix;
    private final CommandManager commandManager;

    public MessageEventListener(ConfigUtil config, CommandManager commandManager) {
        this.prefix = config.getProperty("bot.prefix"); 
        this.commandManager = commandManager;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String content = event.getMessage().getContentRaw();

        if (content.startsWith(prefix)) {
            String[] parts = content.substring(prefix.length()).split("\\s+", 2);
            String commandName = parts[0].toLowerCase();
            LogUtil.debug("Text command received: " + commandName);

            if (commandManager.hasCommand(commandName)) {
                try {
                    commandManager.getCommand(commandName).execute(event);
                    LogUtil.debug("Executed text command: " + commandName);
                } catch (Exception e) {
                    LogUtil.error("Error executing text command: " + commandName, e);
                    event.getChannel().sendMessage("An error occurred while executing the command!").queue();
                }
            }
        }
    }
}