package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.compatibility.VMaterial;
import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.mobs.TickableMob;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.compatibility.Compatibility;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Configurable.Path("mobs.lava-creeper")
public class LavaCreeper extends TickableMob implements Configurable, Listener {
    private static final PotionEffect FIRE_RESISTANCE = new PotionEffect(PotionEffectType.FIRE_RESISTANCE,
            Integer.MAX_VALUE, 1, false, false);
    private int weight;
    private String name;

    private double chance;
    private double fire;
    private double magmaBlock;
    private double obsidian;
    private double lava;

    private int fireTouch;
    private int radius;
    private int radiusSquared;

    public LavaCreeper() {
        super(EntityType.CREEPER, "lava-creeper");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        name = Utils.clr(cfg.getString("name", "&4Lava Creeper"));
        chance = cfg.getDouble("change-chance", 50) / 100;
        fire = cfg.getDouble("blocks.fire", 33.33) / 100;
        magmaBlock = cfg.getDouble("blocks.magma_block", 25) / 100;
        obsidian = cfg.getDouble("blocks.obsidian", 20) / 100;
        lava = cfg.getDouble("blocks.lava", 16.67) / 100;
        fireTouch = cfg.getInt("fire-touch", 10);
        radius = cfg.getInt("radius", 4);
        radiusSquared = radius * radius;
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
        entity.addPotionEffect(FIRE_RESISTANCE);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if(fireTouch <= 0 || !isThis(event.getEntity())) return;
        event.getDamager().setFireTicks(fireTouch);
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        if(!isThis(event.getEntity()) || chance <= 0) return;
        Location start = event.getLocation();
        int cx = start.getBlockX();
        int cy = start.getBlockY();
        int cz = start.getBlockZ();
        Locations.loop(radius, start, (world, x, y, z) -> {
            if(((cx - x)^2 + (cy - y)^2 + (cz - z)^2) > radiusSquared || !Rnd.chance(chance)) return;
            Block block = new Location(world, x, y, z).getBlock();
            if(Compatibility.isAir(block.getType())) {
                if(fire > 0 && Rnd.chance(fire)) block.setType(Material.FIRE);
            } else if(block.getType() != Material.BEDROCK) {
                if(magmaBlock > 0 && Rnd.chance(magmaBlock)) {
                    block.setType(VMaterial.MAGMA_BLOCK.get());
                } else if(obsidian > 0 && Rnd.chance(obsidian)) {
                    block.setType(Material.OBSIDIAN);
                } else if(lava > 0 && Rnd.chance(lava)) {
                    block.setType(Material.LAVA);
                }
            }
        });
    }

    @Override
    public void tick(LivingEntity entity) {
        entity.getWorld().spawnParticle(Particle.LAVA, entity.getLocation().add(0, 1, 0), 1, 0.3, 0.8, 0.3);
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
