package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.gui.screen.ITranslatable;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.render.CactusCubeMapRenderer;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ScreenUtils;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_34;
import net.minecraft.class_342;
import net.minecraft.class_437;
import net.minecraft.class_526;
import net.minecraft.class_528;
import net.minecraft.class_766;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_526.class})
public abstract class SelectWorldScreenMixin extends class_437 implements ITranslatable {
   @Unique
   private static final ExecutorService loaderExecutor = Executors.newSingleThreadExecutor();
   @Shadow
   @Final
   protected class_437 field_3221;
   @Shadow
   private class_528 field_3218;
   @Shadow
   protected class_342 field_3220;
   @Unique
   private class_766 backgroundRenderer;

   public SelectWorldScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At("TAIL")}
   )
   public void init(CallbackInfo ci) {
      String name = this.getTranslatableElement("changeDir", new Object[0]);
      CTextureButtonWidget selectSavesDirectoryButton;
      this.method_37063(selectSavesDirectoryButton = new CTextureButtonWidget(this.field_22789 / 2 + 100 + 4, this.field_3220.method_46427(), 180, (button) -> {
         String path = TinyFileDialogs.tinyfd_selectFolderDialog(name, CactusConstants.mc.method_1586().method_19636().toString());
         if (path != null) {
            CactusSettings.get().customLevelDirectory.set(path);
            CactusConstants.mc.method_1507(new class_526(this.field_3221));
         }
      }));
      selectSavesDirectoryButton.method_47400(ScreenUtils.tooltipLiteral(name));
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float deltaTicks) {
      super.method_25394(context, mouseX, mouseY, deltaTicks);
      if (this.backgroundRenderer != null) {
         context.method_71048();
         this.backgroundRenderer.method_3317(context, this.field_22789, this.field_22790, true);
         int bgColor = Integer.MIN_VALUE;
         context.method_25294(this.field_3218.method_46426(), this.field_3218.method_46427(), this.field_3218.method_55442(), this.field_3218.method_55443(), bgColor);
         context.method_25296(this.field_3218.method_46426(), this.field_3218.method_46427() - 4, this.field_3218.method_55442(), this.field_3218.method_46427(), 0, bgColor);
         context.method_25296(this.field_3218.method_46426(), this.field_3218.method_55443(), this.field_3218.method_55442(), this.field_3218.method_55443() + 4, bgColor, 0);
      }

   }

   @Inject(
      method = {"method_19940"},
      at = {@At("TAIL")}
   )
   public void onSelect(class_34 levelSummary, CallbackInfo ci) {
      if (this.backgroundRenderer != null) {
         this.backgroundRenderer = null;
         if (levelSummary != null) {
            File panorama = levelSummary.method_27020().getParent().resolve("panorama").toFile();
            if (panorama.exists()) {
               loaderExecutor.submit(() -> {
                  CactusCubeMapRenderer renderer = new CactusCubeMapRenderer(panorama);
                  this.backgroundRenderer = new class_766(renderer);
               });
            }

         }
      }
   }

   public String getKey() {
      return "gui.screen.selectWorld";
   }
}
