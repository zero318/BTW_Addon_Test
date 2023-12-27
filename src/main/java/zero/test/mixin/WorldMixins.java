package zero.test.mixin;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.BlockRedstoneLogic;
import net.minecraft.src.BlockComparator;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.BTWAddon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import zero.test.IBlockMixins;
import zero.test.IWorldMixins;
// func_96440_m = updateNeighbourForOutputSignal
// func_94487_f = blockIdIsActiveOrInactive
// func_94485_e = getActiveBlockID
// func_94484_i = getInactiveBlockID
// func_96470_c(metadata) = getRepeaterPoweredState(metadata)
// func_94478_d = shouldTurnOn
// func_94488_g = getAlternateSignal
// func_94490_c = isSubtractMode
// func_94491_m = calculateOutputSignal
// func_94483_i_ = __notifyOpposite
// func_94481_j_ = getComparatorDelay
/// func_94482_f = getInputSignal
// func_96476_c = refreshOutputState
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
// Vanilla observers
// Slime blocks
// Push only and dead coral fans
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

@Mixin(World.class)
public class WorldMixins implements IWorldMixins {
    /*
        Changes:
        - Added getWeakChanges instead of hardcoding the comparator ID
    */
    @Overwrite
    public void func_96440_m(int X, int Y, int Z, int neighbor_id) {
        World world = (World)(Object)this;
        for (int i = 0; i < 4; ++i) {
            int nextX = X + Direction.offsetX[i];
            int nextZ = Z + Direction.offsetZ[i];
            int block_id = world.getBlockId(nextX, Y, nextZ);
            if (block_id != 0) {
                Block block_instance = Block.blocksList[block_id];
                if (((IBlockMixins)block_instance).getWeakChanges(world, nextX, Y, nextZ, neighbor_id)) {
                    block_instance.onNeighborBlockChange(world, nextX, Y, nextZ, neighbor_id);
                }
                // Crashes if this isn't an else? Why?
                // TODO: See if the null check fixed this
                else if (Block.isNormalCube(block_id)) {
                    nextX += Direction.offsetX[i];
                    nextZ += Direction.offsetZ[i];
                    block_id = world.getBlockId(nextX, Y, nextZ);
                    block_instance = Block.blocksList[block_id];
                    if (
                        !((block_instance)==null) &&
                        ((IBlockMixins)block_instance).getWeakChanges(world, nextX, Y, nextZ, neighbor_id)
                    ) {
                        block_instance.onNeighborBlockChange(world, nextX, Y, nextZ, neighbor_id);
                    }
                }
            }
        }
    }
    public void updateNeighbourShapes(int X, int Y, int Z, int flags) {
        World world = (World)(Object)this;
        IBlockMixins neighbor_block;
        boolean allow_drops = (flags & 0x20) == 0;
        flags &= ~0x20;
        int neighbor_meta;
        int new_meta;
        // Offset from neutral to west
        --X;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!((neighbor_block)==null)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, 5, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        // Offset from west to east
        X += 2;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!((neighbor_block)==null)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, 4, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        // Offset from east to north
        --X;
        --Z;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!((neighbor_block)==null)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, 3, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        // Offset from north to south
        Z += 2;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!((neighbor_block)==null)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, 2, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        // Offset from south to down
        --Z;
        --Y;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!((neighbor_block)==null)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, 1, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        // Offset from down to up
        Y += 2;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!((neighbor_block)==null)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, 0, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
    }
    public int updateFromNeighborShapes(int X, int Y, int Z, int block_id, int meta) {
        IBlockMixins block = (IBlockMixins)Block.blocksList[block_id];
        if (!((block)==null)) {
            World world = (World)(Object)this;
            meta = block.updateShape(world, X, Y, Z, 4, meta);
            meta = block.updateShape(world, X, Y, Z, 5, meta);
            meta = block.updateShape(world, X, Y, Z, 2, meta);
            meta = block.updateShape(world, X, Y, Z, 3, meta);
            meta = block.updateShape(world, X, Y, Z, 0, meta);
            meta = block.updateShape(world, X, Y, Z, 1, meta);
        }
        return meta;
    }
    @Overwrite
    public boolean setBlock(int X, int Y, int Z, int block_id, int meta, int flags) {
        if ((((((Integer.compareUnsigned(((Y))-(0),(255)-(0)))<=0))) && (((((Integer.compareUnsigned((((X)))-(-30000000),(29999999)-(-30000000)))<=0))) && ((((Integer.compareUnsigned((((Z)))-(-30000000),(29999999)-(-30000000)))<=0)))))) {
            World world = (World)(Object)this;
            Chunk chunk = world.getChunkFromChunkCoords(X >> 4, Z >> 4);
            int current_block_id = 0;
            if (
                //(flags & (UPDATE_NEIGHBORS | UPDATE_KNOWN_SHAPE)) != UPDATE_KNOWN_SHAPE
                (flags & 0x01) != 0
            ) {
                current_block_id = chunk.getBlockID(X & 0xF, Y, Z & 0xF);
            }
            boolean block_changed = chunk.setBlockIDWithMetadata(X & 0xF, Y, Z & 0xF, block_id, meta);
            if ((flags & 0x80) == 0) {
                world.theProfiler.startSection("checkLight");
                world.updateAllLightTypes(X, Y, Z);
                world.theProfiler.endSection();
            }
            if (block_changed) {
                if (
                    (flags & 0x02) != 0 &&
                    (
                        !world.isRemote ||
                        (flags & 0x04) == 0
                    )
                ) {
                    world.markBlockForUpdate(X, Y, Z);
                }
                //Block block;
                if (
                    !world.isRemote &&
                    (flags & 0x01) != 0
                ) {
                    world.notifyBlockChange(X, Y, Z, current_block_id);
                    Block block = Block.blocksList[block_id];
                    if (
                        !((block)==null) &&
                        block.hasComparatorInputOverride()
                    ) {
                        world.func_96440_m(X, Y, Z, block_id);
                    }
                }
                if (
                    (flags & 0x10) == 0
                ) {
                    //block = Block.blocksList[current_block_id];
                    //if (!BLOCK_IS_AIR(block)) {
                        //((IBlockMixins)block).updateIndirectNeighbourShapes(world, X, Y, Z);
                    //}
                    this.updateNeighbourShapes(X, Y, Z, flags & ~(0x01 | 0x20));
                    //block = Block.blocksList[block_id];
                    //if (!BLOCK_IS_AIR(block)) {
                        //((IBlockMixins)block).updateIndirectNeighbourShapes(world, X, Y, Z);
                    //}
                }
            }
            return block_changed;
        }
        return false;
    }
    @Overwrite
    public boolean setBlockMetadataWithNotify(int X, int Y, int Z, int meta, int flags) {
        if ((((((Integer.compareUnsigned(((Y))-(0),(255)-(0)))<=0))) && (((((Integer.compareUnsigned((((X)))-(-30000000),(29999999)-(-30000000)))<=0))) && ((((Integer.compareUnsigned((((Z)))-(-30000000),(29999999)-(-30000000)))<=0)))))) {
            World world = (World)(Object)this;
            Chunk chunk = world.getChunkFromChunkCoords(X >> 4, Z >> 4);
            boolean block_changed = chunk.setBlockMetadata(X & 0xF, Y, Z & 0xF, meta);
            // Should this be enabled?
            /*
            if ((flags & UPDATE_SUPPRESS_LIGHT) == 0) {
                world.theProfiler.startSection("checkLight");
                world.updateAllLightTypes(X, Y, Z);
                world.theProfiler.endSection();
            }
            */
            if (block_changed) {
                //int current_block_id = 0;
                //if (
                    //(flags & (UPDATE_NEIGHBORS | UPDATE_KNOWN_SHAPE)) != UPDATE_KNOWN_SHAPE
                //) {
                    //current_block_id = chunk.getBlockID(X & 0xF, Y, Z & 0xF);
                //}
                if (
                    (flags & 0x02) != 0 &&
                    (
                        !world.isRemote ||
                        (flags & 0x04) == 0
                    )
                ) {
                    world.markBlockForUpdate(X, Y, Z);
                }
                //Block block = Block.blocksList[current_block_id];
                if (
                    !world.isRemote &&
                    (flags & 0x01) != 0
                ) {
                    int current_block_id = chunk.getBlockID(X & 0xF, Y, Z & 0xF);
                    world.notifyBlockChange(X, Y, Z, current_block_id);
                    Block block = Block.blocksList[current_block_id];
                    if (
                        !((block)==null) &&
                        block.hasComparatorInputOverride()
                    ) {
                        world.func_96440_m(X, Y, Z, current_block_id);
                    }
                }
                if (
                    (flags & 0x10) == 0
                ) {
                    //if (!BLOCK_IS_AIR(block)) {
                        //((IBlockMixins)block).updateIndirectNeighbourShapes(world, X, Y, Z);
                    //}
                    this.updateNeighbourShapes(X, Y, Z, flags & ~(0x01 | 0x20));
                }
            }
            return block_changed;
        }
        return false;
    }
}
