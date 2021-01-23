package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.mobs.TickingMob;
import me.imdanix.caves.util.Locations;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rng;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class CryingBat extends TickingMob {
    private String name;
    private double cryChance;
    private double deathChance;

    public CryingBat() {
        super(EntityType.BAT, "crying-bat", 9);
    }

    @Override
    protected void configure(ConfigurationSection cfg) {
        name = Utils.clr(cfg.getString("name", "&4Crying Bat"));
        cryChance = cfg.getDouble("cry-chance", 3.33) / 100;
        deathChance = cfg.getDouble("death-chance", 20) / 100;
    }

    @Override
    public void setup(LivingEntity entity) {
        if (!name.isEmpty()) entity.setCustomName(name);
    }

    @Override
    public void tick(LivingEntity entity) {
        if (cryChance > 0 && Rng.chance(cryChance)) {
            Locations.playSound(entity.getLocation(), Sound.ENTITY_WOLF_WHINE, 1, (float) (1.4 + Rng.nextDouble(0.6)));
            if (deathChance > 0 && Rng.chance(deathChance))
                entity.damage(1000);
        }
    }
}
