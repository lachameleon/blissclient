package com.dwarslooper.cactus.client.systems.ias.skins;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.impl.ChoiceWarningScreen;
import com.dwarslooper.cactus.client.gui.screen.window.TextInputWindow;
import com.dwarslooper.cactus.client.gui.toast.ToastSystem;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_410;
import net.minecraft.class_412;
import net.minecraft.class_4265;
import net.minecraft.class_437;
import net.minecraft.class_6379;
import net.minecraft.class_639;
import net.minecraft.class_642;
import net.minecraft.class_7532;
import net.minecraft.class_9112;
import net.minecraft.class_4265.class_4266;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkinListWidget extends class_4265<SkinListWidget.SkinEntry> {
   private final SkinChangerScreen parent;

   public SkinListWidget(SkinChangerScreen parent, int width, int height) {
      super(CactusConstants.mc, width, height, 40, 36);
      this.parent = parent;
   }

   public void add(File file) {
      SkinChangerScreen.SkinTextureData data = new SkinChangerScreen.SkinTextureData(file);
      if (data.getTextures() != null) {
         this.method_25321(new SkinListWidget.SkinEntry(data));
      }

   }

   protected int method_65507() {
      return this.method_46426() + this.field_22758 - 6;
   }

   public int method_25322() {
      return this.field_22758 - 18;
   }

   protected boolean method_73379() {
      return true;
   }

   public void method_25395(@Nullable class_364 focused) {
      SkinListWidget.SkinEntry currentSelected = (SkinListWidget.SkinEntry)this.method_25334();
      super.method_25395(focused);
      if (focused == null && currentSelected != null && this.method_25334() == null) {
         this.method_25313(currentSelected);
      }

   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      SkinListWidget.SkinEntry entry = (SkinListWidget.SkinEntry)this.method_25308(click.comp_4798(), click.comp_4799());
      if (entry != null) {
         this.method_25313(entry);
         this.method_25395(entry);
      }

      return super.method_25402(click, doubled);
   }

   public class SkinEntry extends class_4266<SkinListWidget.SkinEntry> {
      private final SkinChangerScreen.SkinTextureData data;
      private final String fileName;
      private final class_339 selectButton;
      private final class_339 deleteButton;
      private final class_339 renameButton;

      public SkinEntry(SkinChangerScreen.SkinTextureData data) {
         this.data = data;
         this.fileName = data.getFile().getName();
         this.selectButton = new CButtonWidget(0, 0, 20, 20, class_2561.method_43470("✔"), (button) -> {
            try {
               String modelName = data.getTextures().comp_1629().name();
               String variant = modelName.equalsIgnoreCase("WIDE") ? "classic" : "slim";
               SkinHelper.uploadAndSetSkin(this.data.getFile(), variant, CactusConstants.mc.method_1548().method_1674());
               ToastSystem.displayMessage(SkinListWidget.this.parent.getTranslatableElement("changeSuccess.title", new Object[0]), SkinListWidget.this.parent.getTranslatableElement("changeSuccess.message", new Object[0]));
               class_642 currentServer = CactusConstants.mc.method_1558();
               if (currentServer != null) {
                  class_437 backScreen = SkinListWidget.this.parent;
                  class_642 reconnectServer = new class_642(currentServer.field_3752, currentServer.field_3761, currentServer.method_55616());
                  CactusConstants.mc.method_1507(new ChoiceWarningScreen(backScreen, class_2561.method_43470(SkinListWidget.this.parent.getTranslatableElement("reconnect.title", new Object[0])), class_2561.method_43470(SkinListWidget.this.parent.getTranslatableElement("reconnect.message", new Object[]{reconnectServer.field_3752})), (class_2561)null, (BooleanConsumer)null, (accepted) -> {
                     if (accepted) {
                        if (CactusConstants.mc.field_1687 != null) {
                           CactusConstants.mc.method_73360(class_2561.method_43470("Disconnecting"));
                        }

                        class_412.method_36877(backScreen, CactusConstants.mc, class_639.method_2950(reconnectServer.field_3761), reconnectServer, false, (class_9112)null);
                     }
                  }));
               }
            } catch (IllegalStateException | IOException var8) {
               ToastSystem.displayMessage("Error", SkinListWidget.this.parent.getTranslatableElement("changeFailed", new Object[0]));
               CactusClient.getLogger().error("Skin change failed", var8);
            }

         });
         this.deleteButton = (new CTextureButtonWidget.Builder((button) -> {
            CactusConstants.mc.method_1507(new class_410((b) -> {
               if (b) {
                  this.data.getFile().delete();
               }

               CactusConstants.mc.method_1507(SkinListWidget.this.parent);
            }, class_2561.method_43470(SkinListWidget.this.parent.getTranslatableElement("deleteSkin.title", new Object[0])), class_2561.method_43470(SkinListWidget.this.parent.getTranslatableElement("deleteSkin.message", new Object[]{this.data.getFile().getName()}))));
         })).uv(200, 0).dimensions(20, 20).build();
         this.renameButton = (new CTextureButtonWidget.Builder((button) -> {
            File file = this.data.getFile();
            CactusConstants.mc.method_1507((new TextInputWindow("none", SkinListWidget.this.parent.getTranslatableElement("renameTitle", new Object[0]))).allowEmptyText(false).setInitialText(this.fileName).setPlaceholder(SkinListWidget.this.parent.getTranslatableElement("renamePlaceholder", new Object[0])).onSubmit((s) -> {
               file.renameTo(file.toPath().resolveSibling(s).toFile());
            }));
         })).uv(220, 0).dimensions(20, 20).build();
      }

      public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         int x = this.method_46426();
         int y = this.method_46427();
         int entryWidth = this.method_25368();
         int entryHeight = this.method_25364();
         int padding = 2;
         int iconSize = 32;
         if (hovered && !Objects.equals(SkinListWidget.this.method_25334(), this)) {
            context.method_25294(x, y, x + entryWidth, y + entryHeight, 587202559);
         }

         int buttonsBaseY = y + entryHeight - 22;
         int buttonRight = x + entryWidth - padding;
         this.deleteButton.method_48229(buttonRight - 20, buttonsBaseY);
         this.renameButton.method_48229(buttonRight - 20 - 22, buttonsBaseY);
         this.selectButton.method_48229(buttonRight - 20 - 44, buttonsBaseY);
         int buttonLeft = this.selectButton.method_46426();
         class_7532.method_52722(context, this.data.getTextures(), x + padding, y + 2, iconSize);
         int textX = x + padding + iconSize + 2;
         int available = buttonLeft - textX - 2;
         String text = this.fileName;
         if (available > 0) {
            int width = CactusConstants.mc.field_1772.method_1727(text);
            if (width > available) {
               text = CactusConstants.mc.field_1772.method_27523(text, available);
            }
         }

         context.method_25303(CactusConstants.mc.field_1772, text, textX, y + 3, -1);
         this.method_25396().forEach((element) -> {
            if (element instanceof class_339) {
               class_339 w = (class_339)element;
               w.method_25394(context, mouseX, mouseY, tickDelta);
            }

         });
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         Iterator var3 = this.method_25396().iterator();

         class_364 element;
         do {
            if (!var3.hasNext()) {
               SkinListWidget.this.method_25313(this);
               SkinListWidget.this.method_25395(this);
               return true;
            }

            element = (class_364)var3.next();
         } while(!element.method_25402(click, doubled));

         return true;
      }

      @NotNull
      public List<? extends class_364> method_25396() {
         return ImmutableList.of(this.selectButton, this.renameButton, this.deleteButton);
      }

      @NotNull
      public List<? extends class_6379> method_37025() {
         return ImmutableList.of(this.selectButton, this.renameButton, this.deleteButton);
      }

      public SkinChangerScreen.SkinTextureData getData() {
         return this.data;
      }
   }
}
