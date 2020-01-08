package lib.brainsynder.item.meta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import lib.brainsynder.item.MetaBuilder;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.reflection.FieldAccessor;
import lib.brainsynder.reflection.Reflection;
import lib.brainsynder.utils.Base64Wrapper;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collection;
import java.util.UUID;

public class SkullMetaBuilder extends MetaBuilder<SkullMeta> {
    private String owner = "", texture = "";

    @Deprecated
    public void setOwner (String owner) {
        this.owner = owner;
        modifyMeta(value -> {
            value.setOwner(owner);
            return value;
        });
    }

    @Deprecated
    public String getOwner() {
        return owner;
    }

    public void setTexture (String texture) {
        if (texture.startsWith("http")) texture = Base64Wrapper.encodeString("{\"textures\":{\"SKIN\":{\"url\":\"" + texture + "\"}}}");
        String finalTexture = texture;
        this.texture = texture;
        modifyMeta(value -> {
            if (finalTexture.length() > 17) {
                return applyTextureToMeta(value, createProfile(finalTexture));
            }else{
                value.setOwner(finalTexture);
            }
            return value;
        });
    }

    public String getTexture() {
        return texture;
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof SkullMeta)) return;
        SkullMeta skullMeta = (SkullMeta) meta;
        if (skullMeta.hasOwner()) setOwner(skullMeta.getOwner());
        GameProfile profile = getGameProfile(skullMeta);
        if (profile != null) setTexture(getTexture(profile));
    }

    @Override
    public void loadCompound(StorageTagCompound compound) {
        super.loadCompound(compound);
        if (compound.hasKey("owner")) setOwner(compound.getString("owner"));
        if (compound.hasKey("texture")) setTexture(compound.getString("texture"));
    }

    @Override
    public StorageTagCompound toCompound() {
        StorageTagCompound compound = super.toCompound();
        if (!owner.isEmpty()) compound.setString("owner", owner);
        if (!texture.isEmpty()) compound.setString("texture", texture);
        return compound;
    }

    private GameProfile createProfile(String data) {
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
    private SkullMeta applyTextureToMeta(SkullMeta meta, GameProfile profile) {
        Class craftMetaSkull = Reflection.getCBCClass("inventory.CraftMetaSkull");
        Class c = craftMetaSkull.cast(meta).getClass();
        FieldAccessor field = FieldAccessor.getField(c, "profile", GameProfile.class);
        field.set(meta, profile);
        return meta;
    }
    private GameProfile getGameProfile(SkullMeta meta) {
        Class craftMetaSkull = Reflection.getCBCClass("inventory.CraftMetaSkull");
        Class c = craftMetaSkull.cast(meta).getClass();
        FieldAccessor<GameProfile> field = FieldAccessor.getField(c, "profile", GameProfile.class);
        return field.get(meta);
    }
    private String getTexture (GameProfile profile) {
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
}
