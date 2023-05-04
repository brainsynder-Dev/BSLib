package lib.brainsynder.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Code provided by https://www.spigotmc.org/threads/594005/
 * With slight modifications by brainsynder
 *
 * @author 7smile7
 * @author brainsynder
 */
public class InventoryButton {

    private Function<Player, ItemStack> iconCreator;
    private Consumer<InventoryClickEvent> eventConsumer;

    /**
     * "Sets the icon creator for this button to the given function."
     * <p>
     * The icon creator is a function that takes a player and returns an item stack. This function is used to create the icon
     * for the button
     *
     * @param iconCreator A function that takes a player and returns an ItemStack. This is used to create the icon for the
     *                    button.
     * @return The InventoryButton object.
     */
    public InventoryButton creator(Function<Player, ItemStack> iconCreator) {
        this.iconCreator = iconCreator;
        return this;
    }

    /**
     * This function sets the eventConsumer variable to the Consumer<InventoryClickEvent> eventConsumer parameter.
     *
     * @param eventConsumer The consumer that will be called when the button is clicked.
     * @return The InventoryButton object.
     */
    public InventoryButton consumer(Consumer<InventoryClickEvent> eventConsumer) {
        this.eventConsumer = eventConsumer;
        return this;
    }

    /**
     * It returns the event consumer
     *
     * @return The eventConsumer variable.
     */
    public Consumer<InventoryClickEvent> getEventConsumer() {
        return this.eventConsumer;
    }

    /**
     * Returns the function that creates the icon for this item.
     *
     * @return The iconCreator variable.
     */
    public Function<Player, ItemStack> getIconCreator() {
        return this.iconCreator;
    }
}