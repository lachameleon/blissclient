package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ImageUtils;
import java.awt.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_3262;
import net.minecraft.class_7367;
import net.minecraft.class_8518;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_8518.class})
public abstract class IconSetMixin {
   @Inject(
      method = {"method_51418"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void getIcons(class_3262 resourcePack, CallbackInfoReturnable<List<class_7367<InputStream>>> cir) {
      if ((Boolean)CactusSettings.get().cactusWindowIconAndTitle.get()) {
         cir.setReturnValue(this.getMultiIcons(16, 32, 48, 128, 256));
      }

   }

   @Inject(
      method = {"method_51420"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void getIconsMac(class_3262 resourcePack, CallbackInfoReturnable<class_7367<InputStream>> cir) {
      if ((Boolean)CactusSettings.get().cactusWindowIconAndTitle.get()) {
         class_7367<InputStream> inputSupplier = () -> {
            return this.modifyColorIfSet("icon.icns");
         };
         cir.setReturnValue(inputSupplier);
      }

   }

   @Unique
   private List<class_7367<InputStream>> getMultiIcons(int... sizes) {
      List<class_7367<InputStream>> icons = new ArrayList();
      int[] var3 = sizes;
      int var4 = sizes.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int size = var3[var5];
         icons.add(() -> {
            return this.modifyColorIfSet("icon_%sx%s.png".formatted(new Object[]{size, size}));
         });
      }

      return icons;
   }

   @Unique
   private InputStream modifyColorIfSet(String iconFile) {
      Color iconColor = this.getIconColor();
      String path = "/assets/cactus/icon/" + iconFile;
      return iconColor != null ? ImageUtils.modifyInputFromResource(path, "PNG", (image) -> {
         return ImageUtils.modifyCactusColorSpace(image, iconColor, 50);
      }) : this.getClass().getResourceAsStream(path);
   }

   @Unique
   @Nullable
   private Color getIconColor() {
      return CactusConstants.DEVBUILD ? (new Color(-13971981)).brighter() : null;
   }
}
