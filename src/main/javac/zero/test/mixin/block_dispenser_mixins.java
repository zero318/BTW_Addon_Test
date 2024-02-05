package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.*;
import btw.block.tileentity.dispenser.BlockDispenserTileEntity;
import btw.AddonHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Random;
import java.util.Collections;

import zero.test.IBlockMixins;
import zero.test.IWorldMixins;

#include "../util.h"
#include "../feature_flags.h"
#include "../ids.h"

#define DIRECTION_META_OFFSET 0
#define POWERED_META_OFFSET 3

@Mixin(BlockDispenserBlock.class)
public abstract class BlockDispenserBlockMixins extends BlockContainer {
    
    public BlockDispenserBlockMixins(int par1, Material par2Material) {
        super(par1, par2Material);
    }
    
#if ENABLE_BETTER_BLOCK_DISPENSER_POWERING
    @Overwrite
    public boolean isReceivingRedstonePower(World world, int x, int y, int z) {
#if ENABLE_LESS_CRAP_BTW_BLOCK_POWERING
        return ((IWorldMixins)world).getBlockWeakPowerInputExceptFacing(x, y, z, READ_META_FIELD(world.getBlockMetadata(x, y, z), DIRECTION)) != 0 || ((IWorldMixins)world).getBlockWeakPowerInputExceptFacing(x, y + 1, z, DIRECTION_DOWN) != 0;
#else
        return ((IWorldMixins)world).getBlockStrongPowerInputExceptFacing(x, y, z, READ_META_FIELD(world.getBlockMetadata(x, y, z), DIRECTION)) != 0 || ((IWorldMixins)world).getBlockStrongPowerInputExceptFacing(x, y + 1, z, DIRECTION_DOWN) != 0;
#endif    
    }
#endif

#if ENABLE_BLOCK_DISPENSER_NEAREST_ENTITY_PRIORITY
    @Shadow
    public abstract boolean validateBlockDispenser(World world, int i, int j, int k);

    @Overwrite
    public boolean consumeEntityAtTargetLoc(World world, int x, int y, int z, int targetX, int targetY, int targetZ) {
        this.validateBlockDispenser(world, x, y, z);
        
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(
            (Entity)null,
            AxisAlignedBB.getAABBPool().getAABB(
                (double)targetX, (double)targetY, (double)targetZ,
                (double)(targetX + 1), (double)(targetY + 1), (double)(targetZ + 1)
            )
        );
        
        const double targetXD = (double)targetX + 0.5D;
        const double targetYD = (double)targetY + 0.5D;
        const double targetZD = (double)targetZ + 0.5D;
        
        if (list != null && !list.isEmpty()) {
            if (list.size() > 1) {
                Collections.sort(list, (Entity lhs, Entity rhs) -> {
                    double lhsXDiff = lhs.posX - targetXD;
                    double lhsYDiff = lhs.posY - targetYD;
                    double lhsZDiff = lhs.posZ - targetZD;
                    double rhsXDiff = rhs.posX - targetXD;
                    double rhsYDiff = rhs.posY - targetYD;
                    double rhsZDiff = rhs.posZ - targetZD;
                    return Double.compare(
                        lhsXDiff * lhsXDiff + lhsYDiff * lhsYDiff + lhsZDiff * lhsZDiff,
                        rhsXDiff * rhsXDiff + rhsYDiff * rhsYDiff + rhsZDiff * rhsZDiff
                    );
                });
            }
            
            BlockDispenserTileEntity tileEentityDispenser = (BlockDispenserTileEntity)world.getBlockTileEntity(x, y, z);
            
            for (Entity targetEntity : list) {
                if (!targetEntity.isDead) {
                    if (targetEntity.onBlockDispenserConsume((BlockDispenserBlock)(Object)this, tileEentityDispenser)) {
                        return true; // false means keep checking other entities, so no return
                    }
                }
            }
        }
        return false;
    }
#endif
}