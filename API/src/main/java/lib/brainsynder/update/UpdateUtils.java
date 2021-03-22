package lib.brainsynder.update;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import lib.brainsynder.utils.Utilities;
import lib.brainsynder.web.WebConnector;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class UpdateUtils {
    private final Properties properties;
    private final Plugin plugin;
    private BukkitRunnable updateTask;
    private final UpdateResult result;

    public UpdateUtils (Plugin plugin, UpdateResult result) {
        this.plugin = plugin;


        Properties prop = null;
        try {
            prop = new Properties();
            prop.load(plugin.getClass().getResourceAsStream("/jenkins.properties"));
        } catch (IOException ignored) {} // If it fails, it means there is no 'jenkins.properties' file

        properties = prop;
        result.setCurrentBuild(Integer.parseInt(properties.getProperty("buildnumber")));
        result.setRepo(properties.getProperty("repo"));
        this.result = result;
    }

    public Properties getProperties() {
        return properties;
    }

    public void startUpdateTask (long ticks, TimeUnit unit) {
        if (updateTask == null) updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                checkUpdate();
            }
        };

        updateTask.runTaskTimer(plugin, 0, Utilities.toUnit(ticks, unit));
    }

    /**
     * Will cancel the update task and remove the instance
     */
    public void stopTask () {
        if (updateTask == null) return;
        updateTask.cancel();
        updateTask = null;
    }

    public UpdateResult getResult() {
        return result;
    }

    /**
     * Will check for an update once without re-checking
     */
    public void checkUpdate() {
        if (properties == null) return;
        int build = Integer.parseInt(properties.getProperty("buildnumber"));
        String url = "https://pluginwiki.us/version/builds.json";
        result.getPreStart().run();

        WebConnector.getInputStreamString(url, plugin, string -> {
            JsonObject main = null;
            try {
                main = (JsonObject) Json.parse(string);
            }catch (Exception e){
                JsonObject json = new JsonObject();
                json.add("url", url);
                json.add("result", string);
                json.add("exception", e.getClass().getSimpleName());
                json.add("exception-message", e.getMessage());
                result.getFailParse().run(json);
                return;
            }

            if (main == null) {
                result.getOnError().run();
                return;
            }

            if (!main.isEmpty()) {
                // Failed to find repo build number
                if (!main.names().contains(properties.getProperty("repo"))) {
                    result.getOnError().run();
                    return;
                }

                int latestBuild = main.getInt(properties.getProperty("repo"), -1);
                this.result.setLatestBuild(latestBuild);
                // New build found
                if (latestBuild > build) {
                    result.getNewBuild().run(main);
                } else {
                    result.getNoNewBuilds().run();
                }
            }
        });
    }


}
