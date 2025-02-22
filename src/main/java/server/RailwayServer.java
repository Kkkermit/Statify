package server;

import spark.Spark;
import utils.LogUtil;
import utils.SpotifyUtil;

public class RailwayServer {
    private final SpotifyUtil spotifyUtil;

    public RailwayServer(SpotifyUtil spotifyUtil) {
        this.spotifyUtil = spotifyUtil;
        initializeServer();
    }

    private void initializeServer() {
        int port = System.getenv("PORT") != null ? 
            Integer.parseInt(System.getenv("PORT")) : 8080;
            
        Spark.port(port);

        Spark.get("/", (req, res) -> "Statsify Bot is running!");

        Spark.get("/health", (req, res) -> {
            res.type("application/json");
            return "{\"status\":\"UP\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}";
        });

        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
        });

        Spark.get("/callback", (request, response) -> {
            String code = request.queryParams("code");
            String state = request.queryParams("state");

            if (code != null && state != null) {
                spotifyUtil.handleAuthorizationCode(state, code);
                return "Authorization successful! You can close this window.";
            }

            return "Error: Missing required parameters";
        });

        LogUtil.info("Railway server initialized and listening on port " + port);
        
        Spark.awaitInitialization();
    }

    public void stop() {
        Spark.stop();
        LogUtil.info("Railway server stopped");
    }
}
