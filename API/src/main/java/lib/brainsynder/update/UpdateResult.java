package lib.brainsynder.update;

import com.eclipsesource.json.JsonObject;
import lib.brainsynder.utils.ReturnValue;

public class UpdateResult {
    private ReturnValue<JsonObject> newBuild;
    private ReturnValue<JsonObject> failParse;
    private Runnable preStart;
    private Runnable onError;
    private Runnable noNewBuilds;

    private int currentBuild = -1, latestBuild = 0;
    private String repo, url = null;

    public UpdateResult() {
        this(value -> {}, () -> {
        });
    }

    public UpdateResult(ReturnValue<JsonObject> newBuild) {
        this(newBuild, () -> {
        });
    }

    public UpdateResult(ReturnValue<JsonObject> newBuild, Runnable noNewBuilds) {
        this(newBuild, () -> {
        }, () -> {
        });
    }

    public UpdateResult(ReturnValue<JsonObject> newBuild, Runnable noNewBuilds, Runnable onError) {
        this(newBuild, () -> {
        }, () -> {
        }, value -> {});
    }

    public UpdateResult(ReturnValue<JsonObject> newBuild, Runnable noNewBuilds, Runnable onError, ReturnValue<JsonObject> failParse) {
        this.newBuild = newBuild;
        this.noNewBuilds = noNewBuilds;
        this.onError = onError;
        this.failParse = failParse;
    }

    /**
     * Sets the url location for the latest build
     *
     * @param url
     * @return
     */
    public UpdateResult setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Will run before it checks for a new build
     *
     * @param preStart
     * @return
     */
    public UpdateResult setPreStart(Runnable preStart) {
        this.preStart = preStart;
        return this;
    }

    /**
     * The failParse will be run when the task fails to parse the JSON text
     *
     * @param failParse
     * @return
     */
    public UpdateResult setFailParse(ReturnValue<JsonObject> failParse) {
        this.failParse = failParse;
        return this;
    }

    /**
     * Will run when ever there is a new Build including the JSON text of the build data (EG: repo and build number)
     *
     * @param newBuild
     * @return
     */
    public UpdateResult setNewBuild(ReturnValue<JsonObject> newBuild) {
        this.newBuild = newBuild;
        return this;
    }

    /**
     * Will run if there are no new builds (you're using the latest)
     *
     * @param noNewBuilds
     * @return
     */
    public UpdateResult setNoNewBuilds(Runnable noNewBuilds) {
        this.noNewBuilds = noNewBuilds;
        return this;
    }

    /**
     * Will run if the JSON contains an error (from site)
     *
     * @param onError
     * @return
     */
    public UpdateResult setOnError(Runnable onError) {
        this.onError = onError;
        return this;
    }

    public String getUrl() {
        return url;
    }

    ReturnValue<JsonObject> getNewBuild() {
        return newBuild;
    }

    Runnable getNoNewBuilds() {
        return noNewBuilds;
    }

    Runnable getOnError() {
        return onError;
    }

    ReturnValue<JsonObject> getFailParse() {
        return failParse;
    }

    Runnable getPreStart() {
        return preStart;
    }

    public int getLatestBuild() {
        return latestBuild;
    }

    public int getCurrentBuild() {
        return currentBuild;
    }

    public String getRepo() {
        return repo;
    }

    public boolean hasUpdateAvailable () {
        return (latestBuild > currentBuild);
    }

    void setCurrentBuild(int currentBuild) {
        this.currentBuild = currentBuild;
    }

    void setLatestBuild(int latestBuild) {
        this.latestBuild = latestBuild;
    }

    void setRepo(String repo) {
        this.repo = repo;
    }
}