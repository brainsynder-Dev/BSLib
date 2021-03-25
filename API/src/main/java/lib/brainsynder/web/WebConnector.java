package lib.brainsynder.web;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import lib.brainsynder.apache.ApacheUtils;
import lib.brainsynder.utils.Callback;
import lib.brainsynder.utils.ReturnValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;

public class WebConnector {
    public static void getOutputStream(String link, Plugin plugin, ReturnValue<OutputStream> streamReturn) {
        CompletableFuture.runAsync(() -> {
            try {
                System.setProperty("http.agent", "Chrome");
                URL url = new URL(link);
                URLConnection connection = url.openConnection();
                connection.addRequestProperty("User-Agent", "Mozilla/5.0");
                connection.addRequestProperty("Content-Encoding", "gzip");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            streamReturn.run(connection.getOutputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.runTask(plugin);
            } catch (Exception ignored) {
            }
        });
    }

    public static void getInputStream(String link, Plugin plugin, ReturnValue<InputStream> streamReturn) {
        CompletableFuture.runAsync(() -> {
            try {
                System.setProperty("http.agent", "Chrome");
                URL url = new URL(link);
                URLConnection connection = url.openConnection();
                connection.addRequestProperty("User-Agent", "Mozilla/5.0");
                connection.addRequestProperty("Content-Encoding", "gzip");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            streamReturn.run(connection.getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.runTask(plugin);
            } catch (Exception ignored) {
            }
        });
    }

    public static void getInputStreamString(String link, Plugin plugin, ReturnValue<String> stringReturn) {
        CompletableFuture.runAsync(() -> {
            try {
                System.setProperty("http.agent", "Chrome");
                URL url = new URL(link);
                URLConnection connection = url.openConnection();
                connection.addRequestProperty("User-Agent", "Mozilla/5.0");
                connection.addRequestProperty("Content-Encoding", "gzip");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                InputStream stream = connection.getInputStream();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            stringReturn.run(ApacheUtils.toString(stream));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.runTask(plugin);
            } catch (Exception ignored) {
            }
        });
    }

    public static void uploadPaste(Plugin plugin, String text, Callback<String, String> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                String url = "http://pastelog.us/documents";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(text);
                wr.flush();
                wr.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) response.append(inputLine);
                in.close();

                try {
                    JsonValue value = Json.parse(response.toString());
                    if (value.isObject()) {
                        JsonObject json = (JsonObject) value;
                        String key = json.getString("key", null);
                        if ((key != null) && !key.isEmpty()) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    callback.success("http://pastelog.us/"+key);
                                }
                            }.runTask(plugin);
                            return;
                        }
                    }
                }catch (Exception ignored) { }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        callback.fail(response.toString());
                    }
                }.runTask(plugin);
            } catch (IOException ignored) {

            }
        });
    }
}