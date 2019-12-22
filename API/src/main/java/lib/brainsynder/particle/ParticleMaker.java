package lib.brainsynder.particle;

import lib.brainsynder.ServerVersion;
import lib.brainsynder.nms.IParticlePacket;
import lib.brainsynder.reflection.Reflection;
import lib.brainsynder.storage.TriLoc;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ParticleMaker {
    private static IParticlePacket particlePacket = null;
    private Particle type;
    private double speed = 0.0;
    private int count = 1;
    private double offsetX = 0.0;
    private double offsetY = 0.0;
    private double offsetZ = 0.0;
    private DustOptions dustOptions = null;
    private ItemStack data = null;
    private boolean colored = false;
    private int repeatAmount = 1;

    public ParticleMaker(Particle type, int count, double radius) {
        this(type, 0.0, count, radius);
    }

    public ParticleMaker(Particle type) {
        this(type, 0.0, 1, 0.0);
    }

    public ParticleMaker(Particle type, double speed, int count, double radius) {
        this(type, speed, count, radius, radius, radius);
    }

    public ParticleMaker(Particle type, int count, double offsetX, double offsetY, double offsetZ) {
        this(type, 0.0D, count, offsetX, offsetY, offsetZ);
    }

    /**
     * If you are using Redstone Particle then
     * Please use {@link ParticleMaker#setDustOptions(DustOptions)}
     *
     * @Deprecated
     */
    @Deprecated
    public ParticleMaker(Particle type, int count, Color color) {
        this(type, 1.0F, 0, getColor(color.getRed()), getColor(color.getGreen()), getColor(color.getBlue()));
        colored = true;
        dustOptions = new DustOptions(color, 1.0F);
        this.repeatAmount = count;
    }

    /**
     * If you are using Redstone Particle then
     * Please use {@link ParticleMaker#setDustOptions(DustOptions)}
     *
     * @Deprecated
     */
    @Deprecated
    public ParticleMaker(Particle type, Color color) {
        this(type, 1, color);
    }

    public ParticleMaker(Particle type, int count, NoteColor color) {
        this(type, 1.0F, 0, color.getValueX(), color.getValueY(), color.getValueZ());
        colored = true;
        this.repeatAmount = count;
    }

    public ParticleMaker(Particle type, NoteColor color) {
        this(type, 0, color);
    }

    public ParticleMaker(Particle type, double speed, int count, double offsetX, double offsetY, double offsetZ) {
        if (ServerVersion.getVersion() == ServerVersion.UNKNOWN) {
            try {
                throw new MissingParticleException("This server version is not supported for this particle class.");
            } catch (MissingParticleException e) {
                e.printStackTrace();
            }
        }
        this.type = type;
        if (!type.isCompatable()) {
            try {
                throw new MissingParticleException("The particle '" + type.getName() + "' is not supported in this version.");
            } catch (MissingParticleException e) {
                e.printStackTrace();
            }
        }
        this.speed = speed;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        particlePacket = IParticlePacket.getInstance();
    }

    public ParticleMaker setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    public ParticleMaker setCount(int count) {
        this.count = count;
        return this;
    }

    public ParticleMaker setDustOptions(DustOptions dustOptions) {
        this.dustOptions = dustOptions;
        return this;
    }

    public ParticleMaker setOffset(double offsetX, double offsetY, double offsetZ) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        return this;
    }

    public ParticleMaker setData(Material material, short itemData) {
        data = new ItemStack(material, 1, itemData);
        return this;
    }

    public ParticleMaker setData(Material material) {
        setData(material, (short) 0);
        return this;
    }

    public ParticleMaker setData(ItemStack item) {
        data = item;
        return this;
    }

    public double getSpeed() {
        return this.speed;
    }

    public int getCount() {
        return this.count;
    }

    public double getOffsetX() {
        return this.offsetX;
    }

    public double getOffsetY() {
        return this.offsetY;
    }

    public double getOffsetZ() {
        return this.offsetZ;
    }

    private static float getColor(double value) {
        if (value <= 0.0F) {
            value = -1.0F;
        }

        return ((float) value) / 255.0F;
    }

    public void sendToLocation(Location location) {
        location.getWorld().getNearbyEntities(location, 100, 100, 100).forEach(entity -> {
            if (entity instanceof Player) {
                sendToPlayer((Player) entity, location);
            }
        });
    }

    public void sendToPlayer(Player player) {
        sendToPlayer(player, player.getLocation());
    }

    public void sendToPlayer(Player player, Location location) {
        try {
            Object packet = createPacket(location);
            if (colored) {
                for (int i = 0; i < repeatAmount; i++) {
                    Reflection.sendPacket(player, packet);
                }
                return;
            }
            Reflection.sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToPlayers(List<Player> players, Location location) {
        for (Player player : players) {
            sendToPlayer(player, location);
        }
    }

    private Object createPacket(Location location) {
        Object data = null;
        if ((type == Particle.ITEM_CRACK)
                || (type == Particle.BLOCK_CRACK)
                || (type == Particle.ITEM_TAKE)
                || (type == Particle.BLOCK_DUST)) {
            if (this.data == null) {
                data = new ItemStack(Material.STONE);
            }else data = this.data;
        } else if (type == Particle.REDSTONE) {
            if (dustOptions == null) dustOptions = new DustOptions(Color.RED, 1);

            if (ServerVersion.isEqualNew(ServerVersion.v1_13_R1)) {
                data = dustOptions;
            }else{
                offsetX = getColor(dustOptions.getColor().getRed());
                offsetY = getColor(dustOptions.getColor().getGreen());
                offsetZ = getColor(dustOptions.getColor().getBlue());
            }
        }


        return particlePacket.getPacket(type, new TriLoc<>((float) location.getX(), (float) location.getY(), (float) location.getZ()), new TriLoc<>((float) offsetX, (float) offsetY, (float) offsetZ), (float) speed, count, data);
    }
}