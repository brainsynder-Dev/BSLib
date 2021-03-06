package lib.brainsynder.utils;

import lib.brainsynder.nbt.StorageTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class BlockLocation {
    private World world;
    private int x, y, z;

    public BlockLocation(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockLocation(Location location) {
        this(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static BlockLocation fromCompound(StorageTagCompound compound) {
        World world = Bukkit.getWorld(compound.getString("world"));
        int x = compound.getInteger("x");
        int y = compound.getInteger("y");
        int z = compound.getInteger("z");
        return new BlockLocation(world, x, y, z);
    }

    public StorageTagCompound toCompound () {
        StorageTagCompound compound = new StorageTagCompound();
        compound.setString("world", world.getName());
        compound.setInteger("x", x);
        compound.setInteger("y", y);
        compound.setInteger("z", z);
        return compound;
    }

    public Location toLocation() {
        return new Location(world, x, y, z);
    }

    public boolean atLocation(BlockLocation location) {
        return ((location.world.getName().equals(world.getName()))
                && (location.x == x)
                && (location.y == y)
                && (location.z == z)
        );
    }

    public World getWorld() {
        return this.world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}