package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.DynamicHeightScrollableWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2598;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5250;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

public class PacketLogsScreen extends CScreen {
   private final File file;
   private PacketLogsScreen.Widget widget;

   public PacketLogsScreen(File file) {
      super("packetLogs");
      this.file = file;
   }

   public void method_25426() {
      super.method_25426();
      if (this.widget == null) {
         this.widget = new PacketLogsScreen.Widget(this.field_22789, this.field_22790 - 80, 40);
      }

      this.method_37063(this.widget);

      try {
         List<PacketLogsScreen.PacketLogEntry> entries = parsePacketsFromFile(this.file);
         Collections.reverse(entries);
         PrintStream var10000 = System.out;
         int var10001 = entries.size();
         var10000.println("Parsed " + var10001 + " packet log entries from file: " + this.file.getName());
         Iterator var2 = entries.iterator();

         while(var2.hasNext()) {
            PacketLogsScreen.PacketLogEntry entry = (PacketLogsScreen.PacketLogEntry)var2.next();
            PacketLogsScreen.Widget var5 = this.widget;
            int var10004 = entry.data().size();
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            var5.addEntry(new PacketLogsScreen.Widget.Entry(entry, var10004 * (9 + 2) + 10 + 24 + 16));
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
   }

   public static List<PacketLogsScreen.PacketLogEntry> parsePacketsFromFile(File file) throws IOException {
      List<PacketLogsScreen.PacketLogEntry> packetLogEntries = new ArrayList();
      BufferedReader reader = new BufferedReader(new FileReader(file));

      try {
         long timestamp = 0L;
         class_2598 side = null;
         String className = null;
         HashMap dataMap = new HashMap();

         String line;
         PacketLogsScreen.PacketLogEntry entry;
         while((line = reader.readLine()) != null) {
            if (isNewPacket(line)) {
               if (className != null) {
                  entry = new PacketLogsScreen.PacketLogEntry(timestamp, side, className, dataMap);
                  packetLogEntries.add(entry);
                  dataMap = new HashMap();
               }

               timestamp = Long.parseLong(line.trim());
               side = readNetworkSide(reader);
               className = reader.readLine().trim();
            } else if (!line.trim().isEmpty()) {
               dataMap.put(line.substring(0, line.lastIndexOf(58)), line.substring(line.indexOf(58) + 2));
            }
         }

         entry = new PacketLogsScreen.PacketLogEntry(timestamp, side, className, dataMap);
         packetLogEntries.add(entry);
      } catch (Throwable var11) {
         try {
            reader.close();
         } catch (Throwable var10) {
            var11.addSuppressed(var10);
         }

         throw var11;
      }

      reader.close();
      return packetLogEntries;
   }

   private static boolean isNewPacket(String line) {
      return line.matches("^\\d+$");
   }

   private static class_2598 readNetworkSide(BufferedReader reader) throws IOException {
      String side = reader.readLine().trim();
      return class_2598.valueOf(side);
   }

   public static class Widget extends DynamicHeightScrollableWidget<PacketLogsScreen.Widget.Entry> {
      public Widget(int width, int height, int y) {
         super(CactusConstants.mc, width, height, y);
      }

      public int method_25322() {
         return this.method_25368() - 40;
      }

      protected int addEntry(PacketLogsScreen.Widget.Entry entry) {
         return super.method_25321(entry);
      }

      public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
         return super.method_25401(mouseX, mouseY, horizontalAmount * 2.0D, verticalAmount * 2.0D);
      }

      public static class Entry extends DynamicHeightScrollableWidget.DynamicEntry<PacketLogsScreen.Widget.Entry> {
         private final PacketLogsScreen.PacketLogEntry entry;
         private final class_327 tr;

         public Entry(PacketLogsScreen.PacketLogEntry entry, int height) {
            super(height);
            this.tr = CactusConstants.mc.field_1772;
            this.entry = entry;
         }

         public void render(class_332 context, int index, int y, int entryX, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int x = this.entry.side() == class_2598.field_11942 ? entryX + entryWidth / 2 + 4 : entryX;
            int width = entryWidth / 2 - 4;
            String time = Long.toString(this.entry.time());
            context.method_25294(x, y + 4, x + width, y + entryHeight - 4, -2013265920);
            RenderUtils.drawTextAlignedRight(context, this.entry.side().method_56444().toUpperCase(), x + width - 4 - this.tr.method_1727(time) - 8, y + 8, 5592405, false);
            RenderUtils.drawTextAlignedRight(context, time, x + width - 4, y + 8, 16755200, false);
            context.method_51433(this.tr, this.entry.className().substring(this.entry.className().lastIndexOf(".") + 1), x + 4, y + 18, 5635925, false);
            int dataY = y + 10 + 24 + 8;

            for(Iterator var15 = this.entry.data().entrySet().iterator(); var15.hasNext(); dataY += 9 + 2) {
               java.util.Map.Entry<String, String> dataEntry = (java.util.Map.Entry)var15.next();
               String key = (String)dataEntry.getKey();
               String value = (String)dataEntry.getValue();
               class_5250 valueFormatted = class_2561.method_43470(value);
               if (value.equals("true") || value.equals("false")) {
                  valueFormatted.method_27692(Boolean.parseBoolean(value) ? class_124.field_1060 : class_124.field_1061);
               }

               if (NumberUtils.isCreatable(value)) {
                  valueFormatted.method_27692(class_124.field_1075);
               }

               context.method_51439(this.tr, class_2561.method_43470(key).method_27693(": ").method_27692(class_124.field_1080).method_10852(valueFormatted), x + 4, dataY, -1, false);
               Objects.requireNonNull(this.tr);
            }

         }
      }
   }

   public static record PacketLogEntry(long time, class_2598 side, String className, Map<String, String> data) {
      public PacketLogEntry(long time, class_2598 side, String className, Map<String, String> data) {
         this.time = time;
         this.side = side;
         this.className = className;
         this.data = data;
      }

      public long time() {
         return this.time;
      }

      public class_2598 side() {
         return this.side;
      }

      public String className() {
         return this.className;
      }

      public Map<String, String> data() {
         return this.data;
      }
   }
}
