import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.Activity;
import utils.ConfigUtil;
import commands.CommandManager;
import events.EventManager;

public class Bot {
    private final JDA jda;
    private final CommandManager commandManager;
    private final EventManager eventManager;
    private final ConfigUtil config;

    public Bot() throws Exception {
        this.config = new ConfigUtil();
        this.commandManager = new CommandManager();
        this.eventManager = new EventManager(config, commandManager);  // Pass required parameters
        
        this.jda = JDABuilder.createDefault(config.getProperty("bot.token"))
            .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
            .setActivity(Activity.playing(config.getProperty("bot.activity")))
            .build();
        
        initialize();
    }

    private void initialize() {
        commandManager.registerCommands(jda);
        eventManager.registerEvents(jda);
    }
}