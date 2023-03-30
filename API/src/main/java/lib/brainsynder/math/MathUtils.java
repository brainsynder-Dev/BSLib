package lib.brainsynder.math;

import java.text.DecimalFormat;
import java.util.Random;

public class MathUtils {
    static final int ATAN2_DIM = (int)Math.sqrt(16384.0D);
    private static final float INV_ATAN2_DIM_MINUS_1;
    private static final int CHUNK_BITS = 4;
    private static final int CHUNK_VALUES = 16;
    public static final float DEGTORAD = 0.017453293F;
    public static final float RADTODEG = 57.29577951F;
    private static final Random random;

    public static Random getRandom() {
        return random;
    }

    /**
     * It trims the double to the specified degree.
     *
     * @param degree the number of decimal places you want to round to
     * @param value The number to be trimmed
     * @return A double value that is rounded to the specified degree.
     */
    public static double trim(int degree, double value) {
        DecimalFormat var5 = new DecimalFormat("#.#" + "#".repeat(Math.max(0, degree - 1)));
        return Double.parseDouble(var5.format(value));
    }

    /**
     * Given two angles, return the absolute value of the difference between them, wrapped to the range 0-180.
     *
     * @param angle1 The first angle
     * @param angle2 The angle to compare to.
     * @return The absolute value of the difference between the two angles.
     */
    public static int getAngleDifference(int angle1, int angle2) {
        return Math.abs(wrapAngle(angle1 - angle2));
    }

    /**
     * Wraps the angle to be between -180 and 180 degrees
     *
     * @param angle to wrap
     * @return [-180 > angle >= 180]
     */
    public static int wrapAngle(int angle) {
        int wrappedAngle = angle;
        while (wrappedAngle <= -180) {
            wrappedAngle += 360;
        }
        while (wrappedAngle > 180) {
            wrappedAngle -= 360;
        }
        return wrappedAngle;
    }

    /**
     * Wraps the angle to be between -180 and 180 degrees
     *
     * @param angle to wrap
     * @return [-180 > angle >= 180]
     */
    public static float wrapAngle(float angle) {
        float wrappedAngle = angle;
        while (wrappedAngle <= -180f) {
            wrappedAngle += 360f;
        }
        while (wrappedAngle > 180f) {
            wrappedAngle -= 360f;
        }
        return wrappedAngle;
    }

    /**
     * Normalizes a 2D-vector to be the length of another 2D-vector<br>
     * Calculates the normalization factor to multiply the input vector with, to get the requested length
     *
     * @param x axis of the vector
     * @param z axis of the vector
     * @param reqx axis of the length vector
     * @param reqz axis of the length vector
     * @return the normalization factor
     */
    public static double normalize(double x, double z, double reqx, double reqz) {
        return Math.sqrt(lengthSquared(reqx, reqz) / lengthSquared(x, z));
    }

    /**
     * Gets the horizontal look-at angle in degrees to look into the 2D-direction specified
     *
     * @param dx axis of the direction
     * @param dz axis of the direction
     * @return the angle in degrees
     */
    public static float getLookAtYaw(double dx, double dz) {
        return atan2(dz, dx) - 180f;
    }

    public static double lengthSquared(double... values) {
        double rval = 0;
        for (double value : values) {
            rval += value * value;
        }
        return rval;
    }

    public static double length(double... values) {
        return Math.sqrt(lengthSquared(values));
    }

    /**
     * Gets the pitch angle in degrees to look into the direction specified
     *
     * @param dX axis of the direction
     * @param dY axis of the direction
     * @param dZ axis of the direction
     * @return look-at angle in degrees
     */
    public static float getLookAtPitch(double dX, double dY, double dZ) {
        return getLookAtPitch(dY, length(dX, dZ));
    }

    /**
     * Gets the pitch angle in degrees to look into the direction specified
     *
     * @param dY axis of the direction
     * @param dXZ axis of the direction (length of x and z)
     * @return look-at angle in degrees
     */
    public static float getLookAtPitch(double dY, double dXZ) {
        return -atan(dY / dXZ);
    }

    /**
     * Gets the inverse tangent of the value in degrees
     *
     * @param value
     * @return inverse tangent angle in degrees
     */
    public static float atan(double value) {
        return RADTODEG * (float) TrigMath.atan(value);
    }

    /**
     * Gets the inverse tangent angle in degrees of the rectangle vector
     *
     * @param y axis
     * @param x axis
     * @return inverse tangent 2 angle in degrees
     */
    public static float atan2(double y, double x) {
        return RADTODEG * (float) TrigMath.atan2(y, x);
    }

    /**
     * Gets the floor integer value from a double value
     *
     * @param value to get the floor of
     * @return floor value
     */
    public static int floor(double value) {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }

    /**
     * Gets the ceiling integer value from a double value
     *
     * @param value to get the ceiling of
     * @return ceiling value
     */
    public static int ceil(double value) {
        return -floor(-value);
    }

