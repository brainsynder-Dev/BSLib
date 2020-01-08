package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaBuilder;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import lib.brainsynder.nbt.StorageTagString;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KnowledgeBookMetaBuilder extends MetaBuilder<KnowledgeBookMeta> {
    private List<NamespacedKey> recipes = new ArrayList();

    public void addRecipe (NamespacedKey... recipes) {
        this.recipes.addAll(Arrays.asList(recipes));
        modifyMeta(value -> {
            value.addRecipe(recipes);
            return value;
        });

    }

    public void setRecipes (List<NamespacedKey> recipes) {
        this.recipes = recipes;
        modifyMeta(value -> {
            value.setRecipes(recipes);
            return value;
        });
    }

    public List<NamespacedKey> getRecipes() {
        return recipes;
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof KnowledgeBookMeta)) return;
        KnowledgeBookMeta book = (KnowledgeBookMeta) meta;
        setRecipes(book.getRecipes());
    }

    @Override
    public void loadCompound(StorageTagCompound compound) {
        super.loadCompound(compound);
        if (compound.hasKey("recipes")) {
            List<NamespacedKey> recipes = new ArrayList();
            StorageTagList list = (StorageTagList) compound.getTag("recipes");
            list.getTagList().forEach(storageBase -> {
                String[] args = ((StorageTagString)storageBase).getString().split(":");
                recipes.add(new NamespacedKey(args[0], args[1]));
            });
            setRecipes(recipes);
        }
    }

    @Override
    public StorageTagCompound toCompound() {
        StorageTagCompound compound = super.toCompound();
        if (!recipes.isEmpty()) {
            StorageTagList list = new StorageTagList();
            recipes.forEach(key -> {
                list.appendTag(new StorageTagString (key.toString()));
            });
            compound.setTag("recipes", list);
        }
        return compound;
    }
}
