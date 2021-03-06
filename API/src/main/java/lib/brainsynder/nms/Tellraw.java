package lib.brainsynder.nms;

import lib.brainsynder.nms.key.BaseTellrawMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public abstract class Tellraw {
    public abstract Tellraw color(Object color);
    public abstract Tellraw style(ChatColor[] styles);
    public abstract Tellraw file(String path);
    public abstract Tellraw link(String url);
    public abstract Tellraw suggest(String command);
    public abstract Tellraw command(String command);
    public abstract Tellraw tooltip(List<String> lines);
    public abstract Tellraw tooltip(String... lines);
    public abstract Tellraw then(Object obj);
    public abstract Tellraw removeLastPart();
    public abstract String toJSONString();
    public abstract void send(CommandSender sender);
    public abstract void send(Player player);
    public void send(Iterable<Player> players) {
        for (Player player : players)
            send (player);
    }
    public void send(Collection<? extends Player> players) {
        for (Player player : players)
            send (player);
    }



    public static Tellraw getInstance(String text) {
        return new BaseTellrawMessage().then(text);
    }
    public static Tellraw fromLegacy(String text) {
        return new BaseTellrawMessage().fromLegacy0(text);
    }
}