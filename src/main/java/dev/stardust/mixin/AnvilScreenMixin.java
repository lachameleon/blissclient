package dev.stardust.mixin;

import dev.stardust.modules.StashBrander;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Tas [0xTas] <root@0xTas.dev>
 **/
@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> {
    @Shadow
    private EditBox name;

    public AnvilScreenMixin(AnvilMenu handler, Inventory playerInventory, Component title, Identifier texture) {
        super(handler, playerInventory, title, texture);
    }

    /**
     * See StashBrander.java
     * Helps to minimize packet spam by drastically reducing the amount of RenameItemC2SPackets that are sent.
     * */
    @Inject(method = "slotChanged", at = @At("HEAD"), cancellable = true)
    private void maybeCancelNameFieldUpdate(AbstractContainerMenu handler, int slotId, ItemStack stack, CallbackInfo ci) {
        Modules mods = Modules.get();
        if (mods == null) return;
        StashBrander sb = mods.get(StashBrander.class);

        if (slotId == 0 && sb.isActive()) {
            ci.cancel();
            this.name.setEditable(true);
            this.setFocused(this.name);
        }
    }
}
