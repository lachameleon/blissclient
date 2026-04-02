package com.dwarslooper.cactus.client.systems.hdb.gui;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.systems.hdb.HeadDataHandler;
import com.dwarslooper.cactus.client.systems.hdb.TagCategory;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import org.jetbrains.annotations.NotNull;

public class HDBScreen extends CScreen {
   private TagCategory currentCategory;
   private long startTime = -1L;
   private float downloadProgress = -1.0F;

   public HDBScreen() {
      super("hdb");
   }

   public void method_25426() {
      super.method_25426();
      this.method_37063(new CButtonWidget(4, this.field_22790 - 72, 100, 20, class_2561.method_43470("Download All"), (button) -> {
         HeadDataHandler.INSTANCE.downloadAndSaveAll(this::handleUpdate);
      }));
      this.method_37063(new CButtonWidget(4, this.field_22790 - 48, 100, 20, class_2561.method_43470("Download Missing"), (button) -> {
         List<String> present = Arrays.asList((String[])Objects.requireNonNull(HeadDataHandler.DIRECTORY.list()));
         HeadDataHandler.INSTANCE.downloadAndSave(Arrays.stream(TagCategory.values()).filter((tc) -> {
            String var10001 = tc.getId();
            return !present.contains(var10001 + ".json");
         }).toList(), this::handleUpdate);
      }));
      this.method_37063(new CButtonWidget(4, this.field_22790 - 24, 100, 20, class_2561.method_43470("Download Plants"), (button) -> {
         HeadDataHandler.INSTANCE.downloadAndSave(List.of(TagCategory.PLANTS), this::handleUpdate);
      }));
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      if (this.startTime != -1L) {
         RenderUtils.renderLabeledProgressBar(context, 40, 40, this.field_22789 - 40, 60, this.downloadProgress, this.startTime, true);
         RenderUtils.drawTextAlignedRight(context, (String)this.currentCategory.getDisplay(), this.field_22789 - 4, 4, -1, true);
      }

   }

   private void handleUpdate(float f, TagCategory c) {
      System.out.println(f);
      System.out.println(this.startTime);
      if (f == -1.0F) {
         this.startTime = System.currentTimeMillis();
      } else if (f == 1.0F) {
         this.startTime = -1L;
         return;
      }

      this.currentCategory = c;
      this.downloadProgress = f;
   }
}
