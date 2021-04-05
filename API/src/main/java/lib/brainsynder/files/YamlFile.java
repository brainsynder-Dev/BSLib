package lib.brainsynder.files;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lib.brainsynder.files.options.YamlOption;
import lib.brainsynder.utils.Colorize;
import lib.brainsynder.utils.Utilities;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
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
    private final HashMap<String, String> movedKeys;
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
        movedKeys = new HashMap<>();
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

        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public abstract void loadDefaults();

    public void remove (YamlOption option) {
        remove(option.getPath());
    }
    public void remove(String key) {
        if (configuration.contains(key)) {
            set(key, null);
        }
    }

    public void addComment(String path, String comment) {
        comments.put(fetchKey(path), comment);
    }

    public void addSectionHeader(String path, String text) {
        sections.put(fetchKey(path), text);
    }
    public void addSectionHeader(String path, Utilities.AlignText alignText, String text) {
        sections.put(fetchKey(path), text);
        sectionAlign.put(fetchKey(path), alignText);
    }

    @Override
    public void addDefault(String key, Object value) {
        configuration.addDefault(fetchKey(key), value);
        tempConfig.set(fetchKey(key), configuration.get(key));
    }

    public void addDefault(String key, Object value, String comment) {
        configuration.addDefault(fetchKey(key), value);
        tempConfig.set(fetchKey(key), configuration.get(key));
        addComment(key, comment);
    }


    public void addDefault(YamlOption option) {
        configuration.addDefault(option.getPath(), option.getDefault());
        tempConfig.set(option.getPath(), configuration.get(option.getPath()));
        if (!option.getComment().isEmpty()) addComment(option.getPath(), option.getComment());
    }

    public void addDefault(YamlOption option, String comment) {
        configuration.addDefault(option.getPath(), option.getDefault());
        tempConfig.set(option.getPath(), configuration.get(option.getPath()));
        addComment(option.getPath(), comment);
    }

    private void writeComments() {
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
                    String[] rawComment = comments.get(fetchKey(path)).split("\n");
                    for (String commentPart : rawComment) {
                        currentLines.add(currentLine, indent + "# " + commentPart);
                        currentLine++;
                    }
                    break;
                } else {
                    writeComment(fetchKey(path), divisions, iteration, i + 1);
                }
            }
        }
    }

    private void writeSections() {
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
                    String section = sections.get(fetchKey(path));
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
                    writeSection(fetchKey(path), divisions, iteration);
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

    public String getString(YamlOption option, boolean color) {
        return (color ? translate(getString(option)) : getString(option));
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
        return configuration.getString(fetchKey(tag), fallback);
    }
    public String getString(YamlOption option) {
        return getString(option.getPath(), String.valueOf(option.getDefault()));
    }

    @Override
    public boolean isString(String tag) {
        return configuration.isString(fetchKey(tag));
    }


    @Override
    public ItemStack getItemStack(String tag) {
        return getItemStack(tag, new ItemStack(Material.AIR));
    }

    @Override
    public ItemStack getItemStack(String tag, ItemStack fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getItemStack(fetchKey(tag), fallback);
    }

    @Override
    public boolean isItemStack(String tag) {
        return configuration.isItemStack(fetchKey(tag));
    }

    @Override
    public Color getColor(String tag) {
        return getColor(tag, Color.WHITE);
    }

    @Override
    public Color getColor(String tag, Color fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getColor(fetchKey(tag), fallback);
    }

    @Override
    public boolean isColor(String tag) {
        return configuration.isColor(fetchKey(tag));
    }


    @Override
    public ConfigurationSection getConfigurationSection(String tag) {
        return configuration.getConfigurationSection(fetchKey(tag));
    }

    @Override
    public boolean isConfigurationSection(String tag) {
        return configuration.isConfigurationSection(fetchKey(tag));
    }

    @Override
    public ConfigurationSection getDefaultSection() {
        return configuration.getDefaultSection();
    }


    @Override
    public boolean getBoolean(String tag) {
        return getBoolean(tag, false);
    }
    public boolean getBoolean(YamlOption option) {
        return getBoolean(option.getPath(), (Boolean) option.getDefault());
    }

    @Override
    public boolean getBoolean(String tag, boolean fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getBoolean(fetchKey(tag), fallback);
    }

    @Override
    public boolean isBoolean(String tag) {
        return configuration.isBoolean(fetchKey(tag));
    }


    @Override
    public int getInt(String tag) {
        return getInt(tag, 0);
    }
    public int getInt(YamlOption option) {
        return getInt(option.getPath(), Integer.parseInt(String.valueOf(option.getDefault())));
    }

    @Override
    public int getInt(String tag, int fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getInt(fetchKey(tag), fallback);
    }

    @Override
    public boolean isInt(String tag) {
        return configuration.isInt(fetchKey(tag));
    }


    @Override
    public double getDouble(String tag) {
        return getDouble(tag, 0);
    }
    public double getDouble(YamlOption option) {
        return getDouble(option.getPath(), Double.parseDouble(String.valueOf(option.getDefault())));
    }

    @Override
    public double getDouble(String tag, double fallback) {
        if (!contains(tag)) return fallback;
        return this.configuration.getDouble(fetchKey(tag), fallback);
    }

    @Override
    public boolean isDouble(String tag) {
        return configuration.isDouble(fetchKey(tag));
    }


    @Override
    public long getLong(String tag) {
        return getLong(tag, 0);
    }
    public long getLong(YamlOption option) {
        return getLong(option.getPath(), Long.parseLong(String.valueOf(option.getDefault())));
    }

    @Override
    public long getLong(String tag, long fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getLong(fetchKey(tag), fallback);
    }

    @Override
    public boolean isLong(String tag) {
        return configuration.isLong(fetchKey(tag));
    }


    @Override
    public List<?> getList(String tag) {
        return configuration.getList(fetchKey(tag));
    }

    @Override
    public List<?> getList(String tag, List<?> fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getList(fetchKey(tag), fallback);
    }

    @Override
    public boolean isList(String tag) {
        return configuration.isList(fetchKey(tag));
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
        return configuration.get(fetchKey(tag)) != null;
    }
    public boolean contains(YamlOption option) {
        return configuration.get(fetchKey(option.getPath())) != null;
    }

    @Override
    public boolean contains(String tag, boolean ignoreDefault) {
        return configuration.contains(fetchKey(tag), ignoreDefault);
    }

    @Override
    public boolean isSet(String tag) {
        return configuration.isSet(fetchKey(tag));
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
        return this.configuration.getStringList(fetchKey(tag));
    }


    @Override
    public List<Integer> getIntegerList(String tag) {
        return configuration.getIntegerList(fetchKey(tag));
    }

    @Override
    public List<Boolean> getBooleanList(String tag) {
        return configuration.getBooleanList(fetchKey(tag));
    }

    @Override
    public List<Double> getDoubleList(String tag) {
        return configuration.getDoubleList(fetchKey(tag));
    }

    @Override
    public List<Float> getFloatList(String tag) {
        return configuration.getFloatList(fetchKey(tag));
    }

    @Override
    public List<Long> getLongList(String tag) {
        return configuration.getLongList(fetchKey(tag));
    }

    @Override
    public List<Byte> getByteList(String tag) {
        return configuration.getByteList(fetchKey(tag));
    }

    @Override
    public List<Character> getCharacterList(String tag) {
        return configuration.getCharacterList(fetchKey(tag));
    }

    @Override
    public List<Short> getShortList(String tag) {
        return configuration.getShortList(fetchKey(tag));
    }

    @Override
    public List<Map<?, ?>> getMapList(String tag) {
        return configuration.getMapList(fetchKey(tag));
    }

    @Override
    public <T> T getObject(String s, Class<T> aClass) {
        return configuration.getObject(fetchKey(s), aClass);
    }

    @Override
    public <T> T getObject(String s, Class<T> aClass, T t) {
        return configuration.getObject(fetchKey(s), aClass, t);
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(String s, Class<T> aClass) {
        return configuration.getSerializable(fetchKey(s), aClass);
    }

    @Override
    public <T extends ConfigurationSerializable> T getSerializable(String s, Class<T> aClass, T t) {
        return configuration.getSerializable(fetchKey(s), aClass, t);
    }


    @Override
    public Vector getVector(String tag) {
        return configuration.getVector(fetchKey(tag));
    }

    @Override
    public Vector getVector(String tag, Vector fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getVector(fetchKey(tag), fallback);
    }

    @Override
    public boolean isVector(String tag) {
        return configuration.isVector(fetchKey(tag));
    }


    @Override
    public OfflinePlayer getOfflinePlayer(String tag) {
        return configuration.getOfflinePlayer(fetchKey(tag));
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String tag, OfflinePlayer fallback) {
        if (!contains(tag)) return fallback;
        return configuration.getOfflinePlayer(fetchKey(tag), fallback);
    }

    @Override
    public boolean isOfflinePlayer(String tag) {
        return configuration.isOfflinePlayer(fetchKey(tag));
    }


    public ConfigurationSection getSection(String tag) {
        return this.configuration.getConfigurationSection(fetchKey(tag));
    }

    @Override
    public Object get(String tag) {
        return this.configuration.get(fetchKey(tag));
    }
    public Object get(YamlOption option) {
        return get(option.getPath(), option.getDefault());
    }

    @Override
    public Object get(String tag, Object fallback) {
        if (!contains(tag)) return fallback;
        return this.configuration.get(fetchKey(tag));
    }

    private String translate(String msg) {
        return Colorize.translateBungeeHex(msg);
    }


    public void set(YamlOption option, Object data){
        set(option.getPath(), data);
    }
    @Override
    public void set(String tag, Object data) {
        // Resets the `currentLines` to clear the previous generations
        currentLines = new ArrayList<>();

        // Set the new data
        configuration.set(fetchKey(tag), data);
        tempConfig.set(fetchKey(tag), data);

        // Handle the saving now
        save(true);

        writeSections();
        writeComments();
        save(false);
    }

    @Override
    public ConfigurationSection createSection(String tag) {
        return configuration.createSection(fetchKey(tag));
    }

    @Override
    public ConfigurationSection createSection(String tag, Map<?, ?> fallback) {
        return configuration.createSection(fetchKey(tag), fallback);
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
            movedKeys.putIfAbsent(oldKey, newKey);

            // Will ensure the comments get moved as well
            if ((!comments.containsKey(newKey)) && comments.containsKey(oldKey)) comments.put(newKey, comments.get(oldKey));
            if ((!sections.containsKey(newKey)) && sections.containsKey(oldKey)) sections.put(newKey, sections.get(oldKey));

            set(newKey, get(oldKey));
            set(oldKey, null);
            return true;
        }
        return false;
    }

    @Override
    public void registerMovedKeys(String newKey, String... oldKeys) {
        Lists.newArrayList(oldKeys).forEach(oldKey -> movedKeys.putIfAbsent(oldKey, newKey));
    }

    // Checks if the key was moved, if it was it will return the correct key
    private String fetchKey (String key) {
        if (key == null) return null;
        if (key.isEmpty()) return key;
        return movedKeys.getOrDefault(key, key);
    }

    public Location getLocation(String path) {
        return this.getSerializable(fetchKey(path), Location.class);
    }

    public Location getLocation(String path, Location def) {
        return this.getSerializable(fetchKey(path), Location.class, def);
    }

    public boolean isLocation(String path) {
        return this.getSerializable(fetchKey(path), Location.class) != null;
    }

    public File getFile() {
        return file;
    }
}