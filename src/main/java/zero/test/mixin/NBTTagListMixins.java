package zero.test.mixin;
import net.minecraft.src.*;
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
import java.util.ArrayList;
import java.util.List;
// Block piston reactions

@Mixin(NBTTagList.class)
public abstract class NBTTagListMixins implements INBTBaseMixins {
    @Shadow
    public List tagList;
    @Shadow
    public byte tagType;
    @Override
    public void toSNBT(StringBuilder str) {
        str.append('[');
        int count;
        if ((count = this.tagList.size()) > 0) {
            --count;
            for (int i = 0; i < count; ++i) {
                ((INBTBaseMixins)this.tagList.get(i)).toSNBT(str);
                str.append(',');
            }
            ((INBTBaseMixins)this.tagList.get(count)).toSNBT(str);
        }
        str.append(']');
    }
}
