package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.StubBlock;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import zero.test.IBlockMixins;
import zero.test.IWorldMixins;
//import zero.test.IBlockRailPoweredMixins;
import zero.test.block.ActivatorRailShim;
import zero.test.block.PoweredRailBlock;
import java.util.List;
// Block piston reactions
@Mixin(Block.class)
public abstract class BlockMixins implements IBlockMixins {
    @Overwrite
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int direction, boolean ignoreTransparency) {
        // Skip the extra block lookup from chaining through world
        // since the block is already known.
  return ((Block)(Object)this).isNormalCube(blockAccess, x, y, z);
 }
    /*
        THESE IGNORE THE WorldUtils VERSIONS OF HARDPOINT CHECKS
        
        This was done deliberately because calls to the WorldUtils functions
        only seem to happen in cases where checking for transparency makes sense
        or an explicit true value is passed.
    */
    /*
        Effects:
    */
    @Overwrite
    public boolean hasSmallCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
        return ((Block)(Object)this).hasSmallCenterHardPointToFacing(blockAccess, x, y, z, facing, true);
    }
    /*
        Effects:
    */
    @Overwrite
    public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
  return ((Block)(Object)this).hasCenterHardPointToFacing(blockAccess, x, y, z, facing, true);
 }
    /*
        Effects:
        - Most block placement works on transparent blocks (maybe including fire? Might disable that)
        - Wall logic doesn't ignore transparent blocks
        - doesBlockHaveSolidTopSurface allows transparent blocks
         - Water makes drip particles when above transparent blocks
         - Leaves don't make drip particles in the rain when above transparent blocks
         - Dismounting an entity considers transparent blocks as valid
         - Entities following the player can teleport onto transparent blocks
         - Players can respawn from beacons on transparent blocks
         - Iron Golems can spawn on transparent blocks (assuming BTW didn't screw with the code elsewhere)
         - World bonus chests can spawn on transparent blocks
         - Ash can be deposited on transparent blocks
         
        Rendering bugs to fix:
        - Redstone dust
        - Repeaters
        - Comparators
    */
    @Overwrite
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
  return ((Block)(Object)this).hasLargeCenterHardPointToFacing(blockAccess, x, y, z, facing, true);
 }
    /*
        Effects:
        - Prevent above changes from impacting grass
    */
    @Overwrite
    public boolean getCanGrassGrowUnderBlock(World world, int x, int y, int z, boolean grassOnHalfSlab) {
  return grassOnHalfSlab || !((Block)(Object)this).hasLargeCenterHardPointToFacing(world, x, y, z, 0, false);
 }
    /*
        Check for effects:
        - isSnowCoveringTopSurface
        - hasFallingBlockRestingOn
    */
    // Extra variant of getMobilityFlag that allows
    // changing the result based on metadata.
    @Override
    public int getMobilityFlag(World world, int x, int y, int z) {
        return ((Block)(Object)this).getMobilityFlag();
    }
    /*
    public void addCollisionBoxesToListForPiston(TileEntityPiston pistonEntity, List list) {
        double x = (double)pistonEntity.xCoord;
        double y = (double)pistonEntity.yCoord;
        double z = (double)pistonEntity.zCoord;
        
        AxisAlignedBB fakeMask = AxisAlignedBB.getAABBPool().getAABB(x - 1.0D, y - 1.0D, z - 1.0D, x + 2.0D, y + 2.0D, z + 2.0D);
        
        int prevMeta = pistonEntity.worldObj.getBlockMetadata(pistonEntity.xCoord, pistonEntity.yCoord, pistonEntity.zCoord);
        pistonEntity.worldObj.setBlockMetadataWithNotify(x, y, z, pistonEntity.getBlockMetadata(), UPDATE_INVISIBLE | UPDATE_KNOWN_SHAPE | UPDATE_SUPPRESS_LIGHT);
        ((Block)(Object)this).addCollisionBoxesToList(pistonEntity.worldObj, x, y, z, fakeMask, list, (Entity)null);
        pistonEntity.worldObj.setBlockMetadataWithNotify(x, y, z, prevMeta, UPDATE_INVISIBLE | UPDATE_KNOWN_SHAPE | UPDATE_SUPPRESS_LIGHT);
    }
    */
    @Overwrite
    public boolean rotateAroundJAxis(World world, int x, int y, int z, boolean reverse) {
        int prevMeta = world.getBlockMetadata(x, y, z);
        Block self = (Block)(Object)this;
        int newMeta = self.rotateMetadataAroundJAxis(prevMeta, reverse);
        if (prevMeta != newMeta) {
            newMeta = ((IWorldMixins)world).updateFromNeighborShapes(x, y, z, self.blockID, newMeta);
            world.setBlockMetadataWithNotify(x, y, z, newMeta, 0x01 | 0x02);
            return true;
        }
        return false;
    }
    // Reduced hardpoint requirement from large to medium.
    // This allows fences to stay connected to extended piston sides.
    @Overwrite
    public boolean shouldFenceConnectToThisBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
        Block self = (Block)(Object)this;
  return self.isNormalCube(blockAccess, x, y, z) || self.isFence(blockAccess.getBlockMetadata(x, y, z)) || self.hasCenterHardPointToFacing(blockAccess, x, y, z, facing, true);
 }
 // Reduced hardpoint requirement from large to small.
    // This allows panes to stay connected to extended piston sides.
    @Overwrite
 public boolean shouldPaneConnectToThisBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
        Block self = (Block)(Object)this;
  return self.isNormalCube(blockAccess, x, y, z) || self.hasSmallCenterHardPointToFacing(blockAccess, x, y, z, facing, true);
 }
    @Inject(
        method = "<clinit>()V",
        at = @At("TAIL")
    )
    private static void static_init_inject(CallbackInfo info) {
        Block.railActivator = null;
        Block.blocksList[157] = null;
        Block.railActivator = (new ActivatorRailShim(157)).setPicksEffectiveOn().setHardness(0.7F).setStepSound(Block.soundMetalFootstep).setUnlocalizedName("activatorRail");
        Block.railPowered = null;
        Block.blocksList[27] = null;
        Block.railPowered = new PoweredRailBlock(27);
    }
/*
#if ENABLE_BETTER_BUDDY_DETECTION
    @Shadow
    public abstract boolean triggersBuddy();

    public boolean triggersBuddy(World world, int x, int y, int z) {
        return this.triggersBuddy();
    }
#endif
*/
/*
#if ENABLE_TURNTABLE_SLIME_SUPPORT
    @Overwrite
    public int getNewMetadataRotatedAroundBlockOnTurntableToFacing(World world, int x, int y, int z, int prevFacing, int newFacing) {
        return world.getBlockMetadata(x, y, z);
    }
#endif
*/
}
