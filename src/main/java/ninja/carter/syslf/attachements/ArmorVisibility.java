package ninja.carter.syslf.attachements;

import com.mojang.serialization.Codec;

public record ArmorVisibility(boolean visibility) {
    public static final ArmorVisibility DEFAULT = new ArmorVisibility(true);

    public static final Codec<ArmorVisibility> CODEC =
        Codec.BOOL.xmap(ArmorVisibility::new, ArmorVisibility::visibility);
}
