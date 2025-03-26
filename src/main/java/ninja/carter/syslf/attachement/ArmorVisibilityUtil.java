package ninja.carter.syslf.attachement;

import net.minecraft.server.level.ServerPlayer;
import ninja.carter.syslf.Attachements;

public class ArmorVisibilityUtil {
    public static boolean get(ServerPlayer player) {
        return player.getAttachedOrCreate(Attachements.ARMOR_VISIBILITY).visibility();
    }

    public static void set(ServerPlayer player, boolean visibility) {
        player.setAttached(
            Attachements.ARMOR_VISIBILITY,
            new ArmorVisibilityData(visibility)
        );
    }
}
