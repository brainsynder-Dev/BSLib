package lib.brainsynder.files;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YamlFile implements ConfigurationSection, Movable{
    private File file;
    private FileConfiguration configuration;

    private void createParentDirs(File file) throws IOException {
        Preconditions.checkNotNull(file);
        File parent = file.getCanonicalFile().getParentFile();
        if (parent != null) {
            parent.mkdirs();
            if (!parent.isDirectory()) {
                throw new IOException("Unable to create parent directories of " + file);
            }
        }
    }

    public YamlFile(Plugin plugin, String directory, String fileName) {
        try {
            File folder = new File(plugin.getDataFolder().toString() + File.separator + directory);
            file = new File(folder, fileName);
            createParentDirs(file);
            if (!file.exists()) file.createNewFile();
        } catch (Throwable ignored) {
        }
        reload();
    }

    public YamlFile(File folder, String fileName) {
        try {
            createParentDirs(folder);
            file = new File(folder, fileName);
            if (!file.exists()) file.createNewFile();

            this.file = new File(folder, fileName);
        } catch (Throwable ignored) {
        }
        reload();
    }

    public YamlFile(File file) {
        try {
            createParentDirs(file);
            if (!file.exists()) file.createNewFile();
            this.file = file;
        } catch (Throwable ignored) {
        }
        reload();
    }

    public void reload () {
        if (file == null) return;
        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void setDefault (String key, Object value) {
        if (!contains(key)) set(key, value);
    }
    
    
    public String getString(String tag, boolean color) {
        return (color ? translate(getString(tag)) : getString(tag));
    }
    @Override
    public String getString(String tag) {
        return getString(tag, "");
    }
    @Override
    public String getString(String tag, String fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getString(tag, fallback);
    }
    @Override
    public boolean isString(String tag) {
        return configuration.isString(tag);
    }

    
    @Override
    public ItemStack getItemStack(String tag) {
        return getItemStack(tag, new ItemStack(Material.AIR));
    }
    @Override
    public ItemStack getItemStack(String tag, ItemStack fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getItemStack(tag, fallback);
    }
    @Override
    public boolean isItemStack(String tag) {
        return configuration.isItemStack(tag);
    }

    @Override
    public Color getColor(String tag) {
        return getColor(tag, Color.WHITE);
    }
    @Override
    public Color getColor(String tag, Color fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getColor(tag, fallback);
    }
    @Override
    public boolean isColor(String tag) {
        return configuration.isColor(tag);
    }

    
    
    @Override
    public ConfigurationSection getConfigurationSection(String tag) {
        return configuration.getConfigurationSection(tag);
    }
    @Override
    public boolean isConfigurationSection(String tag) {
        return configuration.isConfigurationSection(tag);
    }
    @Override
    public ConfigurationSection getDefaultSection() {
        return configuration.getDefaultSection();
    }
    @Override
    public void addDefault(String tag, Object o) {

    }

    
    
    @Override
    public boolean getBoolean(String tag) {
        return this.configuration.get(tag) != null && this.configuration.getBoolean(tag);
    }
    @Override
    public boolean getBoolean(String tag, boolean fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getBoolean(tag, fallback);
    }
    @Override
    public boolean isBoolean(String tag) {
        return configuration.isBoolean(tag);
    }

    
    
    @Override
    public int getInt(String tag) {
        return getInt(tag, 0);
    }
    @Override
    public int getInt(String tag, int fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getInt(tag, fallback);
    }
    @Override
    public boolean isInt(String tag) {
        return configuration.isInt(tag);
    }

    
    
    @Override
    public double getDouble(String tag) {
        return getDouble(tag, 0);
    }
    @Override
    public double getDouble(String tag, double fallback) {
        if (!contains(tag)) return fallback;
        return this.configuration.getDouble(tag, fallback);
    }
    @Override
    public boolean isDouble(String tag) {
        return configuration.isDouble(tag);
    }

    
    
    @Override
    public long getLong(String tag) {
        return configuration.getLong(tag);
    }
    @Override
    public long getLong(String tag, long fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getLong(tag, fallback);
    }
    @Override
    public boolean isLong(String tag) {
        return configuration.isLong(tag);
    }

    
    
    @Override
    public List<?> getList(String tag) {
        return configuration.getList(tag);
    }
    @Override
    public List<?> getList(String tag, List<?> fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getList(tag, fallback);
    }
    @Override
    public boolean isList(String tag) {
        return configuration.isList(tag);
    }
    

    @Override
    public Set<String> getKeys(boolean deep) {
        return this.configuration.getKeys(deep);
    }

    @Override
    public Map<String, Object> getValues(boolean b) {
        return configuration.getValues(b);
    }

    @Override
    public boolean contains(String tag) {
        return configuration.get(tag) != null;
    }

    @Override
    public boolean contains(String tag, boolean ignoreDefault) {
        return configuration.contains(tag, ignoreDefault);
    }

    @Override
    public boolean isSet(String tag) {
        return configuration.isSet(tag);
    }

    
    @Override
    public String getCurrentPath() {
        return configuration.getCurrentPath();
    }
    @Override
    public String getName() {
        return configuration.getName();
    }
    @Override
    public Configuration getRoot() {
        return configuration.getRoot();
    }
    @Override
    public ConfigurationSection getParent() {
        return configuration.getParent();
    }

    
    @Override
    public List<String> getStringList(String tag) {
        return getStringList(tag, new ArrayList<>());
    }
    public List<String> getStringList(String tag, List<String> fallback) {
        if (!contains(tag)) return fallback;
        return this.configuration.getStringList(tag);
    }

    
    @Override
    public List<Integer> getIntegerList(String tag) {
        return configuration.getIntegerList(tag);
    }
    @Override
    public List<Boolean> getBooleanList(String tag) {
        return configuration.getBooleanList(tag);
    }
    @Override
    public List<Double> getDoubleList(String tag) {
        return configuration.getDoubleList(tag);
    }
    @Override
    public List<Float> getFloatList(String tag) {
        return configuration.getFloatList(tag);
    }
    @Override
    public List<Long> getLongList(String tag) {
        return configuration.getLongList(tag);
    }
    @Override
    public List<Byte> getByteList(String tag) {
        return configuration.getByteList(tag);
    }
    @Override
    public List<Character> getCharacterList(String tag) {
        return configuration.getCharacterList(tag);
    }
    @Override
    public List<Short> getShortList(String tag) {
        return configuration.getShortList(tag);
    }
    @Override
    public List<Map<?, ?>> getMapList(String tag) {
        return configuration.getMapList(tag);
    }

    
    
    @Override
    public Vector getVector(String tag) {
        return configuration.getVector(tag);
    }
    @Override
    public Vector getVector(String tag, Vector fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getVector(tag, fallback);
    }
    @Override
    public boolean isVector(String tag) {
        return configuration.isVector(tag);
    }

    
    
    @Override
    public OfflinePlayer getOfflinePlayer(String tag) {
        return configuration.getOfflinePlayer(tag);
    }
    @Override
    public OfflinePlayer getOfflinePlayer(String tag, OfflinePlayer fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getOfflinePlayer(tag, fallback);
    }
    @Override
    public boolean isOfflinePlayer(String tag) {
        return configuration.isOfflinePlayer(tag);
    }

    
    
    
    public ConfigurationSection getSection(String tag) {
        return this.configuration.getConfigurationSection(tag);
    }

    @Override
    public Object get(String tag) {
        return this.configuration.get(tag);
    }

    @Override
    public Object get(String tag, Object fallback) {
        return null;
    }

    private String translate(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @Override
    public void set(String tag, Object data) {
        configuration.set(tag, data);
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ConfigurationSection createSection(String tag) {
        return configuration.createSection(tag);
    }

    @Override
    public ConfigurationSection createSection(String tag, Map<?, ?> fallback) {
        return configuration.createSection(tag, fallback);
    }

    public void setHeader(String... header) {
        this.configuration.options().header(Arrays.toString(header));
    }

    public Map<String, Object> getConfigSectionValue(Object o) {
        return this.getConfigSectionValue(o, false);
    }
    public Map<String, Object> getConfigSectionValue(Object o, boolean deep) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        Map<String, Object> map = new HashMap();
        if (o == null) {
            return map;
        } else {
            if (o instanceof ConfigurationSection) {
                map = ((ConfigurationSection) o).getValues(deep);
            } else if (o instanceof Map) {
                map = (Map) o;
            }

            return map;
        }
    }

    @Override
    public void move(String oldKey, String newKey) {
        if (contains(oldKey)) {
            set(newKey, get(oldKey));
            set(oldKey, null);
        }
    }
}