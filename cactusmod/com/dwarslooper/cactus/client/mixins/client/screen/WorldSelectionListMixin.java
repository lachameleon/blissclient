package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.mixins.accessor.AbstractSelectionListAccessor;
import com.dwarslooper.cactus.client.systems.config.impl.SavedLevelsConfig;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ABValue;
import com.dwarslooper.cactus.client.util.mixinterface.IWorldListWidgetUpdateImpl;
import com.dwarslooper.cactus.client.util.mixinterface.IWorldPinnable;
import com.mojang.logging.LogUtils;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1109;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_33;
import net.minecraft.class_332;
import net.minecraft.class_34;
import net.minecraft.class_3417;
import net.minecraft.class_4280;
import net.minecraft.class_5250;
import net.minecraft.class_528;
import net.minecraft.class_4280.class_4281;
import net.minecraft.class_528.class_4272;
import net.minecraft.class_528.class_7414;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_528.class})
public abstract class WorldSelectionListMixin extends class_4280<class_7414> implements IWorldListWidgetUpdateImpl {
   @Shadow
   private String field_39738;

   public WorldSelectionListMixin(class_310 minecraftClient, int i, int j, int k, int l) {
      super(minecraftClient, i, j, k, l);
   }

   @Shadow
   protected abstract void method_43460(class_2561 var1);

   @Shadow
   protected abstract void method_43454(String var1, List<class_34> var2);

   @Shadow
   @Nullable
   protected abstract List<class_34> method_44679();

   @Shadow
   public abstract void method_20157(@Nullable class_7414 var1);

   @Inject(
      method = {"method_43462"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void load(CallbackInfoReturnable<CompletableFuture<List<class_34>>> cir) {
      try {
         if (CactusConstants.mc.method_1586().method_235().method_43421()) {
            cir.setReturnValue(CompletableFuture.completedFuture(List.of()));
         }
      } catch (class_33 var3) {
         LogUtils.getLogger().error("Couldn't load level list", var3);
         this.method_43460(var3.method_43416());
         cir.setReturnValue(CompletableFuture.completedFuture(List.of()));
      }

   }

   @Inject(
      method = {"method_43454"},
      at = {@At("TAIL")}
   )
   public void summariesLoaded(String search, List<class_34> summaries, CallbackInfo ci) {
      ((AbstractSelectionListAccessor)this).invokeSort((e1, e2) -> {
         class_7414 entry1 = (class_7414)e1;
         class_7414 entry2 = (class_7414)e2;
         if (this.isPinned(entry1) == this.isPinned(entry2)) {
            return 0;
         } else {
            return this.isPinned(entry1) ? -1 : 1;
         }
      });
   }

   @Unique
   public void cactus$update() {
      this.method_43454(this.field_39738, this.method_44679());
      this.method_20157((class_7414)null);
   }

   @Unique
   private boolean isPinned(class_7414 entry) {
      if (entry instanceof class_4272 && entry instanceof IWorldPinnable) {
         IWorldPinnable iwp = (IWorldPinnable)entry;
         return iwp.cactus$isPinned();
      } else {
         return false;
      }
   }

   @Mixin({class_4272.class})
   public abstract static class EntryMixin extends class_4281<class_7414> implements IWorldPinnable {
      @Unique
      private static final ABValue<class_5250> favoriteStars = ABValue.of("★", "☆").map(class_2561::method_43470).apply((text) -> {
         text.method_27692(class_124.field_1065);
      });
      @Unique
      private boolean pinned;
      @Unique
      private class_528 listWidget;
      @Shadow
      @Final
      private class_310 field_19136;
      @Shadow
      @Final
      class_34 field_19138;

      @Inject(
         method = {"<init>"},
         at = {@At("TAIL")}
      )
      public void onInit(class_528 outer, class_528 parent, class_34 summary, CallbackInfo ci) {
         this.listWidget = parent;
         this.pinned = ((SavedLevelsConfig)CactusClient.getInstance().getConfigHandler().getConfig(SavedLevelsConfig.class)).isLevelPinned(summary);
      }

      @Inject(
         method = {"method_25343"},
         at = {@At("TAIL")}
      )
      public void injectRender(class_332 context, int mouseX, int mouseY, boolean hovered, float deltaTicks, CallbackInfo ci) {
         if (hovered || this.pinned) {
            context.method_51439(this.field_19136.field_1772, (class_2561)favoriteStars.fromBoolean(this.pinned), this.method_46426() + this.listWidget.method_25322() - 10, this.method_46427() + 2, (new Color(-153344, true)).getRGB(), false);
         }

      }

      @Inject(
         method = {"method_25402"},
         at = {@At("HEAD")},
         cancellable = true
      )
      public void handleMouseClick(class_11909 click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
         int index = this.listWidget.method_25396().indexOf((class_4272)this);
         int top = ((AbstractSelectionListAccessor)this.listWidget).getRowY(index);
         double x = click.comp_4798() - (double)this.listWidget.method_25342();
         double y = click.comp_4799() - (double)top;
         if (x >= (double)(this.listWidget.method_25322() - 12) && x < (double)this.listWidget.method_25322() && y < 9.0D || click.method_74245() == 2) {
            CactusConstants.mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
            this.pinned = !this.pinned;
            cir.setReturnValue(true);
            this.savePinnedState();
         }

      }

      @Unique
      public void savePinnedState() {
         ((SavedLevelsConfig)CactusClient.getConfig(SavedLevelsConfig.class)).setPinned(this.field_19138, this.pinned);
         ((IWorldListWidgetUpdateImpl)this.listWidget).cactus$update();
         CactusClient.getInstance().getConfigHandler().save(CactusClient.getConfig(SavedLevelsConfig.class));
      }

      @Unique
      public void cactus$setPinned(boolean pinned) {
         this.pinned = pinned;
      }

      @Unique
      public boolean cactus$isPinned() {
         return this.pinned;
      }
   }
}
