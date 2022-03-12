package lib.brainsynder.nms;

import lib.brainsynder.utils.Colorize;
import org.bukkit.entity.Player;

import java.util.Collection;

public class TitleMessage {
    private int fadeIn = 10, stay = 70, fadeOut = 20;
    private String header = null, subHeader = null;

    public TitleMessage setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    public TitleMessage setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    public TitleMessage setStay(int stay) {
        this.stay = stay;
        return this;
    }

    public TitleMessage setHeader(String header) {
        this.header = header;
        return this;
    }

    public TitleMessage setSubHeader(String subHeader) {
        this.subHeader = subHeader;
        return this;
    }

    public void sendMessage(Player player) {
        player.sendTitle((header != null) ? Colorize.translateBungeeHex(header) : "",
                (subHeader != null) ? Colorize.translateBungeeHex(subHeader) : "",
                fadeIn, stay, fadeOut);
    }

    public void sendMessage(Collection<? extends Player> players) {
        for (Player player : players) sendMessage(player);
    }
}
