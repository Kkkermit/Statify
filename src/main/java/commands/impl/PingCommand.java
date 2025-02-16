package commands.impl;

import commands.interfaces.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommand implements ICommand {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Check the bot's latency";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        long ping = event.getJDA().getGatewayPing();
        event.reply("Pong! Gateway Ping: " + ping + "ms").queue();
    }

    public void execute(MessageReceivedEvent event) {
        long ping = event.getJDA().getGatewayPing();
        event.getChannel().sendMessage("Pong! Gateway Ping: " + ping + "ms").queue();
    }
}