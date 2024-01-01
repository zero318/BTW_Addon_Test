package zero.test.command;

import net.minecraft.src.*;

import btw.AddonHandler;

public class ServerNoclipCommand extends CommandBase {
    public ServerNoclipCommand(){}

    @Override
    public String getCommandName() {
        return "noclip";
    }

    @Override
    public String getCommandUsage(ICommandSender command_sender) {
        return "/noclip";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender command_sender, String args[]) {   
        if (command_sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)command_sender;
            boolean send_packet = false;
            if (args.length > 0) {
                switch (args[0]) {
                    case "on": case "true":
                        send_packet = !player.noClip;
                        player.noClip = true;
                        break;
                    case "off": case "false":
                        send_packet = player.noClip;
                        player.noClip = false;
                        break;
                }
            }
            command_sender.sendChatToPlayer(player.noClip ? "\247eNoclip state: On" : "\247eNoclip state: Off");
            if (send_packet) {
                player.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(318, player.noClip ? 1 : 0));
            }
        }
    }
}
