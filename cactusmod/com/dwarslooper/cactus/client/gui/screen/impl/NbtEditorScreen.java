package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.client.NbtFormatter;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2487;
import net.minecraft.class_2509;
import net.minecraft.class_2522;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_437;
import net.minecraft.class_7529;
import net.minecraft.class_7919;
import net.minecraft.class_2568.class_10613;
import net.minecraft.class_7529.class_11383;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public class NbtEditorScreen extends CScreen {
   class_7529 nbtEditor;
   private class_342 itemCount;
   private String editorData;
   public final class_1799 stack;
   public final int slot;
   final List<String> initial;
   final char[] suffixes;
   final char[][] appendPairs;

   public NbtEditorScreen(class_1799 stack) {
      this(stack, CactusConstants.mc.field_1724.method_31548().method_67532());
   }

   public NbtEditorScreen(class_437 parent, class_1799 stack) {
      this(stack, CactusConstants.mc.field_1724.method_31548().method_67532());
      this.parent = parent;
   }

   public NbtEditorScreen(class_1799 stack, int slot) {
      super("nbt_editor");
      this.initial = new ArrayList();
      this.suffixes = new char[]{'b', 's', 'L', 'f', 'd'};
      this.appendPairs = new char[][]{{'"', '"'}, {'{', '}'}, {'\'', '\''}, {'[', ']'}};
      this.stack = stack;
      this.slot = slot;
      class_2487 compound = (class_2487)class_1799.field_24671.encodeStart(CactusConstants.mc.field_1724.method_56673().method_57093(class_2509.field_11560), this.stack).getOrThrow();
      NbtFormatter.RGBColorText formatted = (new NbtFormatter("  ", 0, Lists.newArrayList())).apply(compound);
      StringBuilder current = new StringBuilder();
      Iterator var6 = formatted.getEntries().iterator();

      while(var6.hasNext()) {
         NbtFormatter.RGBColorText.RGBEntry entry = (NbtFormatter.RGBColorText.RGBEntry)var6.next();
         if (entry == NbtFormatter.RGBColorText.NEWLINE) {
            this.initial.add(current.toString());
            current = new StringBuilder();
         } else {
            current.append(entry.value());
         }
      }

      this.initial.add(current.toString());
   }

   public boolean method_25421() {
      return false;
   }

   private void save() {
      assert CactusConstants.mc.field_1687 != null;

      String nbtString = this.editorData == null ? "" : this.editorData;

      try {
         class_2487 nbt = class_2522.method_67315(nbtString);
         class_1799 itemStack = (class_1799)class_1799.field_24671.parse(CactusConstants.mc.field_1687.method_30349().method_57093(class_2509.field_11560), nbt).getOrThrow((error) -> {
            return new RuntimeException("Failed to parse ItemStack: " + error);
         });
         CactusConstants.mc.field_1724.method_31548().method_5447(this.slot, itemStack);
      } catch (Exception var4) {
         var4.printStackTrace();
         ChatUtils.errorPrefix("NBT Editor", (class_2561)class_2561.method_43470("Save failed. Is the syntax correct?").method_27694((style) -> {
            return style.method_10949(new class_10613(class_2561.method_43470(var4.getMessage())));
         }));
      }

   }

   public void saveAndClose() {
      this.save();
      this.method_25419();
   }

   public void method_25426() {
      super.method_25426();
      int mainWidgetHeight = this.field_22790 / 2;
      this.nbtEditor = (new class_11383()).method_71508(this.field_22789 / 2 - 200).method_71512(40).method_71509(this.field_22793, 400, mainWidgetHeight, class_2561.method_43470("NBT Editor"));
      this.nbtEditor.method_44401(this::onTextChange);
      this.nbtEditor.method_44400(String.join("\n", this.initial));
      this.editorData = String.join("\n", this.initial);
      this.method_37063(this.nbtEditor);
      this.method_37063(new CButtonWidget(this.posWithDefaultWidth, this.field_22790 - 32, 120, 20, class_2561.method_43470("Save"), (button) -> {
         this.saveAndClose();
      }));
      if (this.stack.method_7909() == class_1802.field_8694 && (Boolean)CactusSettings.get().experiments.get()) {
         this.method_37063((new CTextureButtonWidget.Builder((button) -> {
            CactusConstants.mc.method_1507(new ArmorStandEditorScreen(this, this.stack));
         })).simpleSingle().positioned(this.field_22789 / 2 - 200, 40 + mainWidgetHeight + 4 + 24).dimensions(16, 16).tooltip(class_7919.method_47407(class_2561.method_43470("Open in AST Editor"))).texture(class_2960.method_60656("textures/item/armor_stand.png")).build());
      }

   }

   private void onTextChange(String data) {
      this.editorData = data;
   }

   public void method_25410(int width, int height) {
      super.method_25410(width, height);
      this.method_41843();
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }
}
