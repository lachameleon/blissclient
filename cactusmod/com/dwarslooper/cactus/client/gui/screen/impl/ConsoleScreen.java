package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CScrollableWidget;
import com.dwarslooper.cactus.client.gui.widget.SubmittableTextWidget;
import com.dwarslooper.cactus.client.systems.localserver.instance.LocalServer;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5250;
import net.minecraft.class_5481;
import org.jetbrains.annotations.NotNull;

public class ConsoleScreen extends CScreen {
   private static final Pattern logLevelRegex = Pattern.compile("(?<= )[A-Z]+(?=])");
   private static final class_2561 OFFLINE_TEXT;
   private final LocalServer server;
   private final List<class_5481> lines = new ArrayList();
   private SubmittableTextWidget commandInput;
   private CScrollableWidget output;
   private int lastUpdateState;
   private int logSize = 0;

   public ConsoleScreen(LocalServer server) {
      super("serverConsole");
      this.server = server;
   }

   public void method_25426() {
      super.method_25426();
      this.method_37063(this.output = new CScrollableWidget(0, 28, this.field_22789, this.field_22790 - 28 - 24, 10, class_2561.method_43473(), () -> {
         Objects.requireNonNull(this.field_22793);
         int h = 9 + 2;
         return h * this.lines.size();
      }, (context, mx, my, delta) -> {
         int y = 32;
         if (!this.server.isOnline()) {
            context.method_27535(this.field_22793, OFFLINE_TEXT, 4, y, -1);
         } else {
            synchronized(this.server.getLog()) {
               for(Iterator var7 = this.lines.iterator(); var7.hasNext(); y += 9 + 2) {
                  class_5481 patchNote = (class_5481)var7.next();
                  context.method_35720(this.field_22793, patchNote, 4, y, -1);
                  Objects.requireNonNull(CactusConstants.mc.field_1772);
               }
            }
         }

      }));
      class_327 var10004 = CactusConstants.mc.field_1772;
      int var10006 = 28 + this.output.method_25364() + 4;
      int var10007 = this.field_22789;
      class_5250 var10009 = class_2561.method_43470("Console");
      LocalServer var10010 = this.server;
      Objects.requireNonNull(var10010);
      this.method_37063(this.commandInput = new SubmittableTextWidget(var10004, 0, var10006, var10007, 20, var10009, var10010::sendCommand));
      this.commandInput.method_1863((s) -> {
         this.commandInput.method_1887(s.isEmpty() && this.server.isOnline() ? "Console command.." : "");
      });
      this.commandInput.method_1880(Integer.MAX_VALUE);
      this.commandInput.method_1852("");
      this.output.method_44382(2.147483647E9D);
      if (this.server.isOnline()) {
         this.method_48265(this.commandInput);
      }

   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      int now = this.server.getLog().hashCode();
      if (!this.server.isOnline()) {
         this.logSize = 0;
         this.lines.clear();
         this.output.method_44382(0.0D);
         this.commandInput.method_1852("");
         this.commandInput.field_22763 = false;
         this.commandInput.method_47404(class_2561.method_43470("This server is currently offline."));
      } else if (this.lastUpdateState != now && !this.server.getLog().isEmpty()) {
         int size = this.server.getLog().size();
         if (size >= this.logSize && this.server.isOnline()) {
            synchronized(this.server.getLog()) {
               (new ArrayList(this.server.getLog().subList(this.logSize, size))).forEach(this::acceptLine);
            }
         }

         this.output.method_44382(2.147483647E9D);
         this.lastUpdateState = now;
      }

      super.method_25394(context, mouseX, mouseY, delta);
   }

   public void method_25419() {
      super.method_25419();
   }

   private void acceptLine(String line) {
      class_124 formatting = class_124.field_1070;
      Matcher matcher = logLevelRegex.matcher(line);
      if (matcher.find()) {
         String var4 = matcher.group();
         byte var5 = -1;
         switch(var4.hashCode()) {
         case 2251950:
            if (var4.equals("INFO")) {
               var5 = 0;
            }
            break;
         case 2656902:
            if (var4.equals("WARN")) {
               var5 = 1;
            }
            break;
         case 66247144:
            if (var4.equals("ERROR")) {
               var5 = 3;
            }
            break;
         case 66665700:
            if (var4.equals("FATAL")) {
               var5 = 4;
            }
            break;
         case 1842428796:
            if (var4.equals("WARNING")) {
               var5 = 2;
            }
         }

         class_124 var10000;
         switch(var5) {
         case 0:
            var10000 = class_124.field_1080;
            break;
         case 1:
         case 2:
            var10000 = class_124.field_1054;
            break;
         case 3:
            var10000 = class_124.field_1061;
            break;
         case 4:
            var10000 = class_124.field_1079;
            break;
         default:
            var10000 = class_124.field_1068;
         }

         formatting = var10000;
      }

      this.lines.addAll(CactusConstants.mc.field_1772.method_1728(class_2561.method_43470(line).method_27692(formatting), this.field_22789 - 16));
      ++this.logSize;
   }

   static {
      OFFLINE_TEXT = class_2561.method_43470("This server is currently offline.").method_27692(class_124.field_1061);
   }
}
