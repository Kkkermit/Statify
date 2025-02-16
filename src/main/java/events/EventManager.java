package events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import events.impl.MessageEventListener;
import events.impl.ReadyEventListener;
import commands.CommandManager;
import events.handlers.EventHandler;
import utils.ConfigUtil;
import utils.LogUtil;

public class EventManager {
    private final MessageEventListener messageListener;
    private final ReadyEventListener readyListener;
    private final EventHandler eventHandler;

    public EventManager(ConfigUtil config, CommandManager commandManager) {
    this.messageListener = new MessageEventListener(config, commandManager);
    this.readyListener = new ReadyEventListener();
        this.eventHandler = new EventHandler();
    }

    public void registerEvents(JDA jda) {
        // Register specific event listeners
        registerListener(jda, messageListener);
        registerListener(jda, readyListener);
        
        // Register general event handler
        jda.addEventListener(eventHandler);
        
        LogUtil.info("Event listeners and handlers registered successfully!");
    }

    private void registerListener(JDA jda, ListenerAdapter listener) {
        jda.addEventListener(listener);
        LogUtil.debug("Registered listener: " + listener.getClass().getSimpleName());
    }
}