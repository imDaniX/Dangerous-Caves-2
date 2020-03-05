package com.github.evillootlye.caves.mobs.defaults;

import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.mobs.TickableMob;
import com.github.evillootlye.caves.utils.random.Rnd;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

@Configurable.Path("mobs.crying-bat")
public class CryingBat extends TickableMob implements Configurable, Listener {
    private int weight;
    private double cryChance;
    private double deathChance;

    public CryingBat() {
        super(EntityType.BAT, "crying-bat");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        cryChance = cfg.getDouble("cry-chance", 3.33) / 100;
        deathChance = cfg.getDouble("death-chance", 20) / 100;
    }

    @Override
    public void setup(LivingEntity entity) {
        //
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void tick(LivingEntity entity) {
        if(cryChance > 0 && cryChance > Rnd.nextDouble())
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_WOLF_WHINE, 1, (float) (1.4 + Rnd.nextDouble(0.6)));
        else if(deathChance > 0 && deathChance > Rnd.nextDouble())
            entity.damage(1000);
    }
}
