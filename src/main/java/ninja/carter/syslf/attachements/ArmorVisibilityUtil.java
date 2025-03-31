package ninja.carter.syslf.attachements;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import ninja.carter.syslf.Attachements;

public class ArmorVisibilityUtil {
    public static ArmorVisibility get(ServerPlayer player) {
        return player.getAttachedOrCreate(Attachements.ARMOR_VISIBILITY);
    }

    public static boolean set(ServerPlayer player, ArmorVisibility visibility) {
        if (get(player).equals(visibility)) return false;

        player.setAttached(Attachements.ARMOR_VISIBILITY, visibility);
        player.serverLevel().getChunkSource().broadcast(
            player,
            new ClientboundSetEquipmentPacket(player.getId(), List.of(
                Pair.of(EquipmentSlot.HEAD, player.getItemBySlot(EquipmentSlot.HEAD)),
                Pair.of(EquipmentSlot.CHEST, player.getItemBySlot(EquipmentSlot.CHEST)),
                Pair.of(EquipmentSlot.LEGS, player.getItemBySlot(EquipmentSlot.LEGS)),
                Pair.of(EquipmentSlot.FEET, player.getItemBySlot(EquipmentSlot.FEET))
            ))
        );
        return true;
    }
}
