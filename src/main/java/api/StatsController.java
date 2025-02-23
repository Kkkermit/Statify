package api;

import com.google.gson.Gson;
import spark.Route;
import utils.SpotifyUtil;
import utils.LogUtil;

public class StatsController {
    private final SpotifyUtil spotifyUtil = null;
    private final Gson gson = new Gson();
    
    private StatsController() {
        // Private constructor to force use of factory method
    }
    
    public static StatsController create(SpotifyUtil util) {
        if (util == null) {
            throw new IllegalArgumentException("SpotifyUtil cannot be null");
        }
        StatsController controller = new StatsController();
        try {
            java.lang.reflect.Field field = StatsController.class.getDeclaredField("spotifyUtil");
            field.setAccessible(true);
            field.set(controller, util);
            LogUtil.debug("StatsController initialized with SpotifyUtil");
            return controller;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize StatsController", e);
        }
    }

    public Route getStats = (request, response) -> {
        String userId = request.queryParams("userId");
        if (userId == null || userId.isEmpty()) {
            response.status(400);
            return gson.toJson(new ErrorResponse("userId parameter is required"));
        }

        try {
            var stats = spotifyUtil.getUserStats(userId);
            response.type("application/json");
            return gson.toJson(stats);
        } catch (Exception e) {
            LogUtil.error("Error fetching stats for user: " + userId, e);
            response.status(500);
            return gson.toJson(new ErrorResponse("Error fetching stats"));
        }
    };

    private static class ErrorResponse {
        private final String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        @SuppressWarnings("unused")
        public String getError() {
            return error;
        }
    }
}
