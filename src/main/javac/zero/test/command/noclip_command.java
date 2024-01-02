package zero.test.command;

import net.minecraft.src.*;
import net.minecraft.server.MinecraftServer;

import btw.AddonHandler;

import java.util.List;

public class ServerNoclipCommand extends CommandBase {
    public ServerNoclipCommand(){}

    @Override
    public String getCommandName() {
        return "noclip";
    }

    @Override
    public String getCommandUsage(ICommandSender command_sender) {
        return command_sender.translateString("commands.noclip.usage", new Object[0]);
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
            switch (args.length) {
                default:
                    player = func_82359_c(command_sender, args[1]);
                case 1:
                    switch (args[0]) {
                        default:
                            throw new WrongUsageException("commands.noclip.usage", new Object[0]);
                        case "on": case "true":
                            send_packet = !player.noClip;
                            player.noClip = true;
                            break;
                        case "off": case "false":
                            send_packet = player.noClip;
                            player.noClip = false;
                        case "query":
                            break;
                    }
                    break;
                case 0:
                    break;
            }
            if (send_packet) {
                player.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(318, player.noClip ? 1 : 0));
            }
            String translate_key = send_packet
                ? (player.noClip ? "commands.noclip.turn_on" : "commands.noclip.turn_off")
                : (player.noClip ? "commands.noclip.query_on" : "commands.noclip.query_off");
            notifyAdmins(command_sender, 1, translate_key, new Object[]{player.getEntityName()});
            player.sendChatToPlayer("\247e"+player.translateString(translate_key, new Object[]{player.getEntityName()}));
            if (command_sender != player) {
                command_sender.sendChatToPlayer("\247e"+command_sender.translateString(translate_key, new Object[]{player.getEntityName()}));
            }
        }
    }
    
    @Override
    public List addTabCompletionOptions(ICommandSender command_sender, String[] args) {
        switch (args.length) {
            default:
                return null;
            case 1:
                return getListOfStringsMatchingLastWord(args, new String[] {"query", "on", "off", "true", "false"});
            case 2:
                return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }
    }
    
    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }
}
