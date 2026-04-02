package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.gui.screen.ITranslatable;
import com.dwarslooper.cactus.client.gui.screen.impl.ArmorStandEditorScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.HeadsScreen;
import com.dwarslooper.cactus.client.gui.screen.window.TextInputWindow;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.systems.ItemGroupSystem;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.InputUtilWrapper;
import com.dwarslooper.cactus.client.util.game.ItemUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.class_10799;
import net.minecraft.class_11908;
import net.minecraft.class_1661;
import net.minecraft.class_1761;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_4185;
import net.minecraft.class_465;
import net.minecraft.class_481;
import net.minecraft.class_5250;
import net.minecraft.class_7077;
import net.minecraft.class_7706;
import net.minecraft.class_7919;
import net.minecraft.class_481.class_483;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {class_481.class},
   priority = 1200
)
public abstract class CreativeModeInventoryScreenMixin extends class_465<class_483> implements ITranslatable {
   @Shadow
   private static class_1761 field_2896;
   @Shadow
   private boolean field_2888;
   @Unique
   private static final class_2960 background = class_2960.method_60654("popup/background");
   @Unique
   private class_339 toolsButton;
   @Unique
   private static boolean toolsExpanded = false;
   @Unique
   private final List<class_4185> tools = new ArrayList();
   @Unique
   private final boolean enabled = ContentPackManager.get().isEnabled(ContentPackManager.get().ofId("creative_tools"));
   @Unique
   private int maxToolWith;

   public CreativeModeInventoryScreenMixin(class_483 handler, class_1661 inventory, class_2561 title) {
      super(handler, inventory, title);
   }

   @Shadow
   protected abstract void method_2466(class_1761 var1);

   @Inject(
      method = {"method_25426"},
      at = {@At("TAIL")}
   )
   public void init(CallbackInfo ci) {
      if (this.enabled) {
         this.toolsButton = (class_339)this.method_37063(new CButtonWidget(this, 4, this.field_22790 - 36, 20, 20, class_2561.method_43473(), (button) -> {
            toolsExpanded = !toolsExpanded;
            this.updateButtons();
         }) {
            public boolean method_25370() {
               return false;
            }
         });
         this.toolsButton.method_47400(class_7919.method_47407(class_2561.method_43470(this.getTranslatableElement("tools", new Object[0]))));
         this.tools.clear();
         this.addTool(class_2561.method_43470(this.getTranslatableElement("head", new Object[0])), (widget) -> {
            CactusConstants.mc.method_1507((new TextInputWindow("none", "Get Player head")).setPlaceholder("Username").range(3, 16).allowEmptyText(false).onSubmit((s) -> {
               ItemUtils.giveItem(ItemUtils.headOfName(s));
            }));
         });
         this.addTool(class_2561.method_43470(this.getTranslatableElement("maps", new Object[0])), (widget) -> {
         });
         this.addTool(class_2561.method_43470(this.getTranslatableElement("ast", new Object[0])), (widget) -> {
            CactusConstants.mc.method_1507(new ArmorStandEditorScreen(CactusConstants.mc.field_1755));
         });
         if ((Boolean)CactusSettings.get().experiments.get()) {
            this.addTool(class_2561.method_43470("Head Browser"), (widget) -> {
               CactusConstants.mc.method_1507(new HeadsScreen());
            });
         }

         if (CactusConstants.APRILFOOLS) {
            this.method_37063(class_4185.method_46430(class_2561.method_43470("ඞ"), (x$0) -> {
               this.method_37066(x$0);
            }).method_46434(22, this.field_22790 - 26, 20, 20).method_46431());
         }

         this.updateButtons();
      }
   }

   @Inject(
      method = {"method_25394"},
      at = {@At("HEAD")}
   )
   public void onRender(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (ItemGroupSystem.hdbGroups.contains(field_2896)) {
         context.method_27534(this.field_22793, class_2561.method_43470("Head Database by minecraft-heads.com"), this.field_22789 / 2, 20, Color.WHITE.getRGB());
      }

      if (this.enabled) {
         if (toolsExpanded) {
            int var10001 = this.tools.size();
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            int i = 12 + var10001 * (9 + 2);
            context.method_52706(class_10799.field_56883, background, 4, this.field_22790 - 40 - i + 2, 4 + this.maxToolWith + 12, i);
         }

         class_327 var7 = CactusConstants.mc.field_1772;
         class_5250 var10002 = class_2561.method_43470(this.getTranslatableElement("editNbt", new Object[0]));
         int var10004 = this.field_22790;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_27535(var7, var10002, 4, var10004 - 9 - 4, Color.GRAY.getRGB());
      }
   }

   @Inject(
      method = {"method_25394"},
      at = {@At("TAIL")}
   )
   public void renderButtonItem(class_332 context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
      if (this.enabled) {
         context.method_51445(class_1802.field_8688.method_7854(), 6, this.field_22790 - 36 + 2);
      }
   }

   @Inject(
      method = {"method_25404"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1761;method_47312()Lnet/minecraft/class_1761$class_7916;"
)},
      cancellable = true
   )
   public void keyEvent(class_11908 input, CallbackInfoReturnable<Boolean> cir) {
      if (input.method_74228() == 70 && InputUtilWrapper.hasControlDown()) {
         this.field_2888 = true;
         this.method_2466(class_7706.method_47344());
         cir.setReturnValue(true);
      }

   }

   public String getKey() {
      return "gui.screen.creative";
   }

   @Unique
   private void updateButtons() {
      this.tools.forEach((buttonWidget) -> {
         buttonWidget.field_22764 = toolsExpanded;
      });
      this.method_25396().stream().filter((element) -> {
         return !element.equals(this.toolsButton);
      }).forEach((element) -> {
         if (element instanceof class_339) {
            class_339 w = (class_339)element;
            if (w.method_46426() + w.method_25368() > this.toolsButton.method_46426() - 20 && w.method_46426() < this.toolsButton.method_46426() + 20 && w.method_46427() + w.method_25364() > this.toolsButton.method_46427() && w.method_46427() < this.toolsButton.method_46427() + 20) {
               w.method_48229(this.toolsButton.method_46426() + 20 + 4, this.toolsButton.method_46427());
            }
         }

      });
   }

   @Unique
   private void addTool(class_2561 name, Consumer<class_7077> clickCallback) {
      int var10000 = this.field_22790 - 46;
      int var10002 = this.tools.size();
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      int i = var10000 - (8 + var10002 * (9 + 2));
      int var10004 = CactusConstants.mc.field_1772.method_27525(name);
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      class_7077 widget = new class_7077(12, i, var10004, 9 + 2, name, (button) -> {
         clickCallback.accept((class_7077)button);
      }, CactusConstants.mc.field_1772);
      this.tools.add(widget);
      this.method_37063(widget);
      this.maxToolWith = Math.max(widget.method_25368(), this.maxToolWith);
   }
}
