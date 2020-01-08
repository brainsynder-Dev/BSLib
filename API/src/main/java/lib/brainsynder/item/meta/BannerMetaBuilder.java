package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaBuilder;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BannerMetaBuilder extends MetaBuilder<BannerMeta> {
    private List<Pattern> patterns = new ArrayList<>();

    public void setPatterns (List<Pattern> patterns) {
        this.patterns = patterns;
        modifyMeta(value -> {
            value.setPatterns(patterns);
            return value;
        });
    }

    public void addPattern (Pattern pattern) {
        patterns.add(pattern);
        setPatterns(patterns);
    }

    public void setPattern (int index, Pattern pattern) {
        patterns.set(index, pattern);
        setPatterns(patterns);
    }

    public void removePattern (Pattern pattern) {
        boolean canRemove = false;
        for (Pattern pat : patterns) {
            if (pat.equals(pattern)) {
                canRemove = true;
                break;
            }
        }
        if (canRemove) patterns.remove(pattern);
        setPatterns(patterns);
    }

    public void removePattern (int index) {
        patterns.remove(index);
        setPatterns(patterns);
    }

    public int count () {
        return patterns.size();
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof BannerMeta)) return;
        BannerMeta bannerMeta = (BannerMeta) meta;
        if (!bannerMeta.getPatterns().isEmpty()) setPatterns(bannerMeta.getPatterns());
    }

    @Override
    public void loadCompound(StorageTagCompound compound) {
        super.loadCompound(compound);
        if (compound.hasKey("patterns")) {
            List<Pattern> patterns = new ArrayList<>();
            StorageTagList list = (StorageTagList) compound.getTag("patterns");
            list.getTagList().forEach(storageBase -> {
                StorageTagCompound tagCompound = (StorageTagCompound) storageBase;
                PatternType type = PatternType.valueOf(tagCompound.getString("type", "BASE"));
                DyeColor color = DyeColor.valueOf(tagCompound.getString("color", "WHITE"));
                patterns.add(new Pattern(color, type));
            });
            setPatterns(patterns);
        }
    }

    @Override
    public StorageTagCompound toCompound() {
        StorageTagCompound compound = super.toCompound();
        if (!patterns.isEmpty()) {
            StorageTagList list = new StorageTagList();
            patterns.forEach(pattern -> {
                StorageTagCompound tagCompound = new StorageTagCompound();
                tagCompound.setString("type", pattern.getPattern().name());
                tagCompound.setString("color", pattern.getColor().name());
                list.appendTag(tagCompound);
            });
            compound.setTag("patterns", list);
        }
        return compound;
    }
}
