package events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import events.impl.MessageEventListener;
import events.impl.ReadyEventListener;
import commands.CommandManager;
import commands.handlers.CommandHandler;
import events.handlers.EventHandler;
import utils.ConfigUtil;
import utils.LogUtil;

public class EventManager {
    private final MessageEventListener messageListener;
    private final ReadyEventListener readyListener;
    private final EventHandler eventHandler;
    private final CommandManager commandManager;
    private final CommandHandler commandHandler;

    public EventManager(ConfigUtil config, CommandManager commandManager) {
        this.messageListener = new MessageEventListener(config, commandManager);
        this.readyListener = new ReadyEventListener();
        this.eventHandler = new EventHandler();
        this.commandManager = commandManager;
        this.commandHandler = new CommandHandler();
    }

    public void registerEvents(JDA jda) {
        registerListener(jda, messageListener);
        registerListener(jda, readyListener);
        
        jda.addEventListener(eventHandler);
        jda.addEventListener(commandHandler);
        
        commandManager.getCommands().forEach((name, command) -> {
            commandHandler.registerCommand(command);
        });
        
        LogUtil.info("Event listeners and handlers registered successfully!");
    }

    private void registerListener(JDA jda, ListenerAdapter listener) {
        jda.addEventListener(listener);
        LogUtil.debug("Registered listener: " + listener.getClass().getSimpleName());
    }
}