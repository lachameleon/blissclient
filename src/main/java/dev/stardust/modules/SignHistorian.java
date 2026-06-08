package dev.stardust.modules;

import dev.stardust.Stardust;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.jetbrains.annotations.Nullable;

public class SignHistorian extends Module {
    public SignHistorian() {
        super(Stardust.CATEGORY, "SignHistorian", "Stub module used for SignatureSign compatibility.");
    }

    public @Nullable SignText getRestoration(SignBlockEntity sign) {
        return null;
    }
}
