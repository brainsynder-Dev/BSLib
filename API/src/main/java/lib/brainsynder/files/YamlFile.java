package lib.brainsynder.files;

import com.google.common.base.Preconditions;
import lib.brainsynder.utils.Utilities;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.*;

public abstract class YamlFile implements ConfigurationSection, Movable {
    private File file;
    private FileConfiguration configuration;
    private FileConfiguration tempConfig;
    private final HashMap<String, String> comments;
    private final HashMap<String, String> sections;
    private final HashMap<String, Utilities.AlignText> sectionAlign;
    private List<String> currentLines;

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
        this(new File(plugin.getDataFolder().toString() + File.separator + directory), fileName);
    }

    public YamlFile(File folder, String fileName) {
        this(new File(folder, fileName));
    }

    public YamlFile(File file) {
        comments = new HashMap<>();
        sections = new HashMap<>();
        sectionAlign = new HashMap<>();
        currentLines = new ArrayList<>();
        try {
            createParentDirs(file);
            if (!file.exists()) file.createNewFile();
            this.file = file;
        } catch (Throwable ignored) {
        }
        reload();
    }

    public void reload() {
        if (file == null) return;
        currentLines = new ArrayList<>();

        this.configuration = YamlConfiguration.loadConfiguration(file);
        tempConfig = new YamlConfiguration();

        loadDefaults();
        configuration.options().copyDefaults(true);
        save(true);

        writeSections();
        writeComments();
        save(false);
    }

    public abstract void loadDefaults();

    public void remove(String key) {
        if (configuration.contains(key)) {
            set(key, null);
        }
    }

    public void addComment(String path, String comment) {
        comments.put(path, comment);
    }

    public void addSectionHeader(String path, String text) {
        sections.put(path, text);
    }
    public void addSectionHeader(String path, Utilities.AlignText alignText, String text) {
        sections.put(path, text);
        sectionAlign.put(path, alignText);
    }

    @Override
    public void addDefault(String key, Object value) {
        configuration.addDefault(key, value);
        tempConfig.set(key, configuration.get(key));
    }

    public void addDefault(String key, Object value, String comment) {
        configuration.addDefault(key, value);
        tempConfig.set(key, configuration.get(key));
        addComment(key, comment);
    }

    public void writeComments() {
        // For each comment to be made...
        for (String path : comments.keySet()) {
            // Get all the divisions made in the config
            String[] divisions = path.split("\\.");

            writeComment(path, divisions, 0, 0);
        }
    }

    private void writeComment(String path, String[] divisions, int iteration, int startingLine) {
        StringBuilder indent = new StringBuilder();
        for (int j = 0; j < iteration; j++) {
            indent.append("  ");
        }
        // Go through each line in the file
        for (int i = startingLine; i < currentLines.size(); i++) {
            String line = currentLines.get(i);
            if (!line.startsWith(indent.toString())) return;
            if (line.startsWith("#")) continue;
            if (line.startsWith(indent.toString() + divisions[iteration]) ||
                    line.startsWith(indent.toString() + "'" + divisions[iteration] + "'")) {
                iteration += 1;
                if (iteration == divisions.length) {
                    int currentLine = i;
                    if (iteration == 1) {
                        currentLines.add(currentLine, "");
                        currentLine++;
                    }
                    String[] rawComment = comments.get(path).split("\n");
                    for (String commentPart : rawComment) {
                        currentLines.add(currentLine, indent + "# " + commentPart);
                        currentLine++;
                    }
                    break;
                } else {
                    writeComment(path, divisions, iteration, i + 1);
                }
            }
        }
    }

    public void writeSections() {
        // For each path the section is to be written above...
        for (String path : sections.keySet()) {
            String[] divisions = path.split("\\.");

            writeSection(path, divisions, 0);
        }
    }

    private void writeSection(String path, String[] divisions, int iteration) {
        StringBuilder indent = new StringBuilder();
        for (int j = 0; j < iteration; j++) {
            indent.append("  ");
        }

        // For each line in the file currently...
        for (int i = 0; i < currentLines.size(); i++) {
            String line = currentLines.get(i);
            if (line.startsWith(indent.toString() + divisions[iteration]) ||
                    line.startsWith(indent.toString() + "'" + divisions[iteration] + "'")) {
                iteration += 1;
                if (iteration == divisions.length) {
                    String section = sections.get(path);
                    StringBuilder length = new StringBuilder();
                    length.append("###");

                    List<String> sectionList = new ArrayList<>(Arrays.asList(section.split("\n")));
                    int largestString = sectionList.get(0).length();

                    for (String s : sectionList) {
                        if (s.length() > largestString) {
                            largestString = s.length();
                        }
                    }

                    for (int j = 0; j < largestString; j++) {
                        length.append("#");
                    }
                    length.append("###");
                    currentLines.add(i, indent + length.toString());

                    // This has to run in reverse to get the right order
                    for (int l = (sectionList.size() - 1); l > -1; l--) {
                        String s = sectionList.get(l);
                        currentLines.add(i, indent + "#  " + Utilities.getPaddedString(s, ' ', largestString, sectionAlign.getOrDefault(path, Utilities.AlignText.CENTER)) + "  #");
                    }
                    currentLines.add(i, indent + length.toString());
                    currentLines.add(i, "");
                    break;
                } else {
                    writeSection(path, divisions, iteration);
                }
            }
        }
    }

    private void save(boolean isConfig) {
        try {
            if (isConfig) {
                tempConfig.save(file);
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    if (currentLine.startsWith("#")) continue;
                    currentLines.add(currentLine);
                }
                reader.close();
            } else {
                // Opens up a new file writer
                FileWriter writer = new FileWriter(file);
                // For each line to write...
                for (String line : currentLines) {
                    // Write that and add in a break.
                    writer.write(line);
                    writer.write("\n");
                }
                // Close the writer.
                writer.close();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
    public <T> T getObject(String s, Class<T> aClass) {
        return configuration.getObject(s, aClass);
    }

    @Override
    public <T> T getObject(String s, Class<T> aClass, T t) {
        return configuration.getObject(s, aClass, t);
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(String s, Class<T> aClass) {
        return configuration.getSerializable(s, aClass);
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(String s, Class<T> aClass, T t) {
        return configuration.getSerializable(s, aClass, t);
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
        // Resets the `currentLines` to clear the previous generations
        currentLines = new ArrayList<>();

        // Set the new data
        configuration.set(tag, data);
        tempConfig.set(tag, data);

        // Handle the saving now
        save(true);

        writeSections();
        writeComments();
        save(false);
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
    public boolean move(String oldKey, String newKey) {
        if (contains(oldKey)) {

            // Will ensure the comments get moved as well
            if ((!comments.containsKey(newKey)) && comments.containsKey(oldKey)) comments.put(newKey, comments.get(oldKey));
            if ((!sections.containsKey(newKey)) && sections.containsKey(oldKey)) sections.put(newKey, sections.get(oldKey));

            set(newKey, get(oldKey));
            set(oldKey, null);
            return true;
        }
        return false;
    }

    public Location getLocation(String path) {
        return this.getSerializable(path, Location.class);
    }

    public Location getLocation(String path, Location def) {
        return this.getSerializable(path, Location.class, def);
    }

    public boolean isLocation(String path) {
        return this.getSerializable(path, Location.class) != null;
    }

}