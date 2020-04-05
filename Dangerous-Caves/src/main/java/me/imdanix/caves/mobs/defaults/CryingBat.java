package me.imdanix.caves.mobs.defaults;

import me.imdanix.caves.configuration.Configurable;
import me.imdanix.caves.mobs.TickableMob;
import me.imdanix.caves.util.Utils;
import me.imdanix.caves.util.random.Rnd;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

@Configurable.Path("mobs.crying-bat")
public class CryingBat extends TickableMob implements Configurable, Listener {
    private int weight;
    private String name;
    private double cryChance;
    private double deathChance;

    public CryingBat() {
        super(EntityType.BAT, "crying-bat");
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        weight = cfg.getInt("priority", 1);
        name = Utils.clr(cfg.getString("name", "&4Crying Bat"));
        cryChance = cfg.getDouble("cry-chance", 3.33) / 100;
        deathChance = cfg.getDouble("death-chance", 20) / 100;
    }

    @Override
    public void setup(LivingEntity entity) {
        if(!name.isEmpty()) entity.setCustomName(name);
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void tick(LivingEntity entity) {
        if(cryChance > 0 && Rnd.chance(cryChance)) {
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_WOLF_WHINE, 1, (float) (1.4 + Rnd.nextDouble(0.6)));
            if(deathChance > 0 && Rnd.chance(deathChance))
                entity.damage(1000);
        }
    }
}
