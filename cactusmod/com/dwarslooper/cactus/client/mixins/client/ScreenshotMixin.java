package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.CRunnableClickEvent;
import com.dwarslooper.cactus.client.feature.commands.TestCommand;
import com.dwarslooper.cactus.client.gui.screen.impl.ScreenshotViewerScreen;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.dwarslooper.cactus.client.util.generic.CopyImageToClipBoard;
import com.dwarslooper.cactus.client.util.networking.ImgurHelper;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1011;
import net.minecraft.class_2561;
import net.minecraft.class_276;
import net.minecraft.class_318;
import net.minecraft.class_437;
import net.minecraft.class_2558.class_10607;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_318.class})
public abstract class ScreenshotMixin {
   @Shadow
   private static File method_1660(File directory) {
      return null;
   }

   @Shadow
   public static void method_1663(class_276 framebuffer, Consumer<class_1011> callback) {
   }

   @Inject(
      method = {"method_22690(Ljava/io/File;Ljava/lang/String;Lnet/minecraft/class_276;ILjava/util/function/Consumer;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void saveScreenshot(File gameDirectory, String fileName, class_276 framebuffer, int downscaleFactor, Consumer<class_2561> messageReceiver, CallbackInfo ci) {
      if (!TestCommand.DISALLOW_ASYNC_SCREENSHOT_SAVING && !FabricLoader.getInstance().isModLoaded("noriskclient")) {
         method_1663(framebuffer, (image) -> {
            File screenshotsDir = new File(gameDirectory, "screenshots");
            screenshotsDir.mkdirs();
            File screenshotFile = method_1660(screenshotsDir);
            CopyImageToClipBoard.getInstance().setLastScreenshot(screenshotFile);
            CompletableFuture.runAsync(() -> {
               try {
                  if (screenshotFile == null) {
                     throw new IllegalStateException("Screenshot file is null");
                  }

                  image.method_4325(screenshotFile);
                  messageReceiver.accept(class_2561.method_43470("§7Screenshot saved as " + screenshotFile.getName() + "!\n").method_10852(class_2561.method_43470("§a(Open File)").method_27694((style) -> {
                     return style.method_10958(new class_10607(screenshotFile.getAbsolutePath()));
                  })).method_27693(" ").method_10852(class_2561.method_43470("§a(Upload)").method_27694((style) -> {
                     return style.method_10958(new CRunnableClickEvent(() -> {
                        ChatUtils.info((class_2561)class_2561.method_43471("gui.screen.screenshot_view.uploading"));
                        ImgurHelper.uploadImage(screenshotFile).whenComplete((obj, error) -> {
                           if (error == null) {
                              String link = obj.get("link").getAsString();
                              CactusClient.getLogger().info("Screenshot uploaded to {} (id={},deletehash={})", new Object[]{link, obj.get("id"), obj.get("deletehash")});
                              ChatUtils.info((class_2561)class_2561.method_43471("gui.screen.screenshot_view.uploadSuccess"));
                              CactusConstants.mc.field_1774.method_1455(link);
                           } else {
                              ChatUtils.error((class_2561)class_2561.method_43471("gui.screen.screenshot_view.uploadFailed"));
                              CactusClient.getLogger().error("Failed to upload image", error);
                           }

                        });
                     }));
                  })).method_27693(" ").method_10852(class_2561.method_43470("§a(Copy)").method_27694((style) -> {
                     return style.method_10958(new CRunnableClickEvent(() -> {
                        boolean copied = CopyImageToClipBoard.getInstance().copyImage(screenshotFile);
                        if (copied) {
                           ChatUtils.info((class_2561)class_2561.method_43471("gui.screen.screenshot_view.copySuccess"));
                        } else {
                           ChatUtils.error((class_2561)class_2561.method_43471("gui.screen.screenshot_view.copyFailed"));
                        }

                     }));
                  })).method_27693(" ").method_10852(class_2561.method_43470("§a(View)").method_27694((style) -> {
                     return style.method_10958(new CRunnableClickEvent(() -> {
                        if (screenshotFile.exists()) {
                           CactusConstants.mc.method_1507(new ScreenshotViewerScreen((class_437)null, screenshotFile));
                        } else {
                           ChatUtils.error("Screenshot file not found!");
                        }

                     }));
                  })));
               } catch (Exception var7) {
                  CactusClient.getLogger().error("Couldn't save screenshot", var7);
                  messageReceiver.accept(class_2561.method_43469("screenshot.failure", new Object[]{var7.getMessage()}));
               } finally {
                  image.close();
               }

            });
         });
         ci.cancel();
      }
   }
}
