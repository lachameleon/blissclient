package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.gui.toast.internal.GenericToast;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_2172;
import net.minecraft.class_2596;
import net.minecraft.class_2598;
import net.minecraft.class_8038;

public class PacketLoggerCommand extends Command {
   public static boolean isRunning = false;
   public static List<PacketLoggerCommand.PacketLogEntry> loggedPackets = new ArrayList();

   public PacketLoggerCommand() {
      super("packet-logger");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(literal("start").executes((context) -> {
         if (isRunning) {
            ChatUtils.error("Already running!");
         } else {
            isRunning = true;
            ChatUtils.info("Packet logger started");
         }

         return 1;
      }))).then(literal("stop").executes((context) -> {
         if (!isRunning) {
            ChatUtils.error("Not running!");
         } else {
            isRunning = false;
            ChatUtils.info("Packet logger stopped");
            this.export();
            loggedPackets.clear();
         }

         return 1;
      }))).then(literal("status").executes((context) -> {
         ChatUtils.info("Status: " + (isRunning ? "§2RUNNING" : "§4STOPPED"));
         if (isRunning) {
            List<PacketLoggerCommand.PacketLogEntry> syncList = new ArrayList(loggedPackets);
            long c = syncList.stream().filter((ple) -> {
               return ple.side() == class_2598.field_11942;
            }).count();
            ChatUtils.info("Packets logged: §e" + syncList.size());
            ChatUtils.info("C/S: §e" + c + "§r/§e" + ((long)syncList.size() - c));
            Stream var10000 = syncList.stream().map(PacketLoggerCommand.PacketLogEntry::timestamp);
            ChatUtils.info("Average PpS: §e" + calculateAverageObjectsPerSecond((List)var10000.collect(Collectors.toList())));
         }

         return 1;
      }));
   }

   public static void add(class_2596<?> packet) {
      if (isRunning) {
         if (packet instanceof class_8038) {
            class_8038<?> bp = (class_8038)packet;
            bp.method_48324().forEach(PacketLoggerCommand::add);
         }

         class_2598 side = packet.getClass().getPackage().getName().matches(".*\\.s2c\\..*") ? class_2598.field_11942 : class_2598.field_11941;
         List<PacketLoggerCommand.PacketFieldData> values = getFields(packet).stream().filter((field) -> {
            return !Modifier.isStatic(field.getModifiers());
         }).map((field) -> {
            try {
               field.trySetAccessible();
               Object data = field.get(packet);
               return new PacketLoggerCommand.PacketFieldData(field.getName(), data != null ? data : "NULL");
            } catch (IllegalAccessException var3) {
               throw new RuntimeException(var3);
            }
         }).toList();
         loggedPackets.add(new PacketLoggerCommand.PacketLogEntry(packet, System.currentTimeMillis(), Collections.emptyList(), side, values));
      }
   }

   private void export() {
      File directory = new File(CactusConstants.DIRECTORY, "packet-logger");
      directory.mkdirs();
      File logFile = new File(directory, "packets-" + System.currentTimeMillis() + ".log");
      long time = System.currentTimeMillis();

      GenericToast toast;
      try {
         FileWriter fw = new FileWriter(logFile);

         try {
            Iterator var13 = loggedPackets.iterator();

            label44:
            while(true) {
               if (var13.hasNext()) {
                  PacketLoggerCommand.PacketLogEntry loggedPacket = (PacketLoggerCommand.PacketLogEntry)var13.next();
                  fw.append("\n").append(Long.toString(time - loggedPacket.timestamp())).append('\n').append(loggedPacket.side() != null ? loggedPacket.side().name() : "UNKNOWN").append('\n').append(loggedPacket.packet().getClass().getName()).append('\n');
                  this.appendData(fw, loggedPacket.data(), 0);
                  if (loggedPacket.parameters.isEmpty()) {
                     continue;
                  }

                  Iterator var8 = loggedPacket.parameters.iterator();

                  while(true) {
                     if (!var8.hasNext()) {
                        continue label44;
                     }

                     Object parameter = var8.next();
                     fw.append("\t".repeat(4)).append(parameter.toString());
                  }
               }

               fw.close();
               toast = (new GenericToast("Packet log exported", logFile.getName())).setStyle(GenericToast.Style.SYSTEM);
               CactusConstants.mc.method_1566().method_1999(toast);
               break;
            }
         } catch (Throwable var11) {
            try {
               fw.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }

            throw var11;
         }

         fw.close();
      } catch (IOException var12) {
         toast = (new GenericToast("Packet log export failed", "§c" + var12.getMessage())).setStyle(GenericToast.Style.SYSTEM);
         CactusConstants.mc.method_1566().method_1999(toast);
      }

   }

   private void appendData(FileWriter builder, Iterable<?> data, int i) throws IOException {
      Iterator var4 = data.iterator();

      while(true) {
         while(var4.hasNext()) {
            Object o = var4.next();
            if (o instanceof Iterable) {
               Iterable<?> it = (Iterable)o;
               this.appendData(builder, it, i + 1);
            } else if (o instanceof PacketLoggerCommand.PacketFieldData) {
               PacketLoggerCommand.PacketFieldData var7 = (PacketLoggerCommand.PacketFieldData)o;
               PacketLoggerCommand.PacketFieldData var10000 = var7;

               String name;
               Object var17;
               label37: {
                  Throwable var15;
                  label45: {
                     String var16;
                     boolean var10001;
                     try {
                        var16 = var10000.name();
                     } catch (Throwable var12) {
                        var15 = var12;
                        var10001 = false;
                        break label45;
                     }

                     String var10 = var16;
                     name = var10;
                     var10000 = var7;

                     try {
                        var17 = var10000.data();
                        break label37;
                     } catch (Throwable var11) {
                        var15 = var11;
                        var10001 = false;
                     }
                  }

                  Throwable var13 = var15;
                  throw new MatchException(var13.toString(), var13);
               }

               Object var14 = var17;
               builder.append(" ".repeat(i * 2)).append(name).append(": ").append(var14.toString().replace("\n", "\\n")).append('\n');
            }
         }

         return;
      }
   }

   public static int calculateAverageObjectsPerSecond(List<Long> values) {
      if (values != null && !values.isEmpty()) {
         double elapsedTimeInSeconds = (double)((Long)values.getLast() - (Long)values.getFirst()) / 1000.0D;
         return (int)((double)values.size() / elapsedTimeInSeconds);
      } else {
         return 0;
      }
   }

   private static <T> List<Field> getFields(T t) {
      List<Field> fields = new ArrayList();

      for(Class clazz = t.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
         fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
      }

      return fields;
   }

   public static record PacketLogEntry(class_2596<?> packet, long timestamp, List<Object> parameters, class_2598 side, List<PacketLoggerCommand.PacketFieldData> data) {
      public PacketLogEntry(class_2596<?> packet, long timestamp, List<Object> parameters, class_2598 side, List<PacketLoggerCommand.PacketFieldData> data) {
         this.packet = packet;
         this.timestamp = timestamp;
         this.parameters = parameters;
         this.side = side;
         this.data = data;
      }

      public class_2596<?> packet() {
         return this.packet;
      }

      public long timestamp() {
         return this.timestamp;
      }

      public List<Object> parameters() {
         return this.parameters;
      }

      public class_2598 side() {
         return this.side;
      }

      public List<PacketLoggerCommand.PacketFieldData> data() {
         return this.data;
      }
   }

   private static record PacketFieldData(String name, Object data) {
      private PacketFieldData(String name, Object data) {
         this.name = name;
         this.data = data;
      }

      public String name() {
         return this.name;
      }

      public Object data() {
         return this.data;
      }
   }
}
