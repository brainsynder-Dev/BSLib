package lib.brainsynder.item;

import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import lib.brainsynder.nbt.StorageTagString;
import lib.brainsynder.utils.Base64Wrapper;
import lib.brainsynder.utils.Colorize;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class ItemBuilder {
    private Map<String, String> replaceLore = new HashMap<>();
    private Map<String, String> replaceName = new HashMap<>();
    private final ItemStack item;
    private ItemMeta meta;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        item = new ItemStack(material, amount);
        meta = item.getItemMeta();
    }

    public static ItemBuilder fromCompound(StorageTagCompound compound) {
        Material material = Material.getMaterial(compound.getString("material"));
        ItemBuilder builder = new ItemBuilder(material, compound.getInteger("amount", 1));
        if (compound.hasKey("enchants")) {
            StorageTagList enchants = (StorageTagList) compound.getTag("enchants");
            enchants.getTagList().forEach(base -> {
                StorageTagCompound enchant = (StorageTagCompound) base;
                Enchantment enchantment = Enchantment.getByName(enchant.getString("name"));
                int level = enchant.getInteger("level", 1);
                builder.withEnchant(enchantment, level);
            });
        }
        if (compound.hasKey("durability")) builder.withDurability(compound.getInteger("durability"));
        if (compound.hasKey("name")) builder.withName(compound.getString("name"));
        if (compound.hasKey("unbreakable")) builder.setUnbreakable(compound.getBoolean("unbreakable"));
        if (compound.hasKey("lore")) {
            StorageTagList list = (StorageTagList) compound.getTag("lore");
            List<String> lore = new ArrayList<>();
            list.getTagList().forEach(storageBase -> {
                lore.add(((StorageTagString) storageBase).getString());
            });
            builder.withLore(lore);
        }
        if (compound.hasKey("flags")) {
            StorageTagList list = (StorageTagList) compound.getTag("flags");
            list.getTagList().forEach(storageBase -> {
                ItemFlag flag = ItemFlag.valueOf(((StorageTagString) storageBase).getString());
                builder.withFlag(flag);
            });
        }

        if (compound.hasKey("meta")) {
            ItemTools.fromCompound(builder.meta, compound.getCompoundTag("meta"));
        }

        return builder;
    }

    public static ItemBuilder fromItem(ItemStack item) {
        ItemBuilder builder = new ItemBuilder(item.getType(), item.getAmount());
        if (!item.getEnchantments().isEmpty()) item.getEnchantments().forEach(builder::withEnchant);
        if (item.getDurability() >= 0) builder.withDurability(item.getDurability());
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) builder.withName(meta.getDisplayName());
            if (meta.hasLore()) builder.withLore(meta.getLore());
            if (!meta.getItemFlags().isEmpty()) meta.getItemFlags().forEach(builder::withFlag);
            builder.meta = meta;
        }

        return builder;
    }

    public ItemBuilder replaceInName(String key, Object replacement) {
        replaceName.put(key, String.valueOf(replacement));
        return this;
    }

    public ItemBuilder replaceInLore(String key, Object replacement) {
        replaceLore.put(key, String.valueOf(replacement));
        return this;
    }

    /*  LORE METHODS  */
    public ItemBuilder withLore(List<String> lore) {
        meta.setLore(translate(lore, false));
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        List<String> itemLore = new ArrayList<>();
        if (meta.hasLore()) itemLore = meta.getLore();

        List<String> finalItemLore = itemLore;
        Arrays.asList(lore).forEach(s -> finalItemLore.add(translate(s, false)));
        meta.setLore(finalItemLore);
        return this;
    }

    public ItemBuilder clearLore() {
        if (meta.hasLore()) meta.getLore().clear();
        return this;
    }

    public ItemBuilder removeLore(String lore) {
        List<String> itemLore = new ArrayList<>();
        if (meta.hasLore()) itemLore = meta.getLore();
        itemLore.remove(translate(lore, false));
        meta.setLore(itemLore);
        return this;
    }

    /*  ENCHANT METHODS  */
    public ItemBuilder withEnchant(Enchantment enchant, int level) {
        item.addUnsafeEnchantment(enchant, level);
        return this;
    }

    public ItemBuilder removeEnchant(Enchantment enchant) {
        item.removeEnchantment(enchant);
        return this;
    }

    /*  ITEMFLAG METHODS  */
    public ItemBuilder withFlag(ItemFlag flag) {
        meta.addItemFlags(flag);
        return this;
    }

    public ItemBuilder removeFlag(ItemFlag flag) {
        meta.removeItemFlags(flag);
        return this;
    }

    /* UNBREAKABLE */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public boolean isUnbreakable() {
        return meta.isUnbreakable();
    }


    /**
     * Will set the Items display name
     *
     * @param name - Custom name for the item
     */
    public ItemBuilder withName(String name) {
        meta.setDisplayName(translate(name, false));
        return this;
    }

    /**
     * Sets the Items durability (If on pre-1.13 then will change the data)
     */
    public ItemBuilder withDurability(int durability) {
        item.setDurability((short) durability);
        return this;
    }

    public String getName() {
        if (meta.hasDisplayName()) return meta.getDisplayName();
        return WordUtils.capitalizeFully(item.getType().name().toLowerCase().replace("_", " "));
    }

    public ItemStack build() {
        if (item == null) return new ItemStack(Material.STONE);
        if (item.getType() == Material.AIR) return item;
        List<String> newLore = new ArrayList<>();
        if (meta.hasLore()) {
            for (String line : meta.getLore()) {
                for (Map.Entry<String, String> entry : replaceLore.entrySet()) {
                    String key = entry.getKey();
                    String replacement = entry.getValue();
                    line = line.replace(key, replacement);
                }
                newLore.add(line);
            }
            meta.setLore(newLore);
        }
        if (meta.hasDisplayName()) {
            String name = meta.getDisplayName();
            for (Map.Entry<String, String> entry : replaceName.entrySet()) {
                String key = entry.getKey();
                String replacement = entry.getValue();
                name = name.replace(key, replacement);
            }
            meta.setDisplayName(name);
        }
        item.setItemMeta(meta);
        return item;
    }

    public boolean isSimilar(ItemStack item) {
        if (item == null) return false;
        ItemStack main = build();
        if (main.getType() == item.getType()) {
            if (item.hasItemMeta() && main.hasItemMeta()) {
                ItemMeta mainMeta = main.getItemMeta();
                ItemMeta checkMeta = item.getItemMeta();
                if (mainMeta.hasDisplayName() && checkMeta.hasDisplayName()) {
                    if (!mainMeta.getDisplayName().equals(checkMeta.getDisplayName())) return false;
                }

                if (mainMeta.hasLore() && checkMeta.hasLore()) {
                    if (!mainMeta.getLore().equals(checkMeta.getLore())) return false;
                }

                if (! mainMeta.getItemFlags().equals(checkMeta.getItemFlags())) return false;

                if (mainMeta.hasEnchants() && checkMeta.hasEnchants()) {
                    if (!mainMeta.getEnchants().equals(checkMeta.getEnchants())) return false;
                }

                return ItemTools.toCompound(mainMeta).equals(ItemTools.toCompound(checkMeta));
            }
        }

        return main.isSimilar(item);

    }

    public boolean isSimilar(ItemBuilder builder) {
        if (builder == null) return false;
        return isSimilar(builder.build());
    }

    public ItemBuilder clone () {
        ItemBuilder builder = fromCompound(toCompound());
        builder.replaceName = replaceName;
        builder.replaceLore = replaceLore;
        return builder;
    }


    /* SKULL METHODS */
    /**
     * If the Item is a player_skull it will set the texture of the skull
     * This is here due to {@link SkullMeta} not having a method to do this
     */
    public ItemBuilder setTexture (String texture) {
        if (texture == null) return this;
        if (texture.isEmpty()) return this;
        if (texture.startsWith("http")) texture = Base64Wrapper.encodeString("{\"textures\":{\"SKIN\":{\"url\":\"" + texture + "\"}}}");
        String finalTexture = texture;
        handleMeta(SkullMeta.class, value -> {
            if (finalTexture.length() > 17) {
                return ItemTools.applyTextureToMeta(value, ItemTools.createProfile(finalTexture));
            }else{
                value.setOwner(finalTexture);
            }
            return value;
        });
        return this;
    }

    /**
     * If the Item is a player_skull it will return the Base64 Encoded texture url
     * This is here due to {@link SkullMeta} not having a method to do this
     */
    public String getTexture() {
        return getMetaValue(SkullMeta.class, value -> ItemTools.getTexture(ItemTools.getGameProfile(value)));
    }

    public StorageTagCompound toCompound() {
        StorageTagCompound compound = new StorageTagCompound();
        compound.setEnum("material", item.getType());
        if (item.getAmount() > 1) compound.setInteger("amount", item.getAmount());
        if (meta.hasDisplayName()) compound.setString("name", Colorize.removeHexColor(meta.getDisplayName().replace(ChatColor.COLOR_CHAR, '&')));
        if (meta.isUnbreakable()) compound.setBoolean("unbreakable", meta.isUnbreakable());
        if (item.getDurability() > 0) compound.setInteger("durability", item.getDurability());

        if (meta.hasLore()) {
            StorageTagList lore = new StorageTagList();
            meta.getLore().forEach(line -> lore.appendTag(new StorageTagString(Colorize.removeHexColor(line.replace(ChatColor.COLOR_CHAR, '&')).replace("\"", ""))));
            compound.setTag("lore", lore);
        }

        if (!item.getEnchantments().isEmpty()) {
            StorageTagList enchants = new StorageTagList();
            item.getEnchantments().forEach((enchantment, level) -> {
                StorageTagCompound enchant = new StorageTagCompound();
                enchant.setString("name", enchantment.getName());
                enchant.setInteger("level", level);
                enchants.appendTag(enchant);
            });
            compound.setTag("enchants", enchants);
        }

        if (!meta.getItemFlags().isEmpty()) {
            StorageTagList flags = new StorageTagList();
            meta.getItemFlags().forEach(itemFlag -> flags.appendTag(new StorageTagString(itemFlag.name())));
            compound.setTag("flags", flags);
        }

        StorageTagCompound meta = ItemTools.toCompound(this.meta);
        if (!meta.hasNoTags()) compound.setTag("meta", meta);
        return compound;
    }


    /* PRIVATE METHODS */
    private List<String> translate(List<String> message, boolean strip) {
        ArrayList<String> newLore = new ArrayList<>();
        message.forEach(msg -> {
            if (strip) {
                msg = msg.replace(ChatColor.COLOR_CHAR, '&');
                msg = Colorize.removeHexColor(msg);
            } else {
                msg = Colorize.translateBungeeHex(msg);
            }
            newLore.add(msg);
        });
        return newLore;
    }
    private String translate(String message, boolean strip) {
        if (strip) {
            message = message.replace(ChatColor.COLOR_CHAR, '&');
            message = Colorize.removeHexColor(message);
        } else {
            message = Colorize.translateBungeeHex(message);
        }
        return message;
    }

    public static boolean isAir(Material mat) {
        return mat.name().endsWith("AIR") && !mat.name().endsWith("AIRS");
    }

    public static boolean isAir(ItemStack item) {
        return item == null || isAir(item.getType());
    }

    public  <T extends ItemMeta> ItemBuilder handleMeta(Class<T> clazz, InnerReturn<T> meta) {
        if (!clazz.isAssignableFrom(this.meta.getClass())) return this;
        this.meta = meta.run((T)this.meta);
        item.setItemMeta(this.meta);
        return this;
    }

    public  <R, T> R getMetaValue(Class<T> clazz, InnerReturnValue<T, R> meta) {
        if (!clazz.isAssignableFrom(this.meta.getClass())) return null;
        return meta.run((T)this.meta);
    }

    public interface InnerReturn<T> {
        T run(T value);
    }

    public interface InnerReturnValue<T, R> {
        R run(T value);
    }
}
