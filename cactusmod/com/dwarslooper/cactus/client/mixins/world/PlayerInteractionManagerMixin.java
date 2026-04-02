package com.dwarslooper.cactus.client.mixins.world;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.impl.AttackBlockEvent;
import com.dwarslooper.cactus.client.event.impl.InteractBlockEvent;
import com.dwarslooper.cactus.client.event.impl.InteractEntityEvent;
import com.dwarslooper.cactus.client.event.impl.InteractItemEvent;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.redstone.AutoRepeater;
import com.dwarslooper.cactus.client.feature.modules.redstone.FluidMarker;
import com.dwarslooper.cactus.client.feature.modules.util.CopyBlockState;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.InputUtilWrapper;
import com.dwarslooper.cactus.client.util.game.ItemUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_156;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1934;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_2769;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_636;
import net.minecraft.class_746;
import net.minecraft.class_7923;
import net.minecraft.class_9275;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_636.class})
public abstract class PlayerInteractionManagerMixin {
   @Shadow
   private class_1934 field_3719;

   @Inject(
      method = {"method_2896"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onBlockAction(class_746 player, class_1268 hand, class_3965 hitResult, CallbackInfoReturnable<class_1269> cir) {
      AutoRepeater.waiting = true;
      InteractBlockEvent event = (InteractBlockEvent)CactusClient.EVENT_BUS.post(new InteractBlockEvent(player, hand, hitResult));
      if (event.hasModifiedResult()) {
         event.applyToCallback(cir);
      }

   }

   @Inject(
      method = {"method_2896"},
      at = {@At("TAIL")}
   )
   public void cancel(class_746 player, class_1268 hand, class_3965 hitResult, CallbackInfoReturnable<class_1269> cir) {
   }

   @Inject(
      method = {"method_2919"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onItemAction(class_1657 player, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
      if (player == CactusConstants.mc.field_1724) {
         InteractItemEvent event = (InteractItemEvent)CactusClient.EVENT_BUS.post(new InteractItemEvent(CactusConstants.mc.field_1724, hand));
         if (event.hasModifiedResult()) {
            event.applyToCallback(cir);
         }
      }

   }

   @Inject(
      method = {"method_2917"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onEntityAction(class_1657 player, class_1297 entity, class_3966 hitResult, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
      if (player == CactusConstants.mc.field_1724) {
         InteractEntityEvent event = (InteractEntityEvent)CactusClient.EVENT_BUS.post(new InteractEntityEvent(CactusConstants.mc.field_1724, entity, hitResult, hand));
         if (event.hasModifiedResult()) {
            event.applyToCallback(cir);
         }
      }

   }

   @Inject(
      method = {"method_2910"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_31549()Lnet/minecraft/class_1656;"
)},
      cancellable = true
   )
   public void onAttackBlock(class_2338 pos, class_2350 direction, CallbackInfoReturnable<Boolean> cir) {
      this.postAttackBlock(pos, direction, cir);
   }

   @Inject(
      method = {"method_2902"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_31549()Lnet/minecraft/class_1656;"
)},
      cancellable = true
   )
   public void onUpdateBreakProgress(class_2338 pos, class_2350 direction, CallbackInfoReturnable<Boolean> cir) {
      if (this.field_3719.method_8386()) {
         this.postAttackBlock(pos, direction, cir);
      }

   }

   @Inject(
      method = {"method_65193"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onPickItem(class_2338 pos, boolean includeData, CallbackInfo ci) {
      class_2680 state = CactusConstants.mc.field_1687.method_8320(pos);
      CopyBlockState module = (CopyBlockState)ModuleManager.get().get(CopyBlockState.class);
      if (module.active() && InputUtilWrapper.hasControlDown()) {
         class_1799 stack = new class_1799(state.method_26204());
         Map<String, String> properties = new HashMap();
         List<class_2561> lines = new ArrayList();
         Iterator var9 = state.method_28501().iterator();

         while(var9.hasNext()) {
            class_2769<?> property = (class_2769)var9.next();
            String name = property.method_11899();
            if (!(((List)module.properties.get()).contains(name) ^ module.listType.get() == CopyBlockState.Mode.Whitelist)) {
               Comparable<?> value = state.method_11654(property);
               String formattedProperty = ItemUtils.propertyToFormattedString(property, value);
               String valueStr = class_156.method_650(property, value);
               properties.put(name, valueStr);
               lines.add(class_2561.method_43470(formattedProperty));
            }
         }

         if (!properties.isEmpty()) {
            stack.method_57379(class_9334.field_49623, new class_9275(properties));
            if ((Boolean)module.appendToLore.get()) {
               stack.method_57379(class_9334.field_49632, new class_9290(lines));
            }

            int matchingSlot = CactusConstants.mc.field_1724.method_31548().method_61494(class_7923.field_41178.method_47983(stack.method_7909()), stack);
            int slot = matchingSlot == -1 ? CactusConstants.mc.field_1724.method_31548().method_7376() : matchingSlot;
            ItemUtils.giveItem(stack, slot);
            ItemUtils.trySelectSlot(slot);
            ci.cancel();
         }
      }

   }

   @Unique
   private void postAttackBlock(class_2338 pos, class_2350 direction, CallbackInfoReturnable<Boolean> cir) {
      AttackBlockEvent event = (AttackBlockEvent)CactusClient.EVENT_BUS.post(new AttackBlockEvent(CactusConstants.mc.field_1724, CactusConstants.mc.field_1687, class_1268.field_5808, pos, direction));
      if (event.hasModifiedResult()) {
         cir.setReturnValue(event.getResult() == class_1269.field_5812);
      }

   }

   @Unique
   private void checkBlockedThenCancel(class_2338 pos, CallbackInfoReturnable<Boolean> cir) {
      FluidMarker waterChecker = (FluidMarker)ModuleManager.get().get(FluidMarker.class);
      if (waterChecker.active() && waterChecker.mode.get() == FluidMarker.Mode.Prevent && waterChecker.getRenderedAsDangerous().containsKey(pos)) {
         cir.setReturnValue(false);
      }

   }
}
