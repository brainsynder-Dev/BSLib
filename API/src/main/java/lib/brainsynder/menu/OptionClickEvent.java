package lib.brainsynder.menu;

import org.bukkit.entity.Player;

public class OptionClickEvent {
    private final Player player;
    private final int position;
    private final int page;
    private final Icon icon;
    private boolean close;
    private boolean destroy;

    public OptionClickEvent(Player player, int position, int page, Icon icon) {
        this.player = player;
        this.position = position;
        this.page = page;
        this.icon = icon;
        this.close = false;
        this.destroy = false;
    }

    public int getPage() {
        return page;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPosition() {
        return position;
    }

    public Icon getIcon() {
        return icon;
    }

    public boolean willClose() {
        return close;
    }

    public boolean willDestroy() {
        return destroy;
    }

    public void setWillClose(boolean close) {
        this.close = close;
    }

    public void setWillDestroy(boolean destroy) {
        this.destroy = destroy;
    }
}