package utils;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import database.DatabaseManager;

public class SpotifyUtil {
    private final DatabaseManager database;
    private final SpotifyCallbackServer callbackServer;
    private final String clientId;
    private final String clientSecret;
    private final URI redirectUri;
    private final Map<String, SpotifyApi> userSpotifyApis;

    public SpotifyUtil(ConfigUtil config, DatabaseManager database) {
        this.database = database;
        this.clientId = config.getProperty("spotify.client.id");
        this.clientSecret = config.getProperty("spotify.client.secret");
        int callbackPort = Integer.parseInt(config.getProperty("spotify.callback.port"));
        
        try {
            this.redirectUri = new URI("http://localhost:" + callbackPort + "/callback");
            this.callbackServer = new SpotifyCallbackServer(callbackPort);
            this.callbackServer.start();
        } catch (Exception e) {
            LogUtil.error("Failed to initialize Spotify callback server", e);
            throw new RuntimeException("Failed to initialize Spotify utility", e);
        }
        
        this.userSpotifyApis = new HashMap<>();
    }

    public boolean isUserAuthorized(String discordUserId) {
        Map<String, String> tokens = database.getSpotifyTokens(discordUserId);
        if (!tokens.isEmpty()) {
            SpotifyApi spotifyApi = new SpotifyApi.Builder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRedirectUri(redirectUri)
                    .setAccessToken(tokens.get("access_token"))
                    .setRefreshToken(tokens.get("refresh_token"))
                    .build();
            userSpotifyApis.put(discordUserId, spotifyApi);
            return true;
        }
        return false;
    }

    public String getAuthorizationUrl(String discordUserId) {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();

        userSpotifyApis.put(discordUserId, spotifyApi);
        
        String state = UUID.randomUUID().toString();
        callbackServer.waitForCode(state).thenAccept(code -> handleAuthorizationCode(discordUserId, code));
        
        return spotifyApi.authorizationCodeUri()
                .state(state)
                .scope("user-read-private user-read-email user-top-read")
                .build()
                .execute()
                .toString();
    }

    public Map<String, Object> getUserStats(String discordUserId) {
        Map<String, Object> stats = new HashMap<>();
        
        if (!isUserAuthorized(discordUserId)) {
            return stats;
        }

        SpotifyApi spotifyApi = userSpotifyApis.get(discordUserId);
        try {

            refreshTokenIfNeeded(discordUserId, spotifyApi);
            
            GetCurrentUsersProfileRequest profileRequest = spotifyApi.getCurrentUsersProfile()
                    .build();
            User user = profileRequest.execute();
            
            stats.put("displayName", user.getDisplayName());
            stats.put("followers", user.getFollowers().getTotal());
            stats.put("spotifyUrl", user.getExternalUrls().get("spotify"));
            
        } catch (Exception e) {
            LogUtil.error("Error fetching Spotify stats", e);
        }
        
        return stats;
    }

    private void refreshTokenIfNeeded(String discordUserId, SpotifyApi spotifyApi) {
        try {
            AuthorizationCodeCredentials credentials = spotifyApi.authorizationCodeRefresh()
                    .build()
                    .execute();

            spotifyApi.setAccessToken(credentials.getAccessToken());
            
            database.updateSpotifyTokens(
                discordUserId,
                null,
                credentials.getAccessToken(),
                spotifyApi.getRefreshToken()
            );
            
        } catch (Exception e) {
            LogUtil.error("Error refreshing token", e);
        }
    }

    public void handleAuthorizationCode(String discordUserId, String code) {
        SpotifyApi spotifyApi = userSpotifyApis.get(discordUserId);
        try {
            AuthorizationCodeCredentials credentials = spotifyApi.authorizationCode(code)
                    .build()
                    .execute();

            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());
            
            User user = spotifyApi.getCurrentUsersProfile()
                    .build()
                    .execute();
            
            database.updateSpotifyTokens(
                discordUserId,
                user.getId(),
                credentials.getAccessToken(),
                credentials.getRefreshToken()
            );
            
            LogUtil.info("Spotify authorization successful for user: " + discordUserId);
        } catch (Exception e) {
            LogUtil.error("Error during Spotify authorization", e);
        }
    }
}