    /**
     * Rounds the specified value to the amount of decimals specified
     *
     * @param value to round
     * @param decimals count
     * @return value round to the decimal count specified
     */
    public static double round(double value, int decimals) {
        double p = Math.pow(10, decimals);
        return Math.round(value * p) / p;
    }

    /**
     * Returns 0 if the value is not-a-number
     *
     * @param value to check
     * @return The value, or 0 if it is NaN
     */
    public static double fixNaN(double value) {
        return fixNaN(value, 0.0);
    }

    /**
     * Returns the default if the value is not-a-number
     *
     * @param value to check
     * @param def value
     * @return The value, or the default if it is NaN
     */
    public static double fixNaN(double value, double def) {
        return Double.isNaN(value) ? def : value;
    }

    /**
     * Converts a location value into a chunk coordinate
     *
     * @param loc to convert
     * @return chunk coordinate
     */
    public static int toChunk(double loc) {
        return floor(loc / (double) CHUNK_VALUES);
    }

    /**
     * Converts a location value into a chunk coordinate
     *
     * @param loc to convert
     * @return chunk coordinate
     */
    public static int toChunk(int loc) {
        return loc >> CHUNK_BITS;
    }

    public static double useOld(double oldvalue, double newvalue, double peruseold) {
        return oldvalue + (peruseold * (newvalue - oldvalue));
    }

    public static double lerp(double d1, double d2, double stage) {
        if (Double.isNaN(stage) || stage > 1) {
            return d2;
        } else if (stage < 0) {
            return d1;
        } else {
            return d1 * (1 - stage) + d2 * stage;
        }
    }

    /**
     * Checks whether one value is negative and the other positive, or opposite
     *
     * @param value1 to check
     * @param value2 to check
     * @return True if value1 is inverted from value2
     */
    public static boolean isInverted(double value1, double value2) {
        return (value1 > 0 && value2 < 0) || (value1 < 0 && value2 > 0);
    }

    /**
     * Clamps the value between -limit and limit
     *
     * @param value to clamp
     * @param limit
     * @return value, -limit or limit
     */
    public static double clamp(double value, double limit) {
        return clamp(value, -limit, limit);
    }

    /**
     * Clamps the value between the min and max values
     * @param value to clamp
     * @param min
     * @param max
     * @return value, min or max
     */
    public static double clamp(double value, double min, double max) {
        return value < min ? min : (value > max ? max : value);
    }

    /**
     * Clamps the value between -limit and limit
     *
     * @param value to clamp
     * @param limit
     * @return value, -limit or limit
     */
    public static float clamp(float value, float limit) {
        return clamp(value, -limit, limit);
    }

    /**
     * Clamps the value between -limit and limit
     *
     * @param value to clamp
     * @param limit
     * @return value, -limit or limit
     */
    public static int clamp(int value, int limit) {
        return clamp(value, -limit, limit);
    }

    /**
     * Turns a value negative or keeps it positive based on a boolean input
     *
     * @param value to work with
     * @param negative - True to invert, False to keep the old value
     * @return the value or inverted (-value)
     */
    public static int invert(int value, boolean negative) {
        return negative ? -value : value;
    }

    /**
     * Turns a value negative or keeps it positive based on a boolean input
     *
     * @param value to work with
     * @param negative - True to invert, False to keep the old value
     * @return the value or inverted (-value)
     */
    public static float invert(float value, boolean negative) {
        return negative ? -value : value;
    }

    /**
     * Turns a value negative or keeps it positive based on a boolean input
     *
     * @param value to work with
     * @param negative - True to invert, False to keep the old value
     * @return the value or inverted (-value)
     */
    public static double invert(double value, boolean negative) {
        return negative ? -value : value;
    }

    /**
     * Gets the angle difference between two angles
     *
     * @param angle1
     * @param angle2
     * @return angle difference
     */
    public static float getAngleDifference(float angle1, float angle2) {
        return Math.abs(wrapAngle(angle1 - angle2));
    }

    public static int r(int i) {
        return random.nextInt(i);
    }

    public static float sin(float radians) {
        return MathUtils.Sin.table[(int)(radians * 2607.5945F) & 16383];
    }

    public static float cos(float radians) {
        return MathUtils.Sin.table[(int)((radians + 1.5707964F) * 2607.5945F) & 16383];
    }

    public static float sinDeg(float degrees) {
        return MathUtils.Sin.table[(int)(degrees * 45.511112F) & 16383];
    }

    public static float cosDeg(float degrees) {
        return MathUtils.Sin.table[(int)((degrees + 90.0F) * 45.511112F) & 16383];
    }

    public static boolean isInteger(Object object) {
        try {
            Integer.parseInt(object.toString());
            return true;
        } catch (Exception var2) {
            return false;
        }
    }

