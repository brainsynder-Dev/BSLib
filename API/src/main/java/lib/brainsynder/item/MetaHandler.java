package lib.brainsynder.item;

import lib.brainsynder.VersionRestricted;
import lib.brainsynder.nbt.StorageTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MetaHandler<T extends ItemMeta> extends ItemTools implements VersionRestricted {
    private T meta = null;
    private StorageTagCompound compound = null;

    public MetaHandler(T meta) {
        this.meta = meta;
        fromItemMeta(meta);
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public StorageTagCompound getCompound() {
        return compound;
    }

    public void updateCompound(StorageTagCompound compound) {
        this.compound = compound;
    }

    protected T getMeta() {
        return meta;
    }

    public void fromItemMeta(ItemMeta meta) {
    }

    public void fromCompound(StorageTagCompound compound) {
    }

    public void modifyMeta(InnerReturn<T> meta) {
        this.meta = meta.run(this.meta);
    }

    protected interface InnerReturn<T> {
        T run(T value);
    }


    protected List<String> translate(List<String> message, boolean strip) {
        ArrayList<String> newLore = new ArrayList<>();
        message.forEach(msg -> {
            if (strip) {
                msg = msg.replace(ChatColor.COLOR_CHAR, '&');
            } else {
                msg = ChatColor.translateAlternateColorCodes('&', msg);
            }
            newLore.add(msg);
        });
        return newLore;
    }
    protected List<String> translate(List<String> message) {
        return translate(message, false);
    }

    protected String translate(String message, boolean strip) {
        if (strip) {
            message = message.replace(ChatColor.COLOR_CHAR, '&');
        } else {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }
        return message;
    }

    protected String translate(String message) {
        return translate(message, false);
    }
}
