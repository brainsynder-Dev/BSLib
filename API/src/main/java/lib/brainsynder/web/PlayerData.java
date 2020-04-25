package lib.brainsynder.web;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import lib.brainsynder.storage.ExpireHashMap;
import lib.brainsynder.utils.Base64Wrapper;
import lib.brainsynder.utils.ReturnValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerData {
    private static final ExpireHashMap<String, JsonObject> cache = new ExpireHashMap<>();
    private final static String rawTexture = "http://textures.minecraft.net/texture/456eec1c2169c8c60a7ae436abcd2dc5417d56f8adef84f11343dc1188fe138";
    private final static String steveTexture = Base64Wrapper.encodeString("{'textures':{'SKIN':{'url':'"+rawTexture+"'}}}");

    public static void findHistory(String name, Plugin plugin, ReturnValue<List<String>> returnValue) {
        findHistory(name, plugin, returnValue, Throwable::printStackTrace);
    }
    public static void findProfile(String name, Plugin plugin, ReturnValue<JsonObject> returnValue) {
        findProfile(name, plugin, returnValue, Throwable::printStackTrace);
    }
    public static void findTexture(Value value, String name, Plugin plugin, ReturnValue<String> returnValue) {
        findTexture(value, name, plugin, returnValue, Throwable::printStackTrace);
    }

    public static void findHistory(String name, Plugin plugin, ReturnValue<List<String>> returnValue, ReturnValue<Throwable> onFailure) {
        findProfile(name, plugin, json -> {
            if (!json.names().contains("history")) return;
            JsonObject history = json.get("history").asObject();
            JsonArray decoded = history.get("decoded").asArray();
            LinkedList<String> names = new LinkedList<>();
            decoded.values().forEach(jsonValue -> {
                JsonObject value = (JsonObject) jsonValue;
                if (value.names().contains("name")) names.addFirst(value.getString("name", "Steve"));
            });
            returnValue.run(names);
        }, onFailure);
    }

    public static void findTexture(Value value, String search, Plugin plugin, ReturnValue<String> returnValue, ReturnValue<Throwable> onFailure) {
        findProfile(search, plugin, json -> {
            if (json.names().contains("properties")) {
                JsonObject prop = json.get("properties").asObject();
                if (value == Value.BASE64) {
                    if (prop.names().contains("raw"))
                        returnValue.run(prop.getString("raw", steveTexture));
                }else if (prop.names().contains("quick_texture")) {
                    returnValue.run(prop.getString("quick_texture", rawTexture));
                }
            }
        }, onFailure);
    }

    public static void findProfile(String search, Plugin plugin, ReturnValue<JsonObject> returnValue, ReturnValue<Throwable> onFailure) {
        if (cache.containsKey(search)) {
            returnValue.run(cache.get(search));
            return;
        }

        WebConnector.getInputStreamString("https://v4.minecraftchar.us/profile.php?user=" + search, plugin, value -> {
            JsonObject profile = new JsonObject();
            try {
                JsonObject main = (JsonObject) Json.parse(value);
                if (!main.isEmpty()) {
                    if (main.names().contains("error")) throw new Exception("Could not fetch profile data of " + search);
                    profile.merge(main);
                }

                sync(plugin, () -> {
                    // Cache the data for 10 minutes
                    cache.put(search, profile, 10, TimeUnit.MINUTES);
                    returnValue.run(profile);
                });
            } catch (Exception e) {
                sync(plugin, () -> onFailure.run(e));
            }
        });
    }

    private static void sync (Plugin plugin, Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(plugin);
    }

    public enum Value {
        DECODED,
        BASE64
    }
}
