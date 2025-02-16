import utils.LogUtil;

public class Main {
    public static void main(String[] args) {
        try {
            new Bot();
            LogUtil.info("Bot started successfully!");
        } catch (Exception e) {
            LogUtil.error("Failed to start bot", e);
        }
    }
}