package server;

import api.StatsController;
import spark.Spark;
import utils.LogUtil;
import utils.SpotifyUtil;

public class RailwayServer {
    private final SpotifyUtil spotifyUtil;
    private final String apiKey;
    private final StatsController statsController;

    public RailwayServer(SpotifyUtil spotifyUtil) {
        this.spotifyUtil = spotifyUtil;
        this.apiKey = System.getenv("API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new IllegalStateException("API_KEY environment variable not set");
        }
        this.statsController = StatsController.create(spotifyUtil);
        initializeServer();
    }

    private void initializeServer() {
        int port = System.getenv("PORT") != null ? 
            Integer.parseInt(System.getenv("PORT")) : 8080;
            
        Spark.port(port);
        
        Spark.before((request, response) -> {
            String path = request.pathInfo();
            if (!path.equals("/health") && !path.equals("/callback") && !path.startsWith("/api")) {
                String providedKey = request.headers("X-API-Key");
                if (!apiKey.equals(providedKey)) {
                    Spark.halt(401, "Unauthorized");
                }
            }
        });

        Spark.before("/api/*", (request, response) -> {
            String providedKey = request.headers("X-API-Key");
            if (!apiKey.equals(providedKey)) {
                response.type("application/json");
                Spark.halt(401, "{\"error\":\"Invalid API Key\"}");
            }
        });

        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
            response.header("Access-Control-Allow-Headers", "X-API-Key");
        });
        
        Spark.get("/", (req, res) -> "Statsify Bot is running!");
        
        Spark.get("/health", (req, res) -> {
            res.type("application/json");
            return "{\"status\":\"UP\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}";
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

        Spark.path("/api", () -> {
            Spark.before("/*", (request, response) -> {
                String providedKey = request.headers("X-API-Key");
                if (!apiKey.equals(providedKey)) {
                    response.type("application/json");
                    Spark.halt(401, "{\"error\":\"Invalid API Key\"}");
                }
            });

            Spark.get("/stats", statsController.getStats);
        });

        LogUtil.info("Railway server initialized and listening on port " + port);
        
        Spark.awaitInitialization();
    }

    public void stop() {
        Spark.stop();
        LogUtil.info("Railway server stopped");
    }
}
