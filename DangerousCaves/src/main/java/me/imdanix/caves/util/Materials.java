package me.imdanix.caves.util;

import me.imdanix.caves.compatibility.VMaterial;
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
    public static final Material[] HELMETS;
    public static final Material[] LEGGINGS;
    public static final Material[] CHESTPLATES;
    public static final Material[] BOOTS;
    static {
        List<Material> helmets = new ArrayList<>();
        List<Material> leggings = new ArrayList<>();
        List<Material> chestplates = new ArrayList<>();
        List<Material> boots = new ArrayList<>();
        for (Material mat : Material.values()) {
            if (mat.isBlock()) continue;
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
        helmets.add(VMaterial.CARVED_PUMPKIN.get());
        HELMETS = helmets.toArray(new Material[0]);
        LEGGINGS = leggings.toArray(new Material[0]);
        CHESTPLATES = chestplates.toArray(new Material[0]);
        BOOTS = boots.toArray(new Material[0]);
    }

    private static final Set<Material> CAVE = new Builder(
            Material.STONE, Material.BONE_BLOCK, Material.OBSIDIAN, Material.BEDROCK,
            Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.IRON_ORE, Material.GOLD_ORE,
            Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.COAL_ORE,
            Material.COBBLESTONE, Material.MOSSY_COBBLESTONE,
            Material.DIRT, Material.GRAVEL,
            Material.SOUL_SAND, Material.NETHERRACK, Material.GLOWSTONE,
            Material.TORCH, Material.CHEST
        ).with(
            // 1.13
            "ANDESITE", "DIORITE", "GRANITE",
            // 1.16
            "SOUL_SOIL", "BLACKSTONE", "BASALT",
            "NETHER_GOLD_ORE", "GILDED_BLACKSTONE",
            // 1.17
            "CALCITE", "TUFF", "DRIPSTONE_BLOCK", "MOSS_BLOCK", "ROOTED_DIRT",
            "DEEPSLATE", "DEEPSLATE_COAL_ORE", "DEEPSLATE_COPPER_ORE",
            "DEEPSLATE_DIAMOND_ORE", "DEEPSLATE_EMERALD_ORE", "DEEPSLATE_GOLD_ORE",
            "DEEPSLATE_IRON_ORE", "DEEPSLATE_LAPIS_ORE", "DEEPSLATE_REDSTONE_ORE"
        ).with(
            or("RAIL", "RAILS"),
            or("SPAWNER", "MOB_SPAWNER"),
            or("OAK_PLANKS", "WOOD"),
            or("END_STONE", "ENDER_STONE"),
            or("NETHER_QUARTZ_ORE", "QUARTZ_ORE")
        ).build(true);

    private static final Set<Material> AIR = new Builder(
            "AIR", "CAVE_AIR", "VOID_AIR"
        ).build(true);

    public static void rotate(Block block, BlockFace face) {
        BlockData data = block.getBlockData();
        if (data instanceof FaceAttachable) {
            ((FaceAttachable) data).setAttachedFace(FaceAttachable.AttachedFace.FLOOR);
        } else if (data instanceof Directional) {
            ((Directional) data).setFacing(face);
        } else if (data instanceof MultipleFacing) {
            ((MultipleFacing) data).setFace(face, true);
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

    public static boolean isAir(Material type) {
        return AIR.contains(type);
    }

    public static boolean isCave(Material type) {
        return CAVE.contains(type);
    }

    public static Set<Material> getSet(Collection<String> types) {
        Set<Material> materials = EnumSet.noneOf(Material.class);
        for (String typeStr : types) {
            Material type = Material.getMaterial(typeStr.toUpperCase(Locale.ENGLISH));
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
            Material type = Material.getMaterial(typeStr.toUpperCase(Locale.ENGLISH));
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
