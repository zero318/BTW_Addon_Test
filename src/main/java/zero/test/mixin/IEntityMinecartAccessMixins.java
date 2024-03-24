package zero.test.mixin;
import net.minecraft.src.*;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import zero.test.IBlockMixins;
@Mixin(EntityMinecart.class)
public interface IEntityMinecartAccessMixins {
    @Accessor
    public IUpdatePlayerListBox getField_82344_g();
    @Accessor
    public int getTurnProgress();
    @Accessor
    public void setTurnProgress(int value);
    @Accessor
    public boolean getIsInReverse();
    @Accessor
    public void setIsInReverse(boolean value);
    @Accessor
    public double getMinecartX();
    @Accessor
    public double getMinecartY();
    @Accessor
    public double getMinecartZ();
    @Accessor
    public double getMinecartYaw();
    @Accessor
    public double getMinecartPitch();
    //@Invoker("consumeFacingBlock")
    //public abstract void callConsumeFacingBlock(World world, int x, int y, int z);
}
