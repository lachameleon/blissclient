package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.feature.content.ContentPackDependent;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.ServerWidget;
import com.dwarslooper.cactus.client.feature.modules.render.StreamerMode;
import com.dwarslooper.cactus.client.gui.widget.CServerEntryWidget;
import com.dwarslooper.cactus.client.mixins.accessor.JoinMultiplayerScreenAccessor;
import com.dwarslooper.cactus.client.util.mixinterface.IMultiplayerAddIndexImpl;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.class_1074;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_422;
import net.minecraft.class_4267;
import net.minecraft.class_437;
import net.minecraft.class_500;
import net.minecraft.class_5676;
import net.minecraft.class_642;
import net.minecraft.class_4267.class_504;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@ContentPackDependent("server_widget")
@Mixin({class_422.class})
public abstract class ManageServerScreenMixin extends class_437 {
   @Shadow
   private class_4185 field_2472;
   @Shadow
   @Final
   private class_437 field_21791;
   @Shadow
   @Final
   private class_642 field_2469;
   @Shadow
   private class_342 field_2474;
   @Shadow
   private class_342 field_2471;
   @Unique
   private String lastAutoName;
   @Unique
   public CServerEntryWidget entryWidget;
   @Unique
   private String lastAddress = "";
   @Unique
   private final boolean enabled = ((ServerWidget)ModuleManager.get().get(ServerWidget.class)).shouldAddTo(this);
   @Unique
   private static final long ANIM_DURATION_NS = 180000000L;
   @Unique
   private int shiftTarget = 0;
   @Unique
   private int lastAppliedShift = 0;
   @Unique
   private float shiftCurrent = 0.0F;
   @Unique
   private float animFrom = 0.0F;
   @Unique
   private float animTo = 0.0F;
   @Unique
   private long animStartNs = 0L;
   @Unique
   private boolean animating = false;
   @Unique
   private boolean initialLaidOut = false;
   @Unique
   private static final int TOP_SAFE = 40;
   @Unique
   private static final int MARGIN = 8;
   @Unique
   private int baseTopY = 0;
   @Unique
   private boolean shiftLockedDown = false;

   @Shadow
   protected abstract void method_36223();

