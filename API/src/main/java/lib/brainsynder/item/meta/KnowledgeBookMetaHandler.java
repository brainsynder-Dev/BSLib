package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaHandler;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import lib.brainsynder.nbt.StorageTagString;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeBookMetaHandler extends MetaHandler<KnowledgeBookMeta> {

    public KnowledgeBookMetaHandler(KnowledgeBookMeta meta) {
        super(meta);
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof KnowledgeBookMeta)) return;
        KnowledgeBookMeta book = (KnowledgeBookMeta) meta;
        if (!book.getRecipes().isEmpty()) {
            StorageTagList list = new StorageTagList();
            book.getRecipes().forEach(key -> {
                list.appendTag(new StorageTagString (key.toString()));
            });
            updateCompound(new StorageTagCompound().setTag("recipes", list));
        }
    }

    @Override
    public void fromCompound(StorageTagCompound compound) {
        super.fromCompound(compound);
        if (compound.hasKey("recipes")) {
            List<NamespacedKey> recipes = new ArrayList();
            StorageTagList list = (StorageTagList) compound.getTag("recipes");
            list.getTagList().forEach(storageBase -> {
                String[] args = ((StorageTagString)storageBase).getString().split(":");
                recipes.add(new NamespacedKey(args[0], args[1]));
            });
            modifyMeta(value -> {
                value.setRecipes(recipes);
                return value;
            });
        }
    }
}
