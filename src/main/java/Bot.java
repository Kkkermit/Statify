import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.Activity;
import utils.ConfigUtil;
import utils.LogUtil;
import utils.SpotifyUtil;
import commands.CommandManager;
import commands.impl.SpotifyStatsCommand;
import events.EventManager;
import database.DatabaseManager;

public class Bot {
    private final JDA jda;
    private final CommandManager commandManager;
    private final EventManager eventManager;

    public Bot(ConfigUtil config, DatabaseManager databaseManager, SpotifyUtil spotifyUtil) throws Exception {
        this.commandManager = new CommandManager();
        this.eventManager = new EventManager(config, commandManager);
        
        String token = System.getenv("BOT_TOKEN");
        if (token == null || token.isEmpty()) {
            token = System.getProperty("BOT_TOKEN");
        }
        if (token == null || token.isEmpty()) {
            token = config.getProperty("bot.token");
        }
        
        if (token == null || token.isEmpty() || token.startsWith("${")) {
            throw new IllegalArgumentException("Bot token not found! Please check your .env file or environment variables.");
        }
        
        LogUtil.info("Initializing bot with token length: " + token.length());
        
        this.jda = JDABuilder.createDefault(token)
            .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
            .setActivity(Activity.playing(config.getProperty("bot.activity")))
            .build();
        
        commandManager.registerCommand(new SpotifyStatsCommand(spotifyUtil));
        
        initialize();
    }

    private void initialize() {
        commandManager.registerCommands(jda);

        try {
            jda.awaitReady();
            LogUtil.info("Bot is ready and commands are registered");
        } catch (InterruptedException e) {
            LogUtil.error("Failed to initialize bot", e);
        }
        
        eventManager.registerEvents(jda);
    }

    public void shutdown() {
        jda.shutdown();
        LogUtil.info("Bot shutting down");
    }
}