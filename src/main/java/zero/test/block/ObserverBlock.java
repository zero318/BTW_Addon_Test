
package zero.test.block;
import btw.client.fx.BTWEffectManager;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import btw.block.blocks.BuddyBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import btw.AddonHandler;
import java.util.Random;
/// Utility Macro Defs
/// Mutable Pos Move X
/// Mutable Pos Move Y
/// Mutable Pos Move Z
/// Mutable Pos Move
/// Mutable Pos Create
/// C-esque stuff
//#define printf(...) System.out.printf(__VA_ARGS__)
/// x86-esque stuff
/// Some operations are available
/// as and @IntrinsicCandidate, in
/// which case that form is preferred
//#define MOVSX(A) ((int)(A))
//#define MOVSXD(A) ((long)(A))
// Efficiently tests if [value] is within the range [min, max)
// Efficiently tests if [value] is within the range [min, max]
// Valid for both signed and unsigned integers
/// Random direction crap
/*
case NEIGHBOR_WEST:
case NEIGHBOR_EAST:
case NEIGHBOR_DOWN:
case NEIGHBOR_DOWN_WEST:
case NEIGHBOR_DOWN_EAST:
case NEIGHBOR_UP:
case NEIGHBOR_UP_WEST:
case NEIGHBOR_UP_EAST:
case NEIGHBOR_NORTH:
case NEIGHBOR_DOWN_NORTH:
case NEIGHBOR_UP_NORTH:
case NEIGHBOR_SOUTH:
case NEIGHBOR_DOWN_SOUTH:
case NEIGHBOR_UP_SOUTH:
*/
/// Expression Crap
/// Metadata stuff
// Meta write mask OFFSET, BITS
// Meta mask values OFFSET/BITS
// Meta mask values before shifting OFFSET, BITS
// Meta high value data OFFSET, BITS
// 0 = Needs != 0 if bool
// 1 = Is last field
// true = Is last field but uses != 0 anyway because it's 4 bits
// Meta const lookup OFFSET, VALUE
// Meta full write BITS, VALUE
//#define READ_META_FIELD_RAW(m,f)(    /*TEXT*/(m)    MACRO_IF_NOT(MACRO_IS_4(META_BITS(f)),        MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),            /*TEXT*/>>>META_OFFSET(f)        )        MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_LAST(f)),            /*TEXT*/&META_MASK(f)        )    ))
//#define READ_META_FIELD_BOOL(m,f)(    /*TEXT*/((m)    MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),        MACRO_TERN(MACRO_IS_TRUTHY(META_IS_LAST(f)),            /*TEXT*/>META_BOOL_CMP(f)        /*ELSE*/,            /*TEXT*/&META_MASK_UNSHIFTED(f)        )    )    /*TEXT*/)    MACRO_IF_NOT(MACRO_IS_TRUTHY(META_BOOL_SKIPS_NEQ(f)),        /*TEXT*/!=0    ))
//#define READ_META_FIELD(m,f)(    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),        READ_META_FIELD_BOOL(m,f)    /*ELSE*/,        READ_META_FIELD_RAW(m,f)    ))
//#define MERGE_META_FIELD_RAW(m,f,v)(    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),        /*TEXT*/(v)        MACRO_IF_NOT(META_VALID_CONST(f,v),            /*TEXT*/&META_WRITE_MASK(f)        )    /*ELSE*/,        /*TEXT*/(m)        MACRO_TERN(META_VALID_CONST(f,v),            MACRO_IF_NOT(META_IS_FULL_WRITE(f,v),                /*TEXT*/&META_WRITE_MASK(f)            )            MACRO_IF_NOT(MACRO_IS_FALSY(v),                /*TEXT*/|META_CONST_LOOKUP(f,v)            )        /*ELSE*/,            /*TEXT*/&META_WRITE_MASK(f)|(v)            MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),                /*TEXT*/<<META_OFFSET(f)            )        )    ))
//#define MERGE_META_FIELD_BOOL(m,f,v)(    MACRO_TERN(MACRO_IS_BOOL_ANY(v),        MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),            /*TEXT*/MACRO_CAST_FROM_BOOL(v)        /*ELSE*/,            /*TEXT*/(m)            MACRO_TERN(MACRO_IS_TRUTHY(v),                /*TEXT*/|META_CONST_LOOKUP(f,1)            /*ELSE*/,                /*TEXT*/&META_WRITE_MASK(f)            )        )    /*ELSE*/,        MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),            /*TEXT*/(v)&1        /*ELSE*/,            /*TEXT*/(m)&META_WRITE_MASK(f)|((v)&1)            MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),                /*TEXT*/<<META_OFFSET(f)            )        )    ))
//#define MERGE_META_FIELD(m,f,v)(    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),        MERGE_META_FIELD_BOOL(m,f,v)    /*ELSE*/,        MERGE_META_FIELD_RAW(m,f,v)    ))
/// Fake Direction Metadata
/// Misc. Flags
// Glazed terracotta
// Z doesn't need to be masked because it's in the top bits anyway
public class ObserverBlock extends BuddyBlock {
    public ObserverBlock(int block_id) {
        super(block_id);
        this.setUnlocalizedName("observer");
        this.setTickRandomly(false);
    }
    @Override
    public int tickRate(World world) {
        return 2;
    }
    @Override
    public boolean triggersBuddy() {
        return true;
    }
    //public boolean caresAboutUpdateDirection() {
        //return true;
    //}
    @Override
    public void onBlockAdded(World world, int X, int Y, int Z) {
        // HACK: 
        // Java doesn't have super.super, so there's no way of
        // calling Block.onBlockAdded without going through
        // BuddyBlock.onBlockAdded, which schedules an update.
        // So instead we're just assuming that Block.onBlockAdded
        // is still empty and doing nothing.
    }
    @Override
    public void onNeighborBlockChange(World world, int X, int Y, int Z, int neighbor_id) {
    }
    public int updateShape(World world, int X, int Y, int Z, int direction, int meta) {
        int update_direction = (((meta)>>>1));
        if (update_direction == ((direction)^1)) {
            if (!world.isUpdateScheduledForBlock(X, Y, Z, this.blockID)) {
                world.scheduleBlockUpdate(X, Y, Z, this.blockID, 2);
            }
        } //else {
            //AddonHandler.logMessage("Observer fail "+OPPOSITE_DIRECTION(direction)+" != "+update_direction);
        //}
        return meta;
    }
    @Override
    public void updateTick(World world, int X, int Y, int Z, Random random) {
        int meta = world.getBlockMetadata(X, Y, Z);
        if (((((meta)&1)!=0))) {
            world.setBlockMetadataWithClient(X, Y, Z, (((meta)&14)));
        } else {
            world.setBlockMetadataWithClient(X, Y, Z, (((meta)|1)));
            world.scheduleBlockUpdate(X, Y, Z, this.blockID, 2);
        }
        //notifyNeigborsToFacingOfPowerChange(world, X, Y, Z, READ_META_FIELD(meta, DIRECTION));
        int direction = (((meta)>>>1));
        X += Facing.offsetsXForSide[direction];
        Y += Facing.offsetsYForSide[direction];
        Z += Facing.offsetsZForSide[direction];
        int neighbor_id = world.getBlockId(X, Y, Z);
        Block neighbor_block = Block.blocksList[neighbor_id];
        if (!((neighbor_block)==null)) {
            neighbor_block.onNeighborBlockChange(world, X, Y, Z, this.blockID);
        }
        world.notifyBlocksOfNeighborChange(X, Y, Z, this.blockID, ((direction)^1));
    }
    // This just gets rid of the clicking sound
    @Override
    public void setBlockRedstoneOn(World world, int X, int Y, int Z, boolean is_activated) {
        if (is_activated != isRedstoneOn(world, X, Y, Z)) {
            int meta = world.getBlockMetadata(X, Y, Z);
            if (is_activated) {
                meta |= 1;
            }
            else {
                meta &= ~1;
            }
            world.setBlockMetadataWithClient(X, Y, Z, meta);
            // only notify on the output side to prevent weird shit like doors auto-closing when the block
            // goes off
            int direction = this.getFacing(world, X, Y, Z);
            notifyNeigborsToFacingOfPowerChange(world, X, Y, Z, direction);
            // the following forces a re-render (for texture change)
            world.markBlockRangeForRenderUpdate(X, Y, Z, X, Y, Z);
        }
    }
    /*@Override
    public int onPreBlockPlacedByPiston(World world, int X, int Y, int Z, int meta, int direction) {
        return meta;
    }*/
    // Maybe this will prevent it getting stuck on when moved?
    @Override
    public int adjustMetadataForPistonMove(int meta) {
        return (((meta)&14));
    }
    @Environment(EnvType.CLIENT)
    private Icon texture_back_off;
    @Environment(EnvType.CLIENT)
    private Icon texture_back_on;
    @Environment(EnvType.CLIENT)
    private Icon texture_front;
    @Environment(EnvType.CLIENT)
    private Icon texture_side;
    @Environment(EnvType.CLIENT)
    private Icon texture_top;
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        this.texture_back_off = register.registerIcon("observer_back");
        this.texture_back_on = register.registerIcon("observer_back_on");
        this.texture_front = register.registerIcon("observer_front");
        this.texture_side = register.registerIcon("observer_side");
        this.texture_top = register.registerIcon("observer_top");
    }
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
        // Why are these different from the regular block texture directions?
        switch (side) {
            case 0:
            case 1:
                return this.texture_top;
            case 2:
                return this.texture_back_off;
            case 3:
                return this.texture_front;
            default:
                return this.texture_side;
        }
    }
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess block_access, int X, int Y, int Z, int side) {
        int meta = block_access.getBlockMetadata(X, Y, Z);
        int facing = (((meta)>>>1));
        if (facing == side) {
            return ((((meta)&1)!=0))
                    ? this.texture_back_on
                    : this.texture_back_off;
        }
        if (facing == ((side)^1)) {
            return this.texture_front;
        }
        if (((facing)&~1) != 0x0) {
            return ((side)&~1) == 0x0 ? this.texture_top : this.texture_side;
        }
        // When facing up, the non-arrow side should be on E/W
        return ((side)&~1) == 0x2 ? this.texture_top : this.texture_side;
    }
    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks render, int X, int Y, int Z) {
        // IDK why these rotation values work
        switch ((((render.blockAccess.getBlockMetadata(X, Y, Z))>>>1))) {
            case 0:
                render.setUVRotateEast(3);
                render.setUVRotateWest(3);
                render.setUVRotateNorth(1);
                render.setUVRotateSouth(2);
                render.setUVRotateTop(3);
                render.setUVRotateBottom(3);
                break;
            case 1:
                render.setUVRotateNorth(2);
                render.setUVRotateSouth(1);
                break;
            case 2:
                break;
            case 3:
                render.setUVRotateTop(3);
                render.setUVRotateBottom(3);
                break;
            case 4:
                render.setUVRotateTop(2);
                render.setUVRotateBottom(1);
                break;
            default:
                render.setUVRotateTop(1);
                render.setUVRotateBottom(2);
                break;
        }
        render.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        boolean ret = render.renderStandardBlock(this, X, Y, Z);
        render.clearUVRotation();
        return ret;
    }
}
