package zero.test.mixin.deco;
import net.minecraft.src.*;
import btw.item.blockitems.WoodMouldingDecorativeStubBlockItem;
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
import deco.block.util.WoodTypeHelper;
// Block piston reactions

@Mixin(
    value = WoodMouldingDecorativeStubBlockItem.class,
    priority = 1100 // This should apply after the mixin from deco, right?
)
public abstract class WoodMouldingDecorativeStubBlockItemMixins extends ItemBlock {
    public WoodMouldingDecorativeStubBlockItemMixins() {
        super(0);
    }
    private static final String[] type_name_lookup = new String[] {
        ".column",
        ".pedestal",
        ".table",
        ".wtf"
    };
    @Inject(
        method = "getUnlocalizedName(Lnet/minecraft/src/ItemStack;)Ljava/lang/String;",
        at = @At("HEAD"),
        cancellable = true
    )
    public void fix_wood_crash(ItemStack stack, CallbackInfoReturnable<String> callback_info) {
        int damage, wood_type;
        if ((wood_type = WoodMouldingDecorativeStubBlockItem.getWoodType(damage = stack.getItemDamage())) > 4) {
            callback_info.setReturnValue(super.getUnlocalizedName() + '.' + WoodTypeHelper.woodNames[wood_type] + type_name_lookup[WoodMouldingDecorativeStubBlockItem.getBlockType(damage)]);
        }
    }
}
