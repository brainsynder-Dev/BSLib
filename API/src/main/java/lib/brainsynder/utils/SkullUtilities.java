package lib.brainsynder.utils;

import com.eclipsesource.json.Json;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class SkullUtilities {
    public static PlayerProfile getProfile (Player player) {
        if (player == null) throw new RuntimeException("Player is null");
        return player.getPlayerProfile();
    }

    public static PlayerProfile getProfile (OfflinePlayer player) {
        if (player == null) throw new RuntimeException("OfflinePlayer is null");
        return player.getPlayerProfile();
    }

    public static PlayerProfile getProfile (ItemStack item) {
        return getProfile(item, UUID.randomUUID());
    }

    public static PlayerProfile getProfile (ItemStack item, UUID uuid) {
        if (!item.hasItemMeta()) throw new RuntimeException("ItemStack is missing ItemMeta: "+item);
        if (!(item.getItemMeta() instanceof SkullMeta skullMeta)) throw new RuntimeException("ItemStack is not a player skull");
        PlayerProfile profile = skullMeta.getOwnerProfile();
        if (profile == null) profile = Bukkit.createPlayerProfile(uuid);
        return profile;
    }

    public static PlayerProfile setProfileTexture(PlayerProfile profile, String url) {
        if (Base64Wrapper.isEncoded(url)) {
            url = Base64Wrapper.decodeString(url);
            url = Json.parse(url).asObject().get("textures").asObject().get("SKIN").asObject().get("url").asString();
        }

        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL(url); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL: '"+url+"'", exception);
        }
        textures.setSkin(urlObject); // Set the skin of the player profile to the URL
        profile.setTextures(textures); // Set the textures back to the profile
        return profile;
    }


}
