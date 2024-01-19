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
import java.util.List;
// Block piston reactions
@Mixin(CommandServerPublishLocal.class)
public abstract class CommandServerPublishLocalMixins extends CommandBase {
    @Override
    public void processCommand(ICommandSender command_sender, String args[]) {
        int port = 0;
        EnumGameType game_type = EnumGameType.SURVIVAL;
        boolean allow_commands = false;
        switch (args.length) {
            default: // Port
                port = this.parseIntBounded(command_sender, args[2], 1024, (0xFFFF));
            case 2: // Gamemode
                switch (args[1]) {
                    case "adventure":
                        game_type = EnumGameType.ADVENTURE;
                        break;
                    //case "spectator":
                    case "creative":
                        game_type = EnumGameType.CREATIVE;
                    case "survival":
                        break;
                    default:
                        throw new WrongUsageException("commands.publish.unknown");
                }
            case 1: // Allow commands
                switch (args[0]) {
                    case "true":
                        allow_commands = true;
                    case "false":
                        break;
                    default:
                        throw new WrongUsageException("commands.publish.unknown");
                }
            case 0:
                break;
        }
        ZeroUtil.lan_port = port;
        String str = MinecraftServer.getServer().shareToLAN(game_type, allow_commands);
        ZeroUtil.lan_port = 0;
        if (str != null) {
            notifyAdmins(command_sender, "commands.publish.started", new Object[] {str});
        }
        else {
            notifyAdmins(command_sender, "commands.publish.failed", new Object[0]);
        }
    }
}
