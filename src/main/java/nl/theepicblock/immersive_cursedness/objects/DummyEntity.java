package nl.theepicblock.immersive_cursedness.objects;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.NetherPortal;
import net.minecraft.world.dimension.DimensionType;

import java.util.Optional;

import static net.minecraft.block.NetherPortalBlock.AXIS;

@SuppressWarnings("EntityConstructor")
public class DummyEntity extends Entity {
    public DummyEntity(World world, BlockPos pos) {
        super(EntityType.BLAZE, world);
        this.setPos(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound tag) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound tag) {

    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
        return null;
    }

    @Override
    public void setBodyYaw(float yaw) {
        this.setYaw(yaw);
    }

    public TeleportTarget getTeleportTargetB(ServerWorld destination) {
        this.tryUsePortal((NetherPortalBlock) Blocks.NETHER_PORTAL, this.getBlockPos());
        return this.getTeleportTarget(destination);
    }

    protected TeleportTarget getTeleportTarget(ServerWorld destination) {
	    if (destination == null) {
            return null;
        } else {
            boolean bl = destination.getRegistryKey() == World.NETHER;
            WorldBorder worldBorder = destination.getWorldBorder();
            double d = DimensionType.getCoordinateScaleFactor(getWorld().getDimension(), destination.getDimension());
            BlockPos blockPos = worldBorder.clampFloored(getX() * d, getY(), getZ() * d);
            return this.getOrCreateExitPortalTarget(destination, this, getBlockPos(), blockPos, bl, worldBorder);
        }
    }

    private TeleportTarget getOrCreateExitPortalTarget(
            ServerWorld world, Entity entity, BlockPos pos, BlockPos scaledPos, boolean inNether, WorldBorder worldBorder
    ) {
        Optional<BlockPos> optional = world.getPortalForcer().getPortalPos(scaledPos, inNether, worldBorder);
        BlockLocating.Rectangle rectangle;
        TeleportTarget.PostDimensionTransition postDimensionTransition;
        if (optional.isPresent()) {
            BlockPos blockPos = optional.get();
            BlockState blockState = world.getBlockState(blockPos);
            rectangle = BlockLocating.getLargestRectangle(
                    blockPos, blockState.get(Properties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, posx -> world.getBlockState(posx) == blockState
            );
            postDimensionTransition = TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(entityx -> entityx.addPortalChunkTicketAt(blockPos));
        } else {
            Direction.Axis axis = entity.getWorld().getBlockState(pos).getOrEmpty(AXIS).orElse(Direction.Axis.X);
            Optional<BlockLocating.Rectangle> optional2 = world.getPortalForcer().createPortal(scaledPos, axis);
            if (optional2.isEmpty()) {
                return null;
            }

            rectangle = optional2.get();
            postDimensionTransition = TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET);
        }

        return getExitPortalTarget(entity, pos, rectangle, world, postDimensionTransition);
    }

    private static TeleportTarget getExitPortalTarget(
            Entity entity, BlockPos pos, BlockLocating.Rectangle exitPortalRectangle, ServerWorld world, TeleportTarget.PostDimensionTransition postDimensionTransition
    ) {
        BlockState blockState = entity.getWorld().getBlockState(pos);
        Direction.Axis axis;
        Vec3d vec3d;
        if (blockState.contains(Properties.HORIZONTAL_AXIS)) {
            axis = blockState.get(Properties.HORIZONTAL_AXIS);
            BlockLocating.Rectangle rectangle = BlockLocating.getLargestRectangle(
                    pos, axis, 21, Direction.Axis.Y, 21, posx -> entity.getWorld().getBlockState(posx) == blockState
            );
            vec3d = entity.positionInPortal(axis, rectangle);
        } else {
            axis = Direction.Axis.X;
            vec3d = new Vec3d(0.5, 0.0, 0.0);
        }

        return getExitPortalTarget(world, exitPortalRectangle, axis, vec3d, entity, postDimensionTransition);
    }

    private static TeleportTarget getExitPortalTarget(
            ServerWorld world,
            BlockLocating.Rectangle exitPortalRectangle,
            Direction.Axis axis,
            Vec3d positionInPortal,
            Entity entity,
            TeleportTarget.PostDimensionTransition postDimensionTransition
    ) {
        BlockPos blockPos = exitPortalRectangle.lowerLeft;
        BlockState blockState = world.getBlockState(blockPos);
        Direction.Axis axis2 = blockState.getOrEmpty(Properties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
        double d = exitPortalRectangle.width;
        double e = exitPortalRectangle.height;
        EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
        int i = axis == axis2 ? 0 : 90;
        double f = entityDimensions.width() / 2.0 + (d - entityDimensions.width()) * positionInPortal.getX();
        double g = (e - entityDimensions.height()) * positionInPortal.getY();
        double h = 0.5 + positionInPortal.getZ();
        boolean bl = axis2 == Direction.Axis.X;
        Vec3d vec3d = new Vec3d(blockPos.getX() + (bl ? f : h), blockPos.getY() + g, blockPos.getZ() + (bl ? h : f));
        Vec3d vec3d2 = NetherPortal.findOpenPosition(vec3d, world, entity, entityDimensions);
        return new TeleportTarget(world, vec3d2, Vec3d.ZERO, i, 0.0F, PositionFlag.combine(PositionFlag.DELTA, PositionFlag.ROT), postDimensionTransition);
    }
}
