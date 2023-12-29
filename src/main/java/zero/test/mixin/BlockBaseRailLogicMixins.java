package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import zero.test.IBlockBaseRailLogicMixins;
import zero.test.mixin.IBlockBaseRailLogicAccessMixins;
import java.util.List;
// Vanilla observers
// Slime blocks
// Push only and dead coral fans
// Allow slime to keep loose blocks
// suspended in midair as if they
// had mortar applied
// Fix how most BTW blocks recieve power
// Allow block dispensers to respond to short pulses
// Block Breaker and Block Placer
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
@Mixin(BlockBaseRailLogic.class)
public class BlockBaseRailLogicMixins {
// func_94511_a
    @Overwrite
    public void func_94511_a(boolean is_powered, boolean par2) {
        IBlockBaseRailLogicAccessMixins self = (IBlockBaseRailLogicAccessMixins)(Object)this;
        World world = self.getLogicWorld();
        int selfX = self.getRailX();
        int selfY = self.getRailY();
        int selfZ = self.getRailZ();
        boolean is_straight_rail = self.getIsStraightRail();
        int meta = world.getBlockMetadata(selfX, selfY, selfZ);
        int previous_shape = meta;
        if (is_straight_rail) {
            previous_shape &= 7;
        }
        boolean has_north_connect = self.hasNeighborRail(selfX, selfY, selfZ - 1);
        boolean has_south_connect = self.hasNeighborRail(selfX, selfY, selfZ + 1);
        boolean has_west_connect = self.hasNeighborRail(selfX - 1, selfY, selfZ);
        boolean has_east_connect = self.hasNeighborRail(selfX + 1, selfY, selfZ);
        int rail_shape = -1;
        if (
            (has_north_connect || has_south_connect) &&
            !(has_west_connect || has_east_connect)
        ) {
            rail_shape = 0;
        }
        if (
            (has_west_connect || has_east_connect) &&
            !(has_north_connect || has_south_connect)
        ) {
            rail_shape = 1;
        }
        if (!is_straight_rail) {
            if (
                has_south_connect &&
                has_east_connect &&
                !(has_north_connect || has_west_connect)
            ) {
                rail_shape = 6;
            }
            if (
                has_south_connect &&
                has_west_connect &&
                !(has_north_connect || has_east_connect)
            ) {
                rail_shape = 7;
            }
            if (
                has_north_connect &&
                has_west_connect &&
                !(has_south_connect || has_east_connect)
            ) {
                rail_shape = 8;
            }
            if (
                has_north_connect &&
                has_east_connect &&
                !(has_south_connect || has_west_connect)
            ) {
                rail_shape = 9;
            }
        }
        if (rail_shape == -1) {
            if (
                (has_north_connect || has_south_connect) &&
                (has_west_connect || has_east_connect)
            ) {
                rail_shape = previous_shape;
            }
            else if (has_north_connect || has_south_connect) {
                rail_shape = 0;
            }
            else if (has_west_connect || has_east_connect) {
                rail_shape = 1;
            }
            if (!is_straight_rail) {
                if (is_powered) {
                    if (has_south_connect && has_east_connect) {
                        rail_shape = 6;
                    }
                    if (has_west_connect && has_south_connect) {
                        rail_shape = 7;
                    }
                    if (has_east_connect && has_north_connect) {
                        rail_shape = 9;
                    }
                    if (has_north_connect && has_west_connect) {
                        rail_shape = 8;
                    }
                }
                else {
                    if (has_north_connect && has_west_connect) {
                        rail_shape = 8;
                    }
                    if (has_east_connect && has_north_connect) {
                        rail_shape = 9;
                    }
                    if (has_west_connect && has_south_connect) {
                        rail_shape = 7;
                    }
                    if (has_south_connect && has_east_connect) {
                        rail_shape = 6;
                    }
                }
            }
        }
        if (rail_shape == 0) {
            if (BlockRailBase.isRailBlockAt(world, selfX, selfY + 1, selfZ - 1)) {
                rail_shape = 4;
            }
            if (BlockRailBase.isRailBlockAt(world, selfX, selfY + 1, selfZ + 1)) {
                rail_shape = 5;
            }
        }
        if (rail_shape == 1) {
            if (BlockRailBase.isRailBlockAt(world, selfX + 1, selfY + 1, selfZ)) {
                rail_shape = 2;
            }
            if (BlockRailBase.isRailBlockAt(world, selfX - 1, selfY + 1, selfZ)) {
                rail_shape = 3;
            }
        }
        if (rail_shape == -1) {
            rail_shape = previous_shape;
        }
        self.updateConnections(rail_shape);
        int new_rail_shape = rail_shape;
        if (is_straight_rail) {
            new_rail_shape = meta & 8 | rail_shape;
        }
        //if (!world.isRemote) AddonHandler.logMessage("Rail metas "+meta+" "+previous_shape+" "+rail_shape+" "+new_rail_shape);
        if (par2 || meta != new_rail_shape) {
            world.setBlockMetadataWithNotify(selfX, selfY, selfZ, new_rail_shape, 0x01 | 0x02);
            List position_list = self.getRailChunkPosition();
            for (int i = 0; i < position_list.size(); ++i) {
                IBlockBaseRailLogicAccessMixins rail_logic = (IBlockBaseRailLogicAccessMixins)self.getRail((ChunkPosition)position_list.get(i));
                if (rail_logic != null) {
                    rail_logic.removeSoftConnections();
                    if (rail_logic.callCanConnectTo((BlockBaseRailLogic)(Object)this)) {
                        rail_logic.connectTo((BlockBaseRailLogic)(Object)this);
                    }
                }
            }
        }
    }
}
