package ninja.carter.syslf;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import ninja.carter.syslf.attachements.ArmorVisibility;

public class Attachements {
    public static final AttachmentType<ArmorVisibility> ARMOR_VISIBILITY =
        AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "armor_visibility"),
            builder -> builder
                .initializer(() -> ArmorVisibility.DEFAULT)
                .persistent(ArmorVisibility.CODEC)
        );

    // Method for ensuring the class is loaded.
    // This is to register attachements at initialization.
    public static void initialize() {
    }
}
