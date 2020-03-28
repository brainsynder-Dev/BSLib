package lib.brainsynder.web;

import lib.brainsynder.apache.ApacheUtils;
import lib.brainsynder.utils.ReturnValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
}