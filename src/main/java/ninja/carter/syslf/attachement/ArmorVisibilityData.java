package ninja.carter.syslf.attachement;

import com.mojang.serialization.Codec;

public record ArmorVisibilityData(boolean visibility) {
    public static final ArmorVisibilityData DEFAULT = new ArmorVisibilityData(true);

    public static final Codec<ArmorVisibilityData> CODEC =
        Codec.BOOL.xmap(ArmorVisibilityData::new, ArmorVisibilityData::visibility);
}
