package lib.brainsynder.web;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import lib.brainsynder.apache.ApacheUtils;
import lib.brainsynder.utils.Callback;
import lib.brainsynder.utils.ReturnValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
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
                String urlBase = "https://pastelog.us";

                URL url = new URL(urlBase+"/api/paste/create");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // Setting the connection to be able to send and receive data.
                connection.setDoOutput(true);
                connection.setDoInput(true);
                // Telling the connection to not follow redirects.
                connection.setInstanceFollowRedirects(false);
                // Setting the request method to POST.
                connection.setRequestMethod("POST");
                // Telling the connection to not use the cache.
                connection.setUseCaches(false);

                // Writing the data to the output stream.
                try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    outputStream.write(("content=" + text).getBytes(StandardCharsets.UTF_8));
                }

                try (InputStream inputStream = connection.getInputStream()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        builder.append(inputLine);
                    }


                    try {
                        JsonValue value = Json.parse(builder.toString());
                        if (value.isObject()) {
                            JsonObject json = (JsonObject) value;
                            String key = json.getString("paste_key", "");
                            if (key != null && !key.isEmpty()) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        callback.success(urlBase + "/paste/"+json.getString("paste_key", ""));
                                    }
                                }.runTask(plugin);
                                return;
                            }
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                callback.fail(value.toString());
                            }
                        }.runTask(plugin);
                    } catch (Exception e) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                callback.fail(builder.toString());
                            }
                        }.runTask(plugin);
                    }
                }
            } catch (IOException ignored) {

            }
        });
    }
}