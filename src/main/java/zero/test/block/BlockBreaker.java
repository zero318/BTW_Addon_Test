package zero.test.block;
import net.minecraft.src.*;
import btw.block.BTWBlocks;
import btw.block.blocks.BlockDispenserBlock;
import btw.AddonHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import zero.test.mixin.IBlockDispenserBlockAccessMixins;
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
// Vanilla observers
// Slime blocks
// Push only and dead coral fans
// Allow slime to keep loose blocks
// suspended in midair as if they
// had mortar applied
// Fix how most BTW blocks recieve power
// Allow block dispensers to respond to short pulses
// Block Breaker and Block Placer
public class BlockBreaker extends BlockDispenserBlock {
    public BlockBreaker(int block_id) {
        super(block_id);
        setTickRandomly(false);
        setUnlocalizedName("block_breaker");
    }
    @Override
    public void onBlockAdded(World world, int X, int Y, int Z) {
    }
    @Override
    public int idDropped(int i, Random random, int fortune_modifier) {
        return this.blockID;
    }
    @Override
    public void onNeighborBlockChange(World world, int X, int Y, int Z, int neighbor_id) {
        boolean receiving_power = world.isBlockIndirectlyGettingPowered(X, Y, Z) || world.isBlockIndirectlyGettingPowered(X, Y + 1, Z);
        int meta = world.getBlockMetadata(X, Y, Z);
        boolean is_powered = ((((meta)>7)));
        if (receiving_power != is_powered) {
            if (!is_powered) {
                world.scheduleBlockUpdate(X, Y, Z, this.blockID, this.tickRate(world));
            }
            world.setBlockMetadataWithNotify(X, Y, Z, meta ^ 8, 0x04);
        }
    }
    // This matches what the base block does
    @Override
    public void randomUpdateTick(World world, int X, int Y, int Z, Random random) {
        updateTick(world, X, Y, Z, random);
    }
    @Override
    public void updateTick(World world, int X, int Y, int Z, Random random) {
        ((IBlockDispenserBlockAccessMixins)this).callConsumeFacingBlock(world, X, Y, Z);
    }
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        Icon[] icon_array = ((IBlockDispenserBlockAccessMixins)this).getIconBySideArray();
        Icon side_icon = register.registerIcon("pickaxe_block_side");
        icon_array[2] = side_icon;
        icon_array[3] = side_icon;
        icon_array[4] = side_icon;
        icon_array[5] = side_icon;
    }
}
