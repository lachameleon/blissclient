package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BedBlock;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Objects;

public class AutoSleep extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> bedSearchRadius = sgGeneral.add(new IntSetting.Builder()
            .name("bed-search-radius")
            .description("Radius to search for beds.")
            .defaultValue(3)
            .min(1)
            .sliderRange(1, 4)
            .build()
    );
    private final Setting<SleepMode> sleepMode = sgGeneral.add(new EnumSetting.Builder<SleepMode>()
            .name("sleep-mode")
            .description("'Default' - sleep every amount of ticks; 'WhenPlayerLiesOnBed' - sleep when other player lies down on bed.")
            .defaultValue(SleepMode.Default)
            .build()
    );
    private final Setting<Integer> sleepDelay = sgGeneral.add(new IntSetting.Builder()
            .name("sleep-delay")
            .description("Delay between tries to sleep in ticks (20 ticks = 1 sec).")
            .defaultValue(200)
            .min(1)
            .sliderRange(1, 1200)
            .visible(() -> sleepMode.get() == SleepMode.Default)
            .build()
    );
    private final Setting<Boolean> atNight = sgGeneral.add(new BoolSetting.Builder()
            .name("at-night")
            .description("Sleep only at night.")
            .defaultValue(true)
            .visible(() -> sleepMode.get() == SleepMode.Default)
            .build()
    );
    private final Setting<Boolean> atThunderstorm = sgGeneral.add(new BoolSetting.Builder()
            .name("at-thunderstorm")
            .description("Sleep only at thunderstorm.")
            .defaultValue(true)
            .visible(() -> sleepMode.get() == SleepMode.Default)
            .build()
    );
    private final Setting<Boolean> useMaxSleepTime = sgGeneral.add(new BoolSetting.Builder()
            .name("use-max-sleep-time")
            .description("Use maximum sleep time.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Integer> maxSleepTime = sgGeneral.add(new IntSetting.Builder()
            .name("max-sleep-time")
            .description("Maximum time to sleep in ticks (20 ticks = 1 sec).")
            .defaultValue(200)
            .min(1)
            .sliderRange(1, 1200)
            .visible(useMaxSleepTime::get)
            .build()
    );
    private final Setting<Boolean> dimensionRestrict = sgGeneral.add(new BoolSetting.Builder()
            .name("dimension-restrict")
            .description("Don't go to bed if you're in a dimension like nether/end.")
            .defaultValue(true)
            .build()
    );

    private int sleepDelayTimer;
    private int sleepTimer;

    public AutoSleep() {
        super(Categories.Misc, "auto-sleep", "Automatically sleeps in beds.");
    }

    @Override
    public void onActivate() {
        sleepDelayTimer = sleepDelay.get();
        sleepTimer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.world == null) return;
        if (dimensionRestrict.get() && !mc.world.getDimension().hasSkyLight()) return;

        if (mc.player.isSleeping()) {
            if (useMaxSleepTime.get()) {
                sleepTimer++;
                if (sleepTimer >= maxSleepTime.get()) {
                    sleepTimer = 0;
                    // mc.getConnection().send(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING));
                }
            }
        } else if (sleepMode.get() == SleepMode.Default) {
            sleepDelayTimer++;
            if (sleepDelayTimer < sleepDelay.get()) return;

            sleepDelayTimer = sleepDelay.get();

            if (atNight.get() && atThunderstorm.get()) {
                if (isDay() && !mc.world.isThundering()) return;
            } else if (atNight.get()) {
                if (isDay()) return;
            } else if (atThunderstorm.get()) {
                if (!mc.world.isThundering()) return;
            }

            if (sleepInNearestBed(bedSearchRadius.get())) sleepDelayTimer = 0;
        }
    }

    // Yeah, hacky, but looks like `world.isDay()` and `world.isNight()` doesn't work on the client
    private boolean isDay() {
        if (mc.world == null) return true;
        float time = mc.world.getTimeOfDay() % 24000;
        return mc.world.isRaining() ? (!(time > 12010) || !(time < 23991)) : (!(time > 12542) || !(time < 23459));
    }

    private boolean sleepInNearestBed(int radius) {
        for (BlockPos blockPos : BlockPos.iterate(mc.player.getBlockPos().add(-radius, -radius, -radius), mc.player.getBlockPos().add(radius, radius, radius))) {
            if (mc.world.getBlockState(blockPos).getBlock() instanceof BedBlock) {
                sleepTimer = 0;
                BlockUtils.interact(new BlockHitResult(blockPos.toCenterPos(), Direction.UP, blockPos, false), Hand.MAIN_HAND, false);
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onReceiveMessage(PacketEvent.Receive event) {
        // Removed WhenPlayerLiesOnBed functionality due to API differences
    }

    public enum SleepMode {
        Default,
        WhenPlayerLiesOnBed
    }
}
