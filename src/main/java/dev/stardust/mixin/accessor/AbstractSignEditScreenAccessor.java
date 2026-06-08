package dev.stardust.mixin.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSignEditScreen.class)
public interface AbstractSignEditScreenAccessor {
    @Accessor
    String[] getMessages();

    @Mutable
    @Accessor("messages")
    void setMessages(String[] messages);

    @Mutable
    @Accessor("text")
    void setText(SignText text);

    @Accessor("sign")
    SignBlockEntity getBlockEntity();
}
