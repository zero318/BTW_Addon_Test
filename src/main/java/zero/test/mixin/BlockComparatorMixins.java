package zero.test.mixin;
import net.minecraft.src.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Implements;
import zero.test.mixin.IBlockComparatorAccessMixins;
import zero.test.mixin.IBlockRedstoneLogicAccessMixins;
import zero.test.IBlockRedstoneLogicMixins;
import java.util.Random;
// Block piston reactions
@Mixin(BlockComparator.class)
public abstract class BlockComparatorMixins extends BlockRedstoneLogic {
    BlockComparatorMixins(int par1, boolean par2) {
        super(par1, par2);
    }
    @Override
    public boolean triggersBuddy() {
        return false;
    }
    // Fixes: MC-12211, MC-63669
    // shouldTurnOn
    @Overwrite
    public boolean func_94478_d(World world, int x, int y, int z, int meta) {
        int inputPower = this.getInputStrength(world, x, y, z, meta);
        if (inputPower != 0) {
            int sidePower = this.func_94482_f(world, x, y, z, meta);
            if (inputPower >= sidePower) {
                return inputPower > sidePower || !((((meta)&4)!=0));
            }
        }
        return false;
    }
    // Fixes: MC-195351
    // calculateOutputSignal
    @Overwrite
    public int func_94491_m(World world, int x, int y, int z, int meta) {
        int inputPower = this.getInputStrength(world, x, y, z, meta);
        if (inputPower != 0) {
            int sidePower = this.func_94482_f(world, x, y, z, meta);
            if (sidePower <= inputPower) {
                return !((((meta)&4)!=0)) ? inputPower : inputPower - sidePower;
            }
        }
        return 0;
    }
    // Fixes: MC-8911, MC-10653
    @Overwrite
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
        int meta = world.getBlockMetadata(x, y, z);
        boolean mode = !((((meta)&4)!=0)); //!this.isSubtractMode(var10);
        world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.click", 0.3F, mode ? 0.55F : 0.5F);
        world.setBlockMetadataWithNotify(x, y, z, (((meta)&11|((mode)?4:0))), 0x02);
        if (
            !world.isRemote && // MC-10653
            !world.isUpdateScheduledForBlock(x, y, z, this.blockID) // MC-8911
        ) {
            ((IBlockComparatorAccessMixins)this).callFunc_96476_c(world, x, y, z, world.rand);
            //this.func_96476_c(world, x, y, z, world.rand);
        }
        return true;
    }
//#if 0
    // Fixes: More of MC-195351?
    // refreshOutputState
    @Overwrite
    public void func_96476_c(World world, int x, int y, int z, Random random) {
        int meta = world.getBlockMetadata(x, y, z);
        int newPower = this.func_94491_m(world, x, y, z, meta);
        TileEntityComparator tileEntity = ((BlockComparator)(Object)this).getTileEntityComparator(world, x, y, z);
        int prevPower = tileEntity.func_96100_a();
        if (newPower != prevPower) {
            tileEntity.func_96099_a(newPower);
            //boolean should_turn_on = this.func_94478_d(world, x, y, z, meta);
            //boolean is_currently_on = READ_META_FIELD(meta, POWERED);
            //if (is_currently_on && !should_turn_on) {
                //world.setBlockMetadataWithNotify(x, y, z, MERGE_META_FIELD(meta, POWERED, false), UPDATE_CLIENTS);
            //}
            //else if (!is_currently_on && should_turn_on) {
                //world.setBlockMetadataWithNotify(x, y, z, MERGE_META_FIELD(meta, POWERED, true), UPDATE_CLIENTS);
            //}
            if (prevPower == 0 || newPower == 0) {
                world.setBlockMetadataWithNotify(x, y, z, ((meta)^8), 0x02);
            }
            this.func_94483_i_(world, x, y, z);
        }
    }
//#endif
    @Override
    public boolean canRotateOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
    @Override
    public boolean rotateAroundJAxis(World world, int x, int y, int z, boolean reverse) {
        int prevMeta = world.getBlockMetadata(x, y, z);
        int newMeta = this.rotateMetadataAroundJAxis(prevMeta, reverse);
        if (prevMeta != newMeta) {
            world.setBlockMetadataWithNotify(x, y, z, newMeta, 0x01 | 0x02);
            this.onNeighborBlockChange(world, x, y, z, 0);
            switch ((((prevMeta)&3))) {
                case 0:
                    world.notifyBlockOfNeighborChange(x, y, z - 1, this.blockID);
                    world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID, 3);
                    break;
                case 1:
                    world.notifyBlockOfNeighborChange(x + 1, y, z, this.blockID);
                    world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID, 4);
                    break;
                case 2:
                    world.notifyBlockOfNeighborChange(x, y, z + 1, this.blockID);
                    world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID, 2);
                    break;
                default:
                    world.notifyBlockOfNeighborChange(x - 1, y, z, this.blockID);
                    world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID, 5);
                    break;
            }
            this.func_94483_i_(world, x, y, z);
            return true;
        }
        return false;
    }
    @Override
    public int rotateMetadataAroundJAxis(int meta, boolean reverse) {
        return (((meta)&12|(meta + (reverse ? 1 : -1) & 3)));
    }
    // handles BD placement, player handled in parent onBlockPlaceBy()
    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int direction, float clickX, float clickY, float clickZ, int meta) {
        switch (meta) {
            case 4:
                return 1;
            case 5:
                return 3;
            case 2:
                return 2;
            default:
                return 0;
        }
    }
    //@Override
    public boolean getWeakChanges(World world, int x, int y, int z, int meta) {
        return true;
    }
    // Deal with the conductivity change...
    //@Shadow
    //public abstract boolean getRenderingBaseTextures();
    // Hacky fix for rendering a bottom texture
    @Environment(EnvType.CLIENT)
    @Overwrite
    public Icon getIcon(int side, int meta) {
        BlockComparator self = (BlockComparator)(Object)this;
        boolean isPowered = ((((meta)>7))) || ((IBlockRedstoneLogicAccessMixins)self).getIsRepeaterPowered();
        if (side == 0 && !((IBlockRedstoneLogicMixins)self).getRenderingBaseTextures()) {
            return (isPowered ? Block.torchRedstoneActive : Block.torchRedstoneIdle).getBlockTextureFromSide(side);
        }
        return side == 1 ? (isPowered ? Block.redstoneComparatorActive : self).blockIcon : Block.stoneDoubleSlab.getBlockTextureFromSide(1);
    }
}
