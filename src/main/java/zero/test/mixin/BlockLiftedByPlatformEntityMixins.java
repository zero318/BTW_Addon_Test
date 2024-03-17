package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.block.blocks.PlatformBlock;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;
import btw.entity.mechanical.platform.MovingPlatformEntity;
import btw.item.util.ItemUtils;
import btw.world.util.WorldUtils;
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
import zero.test.IWorldMixins;
import zero.test.IBlockMixins;
import zero.test.IMovingPlatformEntityMixins;
import zero.test.ZeroUtil;
import zero.test.ZeroMetaUtil;
import java.util.Random;
import java.util.List;
// Block piston reactions
@Mixin(BlockLiftedByPlatformEntity.class)
public abstract class BlockLiftedByPlatformEntityMixins extends Entity {
    public BlockLiftedByPlatformEntityMixins() {
        super(null);
    }
/*
    @Inject(
        method = "onUpdate()V",
        at = @At("HEAD")
    )
    public void onUpdate_inject(CallbackInfo info) {
        AddonHandler.logMessage("BlockLiftedByPlatformEntity tick");
    }
*/
    @Overwrite(remap=false)
    public void destroyBlockWithDrop() {
        if (!this.worldObj.isRemote) {
            BlockLiftedByPlatformEntity self = (BlockLiftedByPlatformEntity)(Object)this;
            int idDropped = Block.blocksList[self.getBlockID()].idDropped(self.getBlockMetadata(), this.worldObj.rand, 0);
            if (idDropped > 0) {
                ItemUtils.ejectSingleItemWithRandomOffset(
                    this.worldObj,
                    MathHelper.floor_double(this.posX),
                    MathHelper.floor_double(this.posY),
                    MathHelper.floor_double(this.posZ),
                    idDropped,
                    0
                );
            }
        }
        this.setDead();
    }
    @Overwrite(remap=false)
    public void convertToBlock(int x, int y, int z) {
        BlockLiftedByPlatformEntity self = (BlockLiftedByPlatformEntity)(Object)this;
        Block block_below = Block.blocksList[self.worldObj.getBlockId(x, y - 1, z)];
        if (
            !((block_below)==null) &&
            ((IBlockMixins)block_below).getPlatformMobilityFlag(self.worldObj, x, y, z) == 1 &&
            WorldUtils.isReplaceableBlock(self.worldObj, x, y, z)
        ) {
            int blockId = self.getBlockID();
            int blockMeta = self.getBlockMetadata();
            int extMeta = ZeroMetaUtil.getBlockLiftedByPlatformEntityExtMeta(self);
            int newMeta = ((IWorldMixins)this.worldObj).updateFromNeighborShapes(x, y, z, blockId, blockMeta);
            if (newMeta >= 0) {
                ZeroMetaUtil.setBlockWithExtra(this.worldObj, x, y, z, blockId, newMeta, extMeta, 0x01 | 0x02);
                this.worldObj.notifyBlockOfNeighborChange(x, y, z, blockId);
            } else {
                ZeroMetaUtil.setBlockWithExtra(this.worldObj, x, y, z, blockId, blockMeta, extMeta, 0x04 | 0x10 | 0x80);
                this.worldObj.destroyBlock(x, y, z, true);
            }
            self.setDead();
        }
        else {
            self.destroyBlockWithDrop();
            // setDead() is called inside destroyBlockWithDrop
        }
    }
    @Overwrite
    public void onUpdate() {
        BlockLiftedByPlatformEntity self = (BlockLiftedByPlatformEntity)(Object)this;
     if (!self.isDead) {
            // search for the associated platform
            int facing = 1;
            do {
                double dX = (double)Facing.offsetsXForSide[facing] + 0.25D;
                double dY = (double)Facing.offsetsYForSide[facing] + 0.25D;
                double dZ = (double)Facing.offsetsZForSide[facing] + 0.25D;
                List<MovingPlatformEntity> collisionList = self.worldObj.getEntitiesWithinAABB(
                    MovingPlatformEntity.class,
                    AxisAlignedBB.getAABBPool().getAABB(
                        self.posX - dX, self.posY - dY, self.posZ - dZ,
                        self.posX + dX, self.posY + dY, self.posZ + dZ
                    )
                );
                if (
                    collisionList != null &&
                    !collisionList.isEmpty()
                ) {
                    for (MovingPlatformEntity platform : collisionList) {
                        if (!platform.isDead) {
                            double newPosX = platform.posX;
                            double newPosY = platform.posY;
                            double newPosZ = platform.posZ;
                            self.prevPosX = self.posX;
                            self.prevPosY = self.posY;
                            self.prevPosZ = self.posZ;
                            switch (facing) {
                                case 5:
                                    newPosX += platform.posX < self.posX ? 1.0D : -1.0D;
                                    break;
                                case 1:
                                    newPosY += platform.posY < self.posY ? 1.0D : -1.0D;
                                    break;
                                default:
                                    newPosZ += platform.posZ < self.posZ ? 1.0D : -1.0D;
                                    break;
                            }
                            self.setPosition(newPosX, newPosY, newPosZ);
                            return;
                        }
                    }
                }
            } while (((facing += 2)<=5));
            if (!self.worldObj.isRemote) {
                this.convertToBlock(
                    MathHelper.floor_double(self.posX),
                    MathHelper.floor_double(self.posY),
                    MathHelper.floor_double(self.posZ)
                );
            }
        }
    }
    /*
    @Redirect(
        method = "<init>(Lnet/minecraft/src/World;III)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z"
        )
    )
    public boolean spawnEntityInWorld_redirect(World world, Entity entity) {
        // This call doesn't work from inside a constructor
        return true;
    }
    
    @Redirect(
        method = "<init>(Lnet/minecraft/src/World;III)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;setBlockWithNotify(IIII)Z"
        )
    )
    public boolean setBlockWithNotify_redirect(World world, int x, int y, int z, int blockId) {
        // Don't do this from inside a constructor
        return true;
    }
    */
}
