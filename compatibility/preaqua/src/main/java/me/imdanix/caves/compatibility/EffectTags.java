package me.imdanix.caves.compatibility;

import org.bukkit.Nameable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectTags implements TagsProvider {
    private static final String TAG_PREFIX = "§0§kdc-tag-";
    private final String[][][] tagsCache;

    public EffectTags() {
        tagsCache = new String[256][256][256];
    }

    public void cacheTag(String s) {
        tagsCache[getFirstId(s)][getSecondId(s)][getThirdId(s)] = s;
    }

    public void setTag(LivingEntity entity, String tag) {
        int firstId = getFirstId(tag);
        int secondId = getSecondId(tag);
        int thirdId = getThirdId(tag);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, firstId, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Integer.MAX_VALUE, secondId, false, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, secondId, false, false));
        tagsCache[firstId][secondId][thirdId] = tag;
    }

    public void setTag(Block block, String tag) {
        BlockState state = block.getState();
        if(!(state instanceof Nameable)) return;
        ((Nameable)state).setCustomName(TAG_PREFIX + tag);
        state.update();
    }

    public String getTag(LivingEntity entity) {
        if(entity.getType() == EntityType.PLAYER) return null;
        PotionEffect effect = entity.getPotionEffect(PotionEffectType.CONFUSION);
        if(effect == null) return null;
        int firstId = effect.getAmplifier();
        effect = entity.getPotionEffect(PotionEffectType.HUNGER);
        if(effect == null) return null;
        int secondId = effect.getAmplifier();
        effect = entity.getPotionEffect(PotionEffectType.NIGHT_VISION);
        if(effect == null) return null;
        int thirdId = effect.getAmplifier();
        return tagsCache[firstId][secondId][thirdId];
    }

    public String getTag(Block block) {
        BlockState state = block.getState();
        if(!(state instanceof Nameable)) return null;
        String name = ((Nameable) state).getCustomName();
        return name != null && name.startsWith(TAG_PREFIX) ? name.substring(TAG_PREFIX.length()) : null;
    }

    private static int getFirstId(String tag) {
        return ((int)((byte)tag.hashCode())) + 128;
    }

    private static int getSecondId(String tag) {
        return ((int)((byte)Integer.toString(tag.hashCode()).hashCode())) + 128;
    }

    private static int getThirdId(String tag) {
        return ((int)((byte)(tag.hashCode() * -1907))) + 128;
    }
}
