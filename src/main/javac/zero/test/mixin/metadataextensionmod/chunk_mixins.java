package zero.test.mixin.metadataextensionmod;

import net.minecraft.src.*;

import btw.community.arminias.metadata.extension.ChunkExtension;
import btw.community.arminias.metadata.extension.WorldExtension;
import btw.community.arminias.metadata.extension.ExtendedBlockStorageExtension;
import btw.community.arminias.metadata.extension.TileEntityExtension;
import btw.community.arminias.metadata.mixin.ChunkMixin;

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

import zero.test.ZeroUtil;

#include "..\..\util.h"
#include "..\..\feature_flags.h"

#define USE_OVERRIDES 0

@Mixin(
    value = Chunk.class,
    priority = 1100
)
public abstract class ChunkMixins
#if USE_OVERRIDES
//implements ChunkExtension
#endif
{
    
#if !USE_OVERRIDES

    @Redirect(
        method = { "setBlockIDWithMetadataAndExtraMetadata(IIIIII)Z", "setBlockExtraMetadata(IIII)Z" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/Chunk;getChunkBlockTileEntity(III)Lnet/minecraft/src/TileEntity;",
            ordinal = 0
        )
    )
    public TileEntity make_tile_entity_redirect(Chunk chunk, int x, int y, int z) {
        return chunk.worldObj.getBlockTileEntity((chunk.xPosition << 4) + x, y, (chunk.zPosition << 4) + z);
    }
    
#else
    /*
    @Shadow
    public ExtendedBlockStorage[] storageArrays;

    //@Override
    public boolean setBlockExtraMetadata(int x, int y, int z, int newExtMeta) {
        ExtendedBlockStorage extStorage = this.storageArrays[y >> 4];
        if (extStorage != null) {
            int extMeta = ((ExtendedBlockStorageExtension)extStorage).getExtBlockExtraMetadata(x, y & 0xF, z);
            if (extMeta != newExtMeta) {
                Chunk self = (Chunk)(Object)this;
                
                self.isModified = true;
                ((ExtendedBlockStorageExtension)extStorage).setExtBlockExtraMetadata(x, y & 0xF, z, newExtMeta);
                
                if (Block.blocksList[extStorage.getExtBlockID(x, y & 0xF, z)] instanceof ITileEntityProvider) {
                    TileEntity tileEntity = chunk.worldObj.getBlockTileEntity((self.xPosition << 4) + x, y, (self.zPosition << 4) + z);
                    if (tileEntity != null) {
                        tileEntity.updateContainingBlockInfo();
                        ((TileEntityExtension)tileEntity).setBlockExtraMetadata(newExtMeta);
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    //@Override
    public boolean setBlockIDWithMetadataAndExtraMetadata(int x, int y, int z, int newBlockId, int newMeta, int newExtMeta) {
        int heightmapIndex = z << 4 | x;
        
        Chunk self = (Chunk)(Object)this;
        if (y >= self.precipitationHeightMap[heightmapIndex] - 1) {
            self.precipitationHeightMap[heightmapIndex] = -999;
        }
        
        int heightmapValue = self.heightMap[heightmapIndex];
        int blockId = self.getBlockID(x, y, z);
        int meta = self.getBlockMetadata(x, y, z);
        int extMeta = ((ChunkExtension)self).getBlockExtraMetadata(x, y, z);
        
        if (
            blockId != newBlockId ||
            meta != newMeta ||
            extMeta != newExtMeta
        ) {
            ExtendedBlockStorage extStorage = this.storageArrays[y >> 4];
            boolean idk = false;
            
            if (extStorage == null) {
                if (blockId == 0) {
                    return false;
                }
                
                extStorage = this.storageArrays[y >> 4] = new ExtendedBlockStorage(y >> 4 << 4, !self.worldObj.provider.hasNoSky);
                idk = y >= heightmapValue;
            }
            
            int fullX = self.xPosition << 4 + x;
            int fullZ = self.zPosition << 4 + z;
            
            Block block = Block.blocksList[blockId];
            
            if (
                !BLOCK_IS_AIR(block) &&
                !self.worldObj.isRemote
            ) {
                block.onSetBlockIDWithMetaData(self.worldObj, fullX, y, fullZ, meta);
            }
            
            extStorage.setExtBlockID(x, y & 0xF, z, newBlockId);
            
            if (!BLOCK_IS_AIR(block)) {
                if (!self.worldObj.isRemote) {
                    block.breakBlock(self.worldObj, fullX, y, fullZ, blockId, meta);
                }
                else if (blockId != newBlockId) {
                    block.clientBreakBlock(self.worldObj, fullX, y, fullZ, blockId, meta);
                    
                    if (
                        block instanceof ITileEntityProvider &&
                        block.shouldDeleteTileEntityOnBlockChange(newBlockId)
                    ) {
                        self.worldObj.removeBlockTileEntity(fullX, y, fullZ);
                    }
                }
            }
            
            if (extStorage.getExtBlockID(x, y & 0xF, z) == newBlockId) {
                extStorage.setExtBlockMetadata(x, y & 0xF, z, newMeta);
                ((ExtendedBlockStorageExtension)extStorage).setExtBlockExtraMetadata(x, y & 0xF, z, newExtMeta);
                
                if (idk) {
                    self.generateSkylightMap();
                }
                else {
                    if (Block.lightOpacity[blockId & 0xFFF] > 0) {
                        if (y >= var7) {
                            this.relightBlock(x, y + 1, z);
                        }
                    }
                    else if (y == var7 - 1) {
                        this.relightBlock(x, y, z);
                    }

                    this.propagateSkylightOcclusion(x, z);
                }
            }
        }
        return false;
    }
    */
#endif
}