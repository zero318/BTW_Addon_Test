package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Random;
// Block piston reactions
@Mixin(RedstoneClutchBlock.class)
public class RedstoneClutchBlockMixins extends GearBoxBlock {
    public RedstoneClutchBlockMixins(int blockId) {
        super(blockId);
    }
    // Prevent clutches getting quasi powered
    @Overwrite
    public void updateTick(World world, int x, int y, int z, Random random) {
        this.updateMechPoweredState(world, x, y, z,
            this.isInputtingMechanicalPower(world, x, y, z) &&
            !world.isBlockGettingPowered(x, y, z) // a Redstone powered gearbox outputs no mechanical power
        );
    }
    @Overwrite
    public boolean isCurrentStateValid(World world, int x, int y, int z) {
        return this.isGearBoxOn(world, x, y, z) == (
            this.isInputtingMechanicalPower(world, x, y, z) &&
            !world.isBlockGettingPowered(x, y, z) // a Redstone powered gearbox outputs no mechanical power
        );
 }
}