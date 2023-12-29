package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.*;
import btw.client.fx.BTWEffectManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Random;
import zero.test.IBlockMixins;
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

@Mixin(LavaReceiverBlock.class)
public abstract class LavaReceiverBlockMixins extends MortarReceiverBlock {
    LavaReceiverBlockMixins(int id, Material material) {
        super(id, material);
    }
    @Shadow
    protected abstract boolean getHasLavaInCracks(IBlockAccess block_access, int X, int Y, int Z);
    @Shadow
    protected abstract void setHasLavaInCracks(World block_access, int X, int Y, int Z, boolean has_lava);
    @Shadow
    protected abstract boolean hasLavaAbove(IBlockAccess block_access, int X, int Y, int Z);
    @Shadow
    protected abstract boolean hasWaterAbove(IBlockAccess block_access, int X, int Y, int Z);
    @Shadow
    public abstract int getStrata(IBlockAccess block_access, int X, int Y, int Z);
    @Override
    public void updateTick(World world, int X, int Y, int Z, Random random) {
        has_adjacent_slime: do {
            int facing = 0;
            do {
                int nextX = X + Facing.offsetsXForSide[facing];
                int nextY = Y + Facing.offsetsYForSide[facing];
                int nextZ = Z + Facing.offsetsZForSide[facing];
                Block neighbor_block = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
                if (
                    !((neighbor_block)==null) &&
                    ((IBlockMixins)neighbor_block).permanentlySupportsMortarBlocks(world, nextX, nextY, nextZ, facing)
                ) {
                    break has_adjacent_slime;
                }
            } while (++facing < 6);
            if (checkForFall(world, X, Y, Z)) {
                return;
            }
        } while(false);
        if (getHasLavaInCracks(world, X, Y, Z)) {
            if (hasWaterAbove(world, X, Y, Z)) {
                world.playAuxSFX(BTWEffectManager.FIRE_FIZZ_EFFECT_ID, X, Y, Z, 0);
                world.setBlockAndMetadataWithNotify(X, Y, Z, Block.stone.blockID, getStrata(world, X, Y, Z));
                return;
            }
        }
        else if (hasLavaAbove(world, X, Y, Z)) {
            setHasLavaInCracks(world, X, Y, Z, true);
        }
    }
}
