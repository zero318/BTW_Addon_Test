package zero.test.mixin.metadataextensionmod;
import net.minecraft.src.*;
import btw.community.arminias.metadata.NBTTagLongArray;
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
import org.spongepowered.asm.mixin.gen.Accessor;
import zero.test.INBTBaseMixins;
// Block piston reactions

@Mixin(NBTTagLongArray.class)
public abstract class NBTTagLongArrayMixins implements INBTBaseMixins {
    @Override
    public void toSNBT(StringBuilder str) {
        str.append("[L;");
        long[] data = ((NBTTagLongArray)(Object)this).longArray;
        int count;
        if ((count = data.length) > 0) {
            --count;
            for (int i = 0; i < count; ++i) {
                str.append(data[i]).append("L,");
            }
            str.append(data[count]).append('L');
        }
        str.append(']');
    }
}
