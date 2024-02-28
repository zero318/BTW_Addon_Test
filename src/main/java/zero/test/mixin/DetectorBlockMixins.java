package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.DetectorBlock;
import btw.client.fx.BTWEffectManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
// Block piston reactions
@Mixin(DetectorBlock.class)
public abstract class DetectorBlockMixins extends Block {
    public DetectorBlockMixins() {
        super(0, null);
    }
    @Overwrite
    public void setBlockOn(World world, int x, int y, int z, boolean newState) {
        int meta = world.getBlockMetadata(x, y, z);
        if (((((meta)&1)!=0)) ^ newState) {
            if (newState) {
                world.playAuxSFX(BTWEffectManager.REDSTONE_CLICK_EFFECT_ID, x, y, z, 0);
            }
            world.setBlockMetadataWithNotify(x, y, z, ((meta)^1), 0x01 | 0x02);
        }
    }
}
