package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_140;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4267;
import net.minecraft.class_4280;
import net.minecraft.class_500;
import net.minecraft.class_4267.class_4270;
import net.minecraft.class_4267.class_504;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_4267.class})
public abstract class ServerSelectionListMixin extends class_4280<class_504> {
   @Unique
   private static boolean replaced = false;
   @Mutable
   @Shadow
   @Final
   static ThreadPoolExecutor field_19105;
   @Shadow
   @Final
   static Logger field_19104;
   @Shadow
   @Final
   private List<class_4270> field_19109;
   @Shadow
   @Final
   private class_500 field_19108;
   @Unique
   private class_4270 cactus$dragEntry;
   @Unique
   private int cactus$dragIndex = -1;
   @Unique
   private int cactus$dragOriginIndex = -1;
   @Unique
   private int cactus$targetIndex = -1;
   @Unique
   private boolean cactus$dragging = false;
   @Unique
   private double cactus$clickY = 0.0D;
   @Unique
   private double cactus$dragOffsetY = 0.0D;
   @Unique
   private double cactus$dragRenderY = 0.0D;
   @Unique
   private double cactus$animatedDragY = 0.0D;
   @Unique
   private double cactus$animatedMarkerY = 0.0D;
   @Unique
   private float cactus$dragAlpha = 0.0F;
   @Unique
   private float cactus$dragScale = 1.0F;
   @Unique
   private double cactus$animatedGlowSize = 0.0D;
   @Unique
   private final Map<class_504, Double> cactus$entryAnimatedY = new HashMap();

   @Shadow
   protected abstract void method_20131();

   public ServerSelectionListMixin(class_310 minecraftClient, int i, int j, int k, int l) {
      super(minecraftClient, i, j, k, l);
   }

   @Inject(
      method = {"method_20131"},
      at = {@At("HEAD")}
   )
   public void hookServerPinger(CallbackInfo info) {
      if (!replaced) {
         replaced = true;
         this.replaceThreadPool();
         CactusClient.getLogger().info("Hooked into ServerPinger");
      }

      if (field_19105.getActiveCount() > 4) {
         this.replaceThreadPool();
      }

   }

   @Unique
   private void replaceThreadPool() {
      field_19105.shutdownNow();
      field_19105 = new ScheduledThreadPoolExecutor(this.field_19109.size() + 4, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger Enhanced #%d").setDaemon(true).setUncaughtExceptionHandler(new class_140(field_19104)).build());
   }

   public boolean method_25404(class_11908 input) {
      class_504 entry = (class_504)this.method_25334();
      if (input.method_74228() == 261 && entry instanceof class_4270) {
         this.field_19109.remove(entry);
         this.method_20131();
      }

      return super.method_25404(input);
   }

   @Unique
   private boolean cactus$isAnimationEnabled() {
      return ContentPackManager.get().ofId("smooth_animations") != null && ContentPackManager.get().ofId("smooth_animations").isEnabled();
   }

   public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
      if (!doubled && (Boolean)CactusSettings.get().dragDropReorderServers.get() && click.method_74245() == 0) {
         class_504 entry = (class_504)this.method_25308(click.comp_4798(), click.comp_4799());
         if (entry instanceof class_4270) {
            class_4270 serverEntry = (class_4270)entry;
            this.cactus$dragEntry = serverEntry;
            this.cactus$dragIndex = this.method_25396().indexOf(entry);
            this.cactus$dragOriginIndex = this.cactus$dragIndex;
            this.cactus$clickY = click.comp_4799();
            this.cactus$targetIndex = this.cactus$dragIndex;
            this.cactus$dragging = false;
         }
      }

      return super.method_25402(click, doubled);
   }

