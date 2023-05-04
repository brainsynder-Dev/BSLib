package lib.brainsynder.utils;

import lib.brainsynder.math.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class BukkitUtils {

    /**
     * If the entity is null, dead, or offline, return false. Otherwise, return true.
     *
     * @param entity The entity to check
     * @return A boolean value.
     */
    public static boolean isValid(Entity entity) {
        if (entity == null) return false;
        if (entity.isValid() || (!entity.isDead())) return true;
        if (entity instanceof Player) return ((Player) entity).isOnline();
        return false;
    }

    /**
     * Converts a time in a given unit to ticks.
     *
     * @param time The time you want to convert
     * @param unit The unit of time to convert from.
     * @return The number of ticks in the given time.
     */
    public static long convertTime2Ticks(long time, TimeUnit unit) {
        return (unit.toSeconds(time) * 20);
    }

    /**
     * For each block in the cube, if it's on the edge of the cube, add it to the list.
     *
     * @param loc      The center of the cube
     * @param distance The distance between each block.
     * @return A list of locations that are on the outside of a cube.
     */
    public List<Location> getHollowCube(Location loc, double distance) {
        List<Location> result = new ArrayList<>();
        World world = loc.getWorld();
        double minX = loc.getBlockX();
        double minY = loc.getBlockY();
        double minZ = loc.getBlockZ();
        double maxX = loc.getBlockX() + 1;
        double maxY = loc.getBlockY() + 1;
        double maxZ = loc.getBlockZ() + 1;

        for (double x = minX; x <= maxX; x = Math.round((x + distance) * 1e2) / 1e2) {
            for (double y = minY; y <= maxY; y = Math.round((y + distance) * 1e2) / 1e2) {
                for (double z = minZ; z <= maxZ; z = Math.round((z + distance) * 1e2) / 1e2) {
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

    /**
     * "Get all the blocks in a radius around a location, optionally hollow, optionally ignore air, optionally filter by
     * block type."
     * <p>
     * The first parameter is the location to get the blocks around. The second parameter is the radius. The third
     * parameter is whether or not to ignore the blocks in the center. The fourth parameter is whether or not to ignore air
     * blocks. The fifth parameter is a predicate that determines whether or not to include a block
     *
     * @param location The location to get the blocks around.
     * @param radius   The radius of the sphere.
     * @param hollow   If true, the blocks on the edge of the radius will not be included.
     * @return A list of blocks in a radius.
     */
    public static List<Block> getBlocksInRadius(Location location, int radius, boolean hollow) {
        return getBlocksInRadius(location, radius, hollow, false, block -> true);
    }

    /**
     * "Returns a list of blocks in a radius around a location, optionally ignoring height, optionally hollow, and
     * optionally filtering the blocks."
     * <p>
     * The first parameter is the location to get the blocks around. The second parameter is the radius. The third
     * parameter is whether or not to ignore height. The fourth parameter is whether or not to make the radius hollow. The
     * fifth parameter is a predicate that filters the blocks
     *
     * @param location     The location to get the blocks around.
     * @param radius       The radius of the sphere.
     * @param hollow       If true, the blocks on the outer edge of the radius will not be included.
     * @param ignoreHeight If true, the height of the location will be ignored.
     * @return A list of blocks in a radius.
     */
    public static List<Block> getBlocksInRadius(Location location, int radius, boolean hollow, boolean ignoreHeight) {
        return getBlocksInRadius(location, radius, hollow, ignoreHeight, block -> true);
    }

    /**
     * "Get all blocks in a radius around a location, optionally ignoring height, optionally hollow, and optionally only
     * including blocks that match a predicate."
     * <p>
     * The first parameter is the location to get the blocks around. The second parameter is the radius to get the blocks
     * in. The third parameter is whether or not to ignore height. The fourth parameter is whether or not to make the
     * radius hollow. The fifth parameter is a predicate that the block must match in order to be included in the list
     *
     * @param location       The center of the sphere
     * @param radius         The radius of the sphere.
     * @param hollow         If true, the blocks in the middle of the sphere will be ignored.
     * @param ignoreHeight   If true, the y-axis will be ignored.
     * @param blockPredicate A predicate that returns true if the block should be added to the list.
     * @return A list of blocks
     */
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

    /**
     * It takes a location and a location to look at, and returns a new location with the same X, Y, and Z, but with the
     * Yaw and Pitch set to look at the second location
     *
     * @param loc    The location you want to look at.
     * @param lookat The location you want to look at.
     * @return A Location object.
     */
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


    public static TreeSet<String> sortKey(Set<String> toSort) {
        TreeSet sortedSet = new TreeSet();
        Iterator i$ = toSort.iterator();

        while (i$.hasNext()) {
            String cur = (String) i$.next();
            sortedSet.add(cur);
        }

        return sortedSet;
    }

    public static Vector getTrajectory(Entity from, Entity to) {
        return getTrajectory(from.getLocation().toVector(), to.getLocation().toVector());
    }

    public static Vector getTrajectory(Location from, Location to) {
        return getTrajectory(from.toVector(), to.toVector());
    }

    public static Vector getTrajectory(Vector from, Vector to) {
        return to.subtract(from).normalize();
    }

    public static Vector getTrajectory2d(Entity from, Entity to) {
        return getTrajectory2d(from.getLocation().toVector(), to.getLocation().toVector());
    }

    public static Vector getTrajectory2d(Location from, Location to) {
        return getTrajectory2d(from.toVector(), to.toVector());
    }

    public static Vector getTrajectory2d(Vector from, Vector to) {
        return to.subtract(from).setY(0).normalize();
    }

    public static float getPitch(Vector vec) {
        double x = vec.getX();
        double y = vec.getY();
        double z = vec.getZ();
        double xz = Math.sqrt(x * x + z * z);
        double pitch = Math.toDegrees(Math.atan(xz / y));
        if (y <= 0.0D) {
            pitch += 90.0D;
        } else {
            pitch -= 90.0D;
        }

        return (float) pitch;
    }

    public static float getYaw(Vector vec) {
        double x = vec.getX();
        double z = vec.getZ();
        double yaw = Math.toDegrees(Math.atan(-x / z));
        if (z < 0.0D) {
            yaw += 180.0D;
        }

        return (float) yaw;
    }

    public static Vector normalize(Vector vec) {
        if (vec.length() > 0.0D) {
            vec.normalize();
        }

        return vec;
    }

    public static Vector clone(Vector vec) {
        return new Vector(vec.getX(), vec.getY(), vec.getZ());
    }

    public static <T> T random(List<T> list) {
        return list.get(MathUtils.r(list.size()));
    }

    public static float getLookAtYaw(Entity loc, Entity lookat) {
        return getLookAtYaw(loc.getLocation(), lookat.getLocation());
    }

    public static float getLookAtYaw(Block loc, Block lookat) {
        return getLookAtYaw(loc.getLocation(), lookat.getLocation());
    }

    public static float getLookAtYaw(Location loc, Location lookat) {
        return MathUtils.getLookAtYaw(lookat.getX() - loc.getX(), lookat.getZ() - loc.getZ());
    }

    public static float getLookAtYaw(Vector motion) {
        return MathUtils.getLookAtYaw(motion.getX(), motion.getZ());
    }

    /**
     * Moves a Location into the yaw and pitch of the Location in the offset specified
     *
     * @param loc    to move
     * @param offset vector
     * @return Translated Location
     */
    public static Location move(Location loc, Vector offset) {
        return move(loc, offset.getX(), offset.getY(), offset.getZ());
    }

    /**
     * Moves a Location into the yaw and pitch of the Location in the offset specified
     *
     * @param loc to move
     * @param dx  offset
     * @param dy  offset
     * @param dz  offset
     * @return Translated Location
     */
    public static Location move(Location loc, double dx, double dy, double dz) {
        Vector off = rotate(loc.getYaw(), loc.getPitch(), dx, dy, dz);
        double x = loc.getX() + off.getX();
        double y = loc.getY() + off.getY();
        double z = loc.getZ() + off.getZ();
        return new Location(loc.getWorld(), x, y, z, loc.getYaw(), loc.getPitch());
    }

    /**
     * Rotates a 3D-vector using yaw and pitch
     *
     * @param yaw    angle in degrees
     * @param pitch  angle in degrees
     * @param vector to rotate
     * @return Vector rotated by the angle (new instance)
     */
    public static Vector rotate(float yaw, float pitch, Vector vector) {
        return rotate(yaw, pitch, vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Rotates a 3D-vector using yaw and pitch
     *
     * @param yaw   angle in degrees
     * @param pitch angle in degrees
     * @param x     axis of the vector
     * @param y     axis of the vector
     * @param z     axis of the vector
     * @return Vector rotated by the angle
     */
    public static Vector rotate(float yaw, float pitch, double x, double y, double z) {
        // Conversions found by (a lot of) testing
        float angle;
        angle = yaw * MathUtils.DEGTORAD;
        double sinyaw = Math.sin(angle);
        double cosyaw = Math.cos(angle);

        angle = pitch * MathUtils.DEGTORAD;
        double sinpitch = Math.sin(angle);
        double cospitch = Math.cos(angle);

        Vector vector = new Vector();
        vector.setX((x * sinyaw) - (y * cosyaw * sinpitch) - (z * cosyaw * cospitch));
        vector.setY((y * cospitch) - (z * sinpitch));
        vector.setZ(-(x * cosyaw) - (y * sinyaw * sinpitch) - (z * sinyaw * cospitch));
        return vector;
    }

    public static Vector lerp(Vector vec1, Vector vec2, double stage) {
        Vector newvec = new Vector();
        newvec.setX(MathUtils.lerp(vec1.getX(), vec2.getX(), stage));
        newvec.setY(MathUtils.lerp(vec1.getY(), vec2.getY(), stage));
        newvec.setZ(MathUtils.lerp(vec1.getZ(), vec2.getZ(), stage));
        return newvec;
    }

    public static Location lerp(Location loc1, Location loc2, double stage) {
        Location newloc = new Location(loc1.getWorld(), 0, 0, 0);
        newloc.setX(MathUtils.lerp(loc1.getX(), loc2.getX(), stage));
        newloc.setY(MathUtils.lerp(loc1.getY(), loc2.getY(), stage));
        newloc.setZ(MathUtils.lerp(loc1.getZ(), loc2.getZ(), stage));
        newloc.setYaw((float) MathUtils.lerp(loc1.getYaw(), loc2.getYaw(), stage));
        newloc.setPitch((float) MathUtils.lerp(loc1.getPitch(), loc2.getPitch(), stage));
        return newloc;
    }

    /**
     * Gets the direction of yaw and pitch angles
     *
     * @param yaw   angle in degrees
     * @param pitch angle in degrees
     * @return Direction Vector
     */
    public static Vector getDirection(float yaw, float pitch) {
        Vector vector = new Vector();
        double rotX = MathUtils.DEGTORAD * yaw;
        double rotY = MathUtils.DEGTORAD * pitch;
        vector.setY(-Math.sin(rotY));
        double h = Math.cos(rotY);
        vector.setX(-h * Math.sin(rotX));
        vector.setZ(h * Math.cos(rotX));
        return vector;
    }

    public static void setVectorLength(Vector vector, double length) {
        setVectorLengthSquared(vector, Math.signum(length) * length * length);
    }

    public static void setVectorLengthSquared(Vector vector, double lengthsquared) {
        double vlength = vector.lengthSquared();
        if (Math.abs(vlength) > 0.0001) {
            if (lengthsquared < 0) {
                vector.multiply(-Math.sqrt(-lengthsquared / vlength));
            } else {
                vector.multiply(Math.sqrt(lengthsquared / vlength));
            }
        }
    }

    public static Vector rotateAroundAxisX(Vector v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisY(Vector v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public static Vector rotateAroundAxisZ(Vector v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.getX() * cos - v.getY() * sin;
        double y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }

    public static Vector rotateVector(Vector v, double angleX, double angleY, double angleZ) {
        rotateAroundAxisX(v, angleX);
        rotateAroundAxisY(v, angleY);
        rotateAroundAxisZ(v, angleZ);
        return v;
    }

    public static void applyVelocity(final Entity ent, Vector v) {
        if (!ent.hasMetadata("NPC")) {
            ent.setVelocity(v);
        }
    }

    public static double angleToXAxis(Vector vector) {
        return Math.atan2(vector.getX(), vector.getY());
    }

    public static Vector getRandomVector() {
        double x = MathUtils.getRandom().nextDouble() * 2.0D - 1.0D;
        double y = MathUtils.getRandom().nextDouble() * 2.0D - 1.0D;
        double z = MathUtils.getRandom().nextDouble() * 2.0D - 1.0D;
        return (new Vector(x, y, z)).normalize();
    }

    public static Vector getRandomCircleVector() {
        double rnd = MathUtils.getRandom().nextDouble() * 2.0D * 3.141592653589793D;
        double x = Math.cos(rnd);
        double z = Math.sin(rnd);
        return new Vector(x, 0.0D, z);
    }

    public static Material getRandomMaterial(Material[] materials) {
        return materials[MathUtils.getRandom().nextInt(materials.length)];
    }

    public static double offset(Entity a, Entity b) {
        return offset(a.getLocation().toVector(), b.getLocation().toVector());
    }

    public static double offset(Location a, Location b) {
        return offset(a.toVector(), b.toVector());
    }

    public static double offset(Vector a, Vector b) {
        return a.subtract(b).length();
    }

    public static double offset2d(Entity a, Entity b) {
        return offset2d(a.getLocation().toVector(), b.getLocation().toVector());
    }

    public static double offset2d(Location a, Location b) {
        return offset2d(a.toVector(), b.toVector());
    }

    public static double offset2d(Vector a, Vector b) {
        a.setY(0);
        b.setY(0);
        return a.subtract(b).length();
    }
}