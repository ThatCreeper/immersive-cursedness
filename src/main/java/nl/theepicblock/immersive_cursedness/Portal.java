package nl.theepicblock.immersive_cursedness;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import javax.sound.sampled.Port;

public class Portal implements AxisAlignedRectangle<BlockPos> {
    //Right is defined as the most positive point in whatever axis this is
    private BlockPos upperRight;
    private BlockPos lowerLeft;
    private Direction.Axis axis;

    public Portal(BlockPos upperRight, BlockPos lowerLeft, Direction.Axis axis) {
        this.upperRight = upperRight;
        this.lowerLeft = lowerLeft;
        this.axis = axis;
    }

    @Override
    public double getDistance(BlockPos pos) {
        return upperRight.getSquaredDistance(pos);
    }

    /**
     * Returns true if this rectangle fully encloses b
     */
    public boolean contains(Portal b) {
        if (this.getAxis() != b.getAxis()) return false;
        if (this.getTop() < b.getTop() ||
                this.getBottom() > b.getBottom()) return false;
        Direction.Axis axis = this.getAxis();
        return this.getRight() >= b.getRight() &&
                this.getLeft() <= b.getLeft();
    }

    @Override
    public BlockPos getUpperRight() {
        return upperRight;
    }

    @Override
    public BlockPos getLowerLeft() {
        return lowerLeft;
    }

    @Override
    public Direction.Axis getAxis() {
        return axis;
    }

    public int getLeft() {
        return Util.get(this.getLowerLeft(), this.getAxis());
    }

    public int getRight() {
        return Util.get(this.getUpperRight(), this.getAxis());
    }

    public int getTop() {
        return this.getUpperRight().getY();
    }

    public int getBottom() {
        return this.getLowerLeft().getY();
    }
}