   protected ManageServerScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At("TAIL")}
   )
   private void cactus$initTail(CallbackInfo ci) {
      if (this.enabled) {
         if (this.entryWidget == null) {
            this.entryWidget = new CServerEntryWidget(0, 0, 100, 32, (class_500)this.field_21791);
            this.method_37063(this.entryWidget);
         }

         if (this.field_2469.field_3761.isEmpty()) {
            this.lastAutoName = this.field_2471 != null ? this.field_2471.method_1882() : null;
         }

         if (this.field_2471 != null && this.field_2474 != null) {
            this.baseTopY = Math.min(this.field_2471.method_46427(), this.field_2474.method_46427());
            ((StreamerMode)ModuleManager.get().get(StreamerMode.class)).hiderWidget(this.field_2474);
         }

         this.bindAddressChangedListener();
         this.updatePreviewVisibility(true);
         this.forceApplyCurrentShift();
         this.alignPreviewToFields();
         this.updateAutoName();
         this.buildAddAtSelectorIfPossible();
         this.initialLaidOut = true;
      }
   }

   @Inject(
      method = {"method_25394"},
      at = {@At("HEAD")}
   )
   private void cactus$animateBeforeRender(class_332 ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (this.enabled && this.field_2474 != null && this.field_2471 != null && this.entryWidget != null) {
         if (this.animating) {
            long now = System.nanoTime();
            float t = (float)Math.min(1.0D, (double)(now - this.animStartNs) / 1.8E8D);
            float eased = easeOutCubic(t);
            this.shiftCurrent = this.animFrom + (this.animTo - this.animFrom) * eased;
            if (t >= 1.0F) {
               this.shiftCurrent = this.animTo;
               this.animating = false;
            }
         }

         int desiredShift = Math.round(this.shiftCurrent);
         int deltaShift = desiredShift - this.lastAppliedShift;
         if (deltaShift != 0) {
            int minFieldYBefore = Math.min(this.field_2471.method_46427(), this.field_2474.method_46427());
            this.field_2471.method_46419(this.field_2471.method_46427() + deltaShift);
            this.field_2474.method_46419(this.field_2474.method_46427() + deltaShift);
            List<? extends class_364> kids = this.method_25396();
            if (kids != null && !kids.isEmpty()) {
               Iterator var10 = kids.iterator();

               while(var10.hasNext()) {
                  class_364 e = (class_364)var10.next();
                  if (e instanceof class_339) {
                     class_339 w = (class_339)e;
                     if (w != this.entryWidget && w != this.field_2471 && w != this.field_2474 && w.method_46427() >= minFieldYBefore) {
                        w.method_46419(w.method_46427() + deltaShift);
                     }
                  }
               }
            }

            this.lastAppliedShift += deltaShift;
         }

         this.alignPreviewToFields();
      }
   }

   @ModifyArgs(
      method = {"method_25394"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_27535(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)V",
   ordinal = 0
),
      require = 0
   )
   private void cactus$shiftNameLabelY(Args args) {
      if (this.field_2471 != null) {
         int newY = this.field_2471.method_46427() - 12;
         args.set(3, newY);
      }

   }

   @ModifyArgs(
      method = {"method_25394"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_27535(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)V",
   ordinal = 1
),
      require = 0
   )
   private void cactus$shiftAddressLabelY(Args args) {
      if (this.field_2474 != null) {
         int newY = this.field_2474.method_46427() - 12;
         args.set(3, newY);
      }

   }

   @Inject(
      method = {"method_25410"},
      at = {@At("TAIL")}
   )
   private void cactus$onResize(int width, int height, CallbackInfo ci) {
      if (this.enabled) {
         if (this.field_2471 != null && this.field_2474 != null) {
            this.baseTopY = Math.min(this.field_2471.method_46427(), this.field_2474.method_46427()) - this.lastAppliedShift;
         }

         this.alignPreviewToFields();
      }
   }

   @Unique
   private void bindAddressChangedListener() {
      if (this.field_2474 != null) {
         this.field_2474.method_1863((s) -> {
            this.onAddressTextChanged(s);
            this.method_36223();
         });
      }
   }

   @Unique
   private void onAddressTextChanged(String newText) {
      if (this.enabled) {
         if (newText == null) {
            newText = "";
         }

         if (!newText.equalsIgnoreCase(this.lastAddress)) {
            this.lastAddress = newText;
            this.updatePreviewVisibility(false);
            this.updateAutoName();
         }
      }
   }

   @Unique
   private void alignPreviewToFields() {
      if (this.enabled && this.field_2474 != null && this.field_2471 != null && this.entryWidget != null) {
         this.entryWidget.method_46421(this.field_2474.method_46426() + (this.field_2474.method_25368() - 300) / 2);

         try {
            this.entryWidget.method_25358(300);
         } catch (Throwable var3) {
         }

         int previewH = this.entryWidget.method_25364();
         int previewY = Math.max(40, this.baseTopY - previewH - 8);
         this.entryWidget.method_46419(previewY);
      }
   }

   @Unique
   private void buildAddAtSelectorIfPossible() {
      class_437 var2 = this.field_21791;
      if (var2 instanceof class_500) {
         class_500 mps = (class_500)var2;
         if (this.field_2469.field_3761.isEmpty()) {
            IMultiplayerAddIndexImpl addAccess = (IMultiplayerAddIndexImpl)mps.method_2529();
            class_4267 serverListWidget = ((JoinMultiplayerScreenAccessor)mps).getServerSelectionList();
            if (serverListWidget == null) {
               return;
            }

            class_504 entry = (class_504)serverListWidget.method_25334();
            int selected = entry != null ? serverListWidget.method_25396().indexOf(entry) : -1;
            String def = "bottom";
            LinkedHashMap<String, Integer> options = new LinkedHashMap();
            options.put("top", 0);
            if (selected >= 0) {
               options.put("above_selected", selected);
               options.put("below_selected", selected + 1);
            }

            options.put(def, Integer.MAX_VALUE);
            addAccess.cactus$setAddIndex((Integer)options.get(def));
            int x = this.field_22789 / 2 - 100;
            int y = Math.max(this.field_2471 != null ? this.field_2471.method_46427() + this.field_2471.method_25364() + 8 : 120, this.field_2474 != null ? this.field_2474.method_46427() + this.field_2474.method_25364() + 8 : 120);
            int w = 200;
            class_5676<String> optionsWidget = class_5676.method_32606(this::getName, def).method_32620(options.keySet()).method_32617(x, y, w, 20, class_2561.method_43470("Add at"), (btn, val) -> {
               addAccess.cactus$setAddIndex((Integer)options.get(val));
            });
            this.method_37063(optionsWidget);
            return;
         }
      }

   }

   @Unique
   private void updatePreviewVisibility(boolean instant) {
      if (this.enabled && this.entryWidget != null && this.field_2474 != null) {
         String input = this.field_2474.method_1882();
         boolean looksLikeAddress = input != null && !input.isEmpty() && (input.contains(".") || input.equalsIgnoreCase("localhost") || input.startsWith("localhost:"));
         if (looksLikeAddress) {
            this.shiftLockedDown = true;
         }

         boolean active = looksLikeAddress || this.shiftLockedDown;
         this.entryWidget.field_22764 = active || this.animating || this.shiftCurrent > 0.0F;
         if (looksLikeAddress) {
            this.entryWidget.update(input, input);
         }

         int desired = active ? this.entryWidget.method_25364() + 8 : 0;
         if (desired != this.shiftTarget) {
            this.shiftTarget = desired;
            if (this.initialLaidOut && !instant) {
               this.animating = true;
               this.animFrom = this.shiftCurrent;
               this.animTo = (float)this.shiftTarget;
               this.animStartNs = System.nanoTime();
            } else {
               this.animating = false;
               this.shiftCurrent = (float)this.shiftTarget;
            }
         }

      }
   }

   @Unique
   private void forceApplyCurrentShift() {
      int desired = Math.round(this.shiftCurrent);
      int delta = desired - this.lastAppliedShift;
      if (delta != 0) {
         int minFieldYBefore = Math.min(this.field_2471.method_46427(), this.field_2474.method_46427());
         this.field_2471.method_46419(this.field_2471.method_46427() + delta);
         this.field_2474.method_46419(this.field_2474.method_46427() + delta);
         List<? extends class_364> kids = this.method_25396();
         if (kids != null && !kids.isEmpty()) {
            Iterator var5 = kids.iterator();

            while(var5.hasNext()) {
               class_364 e = (class_364)var5.next();
               if (e instanceof class_339) {
                  class_339 w = (class_339)e;
                  if (w != this.entryWidget && w != this.field_2471 && w != this.field_2474 && w.method_46427() >= minFieldYBefore) {
                     w.method_46419(w.method_46427() + delta);
                  }
               }
            }
         }

         this.lastAppliedShift += delta;
      }
   }

   @Unique
   private void updateAutoName() {
      if (this.field_2471 != null && this.field_2474 != null) {
         String address = this.field_2474.method_1882();
         String name = this.generateServerName(address);
         if (this.lastAutoName != null && this.lastAutoName.equals(this.field_2471.method_1882())) {
            this.field_2471.method_1852(name);
         }

         this.lastAutoName = name;
      }
   }

   @Unique
   private String generateServerName(String address) {
      if (address != null && !address.isEmpty()) {
         Pattern pattern = Pattern.compile("^([a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)\\.(\\w+)$");
         Matcher matcher = pattern.matcher(address);
         if (matcher.find()) {
            String domain = matcher.group(1);
            String[] domainParts = domain.split("\\.");
            int domainLength = domainParts.length;
            String serverName = this.capitalize(domainParts[domainLength - 1]);
            if (domainLength > 1) {
               String[] subdomainParts = (String[])Arrays.copyOfRange(domainParts, 0, domainLength - 1);
               String subdomainText = this.getSubdomainText(subdomainParts);
               if (!subdomainText.isEmpty()) {
                  serverName = serverName + " (" + subdomainText + ")";
               }
            }

            return serverName;
         } else {
            return address;
         }
      } else {
         return class_1074.method_4662("selectServer.defaultName", new Object[0]);
      }
   }

   @Unique
   private String capitalize(String text) {
      if (text != null && !text.isEmpty()) {
         String var10000 = text.substring(0, 1).toUpperCase();
         return var10000 + text.substring(1).toLowerCase();
      } else {
         return text;
      }
   }

   @Unique
   private String getSubdomainText(String[] subdomainParts) {
      List<String> skipSubs = Arrays.asList("play", "mc");
      return (String)Arrays.stream(subdomainParts).filter((part) -> {
         return !skipSubs.contains(part.toLowerCase());
      }).map(this::capitalize).collect(Collectors.joining(" "));
   }

   @Unique
   private class_2561 getName(String s) {
      return class_2561.method_43471("gui.screen.addServer.mode." + s);
   }

   @Unique
   private static float easeOutCubic(float t) {
      float inv = 1.0F - t;
      return 1.0F - inv * inv * inv;
   }
}
