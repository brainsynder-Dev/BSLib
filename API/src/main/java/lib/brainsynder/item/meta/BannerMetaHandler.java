package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaHandler;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BannerMetaHandler extends MetaHandler<BannerMeta> {

    public BannerMetaHandler(BannerMeta meta) {
        super(meta);
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof BannerMeta)) return;
        BannerMeta bannerMeta = (BannerMeta) meta;
        if (!bannerMeta.getPatterns().isEmpty()) {
            StorageTagList list = new StorageTagList();
            bannerMeta.getPatterns().forEach(pattern -> {
                StorageTagCompound tagCompound = new StorageTagCompound();
                tagCompound.setString("type", pattern.getPattern().name());
                tagCompound.setString("color", pattern.getColor().name());
                list.appendTag(tagCompound);
            });
            updateCompound(new StorageTagCompound().setTag("patterns", list));
        }
    }

    @Override
    public void fromCompound(StorageTagCompound compound) {
        super.fromCompound(compound);
        if (compound.hasKey("patterns")) {
            List<Pattern> patterns = new ArrayList<>();
            StorageTagList list = (StorageTagList) compound.getTag("patterns");
            list.getTagList().forEach(storageBase -> {
                StorageTagCompound tagCompound = (StorageTagCompound) storageBase;
                PatternType type = PatternType.valueOf(tagCompound.getString("type", "BASE"));
                DyeColor color = DyeColor.valueOf(tagCompound.getString("color", "WHITE"));
                patterns.add(new Pattern(color, type));
            });

            modifyMeta(value -> {
                value.setPatterns(patterns);
                return value;
            });
        }
    }
}
