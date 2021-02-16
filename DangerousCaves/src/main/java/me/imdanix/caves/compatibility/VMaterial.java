package me.imdanix.caves.compatibility;

import org.bukkit.Material;

public enum VMaterial {
    MAGMA_BLOCK("MAGMA_BLOCK", "MAGMA"),
    REDSTONE_TORCH("REDSTONE_TORCH", "REDSTONE_TORCH_ON"),
    ANDESITE("ANDESITE", "COBBLESTONE"),
    COBBLESTONE_WALL("COBBLESTONE_WALL", "COBBLE_WALL"),
    STONE_PRESSURE_PLATE("STONE_PRESSURE_PLATE", "STONE_PLATE"),
    COBWEB("COBWEB", "WEB"),
    SPRUCE_PLANKS("SPRUCE_PLANKS", "WOOD"),
    SKELETON_SKULL_BLOCK("SKELETON_SKULL", "SKULL"),
    CRAFTING_TABLE("CRAFTING_TABLE", "WORKBENCH"),
    COBBLESTONE_SLAB("COBBLESTONE_SLAB", "STONE_SLAB2", "STONE_SLAB"),
    OAK_PLANKS("OAK_PLANKS", "WOOD"),
    SPRUCE_LOG("SPRUCE_LOG", "LOG"),
    SPAWNER("SPAWNER", "MOB_SPAWNER"),
    INFESTED_STONE("INFESTED_STONE", "MONSTER_EGGS"),
    STONE_BRICKS("STONE_BRICKS", "SMOOTH_BRICK"),
    CRACKED_STONE_BRICKS("CRACKED_STONE_BRICKS", "SMOOTH_BRICK"),
    POLISHED_ANDESITE("POLISHED_ANDESITE", "SMOOTH_BRICK"),
    STONE_BRICK_SLAB("STONE_BRICK_SLAB", "STONE_SLAB2", "STONE_SLAB"),
    CARVED_PUMPKIN("CARVED_PUMPKIN", "PUMPKIN");

    private final Material type;

    VMaterial(String... typesStr) {
        for (String typeStr : typesStr) {
            Material type = Material.getMaterial(typeStr);
            if (type != null) {
                this.type = type;
                return;
            }
        }
        this.type = null;
    }

    public Material get() {
        return type;
    }
}
