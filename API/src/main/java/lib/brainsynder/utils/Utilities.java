package lib.brainsynder.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Utilities {
    private static final int MILLI_PER_TICK = (1000 / 20);

    private static final List<Block> solidBlocks = new ArrayList<>();

    public List<Location> getHollowCube(Location loc, double particleDistance) {
        List<Location> result = new ArrayList<>();
        World world = loc.getWorld();
        double minX = loc.getBlockX();
        double minY = loc.getBlockY();
        double minZ = loc.getBlockZ();
        double maxX = loc.getBlockX() + 1;
        double maxY = loc.getBlockY() + 1;
        double maxZ = loc.getBlockZ() + 1;

        for (double x = minX; x <= maxX; x = Math.round((x + particleDistance) * 1e2) / 1e2) {
            for (double y = minY; y <= maxY; y = Math.round((y + particleDistance) * 1e2) / 1e2) {
                for (double z = minZ; z <= maxZ; z = Math.round((z + particleDistance) * 1e2) / 1e2) {
                    int components = 0;
                    if (x == minX || x == maxX) components++;
                    if (y == minY || y == maxY) components++;
                    if (z == minZ || z == maxZ) components++;
                    if (components >= 2) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }
        return result;
    }

    public static List<Block> getBlocksInRadius(Location location, int radius, boolean hollow) {
        return getBlocksInRadius(location, radius, hollow, false, block -> true);
    }

    public static List<Block> getBlocksInRadius(Location location, int radius, boolean hollow, boolean ignoreHeight) {
        return getBlocksInRadius(location, radius, hollow, ignoreHeight, block -> true);
    }

    public static List<Block> getBlocksInRadius(Location location, int radius, boolean hollow, boolean ignoreHeight, Predicate<Block> blockPredicate) {
        List<Block> blocks = new ArrayList<>();

        int bX = location.getBlockX();
        int bY = location.getBlockY();
        int bZ = location.getBlockZ();

        if (ignoreHeight) {
            for (int x = bX - radius; x <= bX + radius; x++) {
                for (int z = bZ - radius; z <= bZ + radius; z++) {
                    double distance = (bX - x) * (bX - x) + (bZ - z) * (bZ - z);
                    if ((distance < radius * radius) && ((!hollow) || (distance >= (radius - 1) * (radius - 1)))) {
                        Location loc = new Location(location.getWorld(), x, bY, z);

                        if (blockPredicate.test(loc.getBlock())) blocks.add(loc.getBlock());
                    }
                }
            }
            return blocks;
        }

        for (int x = bX - radius; x <= bX + radius; x++) {
            for (int y = bY - radius; y <= bY + radius; y++) {
                for (int z = bZ - radius; z <= bZ + radius; z++) {
                    double distance = (bX - x) * (bX - x) + (bY - y) * (bY - y) + (bZ - z) * (bZ - z);
                    if ((distance < radius * radius) && ((!hollow) || (distance >= (radius - 1) * (radius - 1)))) {
                        Location loc = new Location(location.getWorld(), x, y, z);

                        if (blockPredicate.test(loc.getBlock())) blocks.add(loc.getBlock());
                    }
                }
            }
        }
        return blocks;
    }

    public static Location lookAt(Location loc, Location lookat) {
        loc = loc.clone();
        double dx = lookat.getX() - loc.getX();
        double dz = lookat.getZ() - loc.getZ();
        if (dx != 0) {
            if (dx < 0) {
                loc.setYaw((float) (1.5 * Math.PI));
            } else {
                loc.setYaw((float) (0.5 * Math.PI));
            }
            loc.setYaw(loc.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            loc.setYaw((float) Math.PI);
        }
        loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
        return loc;
    }


    public static BlockFace getCardinalDirection(final double n) {
        double n2 = (n - 90.0) % 360.0;
        if (n2 < 0.0) {
            n2 += 360.0;
        }
        if (0.0 <= n2 && n2 < 22.5) {
            return BlockFace.WEST;
        }
        if (22.5 <= n2 && n2 < 67.5) {
            return BlockFace.NORTH_WEST;
        }
        if (67.5 <= n2 && n2 < 112.5) {
            return BlockFace.NORTH;
        }
        if (112.5 <= n2 && n2 < 157.5) {
            return BlockFace.NORTH_EAST;
        }
        if (157.5 <= n2 && n2 < 202.5) {
            return BlockFace.EAST;
        }
        if (202.5 <= n2 && n2 < 247.5) {
            return BlockFace.SOUTH_EAST;
        }
        if (247.5 <= n2 && n2 < 292.5) {
            return BlockFace.SOUTH;
        }
        if (292.5 <= n2 && n2 < 337.5) {
            return BlockFace.SOUTH_WEST;
        }
        if (337.5 <= n2 && n2 < 360.0) {
            return BlockFace.NORTH;
        }
        return null;
    }

    public static BlockFace getBlockCardinal(final double n) {
        double n2 = (n - 90.0) % 360.0;
        if (n2 < 0.0) {
            n2 += 360.0;
        }
        if (0.0 <= n2 && n2 < 22.5) {
            return BlockFace.WEST;
        }
        if (22.5 <= n2 && n2 < 67.5) {
            return BlockFace.WEST;
        }
        if (67.5 <= n2 && n2 < 112.5) {
            return BlockFace.NORTH;
        }
        if (112.5 <= n2 && n2 < 157.5) {
            return BlockFace.NORTH;
        }
        if (157.5 <= n2 && n2 < 202.5) {
            return BlockFace.EAST;
        }
        if (202.5 <= n2 && n2 < 247.5) {
            return BlockFace.EAST;
        }
        if (247.5 <= n2 && n2 < 292.5) {
            return BlockFace.SOUTH;
        }
        if (292.5 <= n2 && n2 < 337.5) {
            return BlockFace.SOUTH;
        }
        if (337.5 <= n2 && n2 < 360.0) {
            return BlockFace.NORTH;
        }
        return null;
    }

    public static BlockFace getNextDirection(final BlockFace blockFace) {
        if (blockFace == BlockFace.NORTH) {
            return BlockFace.EAST;
        }
        if (blockFace == BlockFace.EAST) {
            return BlockFace.SOUTH;
        }
        if (blockFace == BlockFace.SOUTH) {
            return BlockFace.WEST;
        }
        if (blockFace == BlockFace.WEST) {
            return BlockFace.NORTH;
        }
        if (blockFace == BlockFace.NORTH_EAST) {
            return BlockFace.EAST;
        }
        if (blockFace == BlockFace.SOUTH_EAST) {
            return BlockFace.SOUTH;
        }
        if (blockFace == BlockFace.SOUTH_WEST) {
            return BlockFace.WEST;
        }
        if (blockFace == BlockFace.NORTH_WEST) {
            return BlockFace.NORTH;
        }
        return BlockFace.NORTH;
    }

    public static Vector spread(Vector vector, float spread) {
        Random random = new Random();
        double x = random.nextDouble() * spread;
        double y = random.nextDouble() * spread;
        double z = random.nextDouble() * spread;

        if (random.nextBoolean()) {
            x = vector.getX() + x;
        } else {
            x = vector.getX() - x;
        }
        if (random.nextBoolean()) {
            y = vector.getY() + y;
        } else {
            y = vector.getY() - y;
        }
        if (random.nextBoolean()) {
            z = vector.getZ() + z;
        } else {
            z = vector.getZ() - z;
        }

        return new Vector(x, y, z);
    }

    public static boolean isValid(Entity entity) {
        if (entity == null) return false;
        if (entity.isValid() || (!entity.isDead())) return true;
        if (entity instanceof Player) return ((Player) entity).isOnline();
        return false;
    }

    public static long toUnit(long time, TimeUnit unit) {
        long ticks = 20;

        if (unit == TimeUnit.DAYS) {
            ticks = (ticks * 60);// minute
            ticks = (ticks * 60);// hour
            ticks = (ticks * 24)*time;// days
        }else if (unit == TimeUnit.HOURS) {
            ticks = (ticks * 60);// minute
            ticks = (ticks * 60)*time;// hour
        }else if (unit == TimeUnit.MINUTES) {
            ticks = (ticks * 60)*time;// minute
        }else{
            ticks = (ticks * time);
        }

        return ticks;
    }
}
