package utils;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import server.RailwayServer;
import database.DatabaseManager;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpotifyUtil {
    private final DatabaseManager database;
    private final String clientId;
    private final String clientSecret;
    private final URI redirectUri;
    private final Map<String, SpotifyApi> userSpotifyApis;
    private final Map<String, String> stateToUserMap;

    public SpotifyUtil(ConfigUtil config, DatabaseManager database) {
        this.database = database;
        this.clientId = config.getProperty("spotify.client.id");
        this.clientSecret = config.getProperty("spotify.client.secret");
        this.userSpotifyApis = new HashMap<>();
        this.stateToUserMap = new HashMap<>();
        
        try {
            String redirectUriStr = config.getProperty("spotify.redirect.uri");
            this.redirectUri = new URI(redirectUriStr);
            LogUtil.info("Using Spotify redirect URI: " + redirectUriStr);
            new RailwayServer(this);
        } catch (Exception e) {
            LogUtil.error("Failed to initialize Spotify callback server", e);
            throw new RuntimeException("Failed to initialize Spotify utility", e);
        }
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

        String state = UUID.randomUUID().toString();
        stateToUserMap.put(state, discordUserId);
        userSpotifyApis.put(discordUserId, spotifyApi);
        
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

    public void handleAuthorizationCode(String state, String code) {
        String discordUserId = stateToUserMap.get(state);
        if (discordUserId == null) {
            LogUtil.error("No user found for state: " + state);
            return;
        }

        SpotifyApi spotifyApi = userSpotifyApis.get(discordUserId);
        if (spotifyApi == null) {
            spotifyApi = new SpotifyApi.Builder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRedirectUri(redirectUri)
                    .build();
            userSpotifyApis.put(discordUserId, spotifyApi);
        }

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
            
            stateToUserMap.remove(state);
            
            LogUtil.info("Spotify authorization successful for user: " + discordUserId);
        } catch (Exception e) {
            LogUtil.error("Error during Spotify authorization", e);
            stateToUserMap.remove(state);
            userSpotifyApis.remove(discordUserId);
        }
    }
}
