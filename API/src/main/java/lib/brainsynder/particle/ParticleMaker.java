package lib.brainsynder.particle;

import lib.brainsynder.ServerVersion;
import lib.brainsynder.item.ItemBuilder;
import lib.brainsynder.nbt.StorageTagCompound;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ParticleMaker {
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
        if (!type.isCompatible()) {
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
    }

    public ParticleMaker (StorageTagCompound compound) {
        type = compound.getEnum("particle", Particle.class, Particle.CRIT);
        speed = compound.getDouble("speed", 0.0);
        count = compound.getInteger("count", 1);
        if (compound.hasKey("offset")) {
            StorageTagCompound offset = compound.getCompoundTag("offset");
            if (offset.getSize() == 1) throw new MissingParticleException("Offset has to have 3 values (x,y,z)");
            offsetX = offset.getDouble("x", 0);
            offsetY = offset.getDouble("y", 0);
            offsetZ = offset.getDouble("z", 0);
        }
        if (compound.hasKey("dust")) dustOptions = new DustOptions (compound.getCompoundTag("dust"));
        if (compound.hasKey("item")) data = ItemBuilder.fromCompound(compound.getCompoundTag("item")).build();
    }

    public ParticleMaker setType(Particle type) {
        this.type = type;
        return this;
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

    public Particle getType() {
        return type;
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
                data = new org.bukkit.Particle.DustOptions(dustOptions.getColor(), dustOptions.getSize());
            }else{
                offsetX = getColor(dustOptions.getColor().getRed());
                offsetY = getColor(dustOptions.getColor().getGreen());
                offsetZ = getColor(dustOptions.getColor().getBlue());
            }
        }

        if (colored) {
            for (int i = 0; i < repeatAmount; i++) {
                player.spawnParticle(org.bukkit.Particle.valueOf(type.name()), location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetY, speed, data);
            }
            return;
        }
        player.spawnParticle(org.bukkit.Particle.valueOf(type.name()), location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetY, speed, data);
    }

    public void sendToPlayers(List<Player> players, Location location) {
        for (Player player : players) {
            sendToPlayer(player, location);
        }
    }

    public StorageTagCompound toCompound () {
        StorageTagCompound compound = new StorageTagCompound ();
        compound.setEnum("particle", type);
        compound.setDouble("speed", speed);
        compound.setInteger("count", count);
        compound.setTag("offset", new StorageTagCompound().setDouble("x", offsetX).setDouble("y", offsetY).setDouble("z", offsetZ));
        if (dustOptions != null) compound.setTag("dust", dustOptions.toCompound());
        if (data != null) compound.setTag("item", ItemBuilder.fromItem(data).toCompound());
        return compound;
    }
}