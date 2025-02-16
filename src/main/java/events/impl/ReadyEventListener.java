package events.impl;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.LogUtil;

public class ReadyEventListener extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        String botName = event.getJDA().getSelfUser().getName();
        int guildCount = event.getJDA().getGuilds().size();
        LogUtil.info(String.format("Bot %s is ready! Connected to %d servers.", botName, guildCount));
    }
}