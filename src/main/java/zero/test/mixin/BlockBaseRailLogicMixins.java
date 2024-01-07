package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import zero.test.IBlockBaseRailLogicMixins;
import zero.test.mixin.IBlockBaseRailLogicAccessMixins;
import java.util.List;
// Block piston reactions
@Mixin(BlockBaseRailLogic.class)
public class BlockBaseRailLogicMixins {
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
            (has_north_connect | has_south_connect) &
            ((has_west_connect | has_east_connect)^true)
        ) {
            rail_shape = 0;
        }
        if (
            (has_west_connect | has_east_connect) &
            ((has_north_connect | has_south_connect)^true)
        ) {
            rail_shape = 1;
        }
        if (!is_straight_rail) {
            if (
                has_south_connect &
                has_east_connect &
                ((has_north_connect | has_west_connect)^true)
            ) {
                rail_shape = 6;
            }
            if (
                has_south_connect &
                has_west_connect &
                ((has_north_connect | has_east_connect)^true)
            ) {
                rail_shape = 7;
            }
            if (
                has_north_connect &
                has_west_connect &
                ((has_south_connect | has_east_connect)^true)
            ) {
                rail_shape = 8;
            }
            if (
                has_north_connect &
                has_east_connect &
                ((has_south_connect | has_west_connect)^true)
            ) {
                rail_shape = 9;
            }
        }
        if (rail_shape == -1) {
            if (
                (has_north_connect | has_south_connect) &
                (has_west_connect | has_east_connect)
            ) {
                rail_shape = previous_shape;
            }
            else if (has_north_connect | has_south_connect) {
                rail_shape = 0;
            }
            else if (has_west_connect | has_east_connect) {
                rail_shape = 1;
            }
            if (!is_straight_rail) {
                if (is_powered) {
                    if (has_south_connect & has_east_connect) {
                        rail_shape = 6;
                    }
                    if (has_west_connect & has_south_connect) {
                        rail_shape = 7;
                    }
                    if (has_east_connect & has_north_connect) {
                        rail_shape = 9;
                    }
                    if (has_north_connect & has_west_connect) {
                        rail_shape = 8;
                    }
                }
                else {
                    if (has_north_connect & has_west_connect) {
                        rail_shape = 8;
                    }
                    if (has_east_connect & has_north_connect) {
                        rail_shape = 9;
                    }
                    if (has_west_connect & has_south_connect) {
                        rail_shape = 7;
                    }
                    if (has_south_connect & has_east_connect) {
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
