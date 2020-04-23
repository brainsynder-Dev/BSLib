package lib.brainsynder.nms;

import lib.brainsynder.ServerVersion;
import lib.brainsynder.apache.EnumUtils;
import lib.brainsynder.item.ItemBuilder;
import lib.brainsynder.utils.DyeColorWrapper;
import org.bukkit.Material;

public class DataConverter {
    public static ItemBuilder getColoredMaterial(MaterialType type, int data) {
        if (ServerVersion.isOlder(ServerVersion.v1_13_R1)) return toBuilder(getMaterial(type.name()), data);

        DyeColorWrapper dye = DyeColorWrapper.getByWoolData((byte) data);
        if ((type == MaterialType.DYE) || (type == MaterialType.INK_SACK)) dye = DyeColorWrapper.getByDyeData((byte) data);

        String name = dye.name();
        if (name.equalsIgnoreCase("SILVER")) name = "LIGHT_GRAY";

        Material material = null;
        if (ServerVersion.isEqualNew(ServerVersion.v1_14_R1)) {
            // This is mostly due to the addition of the new dye items in 1.14+
            if (type == MaterialType.DYE) return toBuilder(getMaterial(name + "_DYE"), -1);
        }else{
            // Below 1.14
            if ((type == MaterialType.INK_SACK) || (type == MaterialType.DYE)) {
                if (dye == DyeColorWrapper.WHITE) {
                    material = getMaterial("BONE_MEAL");
                } else if (dye == DyeColorWrapper.YELLOW) {
                    material = getMaterial("DANDELION_YELLOW");
                } else if (dye == DyeColorWrapper.BLUE) {
                    material = getMaterial("LAPIS_LAZULI");
                } else if (dye == DyeColorWrapper.BROWN) {
                    material = getMaterial("COCOA_BEANS");
                } else if (dye == DyeColorWrapper.GREEN) {
                    material = getMaterial("CACTUS_GREEN");
                } else if (dye == DyeColorWrapper.RED) {
                    material = getMaterial("ROSE_RED");
                } else if (dye == DyeColorWrapper.BLACK) {
                    material = getMaterial("INK_SAC", "INK_SACK");
                } else {
                    material = getMaterial(name + "_DYE");
                }
            }
        }
        if (material == null) material = getMaterial(name + "_" + type.getName());

        return toBuilder(material, -1);
    }

    private static ItemBuilder toBuilder(Material material, int data) {
        ItemBuilder builder = new ItemBuilder(material);
        if (data != -1) builder.withDurability(data);
        return builder;
    }

    public static Material getMaterial (String name) {
        return getMaterial(name, null);
    }

    public static Material getMaterial (String name, String fallback) {
        name = name.toUpperCase();
        if (EnumUtils.isValidEnum(Material.class, name)) return Material.valueOf(name);
        if ((fallback != null) && (!fallback.isEmpty())) return getMaterial(fallback);
        return Material.STONE;
    }

    public enum MaterialType {
        STAINED_GLASS_PANE,
        WOOL,
        STAINED_CLAY("TERRACOTTA"),
        INK_SACK,
        DYE,
        CONCRETE,
        CONCRETE_POWDER;

        private final String name;

        MaterialType() {
            this.name = name();
        }
        MaterialType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
