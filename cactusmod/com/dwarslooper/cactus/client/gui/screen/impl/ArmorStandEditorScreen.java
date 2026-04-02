package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.screen.window.TextInputWindow;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CIntSliderWidget;
import com.dwarslooper.cactus.client.systems.CactusSaves;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.game.ItemUtils;
import com.dwarslooper.cactus.client.util.generic.ColorUtils;
import java.awt.Color;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.class_10192;
import net.minecraft.class_11352;
import net.minecraft.class_11362;
import net.minecraft.class_11580;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1531;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2379;
import net.minecraft.class_2477;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_3532;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_481;
import net.minecraft.class_490;
import net.minecraft.class_5244;
import net.minecraft.class_5250;
import net.minecraft.class_8942;
import net.minecraft.class_9334;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class ArmorStandEditorScreen extends CScreen {
   private static Quaternionf ARMOR_STAND_ROTATION = (new Quaternionf()).rotationXYZ(0.0F, (float)Math.toRadians(180.0D), 3.1415927F);
   private static Vector3f V3F_ZERO = new Vector3f();
   public static boolean awaitingInventoryItemSelection = false;
   private static class_1531 armorStand;
   private static boolean reInit = true;
   private static boolean small;
   private static boolean lockSlots = false;
   private static final int SLOT_LOCK = 4144959;
   private static final class_5250 fileText;

   public ArmorStandEditorScreen(class_437 parent) {
      super("ast_editor");
      this.parent = parent;
      if (reInit) {
         armorStand = new class_1531(CactusConstants.mc.field_1687, 0.0D, 0.0D, 0.0D);
         armorStand.method_6913(true);
         armorStand.method_6907(true);
         armorStand.method_5665(class_2561.method_43473());
         armorStand.method_5880(true);
         armorStand.method_5673(class_1304.field_6169, ItemUtils.headOfName(CactusConstants.mc.method_1548().method_1676()));
         armorStand.method_5673(class_1304.field_6174, ItemUtils.dyeItem(new class_1799(class_1802.field_8577), ColorUtils.randomColor()));
         armorStand.method_5673(class_1304.field_6172, ItemUtils.dyeItem(new class_1799(class_1802.field_8570), ColorUtils.randomColor()));
         armorStand.method_5673(class_1304.field_6166, new class_1799(class_1802.field_22030));
         small = false;
         lockSlots = false;
         armorStand.method_6925(new class_2379(0.0F, 0.0F, 0.0F));
         armorStand.method_6910(new class_2379(0.0F, 0.0F, 0.0F));
         armorStand.method_6926(new class_2379(0.0F, 0.0F, 0.0F));
         armorStand.method_6909(new class_2379(0.0F, 0.0F, 0.0F));
      }

   }

   public ArmorStandEditorScreen(class_437 parent, class_1799 copyFrom) {
      this(parent);
      class_11580<class_1299<?>> data = (class_11580)copyFrom.method_58694(class_9334.field_49609);
      if (data != null) {
         data.method_72531(armorStand);
      }

   }

   public void method_25426() {
      super.method_25426();
      int spacing = this.field_22789 / 6;
      this.method_37063(new CIntSliderWidget(10, this.field_22790 - 6 - 18, spacing - 4, 16, this.option3d("head", "X"), 0, 360, this.sv(armorStand.method_6921().comp_3776()), (integer) -> {
         armorStand.method_6919(this.modifyRotation(armorStand.method_6921(), (a) -> {
            return new class_2379((float)integer, a.comp_3777(), a.comp_3778());
         }));
      }));
      this.method_37063(new CIntSliderWidget(10, this.field_22790 - 6 - 36, spacing - 4, 16, this.option3d("head", "Y"), 0, 360, this.sv(armorStand.method_6921().comp_3777()), (integer) -> {
         armorStand.method_6919(this.modifyRotation(armorStand.method_6921(), (a) -> {
            return new class_2379(a.comp_3776(), (float)integer, a.comp_3778());
         }));
      }));
      this.method_37063(new CIntSliderWidget(10, this.field_22790 - 6 - 54, spacing - 4, 16, this.option3d("head", "Z"), 0, 360, this.sv(armorStand.method_6921().comp_3778()), (integer) -> {
         armorStand.method_6919(this.modifyRotation(armorStand.method_6921(), (a) -> {
            return new class_2379(a.comp_3776(), a.comp_3777(), (float)integer);
         }));
      }));
      this.method_37063(new CIntSliderWidget(10 + spacing, this.field_22790 - 6 - 18, spacing - 4, 16, this.option3d("left.arm", "X"), 0, 360, this.sv(armorStand.method_6930().comp_3776()), (integer) -> {
         armorStand.method_6910(this.modifyRotation(armorStand.method_6930(), (a) -> {
            return new class_2379((float)integer, a.comp_3777(), a.comp_3778());
         }));
      }));
      this.method_37063(new CIntSliderWidget(10 + spacing, this.field_22790 - 6 - 36, spacing - 4, 16, this.option3d("left.arm", "Y"), 0, 360, this.sv(armorStand.method_6930().comp_3777()), (integer) -> {
         armorStand.method_6910(this.modifyRotation(armorStand.method_6930(), (a) -> {
            return new class_2379(a.comp_3776(), (float)integer, a.comp_3778());
         }));
      }));
      this.method_37063(new CIntSliderWidget(10 + spacing, this.field_22790 - 6 - 54, spacing - 4, 16, this.option3d("left.arm", "Z"), 0, 360, this.sv(armorStand.method_6930().comp_3778()), (integer) -> {
         armorStand.method_6910(this.modifyRotation(armorStand.method_6930(), (a) -> {
            return new class_2379(a.comp_3776(), a.comp_3777(), (float)integer);
         }));
      }));
      this.method_37063(new CIntSliderWidget(10 + spacing * 2, this.field_22790 - 6 - 18, spacing - 4, 16, this.option3d("right.arm", "X"), 0, 360, this.sv(armorStand.method_6903().comp_3776()), (integer) -> {
         armorStand.method_6925(this.modifyRotation(armorStand.method_6903(), (a) -> {
            return new class_2379((float)integer, a.comp_3777(), a.comp_3778());
         }));
      }));
      this.method_37063(new CIntSliderWidget(10 + spacing * 2, this.field_22790 - 6 - 36, spacing - 4, 16, this.option3d("right.arm", "Y"), 0, 360, this.sv(armorStand.method_6903().comp_3777()), (integer) -> {
         armorStand.method_6925(this.modifyRotation(armorStand.method_6903(), (a) -> {
            return new class_2379(a.comp_3776(), (float)integer, a.comp_3778());
         }));
      }));
      this.method_37063(new CIntSliderWidget(10 + spacing * 2, this.field_22790 - 6 - 54, spacing - 4, 16, this.option3d("right.arm", "Z"), 0, 360, this.sv(armorStand.method_6903().comp_3778()), (integer) -> {
         armorStand.method_6925(this.modifyRotation(armorStand.method_6903(), (a) -> {
            return new class_2379(a.comp_3776(), a.comp_3777(), (float)integer);
         }));
      }));
      this.method_37063(new CIntSliderWidget(10 + spacing * 3, this.field_22790 - 6 - 18, spacing - 4, 16, this.option3d("left.leg", "X"), 0, 360, this.sv(armorStand.method_6917().comp_3776()), (integer) -> {
         armorStand.method_6909(this.modifyRotation(armorStand.method_6917(), (a) -> {
            return new class_2379((float)integer, a.comp_3777(), a.comp_3778());
         }));
      }));
      this.method_37063(new CIntSliderWidget(10 + spacing * 3, this.field_22790 - 6 - 36, spacing - 4, 16, this.option3d("left.leg", "Y"), 0, 360, this.sv(armorStand.method_6917().comp_3777()), (integer) -> {
         armorStand.method_6909(this.modifyRotation(armorStand.method_6917(), (a) -> {
            return new class_2379(a.comp_3776(), (float)integer, a.comp_3778());
         }));
      }));
      this.method_37063(new CIntSliderWidget(10 + spacing * 3, this.field_22790 - 6 - 54, spacing - 4, 16, this.option3d("left.leg", "Z"), 0, 360, this.sv(armorStand.method_6917().comp_3778()), (integer) -> {
         armorStand.method_6909(this.modifyRotation(armorStand.method_6917(), (a) -> {
            return new class_2379(a.comp_3776(), a.comp_3777(), (float)integer);
         }));
      }));
      this.method_37063(new CIntSliderWidget(10 + spacing * 4, this.field_22790 - 6 - 18, spacing - 4, 16, this.option3d("right.leg", "X"), 0, 360, this.sv(armorStand.method_6900().comp_3776()), (integer) -> {
         armorStand.method_6926(this.modifyRotation(armorStand.method_6900(), (a) -> {
            return new class_2379((float)integer, a.comp_3777(), a.comp_3778());
         }));
      }));
      this.method_37063(new CIntSliderWidget(10 + spacing * 4, this.field_22790 - 6 - 36, spacing - 4, 16, this.option3d("right.leg", "Y"), 0, 360, this.sv(armorStand.method_6900().comp_3777()), (integer) -> {
         armorStand.method_6926(this.modifyRotation(armorStand.method_6900(), (a) -> {
            return new class_2379(a.comp_3776(), (float)integer, a.comp_3778());
         }));
      }));
      this.method_37063(new CIntSliderWidget(10 + spacing * 4, this.field_22790 - 6 - 54, spacing - 4, 16, this.option3d("right.leg", "Z"), 0, 360, this.sv(armorStand.method_6900().comp_3778()), (integer) -> {
         armorStand.method_6926(this.modifyRotation(armorStand.method_6900(), (a) -> {
            return new class_2379(a.comp_3776(), a.comp_3777(), (float)integer);
         }));
      }));
      this.method_37063(new CButtonWidget(10, this.field_22790 - 6 - 20 - 72, spacing - 4, 16, () -> {
         return this.optionBool("basePlate", armorStand.method_61489());
      }, (button) -> {
         armorStand.method_6907(!armorStand.method_61489());
      }));
      this.method_37063(new CButtonWidget(10, this.field_22790 - 6 - 20 - 90, spacing - 4, 16, () -> {
         return this.optionBool("invisible", armorStand.method_5767());
      }, (button) -> {
         armorStand.method_5648(!armorStand.method_5767());
      }));
      this.method_37063(new CButtonWidget(10, this.field_22790 - 6 - 20 - 108, spacing - 4, 16, () -> {
         return this.optionBool("small", small);
      }, (button) -> {
         small = !small;
      }));
      this.method_37063(new CButtonWidget(10, this.field_22790 - 6 - 20 - 126, spacing - 4, 16, () -> {
         return this.optionBool("hasArms", armorStand.method_6929());
      }, (button) -> {
         armorStand.method_6913(!armorStand.method_6929());
      }));
      this.method_37063(new CButtonWidget(10, this.field_22790 - 6 - 20 - 144, spacing - 4, 16, () -> {
         return this.optionBool("gravity", !armorStand.method_5740());
      }, (button) -> {
         armorStand.method_5875(!armorStand.method_5740());
      }));
      this.method_37063(new CButtonWidget(10, this.field_22790 - 6 - 20 - 162, spacing - 4, 16, () -> {
         return this.optionBool("lockSlots", lockSlots);
      }, (button) -> {
         lockSlots = !lockSlots;
      }));
      this.method_37063(new CButtonWidget(10, this.field_22790 - 6 - 20 - 180, spacing - 4, 16, () -> {
         return this.optionBool("invulnerable", armorStand.method_5655());
      }, (button) -> {
         armorStand.method_5684(!armorStand.method_5655());
      }));
      CButtonWidget customNameVisibleBtn;
      this.method_37063(customNameVisibleBtn = new CButtonWidget(10, this.field_22790 - 6 - 20 - 198, spacing - 4, 16, () -> {
         return class_2561.method_43470("Visible Name: " + armorStand.method_5807());
      }, (button) -> {
         armorStand.method_5880(!armorStand.method_5807());
      }, false));
      this.method_37063(new CButtonWidget(this.field_22789 - 6 - 120, 30, 58, 20, class_2561.method_43470(this.getTranslatableElement("widget.load", new Object[0])), (button) -> {
         CompletableFuture.runAsync(() -> {
            String path = TinyFileDialogs.tinyfd_openFileDialog(this.getTranslatableElement("text.loadTitle", new Object[0]), CactusSaves.ARMOR_STAND.dir().getAbsolutePath() + "/", Utils.createFileTypeFilter("*.nbt"), (CharSequence)null, false);
            CactusConstants.mc.execute(() -> {
               if (path != null) {
                  File file = new File(path);
                  class_2487 compound = CactusSaves.ARMOR_STAND.load(file);
                  if (compound == null) {
                     return;
                  }

                  this.loadNbt(compound);
                  this.method_41843();
               }

            });
         });
      }));
      this.method_37063(new CButtonWidget(this.field_22789 - 6 - 58, 30, 58, 20, class_2561.method_43470(this.getTranslatableElement("widget.save", new Object[0])), (button) -> {
         CactusConstants.mc.method_1507((new TextInputWindow("none", this.getTranslatableElement("text.saveTitle", new Object[0]))).setPlaceholder(this.getTranslatableElement("text.saveFilename", new Object[0])).range(1, 64).allowEmptyText(false).onSubmit((s) -> {
            CactusSaves.ARMOR_STAND.save(s, this.getNbt());
         }));
      }));
      class_342 customNameWidget;
      this.method_37063(customNameWidget = new class_342(CactusConstants.mc.field_1772, this.field_22789 - 6 - 120, 54, 120, 20, class_2561.method_43473()));
      customNameWidget.method_1863((s) -> {
         armorStand.method_5665(class_2561.method_43470(s.replace("&", "§")));
         if (s.isEmpty()) {
            armorStand.method_5880(false);
         }

         customNameWidget.method_1887(s.isEmpty() ? this.getTranslatableElement("widget.customName", new Object[0]) : "");
         customNameVisibleBtn.field_22763 = !s.isEmpty();
      });
      customNameWidget.method_1852(armorStand.method_5797().getString().replace("§", "&"));
      this.method_37063(new CButtonWidget(this.field_22789 - 6 - 120, 78, 120, 20, class_2561.method_43470(this.getTranslatableElement("widget.headButton", new Object[0])), (button) -> {
         CactusConstants.mc.method_1507((new TextInputWindow("none", this.getTranslatableElement("widget.headText", new Object[0]))).allowEmptyText(false).range(3, 16).setPlaceholder(class_2477.method_10517().method_48307("gui.screen.account_switcher.offlineSession.text")).onSubmit((s) -> {
            armorStand.method_5673(class_1304.field_6169, ItemUtils.headOfName(s));
         }));
      }));
      this.method_37063(new CButtonWidget(this.field_22789 - 6 - 100, 6, 100, 20, class_2561.method_43470(this.getTranslatableElement("widget.createItem", new Object[0])), (button) -> {
         class_1799 itemStack = new class_1799(class_1802.field_8694);
         itemStack.method_57379(class_9334.field_49609, class_11580.method_72535(class_1299.field_6131, this.getNbt()));
         itemStack.method_57379(class_9334.field_49631, class_2561.method_43470(this.getTranslatableElement("text.itemName", new Object[0])).method_27694((style) -> {
            return style.method_36139(Color.GREEN.getRGB()).method_10978(false);
         }));
         class_437 patt0$temp = this.parent;
         if (patt0$temp instanceof NbtEditorScreen) {
            NbtEditorScreen screen = (NbtEditorScreen)patt0$temp;
            CactusConstants.mc.method_1507(new NbtEditorScreen(screen.parent, itemStack));
         } else {
            ItemUtils.giveItem(itemStack);
            CactusConstants.mc.method_1507((class_437)null);
         }

      }));
      this.method_37063(new CButtonWidget(this.field_22789 - 6 - 64 - 100, 6, 60, 20, class_2561.method_43470(this.getTranslatableElement("widget.reset", new Object[0])), (button) -> {
         reInit = true;
         CactusConstants.mc.method_1507(new ArmorStandEditorScreen(this.parent));
      }));
      this.method_37063(new CIntSliderWidget(this.field_22789 - 6 - 100 - 64 - 104, 6, 100, 20, class_2561.method_43470(this.getTranslatableElement("option.rotationView", new Object[0])), 0, 360, 180, (integer) -> {
         ARMOR_STAND_ROTATION = (new Quaternionf()).rotationXYZ(0.0F, (float)Math.toRadians((double)integer), 3.1415927F);
      }));
      this.method_37063(class_4185.method_46430(class_2561.method_43470("Edit Items"), (button) -> {
         awaitingInventoryItemSelection = true;
         this.field_22787.method_1507(new class_481(this.field_22787.field_1724, this.field_22787.field_1724.field_3944.method_45735(), (Boolean)this.field_22787.field_1690.method_47395().method_41753()));
      }).method_46434(this.field_22789 - 6 - 120, 102, 120, 20).method_46431());
      reInit = false;
   }

   public void applyItem(class_1799 stack) {
      if (ItemUtils.isArmor(stack)) {
         class_10192 equipment = (class_10192)stack.method_58694(class_9334.field_54196);
         if (equipment == null) {
            return;
         }

         armorStand.method_5673(equipment.comp_3174(), stack);
      }

   }

   private class_5250 option3d(String key, String suffix) {
      class_5250 text = class_2561.method_43473();
      String[] var4 = key.split("\\.");
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String s = var4[var6];
         text.method_27693(this.getTranslatableElement("text." + s, new Object[0])).method_10852(class_5244.field_41874);
      }

      text.method_27693(suffix);
      return text;
   }

   private class_5250 optionBool(String key, boolean val) {
      return class_2561.method_43470(this.getTranslatableElement("option." + key, new Object[0])).method_27693(": ").method_10852(val ? class_5244.field_24336 : class_5244.field_24337);
   }

   public static class_2487 getArmorStandDataExtended(class_1531 armorStand) {
      class_11362 writeView = class_11362.method_71458(class_8942.field_60348);
      armorStand.method_5652(writeView);
      class_2487 entityData = new class_2487();
      entityData.method_10543(writeView.method_71475());
      if (armorStand.method_5797() != null && !armorStand.method_5797().getString().isEmpty()) {
         entityData.method_10582("CustomName", armorStand.method_5797().getString());
      }

      entityData.method_10556("CustomNameVisible", armorStand.method_5807());
      entityData.method_10556("Small", small);
      entityData.method_10556("Invulnerable", armorStand.method_5655());
      entityData.method_10556("NoGravity", armorStand.method_5740());
      entityData.method_10569("DisabledSlots", lockSlots ? 4144959 : 0);
      return entityData;
   }

   private class_2487 getNbt() {
      return getArmorStandDataExtended(armorStand);
   }

   private void loadNbt(class_2487 compound) {
      class_11352 readView = (class_11352)class_11352.method_71417(class_8942.field_60348, CactusConstants.WRAPPER_LOOKUP, compound);
      armorStand.method_5749(readView);
      String customName = compound.method_68564("CustomName", "");
      if (!customName.isEmpty()) {
         armorStand.method_5665(class_2561.method_43470(customName));
      } else {
         armorStand.method_5665(class_2561.method_43473());
      }

      armorStand.method_5880(compound.method_68566("CustomNameVisible", false));
      small = compound.method_68566("Small", false);
      armorStand.method_5684(compound.method_68566("Invulnerable", false));
      armorStand.method_5875(compound.method_68566("NoGravity", false));
      lockSlots = compound.method_68083("DisabledSlots", 0) == 4144959;
   }

   private int sv(float armorStand) {
      return reInit ? 0 : class_3532.method_15340((int)armorStand, 0, 360);
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_51448().pushMatrix();
      int size = (this.field_22789 + this.field_22790) / 16;
      class_490.method_2486(context, 0, 0, this.field_22789, this.field_22790, size, 0.0625F, (float)mouseX, (float)mouseY, armorStand);
      context.method_51448().popMatrix();
      context.method_27535(CactusConstants.mc.field_1772, fileText, this.field_22789 - 120 - CactusConstants.mc.field_1772.method_27525(fileText) - 16, 34, Color.WHITE.getRGB());
   }

   public boolean method_25403(@NotNull class_11909 click, double deltaX, double deltaY) {
      return super.method_25403(click, deltaX, deltaY);
   }

   private class_2379 modifyRotation(class_2379 angle, Function<class_2379, class_2379> function) {
      return (class_2379)function.apply(angle);
   }

   static {
      fileText = class_2561.method_43471("gui.screen.ast_editor.text.file").method_27695(new class_124[]{class_124.field_1073, class_124.field_1067});
   }
}
