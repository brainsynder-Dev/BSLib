package lib.brainsynder.utils;

import lib.brainsynder.math.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class BukkitUtils {
    public static TreeSet<String> sortKey(Set<String> toSort) {
        TreeSet sortedSet = new TreeSet();
        Iterator i$ = toSort.iterator();

        while(i$.hasNext()) {
            String cur = (String)i$.next();
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
        if(y <= 0.0D) {
            pitch += 90.0D;
        } else {
            pitch -= 90.0D;
        }

        return (float)pitch;
    }

    public static float getYaw(Vector vec) {
        double x = vec.getX();
        double z = vec.getZ();
        double yaw = Math.toDegrees(Math.atan(-x / z));
        if(z < 0.0D) {
            yaw += 180.0D;
        }

        return (float)yaw;
    }

    public static Vector normalize(Vector vec) {
        if(vec.length() > 0.0D) {
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
     * @param loc to move
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
     * @param dx offset
     * @param dy offset
     * @param dz offset
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
     * @param yaw angle in degrees
     * @param pitch angle in degrees
     * @param vector to rotate
     * @return Vector rotated by the angle (new instance)
     */
    public static Vector rotate(float yaw, float pitch, Vector vector) {
        return rotate(yaw, pitch, vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Rotates a 3D-vector using yaw and pitch
     *
     * @param yaw angle in degrees
     * @param pitch angle in degrees
     * @param x axis of the vector
     * @param y axis of the vector
     * @param z axis of the vector
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
     * @param yaw angle in degrees
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
        if(!ent.hasMetadata("NPC")) {
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