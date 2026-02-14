package dev.stardust.modules;

import dev.stardust.Stardust;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import meteordevelopment.meteorclient.systems.modules.Module;
import org.jetbrains.annotations.Nullable;

public class SignHistorian extends Module {
    public SignHistorian() {
        super(Stardust.CATEGORY, "SignHistorian", "Stub module used for SignatureSign compatibility.");
    }

    public @Nullable SignText getRestoration(SignBlockEntity sign) {
        return null;
    }
}
