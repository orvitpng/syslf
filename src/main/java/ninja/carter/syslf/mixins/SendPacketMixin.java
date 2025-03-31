package ninja.carter.syslf.mixins;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import ninja.carter.syslf.attachements.ArmorVisibilityUtil;
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
        return ((Object) this instanceof ServerGamePacketListenerImpl listener)
            ? processPackets(listener.player.level(), packet)
            : packet;
    }

    @Unique
    private Packet<?> processPackets(Level world, Packet<?> packet) {
        if (packet instanceof ClientboundSetEquipmentPacket ep)
            return processPacket(world, ep);
        if (packet instanceof ClientboundBundlePacket bp) {
            List<Packet<? super ClientGamePacketListener>> bundle = new ArrayList<>();
            for (Packet<?> p : bp.subPackets()) {
                @SuppressWarnings("unchecked")
                Packet<? super ClientGamePacketListener> casted =
                    (Packet<? super ClientGamePacketListener>) processPackets(world, p);
                bundle.add(casted);
            }

            return new ClientboundBundlePacket(bundle);
        }
        return packet;
    }

    @Unique
    private Packet<?> processPacket(Level world, ClientboundSetEquipmentPacket packet) {
        Entity entity = world.getEntity(packet.getEntity());
        if (!(entity instanceof ServerPlayer player) || ArmorVisibilityUtil.get(player).visibility())
            return packet;

        List<Pair<EquipmentSlot, ItemStack>> slots = new ArrayList<>();
        for (Pair<EquipmentSlot, ItemStack> slot : packet.getSlots()) {
            EquipmentSlot eq = slot.getFirst();
            if (eq.getType() == EquipmentSlot.Type.HUMANOID_ARMOR)
                slots.add(Pair.of(eq, ItemStack.EMPTY));
            else
                slots.add(slot);
        }

        return new ClientboundSetEquipmentPacket(packet.getEntity(), slots);
    }
}
