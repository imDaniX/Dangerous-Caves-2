package me.imdanix.caves.util;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class Materials {
    public static final List<Material> HELMETS;
    public static final List<Material> LEGGINGS;
    public static final List<Material> CHESTPLATES;
    public static final List<Material> BOOTS;
    static {
        List<Material> helmets = new ArrayList<>();
        List<Material> leggings = new ArrayList<>();
        List<Material> chestplates = new ArrayList<>();
        List<Material> boots = new ArrayList<>();
        for (Material mat : Material.values()) {
            String name = mat.name();
            if (name.endsWith("_HELMET")) {
                helmets.add(mat);
            } else if (name.endsWith("_LEGGINGS")) {
                leggings.add(mat);
            } else if (name.endsWith("_CHESTPLATE")) {
                chestplates.add(mat);
            } else if (name.endsWith("_BOOTS")) {
                boots.add(mat);
            }
        }
        helmets.add(Material.CARVED_PUMPKIN);

        HELMETS = Collections.unmodifiableList(helmets);
        LEGGINGS = Collections.unmodifiableList(leggings);
        CHESTPLATES = Collections.unmodifiableList(chestplates);
        BOOTS = Collections.unmodifiableList(boots);
    }

    private static final Set<Material> CAVE = new Builder(
            Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.CALCITE, Material.TUFF, Material.DRIPSTONE_BLOCK,
            Material.STONE, Material.BONE_BLOCK, Material.OBSIDIAN, Material.BEDROCK, Material.SMOOTH_BASALT,
            Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.IRON_ORE, Material.GOLD_ORE,
            Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.COAL_ORE, Material.COPPER_ORE,
            Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_EMERALD_ORE, Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_REDSTONE_ORE, Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_COPPER_ORE,
            Material.COBBLESTONE, Material.MOSSY_COBBLESTONE,
            Material.DIRT, Material.GRAVEL, Material.MOSS_BLOCK, Material.ROOTED_DIRT,
            Material.SOUL_SAND, Material.NETHERRACK, Material.GLOWSTONE,
            Material.TORCH, Material.CHEST, Material.OAK_PLANKS, Material.RAIL,
            Material.SPAWNER, Material.END_STONE, Material.NETHER_QUARTZ_ORE,
            Material.SOUL_SOIL, Material.BLACKSTONE, Material.BASALT,
            Material.NETHER_GOLD_ORE, Material.GILDED_BLACKSTONE,
            Material.DEEPSLATE
        ).build(true);

    public static void rotate(Block block, BlockFace face) {
        BlockData data = block.getBlockData();
        if (data instanceof FaceAttachable attachable) {
            attachable.setAttachedFace(FaceAttachable.AttachedFace.FLOOR);
        } else if (data instanceof Directional directional) {
            directional.setFacing(face);
        } else if (data instanceof MultipleFacing multifacing) {
            multifacing.setFace(face, true);
        }
        block.setBlockData(data, false);
    }

    public static ItemStack getHeadFromValue(String value) {
        UUID id = UUID.nameUUIDFromBytes(value.getBytes());
        // Heck yeah, magic numbers
        long less = id.getLeastSignificantBits();
        int lessA = (int) (less >> 32); int lessB = (int) less;
        long most = id.getMostSignificantBits();
        int mostA = (int) (most >> 32); int mostB = (int) most;
        return Bukkit.getUnsafe().modifyItemStack(
                new ItemStack(Material.PLAYER_HEAD),
                "{SkullOwner:{Id:[I;" + lessA + "," + lessB + "," + mostA + "," + mostB + "]," +
                        "Properties:{textures:[{Value:\"" + value + "\"}]}}}"
        );
    }

    public static boolean isCave(Material type) {
        return CAVE.contains(type);
    }

    public static Set<Material> getSet(Collection<String> types) {
        Set<Material> materials = EnumSet.noneOf(Material.class);
        for (String typeStr : types) {
            Material type = Material.getMaterial(typeStr.toUpperCase(Locale.ROOT));
            if (type != null) materials.add(type);
        }
        return materials;
    }

    public static ItemStack getColored(EquipmentSlot slot, int r, int g, int b) {
        ItemStack item;
        switch (slot) {
            default: return null;

            case HEAD:
                item = new ItemStack(Material.LEATHER_HELMET);
                break;

            case CHEST:
                item = new ItemStack(Material.LEATHER_CHESTPLATE);
                break;

            case LEGS:
                item = new ItemStack(Material.LEATHER_LEGGINGS);
                break;

            case FEET:
                item = new ItemStack(Material.LEATHER_BOOTS);
                break;
        }
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(r, g, b));
        item.setItemMeta(meta);
        return item;
    }

    public static Material or(String... types) {
        for (String typeStr : types) {
            Material type = Material.getMaterial(typeStr.toUpperCase(Locale.ROOT));
            if (type != null) return type;
        }
        return null;
    }

    public static class Builder {
        private final Set<Material> materials;

        public Builder() {
            materials = EnumSet.noneOf(Material.class);
        }

        public Builder(Material... types) {
            this(); with(types);
        }

        public Builder(String... types) {
            this(); with(types);
        }

        public Builder with(String... types) {
            materials.addAll(Materials.getSet(Arrays.asList(types)));
            return this;
        }

        public Builder with(Material... types) {
            for (Material type : types) {
                if (type != null)
                    materials.add(type);
            }
            return this;
        }

        public Set<Material> build(boolean unmod) {
            return unmod ? Collections.unmodifiableSet(materials) : materials;
        }
    }
}
