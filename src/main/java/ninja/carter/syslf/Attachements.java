package ninja.carter.syslf;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import ninja.carter.syslf.attachement.ArmorVisibilityData;

public class Attachements {
    public static final AttachmentType<ArmorVisibilityData> ARMOR_VISIBILITY =
        AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath("syslf", "armor_visibility"),
            builder -> builder
                .initializer(() -> ArmorVisibilityData.DEFAULT)
                .persistent(ArmorVisibilityData.CODEC)
        );

    // Method for ensuring the class is loaded.
    // This is to register attachements at initialization.
    public static void initialize() {}
}
