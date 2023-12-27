package zero.test.block;
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

import net.minecraft.src.*;
import btw.AddonHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
public class DeadCoralFan extends Block {
    public DeadCoralFan(int block_id) {
        super(block_id, Material.rock);
        this.setUnlocalizedName("dead_coral_fan");
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    public boolean isOpaqueCube() {
        return false;
    }
    public boolean renderAsNormalBlock() {
        return false;
    }
    public int updateShape(World world, int X, int Y, int Z, int direction, int meta) {
        //AddonHandler.logMessage("CORAL META "+meta);
        int attached_face = (((meta)&7));
        if (attached_face != 7) {
            X -= Facing.offsetsXForSide[attached_face];
            Y -= Facing.offsetsYForSide[attached_face];
            Z -= Facing.offsetsZForSide[attached_face];
            int attached_block_id = world.getBlockId(X, Y, Z);
            Block attached_block = Block.blocksList[attached_block_id];
            if (
                !((attached_block)==null) &&
                !attached_block.hasLargeCenterHardPointToFacing(world, X, Y, Z, ((attached_face)^1))
            ) {
                return -1;
            }
        }
        return meta;
    }
    @Override
    public int onBlockPlaced(World world, int X, int Y, int Z, int side, float hitX, float hitY, float hitZ, int meta) {
        X -= Facing.offsetsXForSide[side];
        Y -= Facing.offsetsYForSide[side];
        Z -= Facing.offsetsZForSide[side];
        Block neighbor_block = Block.blocksList[world.getBlockId(X, Y, Z)];
        if (
            !((neighbor_block)==null) &&
            neighbor_block.hasLargeCenterHardPointToFacing(world, X, Y, Z, ((side)^1))
        ) {
            return side;
        }
        return -1;
    }
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int X, int Y, int Z) {
        this.setBlockBoundsBasedOnState(world, X, Y, Z);
        return super.getSelectedBoundingBoxFromPool(world, X, Y, Z);
    }
    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int X, int Y, int Z) {
        this.setBlockBoundsBasedOnState(world, X, Y, Z);
        return super.getCollisionBoundingBoxFromPool(world, X, Y, Z);
    }
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess block_access, int X, int Y, int Z) {
        this.setBlockBoundsForBlockRender(block_access.getBlockMetadata(X, Y, Z));
    }
    public void setBlockBoundsForBlockRender(int meta) {
        switch ((((meta)&7))) {
            case 0:
                this.setBlockBounds(2.0f, 0.0f, 2.0f, 14.0f, 4.0f, 14.0f);
                return;
            case 1:
                this.setBlockBounds(2.0f, 12.0f, 2.0f, 14.0f, 16.0f, 14.0f);
                return;
            case 2:
                this.setBlockBounds(0.0f, 4.0f, 5.0f, 16.0f, 12.0f, 16.0f);
                return;
            case 3:
                this.setBlockBounds(0.0f, 4.0f, 0.0f, 16.0f, 12.0f, 11.0f);
                return;
            case 4:
                this.setBlockBounds(5.0f, 4.0f, 0.0f, 16.0f, 12.0f, 16.0f);
                return;
            default:
                this.setBlockBounds(0.0f, 4.0f, 0.0f, 11.0f, 12.0f, 16.0f);
                return;
        }
    }
}
