package me.imdanix.caves.regions.griefprevention;

import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.RegionProtector;
import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

public class GriefPreventionFlagsProtector implements RegionProtector {
    private static final List<FlagDefinition.FlagType> FLAG_TYPES = Arrays.asList(FlagDefinition.FlagType.values());
    private final FlagDefinition entityFlag = new EntityGriefFlagDefinition();
    private final FlagDefinition blockFlag = new BlockChangeFlagDefinition();
    private final FlagDefinition effectFlag = new PlayerEffectFlagDefinition();

    @Override
    public void onEnable() {
        FlagManager flags = GPFlags.getInstance().getFlagManager();
        flags.registerFlagDefinition(new EntityGriefFlagDefinition());
    }

    @Override
    public String getName() {
        return "griefprevention-flags";
    }

    @Override
    public boolean test(CheckType checkType, Location location) {
        Flag flag = switch (checkType) {
            default -> entityFlag.GetFlagInstanceAtLocation(location, null);
            case BLOCK -> blockFlag.GetFlagInstanceAtLocation(location, null);
            case EFFECT -> effectFlag.GetFlagInstanceAtLocation(location, null);
        };
        return flag != null;
    }

    private static class EntityGriefFlagDefinition extends FlagDefinition {
        public EntityGriefFlagDefinition() {
            super(GPFlags.getInstance().getFlagManager(), GPFlags.getInstance());
        }

        @Override
        public String getName() {
            return "DCNoEntityGrief";
        }

        @Override
        public MessageSpecifier getSetMessage(String s) {
            return new MessageSpecifier(Messages.DisableMobDamage);
        }

        @Override
        public MessageSpecifier getUnSetMessage() {
            return new MessageSpecifier(Messages.EnableMobDamage);
        }

        @Override
        public List<FlagType> getFlagType() {
            return FLAG_TYPES;
        }
    }

    private static class BlockChangeFlagDefinition extends FlagDefinition {
        public BlockChangeFlagDefinition() {
            super(GPFlags.getInstance().getFlagManager(), GPFlags.getInstance());
        }

        @Override
        public String getName() {
            return "DCNoBlockChange";
        }

        @Override
        public MessageSpecifier getSetMessage(String s) {
            return new MessageSpecifier(Messages.EnableNoVineGrowth);
        }

        @Override
        public MessageSpecifier getUnSetMessage() {
            return new MessageSpecifier(Messages.DisableNoVineGrowth);
        }

        @Override
        public List<FlagType> getFlagType() {
            return FLAG_TYPES;
        }
    }

    private static class PlayerEffectFlagDefinition extends FlagDefinition {
        public PlayerEffectFlagDefinition() {
            super(GPFlags.getInstance().getFlagManager(), GPFlags.getInstance());
        }

        @Override
        public String getName() {
            return "DCNoPlayerEffect";
        }

        @Override
        public MessageSpecifier getSetMessage(String s) {
            return new MessageSpecifier(Messages.EnableNoHunger);
        }

        @Override
        public MessageSpecifier getUnSetMessage() {
            return new MessageSpecifier(Messages.DisableNoHunger);
        }

        @Override
        public List<FlagType> getFlagType() {
            return FLAG_TYPES;
        }
    }


}
