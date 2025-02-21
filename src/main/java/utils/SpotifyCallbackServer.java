package utils;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SpotifyCallbackServer {
    private HttpServer server;
    private final Map<String, CompletableFuture<String>> pendingAuth;
    private final int port;

    public SpotifyCallbackServer(int port) {
        this.port = port;
        this.pendingAuth = new ConcurrentHashMap<>();
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/callback", exchange -> {
                String query = exchange.getRequestURI().getQuery();
                String code = null;
                String state = null;

                if (query != null) {
                    String[] pairs = query.split("&");
                    for (String pair : pairs) {
                        String[] keyValue = pair.split("=");
                        if (keyValue.length == 2) {
                            if ("code".equals(keyValue[0])) {
                                code = keyValue[1];
                            } else if ("state".equals(keyValue[0])) {
                                state = keyValue[1];
                            }
                        }
                    }
                }

                String response = "Authorization received! You can close this window.";
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.getResponseBody().close();

                if (code != null && state != null && pendingAuth.containsKey(state)) {
                    pendingAuth.get(state).complete(code);
                }
            });

            server.start();
            LogUtil.info("Callback server started on port " + port);
        } catch (Exception e) {
            LogUtil.error("Failed to start callback server", e);
        }
    }

    public CompletableFuture<String> waitForCode(String state) {
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingAuth.put(state, future);
        return future;
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            LogUtil.info("Callback server stopped");
        }
    }
}
