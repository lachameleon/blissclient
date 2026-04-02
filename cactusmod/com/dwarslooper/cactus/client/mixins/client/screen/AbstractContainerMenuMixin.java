package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.gui.screen.impl.ArmorStandEditorScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.NbtEditorScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.VisualNbtEditorScreen;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.InputUtilWrapper;
import net.minecraft.class_1657;
import net.minecraft.class_1703;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_437;
import net.minecraft.class_481;
import net.minecraft.class_5536;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1703.class})
public abstract class AbstractContainerMenuMixin {
   @Inject(
      method = {"method_45409"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onSlotClick(class_1657 player, class_5536 clickType, class_1735 slot, class_1799 stack, class_1799 cursorStack, CallbackInfoReturnable<Boolean> cir) {
      if (ContentPackManager.get().isEnabled(ContentPackManager.get().ofId("creative_tools")) && CactusConstants.mc.field_1755 instanceof class_481 && !stack.method_7960()) {
         if (ArmorStandEditorScreen.awaitingInventoryItemSelection) {
            ArmorStandEditorScreen aes = new ArmorStandEditorScreen(CactusConstants.mc.field_1755);
            aes.applyItem(stack);
            CactusConstants.mc.method_1507(aes);
            cir.setReturnValue(true);
            ArmorStandEditorScreen.awaitingInventoryItemSelection = false;
         }

         if (InputUtilWrapper.hasAltDown() && clickType == class_5536.field_27014) {
            CactusConstants.mc.method_1507((class_437)(InputUtilWrapper.hasControlDown() ? new NbtEditorScreen(stack, slot.method_34266()) : new VisualNbtEditorScreen(stack, slot.method_34266())));
            cir.setReturnValue(true);
         }
      }

   }
}
