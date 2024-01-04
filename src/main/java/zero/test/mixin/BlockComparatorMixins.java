package zero.test.mixin;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import zero.test.mixin.IBlockComparatorAccessMixins;
import java.util.Random;
@Mixin(BlockComparator.class)
public abstract class BlockComparatorMixins extends BlockRedstoneLogic {
    BlockComparatorMixins(int par1, boolean par2) {
        super(par1, par2);
    }
    //@Override
    public boolean triggersBuddy() {
        return false;
    }
    // Fixes: MC-12211, MC-63669
    // shouldTurnOn
    @Overwrite
    public boolean func_94478_d(World world, int X, int Y, int Z, int meta) {
        int input_power = this.getInputStrength(world, X, Y, Z, meta);
        if (input_power != 0) {
            int side_power = this.func_94482_f(world, X, Y, Z, meta);
            if (input_power >= side_power) {
                return input_power > side_power || !((((meta)&4)!=0));
            }
        }
        return false;
    }
    // Fixes: MC-195351
    // calculateOutputSignal
    @Overwrite
    public int func_94491_m(World world, int X, int Y, int Z, int meta) {
        int input_power = this.getInputStrength(world, X, Y, Z, meta);
        if (input_power != 0) {
            int side_power = this.func_94482_f(world, X, Y, Z, meta);
            if (side_power <= input_power) {
                return !((((meta)&4)!=0)) ? input_power : input_power - side_power;
            }
        }
        return 0;
    }
    // Fixes: MC-8911, MC-10653
    @Overwrite
    public boolean onBlockActivated(World world, int X, int Y, int Z, EntityPlayer entity_player, int par6, float par7, float par8, float par9) {
        int meta = world.getBlockMetadata(X, Y, Z);
        boolean mode = !((((meta)&4)!=0)); //!this.isSubtractMode(var10);
        world.playSoundEffect((double)X + 0.5D, (double)Y + 0.5D, (double)Z + 0.5D, "random.click", 0.3F, mode ? 0.55F : 0.5F);
        world.setBlockMetadataWithNotify(X, Y, Z, (meta & 11) | (mode ? 4 : 0), 0x02);
        if (
            !world.isRemote && // MC-10653
            !world.isUpdateScheduledForBlock(X, Y, Z, this.blockID) // MC-8911
        ) {
            ((IBlockComparatorAccessMixins)this).callFunc_96476_c(world, X, Y, Z, world.rand);
            //this.func_96476_c(world, X, Y, Z, world.rand);
        }
        return true;
    }
//#if 0
    // Fixes: More of MC-195351?
    // refreshOutputState
    @Overwrite
    public void func_96476_c(World world, int X, int Y, int Z, Random random) {
        int meta = world.getBlockMetadata(X, Y, Z);
        int new_power = this.func_94491_m(world, X, Y, Z, meta);
        TileEntityComparator tile_entity = ((BlockComparator)(Object)this).getTileEntityComparator(world, X, Y, Z);
        int prev_power = tile_entity.func_96100_a();
        if (new_power != prev_power) {
            tile_entity.func_96099_a(new_power);
            //boolean should_turn_on = this.func_94478_d(world, X, Y, Z, meta);
            //boolean is_currently_on = READ_META_FIELD(meta, POWERED);
            //if (is_currently_on && !should_turn_on) {
                //world.setBlockMetadataWithNotify(X, Y, Z, MERGE_META_FIELD(meta, POWERED, false), UPDATE_CLIENTS);
            //}
            //else if (!is_currently_on && should_turn_on) {
                //world.setBlockMetadataWithNotify(X, Y, Z, MERGE_META_FIELD(meta, POWERED, true), UPDATE_CLIENTS);
            //}
            if ((prev_power & new_power) == 0) {
                world.setBlockMetadataWithNotify(X, Y, Z, meta ^ 8, 0x02);
            }
            this.func_94483_i_(world, X, Y, Z);
        }
    }
//#endif
    @Override
    public boolean canRotateOnTurntable(IBlockAccess block_access, int X, int Y, int Z) {
        return true;
    }
    @Override
    public boolean rotateAroundJAxis(World world, int X, int Y, int Z, boolean reverse) {
        int prev_meta = world.getBlockMetadata(X, Y, Z);
        int new_meta = this.rotateMetadataAroundJAxis(prev_meta, reverse);
        if (prev_meta != new_meta) {
            world.setBlockMetadataWithNotify(X, Y, Z, new_meta, 0x01 | 0x02);
            this.onNeighborBlockChange(world, X, Y, Z, 0);
            switch ((((prev_meta)&3))) {
                case 0:
                    world.notifyBlockOfNeighborChange(X, Y, Z - 1, this.blockID);
                    world.notifyBlocksOfNeighborChange(X, Y, Z - 1, this.blockID, 3);
                    break;
                case 1:
                    world.notifyBlockOfNeighborChange(X + 1, Y, Z, this.blockID);
                    world.notifyBlocksOfNeighborChange(X + 1, Y, Z, this.blockID, 4);
                    break;
                case 2:
                    world.notifyBlockOfNeighborChange(X, Y, Z + 1, this.blockID);
                    world.notifyBlocksOfNeighborChange(X, Y, Z + 1, this.blockID, 2);
                    break;
                default:
                    world.notifyBlockOfNeighborChange(X - 1, Y, Z, this.blockID);
                    world.notifyBlocksOfNeighborChange(X - 1, Y, Z, this.blockID, 5);
                    break;
            }
            this.func_94483_i_(world, X, Y, Z);
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
    public int onBlockPlaced(World world, int X, int Y, int Z, int direction, float fClickX, float fClickY, float fClickZ, int meta) {
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
    @Overwrite
    public void onBlockAdded(World world, int X, int Y, int Z) {
        super.onBlockAdded(world, X, Y, Z);
        if (!world.isRemote) {
            world.setBlockTileEntity(X, Y, Z, ((BlockComparator)(Object)this).createNewTileEntity(world));
        }
    }
    @Overwrite
    public void breakBlock(World world, int X, int Y, int Z, int par5, int par6) {
        super.breakBlock(world, X, Y, Z, par5, par6);
        if (!world.isRemote) {
            world.removeBlockTileEntity(X, Y, Z);
        }
        this.func_94483_i_(world, X, Y, Z);
    }
    //@Override
    public boolean getWeakChanges(World world, int X, int Y, int Z, int meta) {
        return true;
    }
    // Deal with the conductivity change...
}
