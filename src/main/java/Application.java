import utils.ConfigUtil;
import utils.EnvLoader;
import utils.SpotifyUtil;
import utils.LogUtil;
import database.DatabaseManager;

public class Application {
    private static Bot bot;

    public static void main(String[] args) {
        try {
            EnvLoader.load();
            
            var config = new ConfigUtil();
            var database = new DatabaseManager();
            var spotifyUtil = new SpotifyUtil(config, database);
            
            bot = new Bot(config, database, spotifyUtil);
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (bot != null) {
                    bot.shutdown();
                }
                database.close();
                LogUtil.info("Application shutting down");
            }));
            
            LogUtil.info("Application started successfully");
        } catch (Exception e) {
            LogUtil.error("Failed to start application", e);
            System.exit(1);
        }
    }
}