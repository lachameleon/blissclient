package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.InventoryTabs;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CCustomRenderWidget;
import com.dwarslooper.cactus.client.render.RenderHelper;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChestUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.class_11909;
import net.minecraft.class_1268;
import net.minecraft.class_1703;
import net.minecraft.class_1723;
import net.minecraft.class_1799;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_2815;
import net.minecraft.class_2885;
import net.minecraft.class_332;
import net.minecraft.class_3545;
import net.minecraft.class_3965;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_2183.class_2184;
import net.minecraft.class_481.class_483;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_465.class})
public abstract class AbstractContainerScreenMixin<T extends class_1703> extends class_437 {
   @Shadow
   @Final
   protected T field_2797;
   @Unique
   private int clickIndex = -1;
   @Unique
   private final AtomicBoolean found = new AtomicBoolean(false);

   protected AbstractContainerScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At("TAIL")}
   )
   public void init(CallbackInfo ci) {
      if (((InventoryTabs)ModuleManager.get().get(InventoryTabs.class)).active()) {
         class_239 hit = CactusConstants.mc.field_1765;
         if (hit instanceof class_3965) {
            assert CactusConstants.mc.field_1687 != null;

            class_2680 state = CactusConstants.mc.field_1687.method_8320(((class_3965)hit).method_17777());
            if (state != null && InventoryTabs.isContainer(state.method_26204())) {
               if (!(this.field_2797 instanceof class_483) && !(this.field_2797 instanceof class_1723)) {
                  CButtonWidget nextPageButton = (CButtonWidget)this.method_37063(new CButtonWidget(this.field_22789 / 2 + 12, 10, 12, 18, class_2561.method_43470(">"), (button) -> {
                     ++InventoryTabs.listIndex;
                     if (InventoryTabs.listIndex > InventoryTabs.blockPosList.size() - 1) {
                        InventoryTabs.listIndex = 0;
                     }

                     if (InventoryTabs.blockPosList.size() > 1) {
                        this.switchToIndex();
                     }

                  }));
                  CButtonWidget previousPageButton = (CButtonWidget)this.method_37063(new CButtonWidget(this.field_22789 / 2 - 23, 10, 12, 18, class_2561.method_43470("<"), (button) -> {
                     --InventoryTabs.listIndex;
                     if (InventoryTabs.listIndex < 0) {
                        InventoryTabs.listIndex = InventoryTabs.blockPosList.size() - 1;
                     }

                     if (InventoryTabs.blockPosList.size() > 1) {
                        this.switchToIndex();
                     }

                  }));
                  this.clickIndex = -1;
                  this.method_37063(new CCustomRenderWidget(this::renderTabs));
                  if (InventoryTabs.nextWasSwitch) {
                     InventoryTabs.nextWasSwitch = false;
                  } else {
                     this.initBlockList((class_3965)hit);
                  }

                  if (InventoryTabs.blockPosList.isEmpty()) {
                     nextPageButton.field_22763 = false;
                     previousPageButton.field_22763 = false;
                  }

               }
            }
         }
      }
   }

   @Inject(
      method = {"method_25402"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mouseClicked(class_11909 click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      if (click.method_74245() == 0 && this.click(click.comp_4798(), click.comp_4799())) {
         cir.cancel();
      }

   }

   @Unique
   private class_3545<Integer, List<class_1799>> generateTabData() {
      int newIndex = 0;
      List<class_1799> outA = new ArrayList();
      List<class_1799> outB = new ArrayList();
      int i = 0;

      for(Iterator var5 = InventoryTabs.blockList.iterator(); var5.hasNext(); ++i) {
         class_2248 block = (class_2248)var5.next();
         if (i == InventoryTabs.listIndex) {
            newIndex = outA.size();
            outA.add(new class_1799(block.method_8389(), 1));
         } else {
            this.found.set(false);
            (i > InventoryTabs.listIndex ? outB : outA).forEach((s) -> {
               if (s.method_7909() == block.method_8389()) {
                  s.method_7933(1);
                  this.found.set(true);
               }

            });
            if (!this.found.get()) {
               (i > InventoryTabs.listIndex ? outB : outA).add(new class_1799(block.method_8389(), 1));
            }
         }
      }

      outA.addAll(outB);
      return new class_3545(newIndex, outA);
   }

   @Unique
   private boolean renderTabs(class_332 context, int mouseX, int mouseY, float delta) {
      boolean ret = false;
      context.method_51448().pushMatrix();
      context.method_51448().translate(0.0F, 0.0F);
      class_3545<Integer, List<class_1799>> tabData = this.generateTabData();
      int listIndex = (Integer)tabData.method_15442();
      int x = listIndex == 0 ? this.field_22789 / 2 : this.field_22789 / 2 - 15 - listIndex * 20;
      int i = 0;
      Iterator var10 = ((List)tabData.method_15441()).iterator();

      while(true) {
         while(var10.hasNext()) {
            class_1799 stack = (class_1799)var10.next();
            if (this.renderUpperSingle(context, mouseX, mouseY, x, i, listIndex, stack)) {
               ret = true;
            }

            ++i;
            if (i != listIndex + 1 && i != listIndex) {
               x += 20;
            } else {
               x += 35;
            }
         }

         context.method_51448().popMatrix();
         return ret;
      }
   }

   @Unique
   private boolean renderUpperSingle(class_332 context, int mouseX, int mouseY, int x, int i, int listIndex, class_1799 stack) {
      boolean ret = false;
      if (i == this.clickIndex) {
         context.method_25294(x - 9, 6, x + 10, stack.method_7947() * 18 + 7, -11184811);
         RenderHelper.drawBorder(context, x - 9, 6, 20, stack.method_7947() * 18 + 2, -5592406);

         for(int a = 0; a < stack.method_7947(); ++a) {
            context.method_51427(stack, x - 7, 8 + a * 18);
            ret |= this.drawSubSingle(context, mouseX, mouseY, x - 9, 6 + a * 18);
         }
      } else {
         context.method_51427(stack, x - 7, i == listIndex ? 11 : 8);
         ret |= this.drawSubSingle(context, mouseX, mouseY, x - 9, i == listIndex ? 9 : 6);
         if (stack.method_7947() > 1) {
            context.method_51448().pushMatrix();
            context.method_51448().scale(0.75F, 0.75F);
            context.method_51448().translate(0.0F, 0.0F);
            context.method_25300(this.field_22793, String.valueOf(stack.method_7947()), (int)(((float)x + 1.5F) / 0.75F), 8, -1);
            context.method_51448().popMatrix();
         }
      }

      return ret;
   }

   @Unique
   private boolean drawSubSingle(class_332 context, int mouseX, int mouseY, int x, int y) {
      if (mouseX > x && mouseX < x + 19 && mouseY > y && mouseY < y + 19) {
         RenderHelper.drawBorder(context, x + 1, y + 1, 18, 18, -1);
         return true;
      } else {
         return false;
      }
   }

   @Unique
   private boolean click(double mouseX, double mouseY) {
      class_3545<Integer, List<class_1799>> tabData = this.generateTabData();
      int listIndex = (Integer)tabData.method_15442();
      int x = listIndex == 0 ? this.field_22789 / 2 : this.field_22789 / 2 - 15 - listIndex * 20;
      int i = 0;
      int i2 = 0;
      Iterator var10 = ((List)tabData.method_15441()).iterator();

      while(true) {
         while(var10.hasNext()) {
            class_1799 stack = (class_1799)var10.next();
            if (this.clickNestedSingle(mouseX, mouseY, x, i, i2, listIndex, stack)) {
               return true;
            }

            ++i;
            i2 += stack.method_7947();
            if (i != listIndex + 1 && i != listIndex) {
               x += 20;
            } else {
               x += 35;
            }
         }

         return false;
      }
   }

   @Unique
   private boolean clickNestedSingle(double mouseX, double mouseY, int x, int i, int i2, int listIndex, class_1799 stack) {
      if (this.clickSubSingle(mouseX, mouseY, x - 9, i == listIndex ? 9 : 6, i != this.clickIndex && stack.method_7947() > 1 ? () -> {
         this.clickIndex = i;
      } : () -> {
         InventoryTabs.listIndex = i2;
         this.switchToIndex();
      })) {
         return true;
      } else if (i != this.clickIndex) {
         return false;
      } else {
         for(int a = 1; a < stack.method_7947(); ++a) {
            if (this.clickSubSingle(mouseX, mouseY, x - 9, 6 + a * 18, () -> {
               InventoryTabs.listIndex = i2 + a;
               this.switchToIndex();
            })) {
               return true;
            }
         }

         return false;
      }
   }

   @Unique
   private boolean clickSubSingle(double mouseX, double mouseY, int x, int y, Runnable onClick) {
      if (mouseX > (double)x && mouseX < (double)(x + 19) && mouseY > (double)y && mouseY < (double)(y + 19)) {
         onClick.run();
         return true;
      } else {
         return false;
      }
   }

   @Unique
   public void switchToIndex() {
      InventoryTabs.nextWasSwitch = true;
      class_2338 pos = (class_2338)InventoryTabs.blockPosList.get(InventoryTabs.listIndex);

      assert CactusConstants.mc.method_1562() != null;

      assert CactusConstants.mc.field_1724 != null;

      CactusConstants.mc.method_1562().method_52787(new class_2815(this.field_2797.field_7763));
      CactusConstants.mc.field_1724.method_5702(class_2184.field_9851, pos.method_46558());
      CactusConstants.mc.method_1562().method_52787(new class_2885(class_1268.field_5808, new class_3965(new class_243((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260()), class_2350.field_11033, pos, false), 1));
   }

   @Unique
   public void initBlockList(class_3965 hit) {
      class_2338 pos = hit.method_17777();
      InventoryTabs.corePos = pos;
      InventoryTabs.blockPosList.clear();
      InventoryTabs.blockList.clear();
      InventoryTabs.listIndex = 0;
      this.initSingleBlock(pos, new ArrayList());
   }

   @Unique
   private void initSingleBlock(class_2338 pos, List<class_2338> alreadyAdded) {
      if (!alreadyAdded.contains(pos)) {
         assert CactusConstants.mc.field_1687 != null;

         assert CactusConstants.mc.field_1724 != null;

         class_2680 state = CactusConstants.mc.field_1687.method_8320(pos);
         if (InventoryTabs.isContainer(state.method_26204())) {
            if (CactusConstants.mc.field_1724.method_33571().method_1022(pos.method_46558()) >= 5.0D) {
               return;
            }

            if (!ChestUtils.isDouble(CactusConstants.mc.field_1687, pos) || !InventoryTabs.blockPosList.contains(ChestUtils.getOtherChestBlockPos(CactusConstants.mc.field_1687, pos))) {
               InventoryTabs.blockList.add(state.method_26204());
               InventoryTabs.blockPosList.add(pos);
            }

            alreadyAdded.add(pos);
            this.initSingleBlock(pos.method_10081(class_2350.field_11036.method_62675()), alreadyAdded);
            this.initSingleBlock(pos.method_10081(class_2350.field_11033.method_62675()), alreadyAdded);
            this.initSingleBlock(pos.method_10081(class_2350.field_11043.method_62675()), alreadyAdded);
            this.initSingleBlock(pos.method_10081(class_2350.field_11034.method_62675()), alreadyAdded);
            this.initSingleBlock(pos.method_10081(class_2350.field_11035.method_62675()), alreadyAdded);
            this.initSingleBlock(pos.method_10081(class_2350.field_11039.method_62675()), alreadyAdded);
         }

      }
   }
}
