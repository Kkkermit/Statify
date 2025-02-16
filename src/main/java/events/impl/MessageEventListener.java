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
        // Ignore messages from bots
        if (event.getAuthor().isBot()) return;

        String content = event.getMessage().getContentRaw();

        // Check if message starts with prefix
        if (content.startsWith(prefix)) {
            String commandName = content.substring(prefix.length()).split(" ")[0];
            LogUtil.debug("Command received: " + commandName);

            if (commandManager.hasCommand(commandName)) {
                try {
                    commandManager.getCommand(commandName).execute(event);
                } catch (Exception e) {
                    LogUtil.error("Error executing command: " + commandName, e);
                    event.getChannel().sendMessage("An error occurred while executing the command!").queue();
                }
            }
        }
    }
}