    public static boolean isDouble(Object object) {
        try {
            Double.parseDouble(object.toString());
            return true;
        } catch (Exception var2) {
            return false;
        }
    }

    public static float atan2(float y, float x) {
        float add;
        float mul;
        if(x < 0.0F) {
            if(y < 0.0F) {
                y = -y;
                mul = 1.0F;
            } else {
                mul = -1.0F;
            }

            x = -x;
            add = -3.1415927F;
        } else {
            if(y < 0.0F) {
                y = -y;
                mul = -1.0F;
            } else {
                mul = 1.0F;
            }

            add = 0.0F;
        }

        float invDiv = 1.0F / ((x < y?y:x) * INV_ATAN2_DIM_MINUS_1);
        if(invDiv == 1.0F / 0.0) {
            return ((float)Math.atan2(y, x) + add) * mul;
        } else {
            int xi = (int)(x * invDiv);
            int yi = (int)(y * invDiv);
            return (MathUtils.Atan2.table[yi * ATAN2_DIM + xi] + add) * mul;
        }
    }

    public static int random(int range) {
        return random.nextInt(range + 1);
    }

    public static int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    public static boolean randomBoolean(float chance) {
        return random() < chance;
    }

    public static float random() {
        return random.nextFloat();
    }

    public static float random(float range) {
        return random.nextFloat() * range;
    }

    public static float random(float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    public static int nextPowerOfTwo(int value) {
        if(value == 0) {
            return 1;
        } else {
            --value;
            value |= value >> 1;
            value |= value >> 2;
            value |= value >> 4;
            value |= value >> 8;
            value |= value >> 16;
            return value + 1;
        }
    }

    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    public static int clamp(int value, int min, int max) {
        return value < min?min:(value > max?max:value);
    }

    public static short clamp(short value, short min, short max) {
        return value < min?min:(value > max?max:value);
    }

    public static float clamp(float value, float min, float max) {
        return value < min?min:(value > max?max:value);
    }

    public static int floor(float x) {
        return (int)((double)x + 16384.0D) - 16384;
    }

    public static int floorPositive(float x) {
        return (int)x;
    }

    public static int ceil(float x) {
        return (int)((double)x + 16384.999999999996D) - 16384;
    }

    public static int ceilPositive(float x) {
        return (int)((double)x + 0.9999999D);
    }

    public static int round(float x) {
        return (int)((double)x + 16384.5D) - 16384;
    }

    public static int roundPositive(float x) {
        return (int)(x + 0.5F);
    }

    public static boolean isZero(float value) {
        return Math.abs(value) <= 1.0E-6F;
    }

    public static boolean isZero(float value, float tolerance) {
        return Math.abs(value) <= tolerance;
    }

    public static boolean isEqual(float a, float b) {
        return Math.abs(a - b) <= 1.0E-6F;
    }

    public static boolean isEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    public static double getRandomAngle() {
        return random.nextDouble() * 2.0D * 3.141592653589793D;
    }

    public static double randomDouble(double min, double max) {
        return Math.random() < 0.5D?(1.0D - Math.random()) * (max - min) + min:Math.random() * (max - min) + min;
    }

    public static float randomRangeFloat(float min, float max) {
        return (float)(Math.random() < 0.5D?(1.0D - Math.random()) * (double)(max - min) + (double)min:Math.random() * (double)(max - min) + (double)min);
    }

    public static byte randomByte(int max) {
        return (byte)random.nextInt(max + 1);
    }

    public static int randomRangeInt(int min, int max) {
        return (int)(Math.random() < 0.5D?(1.0D - Math.random()) * (double)(max - min) + (double)min:Math.random() * (double)(max - min) + (double)min);
    }

    static {
        INV_ATAN2_DIM_MINUS_1 = 1.0F / (float)(ATAN2_DIM - 1);
        random = new Random();
    }

    private static class Atan2 {
        static final float[] table = new float[16384];

        private Atan2() {
        }

        static {
            for(int i = 0; i < MathUtils.ATAN2_DIM; ++i) {
                for(int j = 0; j < MathUtils.ATAN2_DIM; ++j) {
                    float x0 = (float)i / (float) MathUtils.ATAN2_DIM;
                    float y0 = (float)j / (float) MathUtils.ATAN2_DIM;
                    table[j * MathUtils.ATAN2_DIM + i] = (float)Math.atan2(y0, x0);
                }
            }

        }
    }
    private static class Sin {
        static final float[] table = new float[16384];

        private Sin() {
        }

        static {
            int i;
            for(i = 0; i < 16384; ++i) {
                table[i] = (float)Math.sin(((float)i + 0.5F) / 16384.0F * 6.2831855F);
            }

            for(i = 0; i < 360; i += 90) {
                table[(int)((float)i * 45.511112F) & 16383] = (float)Math.sin((float)i * 0.017453292F);
            }

        }
    }
}