package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CScrollableWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ScreenUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.jetbrains.annotations.NotNull;

public class PatchInfoScreen extends CScreen {
   private final List<String> patchNotes;

   public static void openPatches() throws IOException, URISyntaxException {
      InputStream stream = (new URI("https://cdn.cactusmod.xyz/client/shared/patches.txt")).toURL().openStream();
      byte[] notesServerRaw = stream.readAllBytes();
      stream.close();
      List<String> patchNotes = Arrays.asList((new String(notesServerRaw)).split("\n"));
      CactusConstants.mc.method_1507(new PatchInfoScreen(CactusConstants.mc.field_1755, patchNotes));
   }

   public PatchInfoScreen(class_437 parent, List<String> patchNotes) {
      super("patches");
      this.parent = parent;
      this.patchNotes = patchNotes;
   }

   public void method_25426() {
      super.init(false);
      this.method_37063(new CButtonWidget(this.field_22789 / 2 - 100, this.field_22790 - this.field_22790 / 8 + 10, 200, 20, class_2561.method_30163("Close"), (button) -> {
         CactusConstants.mc.method_1507(this.parent);
      }));
      this.method_37063(new CScrollableWidget(20, ScreenUtils.baseY + 10, this.field_22789 - 40, this.field_22790 - (ScreenUtils.baseY + 10) * 2 - 10, 10, class_2561.method_43470("Sieht man das überhaupt?"), () -> {
         Objects.requireNonNull(this.field_22793);
         int h = 9 + 2;
         int y = h;
         Iterator var3 = this.patchNotes.iterator();

         while(var3.hasNext()) {
            String patchNote = (String)var3.next();
            y += h;
            if (patchNote.trim().startsWith("--")) {
               y += h;
            }
         }

         return y;
      }, (context, mx, my, delta) -> {
         int y = ScreenUtils.baseY + 20;

         for(Iterator var6 = this.patchNotes.iterator(); var6.hasNext(); y += 10) {
            String patchNote = (String)var6.next();
            if (!patchNote.trim().startsWith("--") && !patchNote.startsWith("# ")) {
               context.method_25303(this.field_22793, patchNote, 30, y, -1);
            } else {
               if (patchNote.startsWith("# ")) {
                  patchNote = patchNote.substring(2);
               }

               context.method_51448().scale(1.3333334F, 1.3333334F);
               context.method_25300(this.field_22793, "§n" + patchNote, this.field_22789 * 3 / 4 / 2, (int)((float)(y * 3) / 4.0F + 5.0F), -1);
               context.method_51448().scale(0.75F, 0.75F);
               y += 10;
            }
         }

      }));
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }
}
