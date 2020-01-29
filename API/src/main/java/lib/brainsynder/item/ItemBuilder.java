package lib.brainsynder.item;

import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import lib.brainsynder.nbt.StorageTagString;
import lib.brainsynder.reflection.FieldAccessor;
import lib.brainsynder.reflection.Reflection;
import lib.brainsynder.utils.ReturnValue;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {
    private ItemStack item;
    private ItemMeta meta;
    private MetaBuilder metaBuilder;

    public ItemBuilder (Material material) {
        this(material, 1);
    }
    public ItemBuilder (Material material, int amount) {
        item = new ItemStack(material, amount);
        meta = item.getItemMeta();
    }

    public static ItemBuilder fromCompound (StorageTagCompound compound) {
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
                lore.add(((StorageTagString)storageBase).getString());
            });
            builder.withLore(lore);
        }
        if (compound.hasKey("flags")) {
            StorageTagList list = (StorageTagList) compound.getTag("flags");
            list.getTagList().forEach(storageBase -> {
                ItemFlag flag = ItemFlag.valueOf(((StorageTagString)storageBase).getString());
                builder.withFlag(flag);
            });
        }

        if (compound.hasKey("meta")) {
            builder.metaBuilder.loadCompound(compound.getCompoundTag("meta"));
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

            MetaBuilder metaBuilder = getMeta(meta);
            if (metaBuilder != null) {
                metaBuilder.fromItemMeta(meta);
                builder.metaBuilder = metaBuilder;
            }
        }

        return builder;
    }

    /*  LORE METHODS  */
    public ItemBuilder withLore(List<String> lore) {
        meta.setLore(translate(lore));
        return this;
    }
    public ItemBuilder addLore(String... lore) {
        List<String> itemLore = new ArrayList<>();
        if (meta.hasLore()) itemLore = meta.getLore();

        List<String> finalItemLore = itemLore;
        Arrays.asList(lore).forEach(s -> finalItemLore.add(translate(s)));
        meta.setLore(finalItemLore);
        return this;
    }
    public ItemBuilder clearLore() {
        meta.getLore().clear();
        return this;
    }
    public ItemBuilder removeLore(String lore) {
        List<String> itemLore = new ArrayList<>();
        if (meta.hasLore()) itemLore = meta.getLore();
        itemLore.remove(translate(lore));
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
    public boolean isUnbreakable () {
        return meta.isUnbreakable();
    }


    /**
     * Will set the Items display name
     * @param name - Custom name for the item
     */
    public ItemBuilder withName(String name) {
        meta.setDisplayName(translate(name));
        return this;
    }

    /**
     * Sets the Items durability (If on pre-1.13 then will change the data)
     */
    public ItemBuilder withDurability(int durability) {
        item.setDurability((short) durability);
        return this;
    }

    public String getName () {
        if (meta.hasDisplayName()) return meta.getDisplayName();
        return WordUtils.capitalizeFully(item.getType().name().toLowerCase().replace("_", " "));
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    public boolean isSimilar (ItemStack item) {
        List<Boolean> values = new ArrayList<>();
        if (item == null) return false;
        ItemStack main = build();
        if (main.getType() == item.getType()) {
            if (item.hasItemMeta() && main.hasItemMeta()) {
                ItemMeta mainMeta = main.getItemMeta();
                ItemMeta checkMeta = item.getItemMeta();
                if (mainMeta.hasDisplayName() && checkMeta.hasDisplayName()) {
                    values.add(mainMeta.getDisplayName().equals(checkMeta.getDisplayName()));
                }

                if (mainMeta.hasLore() && checkMeta.hasLore()) {
                    values.add(mainMeta.getLore().equals(checkMeta.getLore()));
                }

                if (mainMeta.hasEnchants() && checkMeta.hasEnchants()) {
                    values.add(mainMeta.getEnchants().equals(checkMeta.getEnchants()));
                }

                if ((getMeta(checkMeta) != null) && (metaBuilder != null)) {
                    values.add(getMeta(checkMeta).toCompound().toString().equals(metaBuilder.toCompound().toString()));
                }

                if (!values.isEmpty()) return !values.contains(false);
            }
        }

        return main.isSimilar(item);

    }

    public <T extends MetaBuilder> ItemBuilder handleMeta (Class<T> clazz, ReturnValue<T> value) {
        MetaBuilder builder = getMeta(meta);
        if (builder == null) return this;

        metaBuilder = Reflection.initiateClass(clazz);
        FieldAccessor<ItemMeta> field = FieldAccessor.getField(MetaBuilder.class, "meta", ItemMeta.class);
        field.set(metaBuilder, meta);
        if (!clazz.isAssignableFrom(builder.getClass())) return this;
        metaBuilder.fromItemMeta(meta);
        value.run((T) metaBuilder);
        meta = field.get(metaBuilder); // Should update the ItemMeta field
        return this;
    }

    public StorageTagCompound toCompound () {
        StorageTagCompound compound = new StorageTagCompound ();
        compound.setString("material", item.getType().name());
        if (item.getAmount() > 1) compound.setInteger("amount", item.getAmount());
        if (meta.hasDisplayName()) compound.setString("name", meta.getDisplayName());
        if (meta.isUnbreakable()) compound.setBoolean("unbreakable", meta.isUnbreakable());
        if (item.getDurability() > 0) compound.setInteger("durability", item.getDurability());

        if (meta.hasLore()) {
            StorageTagList lore = new StorageTagList();
            meta.getLore().forEach(line -> lore.appendTag(new StorageTagString(line)));
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
        if (metaBuilder != null) compound.setTag("meta", metaBuilder.toCompound());
        return compound;
    }


    /* PRIVATE METHODS */
    private String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    private List<String> translate(List<String> message) {
        ArrayList<String> newLore = new ArrayList<>();
        message.forEach(msg -> newLore.add(translate(msg)));
        return newLore;
    }

    private static MetaBuilder getMeta (ItemMeta meta) {
        if (meta == null) return null;
        if (meta.getClass().getInterfaces() == null) return null;
        if (meta.getClass().getInterfaces()[0] == null) return null;
        Class<?> clazz;

        try {
            Class metaClass = meta.getClass().getInterfaces()[0];
            clazz = Class.forName("lib.brainsynder.item.meta."+metaClass.getSimpleName().replace("Meta", "").replace("Craft", "")+"MetaBuilder");
        }catch (ClassNotFoundException e) {
            clazz = MetaBuilder.class;
        }
        MetaBuilder metaBuilder = Reflection.initiateClass(clazz);
        if (metaBuilder == null) return null;
        FieldAccessor<ItemMeta> field = FieldAccessor.getField(clazz, "meta", ItemMeta.class);
        field.set(metaBuilder, meta);
        metaBuilder.fromItemMeta(meta);
        return metaBuilder;
    }
}
