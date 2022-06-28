package space.emptiness.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.util.ArrayList;

public class PacketUtils {
    private static ArrayList packets = new ArrayList<Packet<INetHandlerPlayServer>>();

    //Send Packet
    public static void sendPacket(Packet packet){
        Minecraft.getNetHandler().addToSendQueue(packet);
    }

    //Send No Event Packet
    public static void sendPacketNoEvent(Packet<INetHandlerPlayServer> packet){
        packets.add(packet);
        Minecraft.thePlayer.sendQueue.addToSendQueue(packet);
    }

    //Handle send packet
    public Boolean handleSendPacket(Packet packet){
        if (packets.contains(packet)) {
            packets.remove(packet);
            return true;
        }
        return false;
    }

    //get packet type
    public PacketType getPacketType(Packet packet){
        if(packet.getClass().getSimpleName().startsWith("C"))
            return  PacketType.CLIENTSIDE;
        else if(packet.getClass().getSimpleName().startsWith("S"))
            return  PacketType.SERVERSIDE;

        return PacketType.UNKNOWN;
    }

    public enum PacketType {
        SERVERSIDE,
        CLIENTSIDE,
        UNKNOWN
    }
}
