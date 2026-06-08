package dev.stardust.modules;

import meteordevelopment.meteorclient.systems.modules.Categories;
import net.minecraft.world.level.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.settings.*;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;

/**
 * @author Tas [0xTas] <root@0xTas.dev>
 **/
public class AutoDoors extends Module {
    public AutoDoors()  {
        super(Categories.World, "AutoDoors", "Automatically interact with doors.");
    }

    public enum DoorModes {
        Classic, Spammer
    }
    public enum MuteModes {
        Never, Always, Spammer
    }

    private final Setting<DoorModes> modeSetting = settings.getDefaultGroup().add(
        new EnumSetting.Builder<DoorModes>()
            .name("mode")
            .description("Which mode to operate in.")
            .defaultValue(DoorModes.Classic)
            .build()
    );

    private final Setting<MuteModes> muteSetting = settings.getDefaultGroup().add(
        new EnumSetting.Builder<MuteModes>()
            .name("mute-doors")
            .description("Whether to mute door sounds when the module is active.")
            .defaultValue(MuteModes.Never)
            .build()
    );

    private final Setting<Integer> spamRange = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("spam-range")
            .description("Range of blocks to look for doors in.")
            .range(1, 5)
            .sliderRange(1, 5)
            .defaultValue(5)
            .visible(() -> modeSetting.get() == DoorModes.Spammer)
            .build()
    );

    private final Setting<Integer> spamRate = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("spam-delay")
            .description("Delay (in ticks) between each interaction.")
            .range(2, 20)
            .sliderRange(2, 20)
            .defaultValue(2)
            .visible(() -> modeSetting.get() == DoorModes.Spammer)
            .build()
    );

    private final Setting<Integer> interactDelay = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("lever-delay")
            .description("Increase this if iron doors controlled by levers are acting scuffed.")
            .range(0, 100)
            .sliderRange(2, 60)
            .defaultValue(5)
            .build()
    );

    private final Setting<Boolean> autoOpen = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("auto-open")
            .description("Automatically open doors as you move towards them.")
            .defaultValue(true)
            .visible(() -> modeSetting.get() == DoorModes.Classic)
            .build()
    );

    private final Setting<Boolean> silentSwing = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("silent-swing")
            .description("No client-side hand swing.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> ninjaSwing = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("ninja-swing")
            .description("No server-side hand swing.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> useIronDoors = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("iron-doors")
            .description("Interact with iron doors using nearby buttons or levers.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> useTrapdoors = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("trapdoors")
            .description("Interact with trapdoors (only works when not on ladders.)")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> useFenceGates = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("fence-gates")
            .description("Interact with fence gates.")
            .defaultValue(false)
            .build()
    );

    private int tickCounter = 0;
    private int ticksSinceInteracted = 0;
    private Vec3 lastBlock = new Vec3(0.0, 0.0, 0.0);


    // See DoorBlockMixin.java
    public boolean shouldMute() {
        return this.isActive() && (muteSetting.get() == MuteModes.Always
            || (modeSetting.get() == DoorModes.Spammer && muteSetting.get() == MuteModes.Spammer));
    }

    private void interactDoor(BlockPos pos, Direction direction) {
        if (mc.player == null) return;
        if (mc.gameMode == null) return;
        Direction side = getDirection(pos, direction);
        mc.gameMode.useItemOn(
            mc.player,
            InteractionHand.MAIN_HAND,
            new BlockHitResult(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), side, pos, true)
        );

        if (silentSwing.get() && ninjaSwing.get()) return;
        if (!silentSwing.get() && ninjaSwing.get()) {
            ((LivingEntity) mc.player).swing(InteractionHand.MAIN_HAND);
        }else if (silentSwing.get() && !ninjaSwing.get()) {
            if (mc.getConnection() != null) mc.getConnection().send(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
        }else mc.player.swing(InteractionHand.MAIN_HAND);
    }

    private @NotNull Direction getDirection(BlockPos pos, Direction direction) {
        Vec3 pPos = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());

        Direction side;
        switch (direction) {
            case EAST -> {
                if (pPos.x < pos.getX()) side = Direction.WEST;
                else side = Direction.EAST;
            }
            case WEST -> {
                if (pPos.x > pos.getX()) side = Direction.EAST;
                else side = Direction.WEST;
            }
            case NORTH -> {
                if (pPos.z > pos.getZ()) side = Direction.SOUTH;
                else side = Direction.NORTH;
            }
            case SOUTH -> {
                if (pPos.z < pos.getZ()) side = Direction.NORTH;
                else side = Direction.SOUTH;
            }
            case UP -> {
                if (pPos.y < pos.getY()) side = Direction.DOWN;
                else side = Direction.UP;
            }
            case DOWN -> {
                if (pPos.y > pos.getY()) side = Direction.DOWN;
                else side = Direction.UP;
            }
            default -> side = Direction.DOWN;
        }
        return side;
    }


    private static @NotNull Direction getMovementDirection(PlayerMoveEvent event) {
        double velocityX = event.movement.x;
        double velocityY = event.movement.y;
        double velocityZ = event.movement.z;

        double directionRadians = Math.atan2(-velocityZ, velocityX);
        double directionDegrees = Math.toDegrees(directionRadians);
        double normalizedDegrees = (directionDegrees + 360) % 360;

        Direction movementDirection;
        if (normalizedDegrees >= 45 && normalizedDegrees < 135) movementDirection = Direction.NORTH;
        else if (normalizedDegrees >= 135 && normalizedDegrees < 225) movementDirection = Direction.WEST;
        else if (normalizedDegrees >= 225 && normalizedDegrees < 315) movementDirection = Direction.SOUTH;
        else if (normalizedDegrees >= 315 || normalizedDegrees < 45) movementDirection = Direction.EAST;
        else if (velocityY > 0) movementDirection = Direction.UP;
        else movementDirection = Direction.DOWN;
        return movementDirection;
    }

    private boolean scanForSwitches(BlockPos pos, Block block, Boolean open, Direction moving, Direction side, int n) {
        if (mc.level == null) return true;
        if (block instanceof ButtonBlock || block instanceof LeverBlock) {
            BlockState state = mc.level.getBlockState(pos);
            try {
                if (open && block instanceof ButtonBlock && state.getValue(ButtonBlock.POWERED)) return false;
                else if (open && block instanceof LeverBlock && state.getValue(LeverBlock.POWERED)) return false;
                else if(!open && block instanceof LeverBlock && !state.getValue(LeverBlock.POWERED)) return false;
            } catch (IllegalArgumentException ignored) {} // skill issue insurance

            if (!open && block instanceof ButtonBlock) return true;
            this.interactDoor(pos.relative(side, n), moving);
            return true;
        } else {
            BlockState upState = mc.level.getBlockState(pos.relative(moving.getOpposite()).relative(side, n).above());
            BlockState downState = mc.level.getBlockState(pos.relative(moving.getOpposite()).relative(side, n).below());
            Block upBlock = upState.getBlock();
            Block downBlock = downState.getBlock();

            if (upBlock instanceof ButtonBlock || upBlock instanceof LeverBlock) {
                try {
                    if (open && upBlock instanceof ButtonBlock && upState.getValue(ButtonBlock.POWERED)) return false;
                    else if (open && upBlock instanceof LeverBlock && upState.getValue(LeverBlock.POWERED)) return false;
                    else if(!open && upBlock instanceof LeverBlock && !upState.getValue(LeverBlock.POWERED)) return false;
                }catch (IllegalArgumentException ignored) {}

                if (!open && upBlock instanceof ButtonBlock) return true;
                this.interactDoor(pos.relative(moving.getOpposite()).relative(side, n).above(), moving);
                return true;
            } else if (downBlock instanceof ButtonBlock || downBlock instanceof LeverBlock) {
                try {
                    if (open && downBlock instanceof ButtonBlock && downState.getValue(ButtonBlock.POWERED)) return false;
                    else if (open && downBlock instanceof LeverBlock && downState.getValue(LeverBlock.POWERED)) return false;
                    else if(!open && downBlock instanceof LeverBlock && !downState.getValue(LeverBlock.POWERED)) return false;
                } catch (IllegalArgumentException ignored) {}

                if (!open && downBlock instanceof ButtonBlock) return true;
                this.interactDoor(pos.relative(moving).relative(side, n).below(), moving);
                return true;
            }
        }

        return false;
    }

    private void tryInteractIronDoor(BlockPos pos, BlockState state, Direction direction, boolean open) {
        if (mc.level == null || mc.gameMode == null) return;
        if (!(state.getBlock() instanceof DoorBlock ironDoor)) return;
        if (open == ironDoor.isOpen(state)) return;

        this.ticksSinceInteracted = 0;
        for (int n = 0; n < 4; n++) {
            for (Direction side : Direction.values()) {
                Block offset = mc.level.getBlockState(pos.relative(direction.getOpposite()).relative(side, n)).getBlock();
                Block offset2 = mc.level.getBlockState(pos.relative(side, n)).getBlock();
                Block offset3 = mc.level.getBlockState(pos.relative(direction).relative(side, n)).getBlock();

                if (this.scanForSwitches(pos, offset, open, direction, side, n)) return;
                else if (this.scanForSwitches(pos, offset2, open, direction, side, n)) return;
                else if (this.scanForSwitches(pos, offset3, open, direction, side, n)) return;
            }
        }
    }

    private LongArrayList getSurroundingDoors() {
        LongArrayList doors = new LongArrayList();
        if (mc.player == null || mc.level == null) return doors;

        int range = spamRange.get();
        BlockPos bPos = mc.player.blockPosition();
        BlockPos.MutableBlockPos doorPos = new BlockPos.MutableBlockPos();
        for (int x = bPos.getX() - range; x < bPos.getX() + range; x++) {
            for (int y = bPos.getY() - range; y < bPos.getY() + range; y++) {
                for (int z = bPos.getZ() - range; z < bPos.getZ() + range; z++) {
                    doorPos.set(x, y, z);
                    if (mc.level.getBlockState(doorPos).getBlock() instanceof DoorBlock) {
                        doors.add(doorPos.asLong());
                    }
                }
            }
        }

        return doors;
    }


    @Override
    public void onDeactivate() {
        this.tickCounter = 0;
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        if (modeSetting.get() == DoorModes.Spammer || mc.player == null || mc.level == null) return;
        if (mc.level.getBlockState(mc.player.blockPosition()).getBlock() instanceof PressurePlateBlock) return;

        ++this.ticksSinceInteracted;
        Vec3 pPos = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        if (pPos.x <= this.lastBlock.x + .1337 && pPos.x >= this.lastBlock.x - .1337
            && pPos.z <= this.lastBlock.z + .1337 && pPos.z >= this.lastBlock.z - .1337) return;

        this.lastBlock = pPos;
        Direction movementDirection = getMovementDirection(event);

        BlockPos frontPos;
        BlockPos behindPos;
        BlockPos pbPos = mc.player.blockPosition();
        switch (movementDirection) {
            case NORTH -> {
                frontPos = pbPos.north();
                behindPos = pbPos.south();
            }
            case SOUTH -> {
                frontPos = pbPos.south();
                behindPos = pbPos.north();
            }
            case EAST -> {
                frontPos = pbPos.east();
                behindPos = pbPos.west();
            }
            case WEST -> {
                frontPos = pbPos.west();
                behindPos = pbPos.east();
            }
            case UP -> {
                frontPos = pbPos.above();
                behindPos = pbPos.below();
            }
            default -> {
                frontPos = pbPos.below();
                behindPos = pbPos.above();
            }
        }

        String yString = String.valueOf(pPos.y);
        String sunk = yString.substring(yString.indexOf(".")+1, yString.indexOf(".")+2);
        if (mc.player.onGround() && Integer.parseInt(sunk) >= 5) {
            frontPos = frontPos.above();
            behindPos = behindPos.above();
        }
        BlockState frontState = mc.level.getBlockState(frontPos);
        BlockState behindState = mc.level.getBlockState(behindPos);
        Block doorInFront = frontState.getBlock();
        Block doorBehind = behindState.getBlock();

        if (useTrapdoors.get() && doorInFront instanceof TrapDoorBlock && autoOpen.get()) {
            try {
                if (!frontState.getValue(TrapDoorBlock.OPEN)) {
                    interactDoor(frontPos, movementDirection);
                    return;
                }
            } catch (IllegalArgumentException ignored) {} // skill issue insurance
        } else if (useTrapdoors.get() && mc.level.getBlockState(frontPos.below()).getBlock() instanceof TrapDoorBlock && autoOpen.get()) {
            try {
                if (!mc.level.getBlockState(frontPos.below()).getValue(TrapDoorBlock.OPEN)) {
                    interactDoor(frontPos.below(), Direction.DOWN);
                    return;
                }
            } catch (IllegalArgumentException ignored) {}
        }
        Block doorAboveFront = mc.level.getBlockState(frontPos.above()).getBlock();
        Block doorAboveBack = mc.level.getBlockState(behindPos.above()).getBlock();
        if (useFenceGates.get() && doorInFront instanceof FenceGateBlock || doorAboveFront instanceof FenceGateBlock && autoOpen.get()) {
            try {
                if (!frontState.getValue(FenceGateBlock.OPEN)) {
                    interactDoor(frontPos, movementDirection);
                    if (doorAboveFront instanceof FenceGateBlock && !mc.level.getBlockState(frontPos.above()).getValue(FenceGateBlock.OPEN)) {
                        interactDoor(frontPos.above(), movementDirection);
                    }
                    return;
                }
            }catch (IllegalArgumentException ignored) {}
        }
        if (doorInFront instanceof DoorBlock frontDoor && autoOpen.get()) {
            if (useIronDoors.get() && frontDoor == Blocks.IRON_DOOR) {
                if (this.ticksSinceInteracted >= interactDelay.get()) {
                    this.tryInteractIronDoor(frontPos, frontState, movementDirection, true);
                }
                return;
            } else if (frontDoor == Blocks.IRON_DOOR) return;
            if (!frontDoor.isOpen(frontState)) this.interactDoor(frontPos, movementDirection);
            switch (movementDirection) {
                case NORTH, SOUTH -> {
                    BlockState eastState = mc.level.getBlockState(frontPos.east());
                    BlockState westState = mc.level.getBlockState(frontPos.west());
                    if (eastState.getBlock() instanceof DoorBlock nextDoor) {
                        if (nextDoor == Blocks.IRON_DOOR) return;
                        if (!nextDoor.isOpen(eastState)) this.interactDoor(frontPos.east(), movementDirection);
                    } else if (westState.getBlock() instanceof DoorBlock nextDoor) {
                        if (nextDoor == Blocks.IRON_DOOR) return;
                        if (!nextDoor.isOpen(westState)) this.interactDoor(frontPos.west(), movementDirection);
                    }
                }
                case EAST, WEST -> {
                    BlockState northState = mc.level.getBlockState(frontPos.north());
                    BlockState southState = mc.level.getBlockState(frontPos.south());
                    if (northState.getBlock() instanceof DoorBlock nextDoor) {
                        if (nextDoor == Blocks.IRON_DOOR) return;
                        if (!nextDoor.isOpen(northState)) this.interactDoor(frontPos.north(), movementDirection);
                    } else if (southState.getBlock() instanceof DoorBlock nextDoor) {
                        if (nextDoor == Blocks.IRON_DOOR) return;
                        if (!nextDoor.isOpen(southState)) this.interactDoor(frontPos.south(), movementDirection);
                    }
                }
                default -> {}
            }
        }
        if (useTrapdoors.get() && doorBehind instanceof TrapDoorBlock) {
            try {
                if (behindState.getValue(TrapDoorBlock.OPEN)) {
                    this.interactDoor(behindPos, movementDirection);
                    return;
                }
            }catch (IllegalArgumentException ignored) {}
        } else if (useTrapdoors.get() && mc.level.getBlockState(behindPos.below()).getBlock() instanceof TrapDoorBlock) {
            try {
                if (mc.level.getBlockState(behindPos.below()).getValue(TrapDoorBlock.OPEN)) {
                    this.interactDoor(behindPos.below(), Direction.DOWN);
                    return;
                }
            } catch (IllegalArgumentException ignored) {}
        }
        if (useFenceGates.get() && doorBehind instanceof FenceGateBlock || doorAboveBack instanceof FenceGateBlock) {
            try {
                if (behindState.getValue(FenceGateBlock.OPEN)) {
                    interactDoor(behindPos, movementDirection);
                    if (doorAboveBack instanceof FenceGateBlock && mc.level.getBlockState(behindPos.above()).getValue(FenceGateBlock.OPEN)) {
                        interactDoor(behindPos.above(), movementDirection);
                    }
                    return;
                }
            } catch (IllegalArgumentException ignored) {}
        }
        if (doorBehind instanceof DoorBlock behindDoor) {
            if (useIronDoors.get() && behindDoor == Blocks.IRON_DOOR) {
                if (this.ticksSinceInteracted >= interactDelay.get()) {
                    this.tryInteractIronDoor(behindPos, behindState, movementDirection, false);
                }
                return;
            } else if (behindDoor == Blocks.IRON_DOOR) return;
            if (behindDoor.isOpen(behindState)) this.interactDoor(behindPos, movementDirection);
            switch (movementDirection) {
                case NORTH, SOUTH -> {
                    BlockState eastState = mc.level.getBlockState(behindPos.east());
                    BlockState westState = mc.level.getBlockState(behindPos.west());
                    if (eastState.getBlock() instanceof DoorBlock nextDoor) {
                        if (nextDoor == Blocks.IRON_DOOR) return;
                        if (nextDoor.isOpen(eastState)) this.interactDoor(behindPos.east(), movementDirection);
                    } else if (westState.getBlock() instanceof DoorBlock nextDoor) {
                        if (nextDoor == Blocks.IRON_DOOR) return;
                        if (nextDoor.isOpen(westState)) this.interactDoor(behindPos.west(), movementDirection);
                    }
                }
                case EAST, WEST -> {
                    BlockState northState = mc.level.getBlockState(behindPos.north());
                    BlockState southState = mc.level.getBlockState(behindPos.south());
                    if (northState.getBlock() instanceof DoorBlock nextDoor) {
                        if (nextDoor == Blocks.IRON_DOOR) return;
                        if (nextDoor.isOpen(northState)) this.interactDoor(behindPos.north(), movementDirection);
                    } else if (southState.getBlock() instanceof DoorBlock nextDoor) {
                        if (nextDoor == Blocks.IRON_DOOR) return;
                        if (nextDoor.isOpen(southState)) this.interactDoor(behindPos.south(), movementDirection);
                    }
                }
                default -> {}
            }
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (modeSetting.get() == DoorModes.Classic) return;

        ++this.tickCounter;
        if (this.tickCounter >= spamRate.get()) {
            this.tickCounter = 0;
            if (mc.player == null || mc.level == null) return;

            Vec3 pPos = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());
            LongArrayList doors = this.getSurroundingDoors();
            for (long door : doors) {
                BlockPos doorPos = BlockPos.of(door);
                if (mc.level.getBlockState(doorPos).getBlock() == Blocks.IRON_DOOR) continue;

                Direction side;
                if (pPos.x > doorPos.getX()) side = Direction.EAST;
                else if (pPos.x < doorPos.getX()) side = Direction.WEST;
                else if (pPos.z > doorPos.getZ()) side = Direction.SOUTH;
                else if (pPos.z < doorPos.getZ()) side = Direction.NORTH;
                else if (pPos.y > doorPos.getY()) side = Direction.UP;
                else side = Direction.DOWN;

                this.interactDoor(doorPos, side);
            }
        }
    }
}
