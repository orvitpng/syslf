package ninja.carter.syslf.mixin;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import ninja.carter.syslf.attachement.ArmorVisibilityUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerCommonPacketListenerImpl.class)
public class SendPacketMixin {
    @ModifyVariable(
        method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V",
        at = @At("HEAD"),
        argsOnly = true
    )
    private Packet<?> modify(Packet<?> packet) {
        return ((Object)this instanceof ServerGamePacketListenerImpl listener)
            ? processPackets(listener.player.level(), packet)
            : packet;
    }

    @Unique
    private Packet<?> processPackets(Level world, Packet<?> packet) {
        if (packet instanceof ClientboundSetEquipmentPacket ep)
            return processPacket(world, ep);
        if (packet instanceof ClientboundBundlePacket bp)
            for (Packet<?> p : bp.subPackets())
                processPackets(world, p);
        return packet;
    }

    @Unique Packet<?> processPacket(Level world, ClientboundSetEquipmentPacket packet) {
        Entity entity = world.getEntity(packet.getEntity());
        if (!(entity instanceof ServerPlayer player) || ArmorVisibilityUtil.get(player))
            return packet;
        
        List<Pair<EquipmentSlot, ItemStack>> slots = packet.getSlots();
        for (int i = 0; i < slots.size(); i++) {
            EquipmentSlot slot = slots.get(i).getFirst();
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR)
                slots.set(i, Pair.of(slot, ItemStack.EMPTY));
        }

        return packet;
    }
}
