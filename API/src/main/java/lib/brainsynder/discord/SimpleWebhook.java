package lib.brainsynder.discord;

import com.eclipsesource.json.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class SimpleWebhook {
    private final String webhook;
    private String content;
    private String username;
    private String avatarUrl;

    public SimpleWebhook(String webhook) {
        this.webhook = webhook;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void send() {
        JsonObject json = new JsonObject();
        json.add("content", this.content);
        json.add("username", this.username);
        json.add("avatar_url", this.avatarUrl);
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(webhook);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.addRequestProperty("Content-Type", "application/json");
                connection.addRequestProperty("User-Agent", "BSLib");
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                OutputStream stream = connection.getOutputStream();
                stream.write(json.toString().getBytes());
                stream.flush();
                stream.close();

                connection.getInputStream().close();
                connection.disconnect();
            }catch(IOException ignored) {}
        });
    }
}
