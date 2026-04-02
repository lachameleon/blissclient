package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.screen.ITranslatable;
import com.dwarslooper.cactus.client.gui.toast.ToastSystem;
import com.dwarslooper.cactus.client.gui.toast.internal.GenericToast;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CVisualNbtEditorWidget;
import com.dwarslooper.cactus.client.mixins.accessor.ButtonAccessor;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.generic.ImageUtils;
import com.dwarslooper.cactus.client.util.generic.ScreenUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;
import javax.imageio.ImageIO;
import net.minecraft.class_1802;
import net.minecraft.class_2487;
import net.minecraft.class_2505;
import net.minecraft.class_2507;
import net.minecraft.class_2561;
import net.minecraft.class_2588;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_437;
import net.minecraft.class_5218;
import net.minecraft.class_524;
import net.minecraft.class_5244;
import net.minecraft.class_32.class_5143;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_524.class})
public abstract class EditWorldScreenMixin extends class_437 implements ITranslatable {
   @Shadow
   @Final
   private class_5143 field_23777;
   @Shadow
   @Final
   private BooleanConsumer field_3169;
   @Unique
   private boolean forceListReload = false;
   @Unique
   private boolean failSet = false;

   public EditWorldScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At("TAIL")}
   )
   public void init(CallbackInfo ci) {
      File levelStorage = this.field_23777.method_27010(class_5218.field_24188).toAbsolutePath().normalize().toFile();
      File savesDirectory = CactusConstants.mc.method_1586().method_19636().toFile().getAbsoluteFile();
      class_339 resetIconButton = ScreenUtils.getButton(this, "selectWorld.edit.resetIcon");
      class_339 backupButton = ScreenUtils.getButton(this, "selectWorld.edit.backup");
      if (resetIconButton != null && backupButton != null) {
         ((ButtonAccessor)ScreenUtils.getButton(this, ((class_2588)class_5244.field_24335.method_10851()).method_11022())).setOnPress((button) -> {
            this.field_3169.accept(this.forceListReload);
         });
         int buttonWidth = resetIconButton.method_25368() / 2 - 2;
         int x = resetIconButton.method_46426();
         resetIconButton.method_25358(buttonWidth);
         backupButton.method_25358(buttonWidth);
         this.method_37063(new CButtonWidget(x + buttonWidth + 4, resetIconButton.method_46427(), buttonWidth, 20, class_2561.method_43470(this.getTranslatableElement("icon", new Object[0])), (button) -> {
            PointerBuffer filter = Utils.createFileTypeFilter("*.png");
            String path = TinyFileDialogs.tinyfd_openFileDialog("Select Icon", (CharSequence)null, filter, this.getTranslatableElement("icon.types", new Object[0]), false);
            if (path != null) {
               File file = new File(path);

               try {
                  BufferedImage image = ImageIO.read(file);
                  BufferedImage resized = ImageUtils.resize(image, 64, 64);
                  ImageIO.write(resized, "png", new File(levelStorage, class_5218.field_38979.method_27423()));
                  ImageIO.write(resized, "png", new File(levelStorage, class_5218.field_38979.method_27423().replace(".png", "-def.png")));
               } catch (IOException var8) {
                  CactusClient.getLogger().error("Failed to change world icon", var8);
                  ToastSystem.displayMessage("§cError", var8.getMessage());
               }
            }

         }));
         this.method_37063(new CButtonWidget(x + buttonWidth + 4, backupButton.method_46427(), buttonWidth, 20, class_2561.method_43470("Make Copy"), (button) -> {
            int copy = 1;

            String newDirectoryName;
            for(newDirectoryName = levelStorage.getName() + " "; Files.exists(savesDirectory.toPath().resolve(newDirectoryName + copy), new LinkOption[0]); ++copy) {
            }

            try {
               String name = newDirectoryName + copy;
               File targetDir = new File(savesDirectory, name);
               FileUtils.copyDirectory(levelStorage, targetDir, FileFilterUtils.suffixFileFilter(".lock").or(FileFilterUtils.nameFileFilter("uid.dat")).negate());
               File levelDat = new File(targetDir, "level.dat");
               class_2487 compound = class_2507.method_30613(levelDat.toPath(), class_2505.method_53898());
               class_2487 data = (class_2487)compound.method_10562("Data").orElse((Object)null);
               if (data != null) {
                  data.method_10544("LastPlayed", System.currentTimeMillis());
                  String var10002 = data.method_68564("LevelName", "unknown");
                  data.method_10582("LevelName", var10002 + " " + copy);
                  class_2507.method_30614(compound, levelDat.toPath());
                  GenericToast cToast = (new GenericToast(this.getTranslatableElement("copy.success", new Object[0]), class_2561.method_43469("selectWorld.edit.backupSize", new Object[]{Files.size(levelStorage.toPath()) / 1000L}).getString())).setStyle(GenericToast.Style.SYSTEM);
                  CactusConstants.mc.method_1566().method_1999(cToast);
                  this.forceListReload = true;
               }
            } catch (IOException var12) {
               CactusClient.getLogger().error("Failed to create copy of world", var12);
               GenericToast cToastx = (new GenericToast("§cCopy Failed", "A copy of you world could not be created")).setStyle(GenericToast.Style.SYSTEM);
               CactusConstants.mc.method_1566().method_1999(cToastx);
            }

            button.field_22763 = false;
         }));
         this.method_37063(new CButtonWidget(4, 4, 100, 20, class_2561.method_43470("Edit §elevel.dat"), (button) -> {
            CactusConstants.mc.method_1507(new CScreen(this, "editWorld") {
               private CVisualNbtEditorWidget instance;

               public void method_25426() {
                  super.method_25426();
                  Path levelPath = (new File(levelStorage, "level.dat")).toPath();
                  if (this.instance == null) {
                     class_2487 level;
                     try {
                        level = class_2507.method_30613(levelPath, class_2505.method_53898());
                     } catch (IOException var4) {
                        button.field_22763 = false;
                        this.method_25419();
                        return;
                     }

                     this.method_37063(this.instance = new CVisualNbtEditorWidget(this, 50, 50, this.field_22789 - 100, this.field_22790 - 100, level, (nbt) -> {
                        try {
                           class_2507.method_30614(nbt, levelPath);
                           return null;
                        } catch (IOException var3) {
                           ToastSystem.displayMessage("Save failed", "Can't write to level.dat", class_1802.field_8077);
                           return var3;
                        }
                     }));
                  } else {
                     this.instance.method_25358(this.field_22789 - 100);
                     this.instance.method_53533(this.field_22790 - 100);
                     this.method_37063(this.instance);
                  }

               }
            });
         }));
      } else {
         this.failSet = true;
      }
   }

   @Inject(
      method = {"method_25394"},
      at = {@At("TAIL")}
   )
   public void render(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (this.failSet) {
         class_327 var10001 = this.field_22793;
         int var10004 = this.field_22790;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_25303(var10001, "Another mod prevents cactus from correctly modifying this screen.", 4, var10004 - 9 * 2 - 4, -1);
         var10001 = this.field_22793;
         var10004 = this.field_22790;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_25303(var10001, "We can't provide features like copys or icon and level editing.", 4, var10004 - 9 - 2, -1);
      }

   }

   @Unique
   public String getKey() {
      return "gui.screen.editWorld";
   }
}
