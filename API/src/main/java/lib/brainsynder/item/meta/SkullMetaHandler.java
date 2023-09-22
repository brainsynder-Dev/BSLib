package lib.brainsynder.item.meta;

import com.eclipsesource.json.Json;
import com.mojang.authlib.GameProfile;
import lib.brainsynder.item.MetaHandler;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.utils.Base64Wrapper;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullMetaHandler extends MetaHandler<SkullMeta> {

    public SkullMetaHandler(SkullMeta meta) {
        super(meta);
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof SkullMeta)) return;
        SkullMeta skullMeta = (SkullMeta) meta;
        StorageTagCompound compound = new StorageTagCompound();
        if (skullMeta.hasOwner() && (!skullMeta.getOwner().equals("Steve"))) compound.setString("owner", skullMeta.getOwner());
        if (skullMeta.getOwnerProfile() != null) {
            String texture = getTexture(skullMeta.getOwnerProfile());
            if ((texture != null) && (!texture.isEmpty())) compound.setString("texture", texture);
        }
        updateCompound(compound);
    }

    @Override
    public void fromCompound(StorageTagCompound compound) {
        super.fromCompound(compound);
        modifyMeta(value -> {
            if (compound.hasKey("owner")) value.setOwner(compound.getString("owner"));
            if (compound.hasKey("texture")) {
                String texture = compound.getString("texture");
                if (texture == null) return value;
                if (texture.isEmpty()) return value;
                if ((!texture.startsWith("http")) && Base64Wrapper.isEncoded(texture)) {
                    texture = Base64Wrapper.decodeString(texture);
                    texture = Json.parse(texture).asObject().get("textures").asObject().get("SKIN").asObject().get("url").asString();
                }
                String finalTexture = texture;
                value.setOwnerProfile(createProfile(finalTexture));
            }
            return value;
        });
    }
}
