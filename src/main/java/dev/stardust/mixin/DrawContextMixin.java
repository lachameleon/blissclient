package dev.stardust.mixin;

import dev.stardust.modules.LoreLocator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Tas [0xTas] <root@0xTas.dev>
 **/
@Mixin(GuiGraphicsExtractor.class)
public abstract class DrawContextMixin {
    @Shadow
    public abstract void fill(int x1, int y1, int x2, int y2, int color);

    // See LoreLocator.java
    @Inject(method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V", at = @At("HEAD"))
    private void highlightNamedItemsEntity(LivingEntity entity, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        Modules modules = Modules.get();
        if (modules == null) return;
        LoreLocator ll = modules.get(LoreLocator.class);
        if (!ll.isActive() || !ll.shouldHighlightSlot(stack)) return;
        this.fill(x, y, x + 16, y + 16, ll.color.get().getPacked());
    }

    @Inject(method = "item(Lnet/minecraft/world/item/ItemStack;III)V", at = @At("HEAD"))
    private void highlightNamedItems(ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        Modules modules = Modules.get();
        if (modules == null) return;
        LoreLocator ll = modules.get(LoreLocator.class);
        if (!ll.isActive() || !ll.shouldHighlightSlot(stack)) return;
        this.fill(x, y, x + 16, y + 16, ll.color.get().getPacked());
    }

    @Inject(method = "fakeItem(Lnet/minecraft/world/item/ItemStack;III)V", at = @At("HEAD"))
    private void highlightNamedItemsNoEntity(ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        Modules modules = Modules.get();
        if (modules == null) return;
        LoreLocator ll = modules.get(LoreLocator.class);
        if (!ll.isActive() || !ll.shouldHighlightSlot(stack)) return;
        this.fill(x, y, x + 16, y + 16, ll.color.get().getPacked());
    }
}
