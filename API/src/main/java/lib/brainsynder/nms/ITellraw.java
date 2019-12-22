package lib.brainsynder.nms;

import lib.brainsynder.nms.key.BaseTellrawMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public abstract class ITellraw {
    private static ITellraw tellraw = null;

    public abstract ITellraw color(ChatColor color);
    public abstract ITellraw style(ChatColor[] styles);
    public abstract ITellraw file(String path);
    public abstract ITellraw link(String url);
    public abstract ITellraw suggest(String command);
    public abstract ITellraw command(String command);
    public abstract ITellraw tooltip(List<String> lines);
    public abstract ITellraw tooltip(String... lines);
    public abstract ITellraw then(Object obj);
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


    public static ITellraw getInstance() {
        if (tellraw != null) return tellraw;
        tellraw = new BaseTellrawMessage();
        return tellraw;
    }
}