package zero.test.mixin;
import net.minecraft.src.*;
import net.minecraft.server.MinecraftServer;
import btw.AddonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import zero.test.ZeroUtil;
import java.io.IOException;
import java.util.List;
import java.net.ServerSocket;
// Block piston reactions

@Mixin(HttpUtil.class)
public class HttpUtilMixins {
    @Redirect(
        method = "func_76181_a()I",
        at = @At(
            value = "NEW",
            target = "java/net/ServerSocket"
        )
    )
    private static ServerSocket make_socket_redirect(int port_arg) throws IOException {
        return new ServerSocket(ZeroUtil.lan_port);
    }
}