   public boolean method_25403(@NotNull class_11909 click, double offsetX, double offsetY) {
      if (this.cactus$dragEntry != null && (Boolean)CactusSettings.get().dragDropReorderServers.get()) {
         double dy = Math.abs(click.comp_4799() - this.cactus$clickY);
         if (!this.cactus$dragging) {
            if (!(dy > 4.0D)) {
               return true;
            }

            this.cactus$dragging = true;
            this.cactus$dragOffsetY = this.cactus$clickY - (double)this.cactus$dragEntry.method_46427();
            this.cactus$dragRenderY = click.comp_4799() - this.cactus$dragOffsetY;
            this.cactus$animatedDragY = (double)this.cactus$dragEntry.method_46427();
            this.cactus$dragAlpha = 0.0F;
            this.cactus$dragScale = 1.0F;
            this.cactus$animatedGlowSize = 0.0D;
         } else {
            this.cactus$dragRenderY = click.comp_4799() - this.cactus$dragOffsetY;
         }

         this.cactus$targetIndex = this.cactus$computeTargetIndex(click.comp_4799());
         return true;
      } else {
         return super.method_25403(click, offsetX, offsetY);
      }
   }

   public boolean method_25406(@NotNull class_11909 click) {
      if (this.cactus$dragEntry != null && this.cactus$dragging && this.cactus$targetIndex >= 0) {
         int to = this.cactus$targetIndex;
         if (this.cactus$dragOriginIndex < to) {
            to = Math.max(0, to - 1);
         }

         this.cactus$moveEntry(this.cactus$dragOriginIndex, to);
      }

      this.cactus$dragEntry = null;
      this.cactus$dragIndex = -1;
      this.cactus$dragOriginIndex = -1;
      this.cactus$targetIndex = -1;
      this.cactus$dragging = false;
      this.cactus$dragAlpha = 0.0F;
      this.cactus$dragScale = 1.0F;
      this.cactus$animatedGlowSize = 0.0D;
      this.cactus$entryAnimatedY.clear();
      return super.method_25406(click);
   }

   @Unique
   private void cactus$moveEntry(int from, int to) {
      int serverCount = this.field_19108.method_2529().method_2984();
      if (from >= 0 && to >= 0 && from < serverCount && to < serverCount) {
         if (from != to) {
            int pos;
            for(pos = from; pos < to; ++pos) {
               this.cactus$swap(pos, pos + 1);
            }

            while(pos > to) {
               this.cactus$swap(pos, pos - 1);
               --pos;
            }

         }
      }
   }

   @Unique
   private void cactus$swap(int i, int j) {
      this.field_19108.method_2529().method_2985(i, j);
      this.method_73368(i, j);
   }

   @Unique
   private int cactus$computeTargetIndex(double mouseY) {
      int serverCount = this.field_19108.method_2529().method_2984();
      if (serverCount == 0) {
         return -1;
      } else {
         List<class_504> children = this.method_25396();
         int insertion = serverCount;

         for(int idx = 0; idx < Math.min(serverCount, children.size()); ++idx) {
            class_504 entry = (class_504)children.get(idx);
            if (entry != this.cactus$dragEntry && mouseY < (double)entry.method_46427() + (double)entry.method_25364() / 2.0D) {
               insertion = idx;
               break;
            }
         }

         return insertion;
      }
   }

