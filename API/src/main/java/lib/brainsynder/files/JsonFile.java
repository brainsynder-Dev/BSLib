package lib.brainsynder.files;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;
import com.google.common.base.Charsets;

import java.io.*;
import java.nio.charset.Charset;

public class JsonFile implements Movable {
    private final Charset ENCODE = Charsets.UTF_8;
    private JsonObject json;
    protected JsonObject defaults = new JsonObject();
    private final File file;
    private boolean update = false;

    public JsonFile(File file) {
        this(file, true);
    }

    public JsonFile(File file, boolean loadDefaults) {
        this.file = file;
        if (loadDefaults) reload();
    }

    public void loadDefaults() {}

    public void reload() {
        try {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists()) {
                OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
                pw.write(defaults.toString(WriterConfig.PRETTY_PRINT).replace("\u0026", "&"));
                pw.flush();
                pw.close();
            }

            json = (JsonObject) Json.parse(new InputStreamReader(new FileInputStream(file), ENCODE));
            loadDefaults();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean save() {
        String text = defaults.toString(WriterConfig.PRETTY_PRINT).replace("\u0026", "&");
        if (update) text = json.toString(WriterConfig.PRETTY_PRINT).replace("\u0026", "&");

        try {
            OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
            try {
                fw.write(text);
            } finally {
                fw.flush();
                fw.close();
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public String getName() {
        return file.getName().replace(".json", "");
    }

    public boolean hasKey (String key) {
        return json.names().contains(key);
    }
    private boolean hasDefaultKey (String key) {
        return defaults.names().contains(key);
    }

    public JsonValue getValue (String key) {
        JsonValue value = null;
        if (hasKey(key)) value = json.get(key);
        if ((value == null) && hasDefaultKey(key)) value = defaults.get(key);
        return value;
    }
    public String getString (String key) {
        JsonValue value = getValue(key);
        if (value == null) return "";
        return value.asString();
    }
    public double getDouble (String key) {
        JsonValue value = getValue(key);
        if (value == null) return 0;
        if (value.isString()) return Double.parseDouble(value.asString());
        return value.asDouble();
    }
    public byte getByte (String key) {
        JsonValue value = getValue(key);
        if (value == null) return 0;
        if (value.isString()) return Byte.parseByte(value.asString());
        return (byte) value.asInt();
    }
    public boolean getBoolean (String key) {
        JsonValue value = getValue(key);
        if (value == null) return false;
        if (value.isString()) return Boolean.getBoolean(value.asString());
        return value.asBoolean();
    }
    public short getShort (String key) {
        JsonValue value = getValue(key);
        if (value == null) return 0;
        if (value.isString()) return Short.parseShort(value.asString());
        return (short) value.asLong();
    }
    public float getFloat (String key) {
        JsonValue value = getValue(key);
        if (value == null) return 0;
        if (value.isString()) return Float.parseFloat(value.asString());
        return value.asFloat();
    }
    public long getLong (String key) {
        JsonValue value = getValue(key);
        if (value == null) return 0;
        if (value.isString()) return Long.parseLong(value.asString());
        return value.asLong();
    }
    public int getInteger (String key) {
        JsonValue value = getValue(key);
        if (value == null) return 0;
        if (value.isString()) return Integer.parseInt(value.asString());
        return value.asInt();
    }


    public void set(String key, int value) {
        update = true;
        json.add(key, value);
    }
    public void set(String key, long value) {
        update = true;
        json.add(key, value);
    }
    public void set(String key, float value) {
        update = true;
        json.add(key, value);
    }
    public void set(String key, short value) {
        update = true;
        json.add(key, value);
    }
    public void set(String key, byte value) {
        update = true;
        json.add(key, value);
    }
    public void set(String key, double value) {
        update = true;
        json.add(key, value);
    }
    public void set(String key, boolean value) {
        update = true;
        json.add(key, value);
    }
    public void set(String key, String value) {
        update = true;
        json.add(key, value);
    }
    public void set(String key, JsonValue value) {
        update = true;
        json.add(key, value);
    }




    public void setDefault(String key, int value) {
        defaults.add(key, value);
    }
    public void setDefault(String key, long value) {
        defaults.add(key, value);
    }
    public void setDefault(String key, float value) {
        defaults.add(key, value);
    }
    public void setDefault(String key, short value) {
        defaults.add(key, value);
    }
    public void setDefault(String key, byte value) {
        defaults.add(key, value);
    }
    public void setDefault(String key, double value) {
        defaults.add(key, value);
    }
    public void setDefault(String key, boolean value) {
        defaults.add(key, value);
    }
    public void setDefault(String key, String value) {
        defaults.add(key, value);
    }
    public void setDefault(String key, JsonValue value) {
        defaults.add(key, value);
    }

    @Override
    public void move(String oldKey, String newKey) {
        if (hasKey(oldKey)) {
            json.set(newKey, getValue(oldKey));
            json.remove(oldKey);
            save();
        }
    }
}
