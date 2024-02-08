package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.NoteBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

//import zero.test.mixin.IBlockComparatorAccessMixins;

import java.util.Random;

import zero.test.IWorldMixins;

#include "..\feature_flags.h"
#include "..\util.h"

#define POWERED_META_OFFSET 0

#define CLICK_META_OFFSET 1
#define CLICK_META_BITS 1
#define CLICK_META_IS_BOOL true

//updateNeighbourShapes

@Mixin(NoteBlock.class)
public class NoteBlockMixins extends Block {
    
    public NoteBlockMixins(int par1, Material par2) {
        super(par1, par2);
    }
    
#if ENABLE_DIRECTIONAL_UPDATES

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        boolean isReceivingPower = world.isBlockIndirectlyGettingPowered(x, y, z);
        
        int meta = world.getBlockMetadata(x, y, z);
        
        if (isReceivingPower != READ_META_FIELD(meta, POWERED)) {
            TileEntityNote tile_entity = (TileEntityNote)world.getBlockTileEntity(x, y, z);
            if (tile_entity != null) {
                tile_entity.previousRedstoneState = isReceivingPower;
                tile_entity.triggerNote(world, x, y, z);
            }
            world.setBlockMetadataWithNotify(x, y, z, TOGGLE_META_FIELD(meta, POWERED), UPDATE_NEIGHBORS | UPDATE_CLIENTS | UPDATE_SUPPRESS_LIGHT);
        }
    }
    
    @Inject(
        method = "onBlockActivated",
        at = @At("HEAD")
    )
    public void onBlockActivated_inject(World world, int x, int y, int z, EntityPlayer player, int facing, float xClick, float yClick, float zClick, CallbackInfoReturnable info) {
        if (!world.isRemote) {
            // Just do *something* to the metadata so that stuff updates
            world.setBlockMetadataWithNotify(x, y, z, TOGGLE_META_FIELD(world.getBlockMetadata(x, y, z), CLICK), UPDATE_NEIGHBORS | UPDATE_CLIENTS | UPDATE_SUPPRESS_LIGHT);
        }
    }
#endif
}