   protected void method_25311(@NotNull class_332 context, int mouseX, int mouseY, float deltaTicks) {
      class_504 dragging = this.cactus$dragging ? this.cactus$dragEntry : null;
      int insertion = this.cactus$dragging ? this.cactus$targetIndex : -1;
      Iterator var7 = this.method_25396().iterator();

      int rowRight;
      int currentIndex;
      int dragOrigin;
      int dragTarget;
      int entryHeight;
      while(var7.hasNext()) {
         class_504 entry = (class_504)var7.next();
         if (entry != dragging) {
            rowRight = entry.method_46427();
            if (this.cactus$dragging && dragging != null && this.cactus$isAnimationEnabled()) {
               currentIndex = this.method_25396().indexOf(entry);
               dragOrigin = this.cactus$dragOriginIndex;
               dragTarget = insertion;
               if (insertion > dragOrigin) {
                  dragTarget = Math.max(0, insertion - 1);
               }

               if (currentIndex >= Math.min(dragOrigin, dragTarget) && currentIndex <= Math.max(dragOrigin, dragTarget)) {
                  entryHeight = dragging.method_25364();
                  if (currentIndex > dragOrigin) {
                     rowRight -= entryHeight;
                  } else if (currentIndex >= dragTarget && currentIndex < dragOrigin) {
                     rowRight += entryHeight;
                  }
               }
            }

            currentIndex = entry.method_46427();
            if (this.cactus$isAnimationEnabled()) {
               if (!this.cactus$entryAnimatedY.containsKey(entry)) {
                  this.cactus$entryAnimatedY.put(entry, (double)entry.method_46427());
               }

               double animatedY = (Double)this.cactus$entryAnimatedY.get(entry);
               animatedY += ((double)rowRight - animatedY) * 0.25D;
               this.cactus$entryAnimatedY.put(entry, animatedY);
               entry.method_46419((int)animatedY);
            } else {
               entry.method_46419(rowRight);
            }

            if (entry.method_46427() + entry.method_25364() >= this.method_46427() && entry.method_46427() <= this.method_55443()) {
               this.method_44397(context, mouseX, mouseY, deltaTicks, entry);
            }

            entry.method_46419(currentIndex);
         }
      }

      if (dragging != null) {
         boolean animationsEnabled = this.cactus$isAnimationEnabled();
         int rowLeft = this.method_25342();
         rowRight = this.method_31383();
         if (insertion <= 0) {
            currentIndex = this.method_46427() + 1;
         } else {
            class_504 last;
            if (insertion >= this.method_25396().size()) {
               last = (class_504)this.method_25396().getLast();
               currentIndex = last.method_46427() + last.method_25364();
            } else {
               last = (class_504)this.method_25396().get(Math.min(insertion, this.method_25396().size() - 1));
               currentIndex = last.method_46427();
            }
         }

         if (animationsEnabled) {
            this.cactus$animatedDragY += (this.cactus$dragRenderY - this.cactus$animatedDragY) * 0.3D;
            this.cactus$dragAlpha = Math.min(1.0F, this.cactus$dragAlpha + deltaTicks * 0.15F);
            this.cactus$animatedMarkerY += ((double)currentIndex - this.cactus$animatedMarkerY) * 0.25D;
            dragOrigin = (int)this.cactus$animatedMarkerY;
            dragTarget = (int)(Math.min(this.cactus$dragAlpha, 0.8F) * 255.0F);
            entryHeight = (int)((0.5D + Math.sin((double)System.currentTimeMillis() / 150.0D) * 0.30000001192092896D) * (double)dragTarget);
            context.method_25294(rowLeft, dragOrigin - 2, rowRight, dragOrigin + 2, entryHeight << 24 | 16777215);
         } else {
            context.method_25294(rowLeft, currentIndex - 2, rowRight, currentIndex + 2, -1996488705);
         }

         dragTarget = dragging.method_46427();
         entryHeight = animationsEnabled ? (int)this.cactus$animatedDragY : (int)this.cactus$dragRenderY;
         dragging.method_46419(entryHeight);
         if (dragging.method_46427() + dragging.method_25364() >= this.method_46427() && dragging.method_46427() <= this.method_55443()) {
            if (animationsEnabled) {
               double targetScale = 1.0D + Math.sin((double)System.currentTimeMillis() / 400.0D) * 0.015D;
               this.cactus$animatedGlowSize += (targetScale - this.cactus$animatedGlowSize) * 0.15D;
               int bgAlpha = (int)(this.cactus$dragAlpha * 120.0F);
               int accentAlpha = (int)(this.cactus$dragAlpha * 180.0F);
               int yOffset = (int)(Math.sin((double)System.currentTimeMillis() / 500.0D) * 1.5D);
               context.method_25294(rowLeft - 1, dragging.method_46427() - 1 + yOffset, rowRight + 1, dragging.method_46427() + dragging.method_25364() + 1 + yOffset, bgAlpha << 24);
               context.method_25294(rowLeft, dragging.method_46427(), rowRight, dragging.method_46427() + 1, accentAlpha << 24 | '裿');
               context.method_25294(rowLeft, dragging.method_46427() + dragging.method_25364() - 1, rowRight, dragging.method_46427() + dragging.method_25364(), accentAlpha << 24 | '裿');
            }

            this.method_44397(context, mouseX, mouseY, deltaTicks, dragging);
         }

         dragging.method_46419(dragTarget);
      }

   }
}
