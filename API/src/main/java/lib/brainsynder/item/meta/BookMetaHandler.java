package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaHandler;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import lib.brainsynder.nbt.StorageTagString;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BookMetaHandler extends MetaHandler<BookMeta> {

    public BookMetaHandler(BookMeta meta) {
        super(meta);
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof BookMeta)) return;
        BookMeta book = (BookMeta) meta;
        StorageTagCompound compound = new StorageTagCompound();
        if ((book.getTitle() != null) && (!book.getTitle().isEmpty())) compound.setString("title", book.getTitle());
        if (!book.getAuthor().isEmpty()) compound.setString("author", book.getAuthor());
        if (book.getGeneration() != null) compound.setString("generation", book.getGeneration().name());
        if (!book.getPages().isEmpty()) {
            StorageTagList list = new StorageTagList();
            translate(book.getPages(), true).forEach(page -> {
                list.appendTag(new StorageTagString (translate(page, true)));
            });
            compound.setTag("pages", list);
        }
        updateCompound(compound);
    }

    @Override
    public void fromCompound(StorageTagCompound compound) {
        modifyMeta(value -> {
            if (compound.hasKey("title")) value.setTitle(translate(compound.getString("title")));
            if (compound.hasKey("author")) value.setAuthor(compound.getString("author"));
            if (compound.hasKey("generation")) value.setGeneration(BookMeta.Generation.valueOf(compound.getString("generation")));
            if (compound.hasKey("pages")) {
                List<String> pages = new ArrayList<>();
                StorageTagList list = (StorageTagList) compound.getTag("patterns");
                list.getTagList().forEach(storageBase -> {
                    pages.add(((StorageTagString)storageBase).getString());
                });
                value.setPages(translate(pages));
            }
            return value;
        });
    }
}
