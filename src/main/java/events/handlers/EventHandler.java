package events.handlers;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import utils.LogUtil;

public class EventHandler implements EventListener {
    @Override
    public void onEvent(GenericEvent event) {
        try {
            // Log event for debugging
            LogUtil.debug("Handling event: " + event.getClass().getSimpleName());
            
            // Handle different event types
            handleEvent(event);
            
        } catch (Exception e) {
            LogUtil.error("Error handling event: " + event.getClass().getSimpleName(), e);
        }
    }
    
    private void handleEvent(GenericEvent event) {
        if (event instanceof ReadyEvent) {
            handleReady((ReadyEvent) event);
        }
        else if (event instanceof MessageReceivedEvent) {
            handleMessage((MessageReceivedEvent) event);
        }
        else if (event instanceof GuildJoinEvent) {
            handleGuildJoin((GuildJoinEvent) event);
        }
        else if (event instanceof GuildMemberJoinEvent) {
            handleMemberJoin((GuildMemberJoinEvent) event);
        }
    }
    
    private void handleReady(ReadyEvent event) {
        String botName = event.getJDA().getSelfUser().getName();
        int guildCount = event.getJDA().getGuilds().size();
        LogUtil.info(String.format("Bot %s is ready! Connected to %d servers.", botName, guildCount));
    }
    
    private void handleMessage(MessageReceivedEvent event) {
        // Ignore bot messages
        if (event.getAuthor().isBot()) return;
        
        String content = event.getMessage().getContentRaw();
        String author = event.getAuthor().getName(); // Updated from getAsTag()
        LogUtil.debug(String.format("Message from %s: %s", author, content));
    }
    
    private void handleGuildJoin(GuildJoinEvent event) {
        String guildName = event.getGuild().getName();
        LogUtil.info(String.format("Joined new server: %s", guildName));
    }
    
    private void handleMemberJoin(GuildMemberJoinEvent event) {
        String memberName = event.getMember().getUser().getName();
        String guildName = event.getGuild().getName();
        LogUtil.info(String.format("User %s joined server: %s", memberName, guildName));
    }
}