import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.Activity;
import utils.ConfigUtil;
import utils.SpotifyUtil;
import commands.CommandManager;
import commands.impl.SpotifyStatsCommand;
import events.EventManager;
import database.DatabaseManager;

public class Bot {
    private final JDA jda;
    private final CommandManager commandManager;
    private final EventManager eventManager;
    private final ConfigUtil config;
    private final SpotifyUtil spotifyUtil;
    private final DatabaseManager databaseManager;

    public Bot() throws Exception {
        this.config = new ConfigUtil();
        this.databaseManager = new DatabaseManager();
        this.spotifyUtil = new SpotifyUtil(config, databaseManager);
        this.commandManager = new CommandManager();
        this.eventManager = new EventManager(config, commandManager);
        
        this.jda = JDABuilder.createDefault(config.getProperty("bot.token"))
            .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
            .setActivity(Activity.playing(config.getProperty("bot.activity")))
            .build();
        
        commandManager.registerCommand(new SpotifyStatsCommand(spotifyUtil));
        
        initialize();
    }

    private void initialize() {
        commandManager.registerCommands(jda);
        eventManager.registerEvents(jda);
    }
}