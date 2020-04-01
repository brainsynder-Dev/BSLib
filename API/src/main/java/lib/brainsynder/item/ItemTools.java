package lib.brainsynder.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import lib.brainsynder.item.meta.*;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.reflection.FieldAccessor;
import lib.brainsynder.reflection.Reflection;
import org.bukkit.inventory.meta.*;

import java.util.Collection;
import java.util.UUID;

class ItemTools {
    /*
    Caused by: java.lang.IllegalArgumentException: Name and ID cannot both be blank
    at com.mojang.authlib.GameProfile.<init>(GameProfile.java:26) ~[spigot-1.15.1.jar:git-Spigot-2ee05fe-d31f05f]
    at org.bukkit.craftbukkit.v1_15_R1.inventory.CraftMetaSkull.setOwner(CraftMetaSkull.java:158) ~[spigot-1.15.1.jar:git-Spigot-2ee05fe-d31f05f]
    at cskulls.brainsynder.shaded.bslib.item.meta.SkullMetaBuilder.lambda$setTexture$1(SkullMetaBuilder.java:42) ~[?:?]
    at cskulls.brainsynder.shaded.bslib.item.MetaBuilder.modifyMeta(MetaBuilder.java:22) ~[?:?]
    at cskulls.brainsynder.shaded.bslib.item.meta.SkullMetaBuilder.setTexture(SkullMetaBuilder.java:38) ~[?:?]
    at cskulls.brainsynder.shaded.bslib.item.meta.SkullMetaBuilder.fromItemMeta(SkullMetaBuilder.java:58) ~[?:?]
    at cskulls.brainsynder.shaded.bslib.item.ItemBuilder.getMeta(ItemBuilder.java:281) ~[?:?]
    at cskulls.brainsynder.shaded.bslib.item.ItemBuilder.isSimilar(ItemBuilder.java:196) ~[?:?]
    at cskulls.brainsynder.Inventory.Menu.onClick(Menu.java:78) ~[?:?]
    at cskulls.brainsynder.Listeners.MenuClick.onClick(MenuClick.java:57) ~[?:?]
     */
    protected static GameProfile createProfile(String data) {
        return createProfile("", data);
    }
    protected static GameProfile createProfile(String owner, String data) {
        try {
            GameProfile profile = new GameProfile(UUID.randomUUID(), (owner.isEmpty() ? "Steve" : owner));
            PropertyMap propertyMap = profile.getProperties();
            Property property = new Property("textures", data);
            propertyMap.put("textures", property);
            return profile;
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }
    protected static SkullMeta applyTextureToMeta(SkullMeta meta, GameProfile profile) {
        Class craftMetaSkull = Reflection.getCBCClass("inventory.CraftMetaSkull");
        Class c = craftMetaSkull.cast(meta).getClass();
        FieldAccessor field = FieldAccessor.getField(c, "profile", GameProfile.class);
        field.set(meta, profile);
        return meta;
    }
    protected static GameProfile getGameProfile(SkullMeta meta) {
        Class craftMetaSkull = Reflection.getCBCClass("inventory.CraftMetaSkull");
        FieldAccessor<GameProfile> field = FieldAccessor.getField(craftMetaSkull, "profile", GameProfile.class);
        return field.get(meta);
    }
    protected static String getTexture (GameProfile profile) {
        PropertyMap propertyMap = profile.getProperties();
        Collection<Property> properties = propertyMap.get("textures");
        String text = "";

        for (Property property : properties) {
            if (property.getName().equals("textures")) {
                text = property.getValue();
                break;
            }
        }
        return text;
    }

    // This method might change in the future to better handle the other ItemMetas
    static MetaHandler getHandler (ItemMeta meta) {
        if (meta == null) return null;
        MetaHandler handler = null;
        if (meta instanceof BannerMeta) handler = new BannerMetaHandler((BannerMeta) meta);
        if (meta instanceof BookMeta) handler = new BookMetaHandler((BookMeta) meta);
        if (meta instanceof FireworkMeta) handler = new FireworkMetaHandler((FireworkMeta) meta);
        if (meta instanceof KnowledgeBookMeta) handler = new KnowledgeBookMetaHandler((KnowledgeBookMeta) meta);
        if (meta instanceof LeatherArmorMeta) handler = new LeatherArmorMetaHandler((LeatherArmorMeta) meta);
        if (meta instanceof MapMeta) handler = new MapMetaHandler((MapMeta) meta);
        if (meta instanceof PotionMeta) handler = new PotionMetaHandler((PotionMeta) meta);
        if (meta instanceof SkullMeta) handler = new SkullMetaHandler((SkullMeta) meta);
        if (isInstance(meta, "TropicalFishBucketMeta")) handler = new TropicalFishBucketMetaHandler(meta);
        if (isInstance(meta, "CrossbowMeta")) handler = new CrossbowMetaHandler(meta);

        if ((handler != null) && (!handler.isSupported())) return null;
        return handler;
    }

    private static boolean isInstance (ItemMeta meta, String name) {
        if (meta.getClass().getInterfaces() == null) return false;
        if (meta.getClass().getInterfaces()[0] == null) return false;
        Class metaClass = meta.getClass().getInterfaces()[0];
        String className = metaClass.getSimpleName().replace("Meta", "").replace("Craft", "")+"Meta";
        return (name.equals(className));
    }

    static StorageTagCompound toCompound (ItemMeta meta) {
        StorageTagCompound compound = new StorageTagCompound();
        MetaHandler handler = getHandler(meta);
        if (handler != null) compound = handler.getCompound();
        return compound;
    }

    static ItemMeta fromCompound (ItemMeta meta, StorageTagCompound compound) {
        MetaHandler handler = getHandler(meta);
        if (handler != null) {
            handler.fromCompound(compound);
            meta = handler.getMeta();
        }

        return meta;
    }
}
