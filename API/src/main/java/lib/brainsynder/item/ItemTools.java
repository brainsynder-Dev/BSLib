package lib.brainsynder.item;

import lib.brainsynder.item.meta.*;
import lib.brainsynder.nbt.StorageTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.*;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
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
    protected static PlayerProfile createProfile(String data) {
        return createProfile("", data);
    }
    protected static PlayerProfile createProfile(String owner, String data) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), (owner.isEmpty() ? "Steve" : owner));
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL(data));
        } catch (MalformedURLException ignored) {}
        profile.setTextures(textures);
        return profile;
    }
    protected static PlayerProfile getGameProfile(SkullMeta meta) {
        return meta.getOwnerProfile();
    }
    protected static String getTexture (PlayerProfile profile) {
        return profile.getTextures().getSkin().toString();
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
        if (meta.getClass().getInterfaces().length == 0) return false;
        Class<?> metaClass = meta.getClass().getInterfaces()[0];
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
