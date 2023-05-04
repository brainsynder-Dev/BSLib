package lib.brainsynder.nms;

public enum GameState {
    /**
     * Sends a message to the player saying that their bed is missing or obstructed.
     * This does not change if you have slept in a bed or not
     */
    NO_RESPAWN_BLOCK_AVAILABLE,

    /**
     * Shows rain for the player.
     */
    START_RAINING,

    /**
     * Stops rain for the player.
     */
    STOP_RAINING,

    /**
     * Changes the players gamemode
     * <p>
     * Values:
     * -1 = This sets the player's gamemode to one which isn't set. It's the same as survival, but you can't see your health nor food bar
     * 0 - This sets the player's gamemode to survival.
     * 1 - This sets the player's gamemode to creative
     * 2 - This sets the player's gamemode to adventure
     * 3 - This sets the player's gamemode to spectator
     */
    CHANGE_GAME_MODE,

    /**
     * Shows the credits (when you've beaten the enderdragon)
     * <p>
     * Values:
     * 0 = The credits are skipped immediately, so it teleports you to your bed or the main spawn
     * 1 = The credits are not skipped
     */
    WIN_GAME,

    /**
     * Shows the minecraft demo mode menu
     * <p>
     * Values:
     * 0 = Shows the minecraft demo mode menu
     * 101 = Tells you how to walk and turn
     * 102 = Tells you how to jump
     * 103 = Tells you how to open your inventory
     */
    DEMO_EVENT,

    /**
     * Plays a ping sound (from when you hit someone with an arrow)
     */
    ARROW_HIT_PLAYER,

    /**
     * This changes your minecraft screen color to blue.
     * If the value is higher the sky will slowly turn lighter blue
     * (all other colors are changed as well, with the exception fo the inventory)
     * <p>
     * This also shows rain which increases by setting it higher (can cause massive lag)
     */
    RAIN_LEVEL_CHANGE,

    /**
     * This changes the color of the sky to white.
     * (all other colors are changed as well)
     */
    THUNDER_LEVEL_CHANGE,

    /**
     * TODO: Needs investigating...
     */
    PUFFER_FISH_STING,

    /**
     * Sends a mob appearance effec t to the player (the elder guardian).
     */
    GUARDIAN_ELDER_EFFECT,

    /**
     * TODO: Needs investigating...
     */
    IMMEDIATE_RESPAWN
}
