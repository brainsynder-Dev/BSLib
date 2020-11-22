package lib.brainsynder.utils;

import com.eclipsesource.json.JsonObject;

public class UpdateResult {
    private final ReturnValue<JsonObject> newBuild;
    private final Runnable onError;
    private final Runnable noNewBuilds;

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
        this.newBuild = newBuild;
        this.noNewBuilds = noNewBuilds;
        this.onError = onError;
    }

    public ReturnValue<JsonObject> getNewBuild() {
        return newBuild;
    }

    public Runnable getNoNewBuilds() {
        return noNewBuilds;
    }

    public Runnable getOnError() {
        return onError;
    }
}