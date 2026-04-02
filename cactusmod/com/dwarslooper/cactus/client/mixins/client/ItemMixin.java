package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.BetterTooltips;
import com.dwarslooper.cactus.client.render.tooltip.ArmorTrimTooltipComponent;
import com.dwarslooper.cactus.client.render.tooltip.ArmorstandTooltipComponent;
import com.dwarslooper.cactus.client.render.tooltip.BannerTooltipComponent;
import com.dwarslooper.cactus.client.render.tooltip.MapTooltipComponent;
import com.dwarslooper.cactus.client.render.tooltip.ShulkerTooltipComponent;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ItemUtils;
import java.awt.Color;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1263;
import net.minecraft.class_1746;
import net.minecraft.class_1747;
import net.minecraft.class_1767;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1806;
import net.minecraft.class_22;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2343;
import net.minecraft.class_2371;
import net.minecraft.class_2480;
import net.minecraft.class_2586;
import net.minecraft.class_5321;
import net.minecraft.class_5632;
import net.minecraft.class_7803;
import net.minecraft.class_7924;
import net.minecraft.class_8052;
import net.minecraft.class_8056;
import net.minecraft.class_9209;
import net.minecraft.class_9288;
import net.minecraft.class_9334;
import net.minecraft.class_7225.class_7226;
import net.minecraft.class_7803.class_8905;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({class_1792.class})
public abstract class ItemMixin {
   @Inject(
      method = {"method_32346"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void appendTooltip(class_1799 stack, CallbackInfoReturnable<Optional<class_5632>> cir) {
      BetterTooltips tooltips = (BetterTooltips)ModuleManager.get().get(BetterTooltips.class);
      if (tooltips.active()) {
         if ((stack.method_7909() instanceof class_1746 || stack.method_57826(class_9334.field_56398)) && (Boolean)tooltips.showBannerTooltip.get()) {
            cir.setReturnValue(Optional.of(new BannerTooltipComponent(stack)));
         } else {
            class_1792 var8 = stack.method_7909();
            if (var8 instanceof class_1747) {
               class_1747 blockItem = (class_1747)var8;
               class_2248 var14 = blockItem.method_7711();
               if (var14 instanceof class_2343) {
                  class_2343 blockEntityProvider = (class_2343)var14;
                  if (ItemUtils.hasItems(stack) && (Boolean)tooltips.showShulkerTooltip.get()) {
                     class_9288 container = (class_9288)stack.method_58694(class_9334.field_49622);

                     assert container != null;

                     class_2586 var11 = blockEntityProvider.method_10123(class_2338.field_10980, blockItem.method_7711().method_9564());
                     int var22;
                     if (var11 instanceof class_1263) {
                        class_1263 inventoryProvider = (class_1263)var11;
                        var22 = inventoryProvider.method_5439();
                     } else {
                        var22 = 27;
                     }

                     int slots = var22;
                     class_2371<class_1799> itemStacks = class_2371.method_10213(slots, class_1799.field_8037);
                     container.method_57492(itemStacks);
                     Color color = Color.WHITE;
                     class_2248 var13 = blockItem.method_7711();
                     if (var13 instanceof class_2480) {
                        class_2480 shulker = (class_2480)var13;
                        if ((Boolean)tooltips.shulkerColor.get()) {
                           class_1767 dyeColor = shulker.method_10528();
                           if (dyeColor != null) {
                              color = new Color(shulker.method_10528().method_7787());
                           }
                        }
                     }

                     cir.setReturnValue(Optional.of(new ShulkerTooltipComponent(itemStacks, color)));
                     return;
                  }

                  return;
               }
            }

            if (stack.method_7909() == class_1802.field_8694 && (Boolean)tooltips.showArmorstandTooltip.get()) {
               if (!stack.method_57380().method_57848() && stack.method_57353().method_57832(class_9334.field_49609)) {
                  cir.setReturnValue(Optional.of(new ArmorstandTooltipComponent(stack)));
               }
            } else {
               var8 = stack.method_7909();
               if (var8 instanceof class_1806) {
                  class_1806 i = (class_1806)var8;
                  if ((Boolean)tooltips.showMapTooltip.get()) {
                     class_22 state = class_1806.method_8001(stack, CactusConstants.mc.field_1687);
                     cir.setReturnValue(Optional.ofNullable(state != null ? new MapTooltipComponent((class_9209)stack.method_58694(class_9334.field_49646)) : null));
                     return;
                  }
               }

               var8 = stack.method_7909();
               if (var8 instanceof class_8052) {
                  class_8052 item = (class_8052)var8;
                  if (stack.method_7909() != class_1802.field_41946 && (Boolean)tooltips.showArmorTrimTooltip.get()) {
                     Stream<class_5321<class_8056>> keys = class_7803.method_51694().filter((st) -> {
                        return item.equals(st.comp_2012());
                     }).map(class_8905::comp_3630);
                     class_7226<class_8056> wrapper = CactusConstants.WRAPPER_LOOKUP.method_46762(class_7924.field_42082);
                     Optional var10000 = keys.findFirst();
                     Objects.requireNonNull(wrapper);
                     var10000.flatMap(wrapper::method_46746).ifPresent((ref) -> {
                        cir.setReturnValue(Optional.of(new ArmorTrimTooltipComponent(ref, (Boolean)tooltips.rotateArmorPreview.get())));
                     });
                  }
               }
            }
         }

      }
   }
}
