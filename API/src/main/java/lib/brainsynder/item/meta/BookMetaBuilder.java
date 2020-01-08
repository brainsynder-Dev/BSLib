package lib.brainsynder.item.meta;

import lib.brainsynder.item.MetaBuilder;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import lib.brainsynder.nbt.StorageTagString;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BookMetaBuilder extends MetaBuilder<BookMeta> {
    private String title = "", author = "";
    private BookMeta.Generation generation = null;
    private List<String> pages = new ArrayList<>();

    public void setTitle(String title) {
        this.title = title;
        modifyMeta(value -> {
            value.setTitle(title);
            return value;
        });
    }

    public void setAuthor(String author) {
        this.author = author;
        modifyMeta(value -> {
            value.setAuthor(author);
            return value;
        });
    }

    public void setGeneration(BookMeta.Generation generation) {
        this.generation = generation;
        modifyMeta(value -> {
            value.setGeneration(generation);
            return value;
        });
    }

    public void setPages(List<String> pages) {
        pages = translate(pages, false);
        this.pages = pages;
        modifyMeta(value -> {
            value.setPages(this.pages);
            return value;
        });
    }

    public List<String> getPages() {
        return pages;
    }
    public String getAuthor() {
        return author;
    }
    public BookMeta.Generation getGeneration() {
        return generation;
    }
    public String getTitle() {
        return title;
    }

    @Override
    public void fromItemMeta(ItemMeta meta) {
        if (!(meta instanceof BookMeta)) return;
        BookMeta book = (BookMeta) meta;
        setTitle(book.getTitle());
        setAuthor(book.getAuthor());
        setGeneration(book.getGeneration());
        setPages(book.getPages());
    }

    @Override
    public void loadCompound(StorageTagCompound compound) {
        super.loadCompound(compound);
        if (compound.hasKey("title")) setTitle(compound.getString("title"));
        if (compound.hasKey("author")) setTitle(compound.getString("author"));
        if (compound.hasKey("generation")) setTitle(compound.getString("generation"));
        if (compound.hasKey("pages")) {
            List<String> pages = new ArrayList<>();
            StorageTagList list = (StorageTagList) compound.getTag("patterns");
            list.getTagList().forEach(storageBase -> {
                pages.add(((StorageTagString)storageBase).getString());
            });
            setPages(pages);
        }
    }

    @Override
    public StorageTagCompound toCompound() {
        StorageTagCompound compound = super.toCompound();
        if (!title.isEmpty()) compound.setString("title", title);
        if (!author.isEmpty()) compound.setString("author", author);
        if (generation != null) compound.setString("generation", generation.name());
        if (!pages.isEmpty()) {
            StorageTagList list = new StorageTagList();
            translate(pages, true).forEach(page -> {
                list.appendTag(new StorageTagString ());
            });
            compound.setTag("pages", list);
        }
        return compound;
    }

    private List<String> translate(List<String> message, boolean strip) {
        ArrayList<String> newLore = new ArrayList<>();
        message.forEach(msg -> {
            if (strip) {
                msg = msg.replace(ChatColor.COLOR_CHAR, '&');
            }else{
                msg = ChatColor.translateAlternateColorCodes('&', msg);
            }
            newLore.add(msg);
        });
        return newLore;
    }
}
