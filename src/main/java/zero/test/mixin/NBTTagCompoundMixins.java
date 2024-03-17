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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
// Block piston reactions

@Mixin(NBTTagCompound.class)
public abstract class NBTTagCompoundMixins extends NBTBase implements INBTBaseMixins {
    public NBTTagCompoundMixins() {
        super(null);
    }
    @Shadow
    public Map tagMap;
    @Override
    public void toSNBT(StringBuilder str) {
        String name;
        if ((name = this.getName()) != "") {
            str.append(name).append(':');
        }
        str.append('{');
        if (!this.tagMap.isEmpty()) {
            Iterator iter = this.tagMap.keySet().iterator();
            while (true) {
                str.append(name = (String)iter.next()).append(':');
                ((INBTBaseMixins)this.tagMap.get(name)).toSNBT(str);
                if (!iter.hasNext()) break;
                str.append(',');
            }
        }
        str.append('}');
    }
}
