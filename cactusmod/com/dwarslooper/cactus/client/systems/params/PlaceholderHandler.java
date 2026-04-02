package com.dwarslooper.cactus.client.systems.params;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.SingleInstance;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.game.ServerUtils;
import com.dwarslooper.cactus.client.util.game.TickRateProcessor;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import net.minecraft.class_1074;
import net.minecraft.class_155;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import org.jetbrains.annotations.Nullable;

public class PlaceholderHandler {
   public static String DEFAULT_FORMAT = "{%s}";
   private static final SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
   private final Map<String, PlaceholderHandler.Placeholder> placeholderStaticRegistry = new HashMap();

   public static PlaceholderHandler get() {
      return (PlaceholderHandler)SingleInstance.of(PlaceholderHandler.class, PlaceholderHandler::new, PlaceholderHandler::register);
   }

   public void register() {
      String UNKNOWN = class_1074.method_4662("selectWorld.versionUnknown", new Object[0]);
      this.registerPlaceholder("cactus.version", () -> {
         return CactusConstants.VERSION;
      });
      this.registerPlaceholder("cactus.dev", () -> {
         return CactusConstants.DEVBUILD;
      });
      this.registerPlaceholder("player.name", () -> {
         return CactusConstants.mc.method_1548().method_1676();
      });
      this.registerPlaceholder("player.x", () -> {
         return CactusConstants.mc.field_1724.method_24515().method_10263();
      }, "0");
      this.registerPlaceholder("player.y", () -> {
         return CactusConstants.mc.field_1724.method_24515().method_10264();
      }, "0");
      this.registerPlaceholder("player.z", () -> {
         return CactusConstants.mc.field_1724.method_24515().method_10260();
      }, "0");
      this.registerPlaceholder("player.x.opposite", () -> {
         return PlaceholderHandler.ValueUtils.getOpposite(CactusConstants.mc.field_1724.method_24515().method_10263());
      }, "0");
      this.registerPlaceholder("player.y.opposite", () -> {
         return CactusConstants.mc.field_1724.method_24515().method_10264();
      }, "0");
      this.registerPlaceholder("player.z.opposite", () -> {
         return PlaceholderHandler.ValueUtils.getOpposite(CactusConstants.mc.field_1724.method_24515().method_10260());
      }, "0");
      this.registerPlaceholder("player.chunk.x", () -> {
         return CactusConstants.mc.field_1724.method_31476().field_9181;
      }, "0");
      this.registerPlaceholder("player.chunk.z", () -> {
         return CactusConstants.mc.field_1724.method_31476().field_9180;
      }, "0");
      this.registerPlaceholder("player.chunk.x.offset", () -> {
         return CactusConstants.mc.field_1724.method_24515().method_10263() - CactusConstants.mc.field_1724.method_31476().method_8326();
      }, "0");
      this.registerPlaceholder("player.chunk.z.offset", () -> {
         return CactusConstants.mc.field_1724.method_24515().method_10260() - CactusConstants.mc.field_1724.method_31476().method_8328();
      }, "0");
      this.registerPlaceholder("player.rotation.yaw", () -> {
         return CactusConstants.mc.field_1724.method_36454();
      }, "0");
      this.registerPlaceholder("player.rotation.pitch", () -> {
         return CactusConstants.mc.field_1724.method_36455();
      }, "0");
      this.registerPlaceholder("player.health", () -> {
         return CactusConstants.mc.field_1724.method_6032();
      }, "0");
      this.registerPlaceholder("player.max_health", () -> {
         return CactusConstants.mc.field_1724.method_6063();
      }, "0");
      this.registerPlaceholder("player.hunger", () -> {
         return CactusConstants.mc.field_1724.method_7344().method_7586();
      }, "0");
      this.registerPlaceholder("player.saturation", () -> {
         return CactusConstants.mc.field_1724.method_7344().method_7589();
      }, "0");
      this.registerPlaceholder("player.xp.level", () -> {
         return CactusConstants.mc.field_1724.field_7520;
      }, "0");
      this.registerPlaceholder("player.xp.progress", () -> {
         return CactusConstants.mc.field_1724.field_7510;
      }, "0");
      this.registerPlaceholder("player.velocity.x", () -> {
         return CactusConstants.mc.field_1724.method_18798().field_1352;
      }, "0");
      this.registerPlaceholder("player.velocity.y", () -> {
         return CactusConstants.mc.field_1724.method_18798().field_1351;
      }, "0");
      this.registerPlaceholder("player.velocity.z", () -> {
         return CactusConstants.mc.field_1724.method_18798().field_1350;
      }, "0");
      this.registerPlaceholder("system.os.name", () -> {
         return System.getProperty("os.name");
      }, UNKNOWN);
      this.registerPlaceholder("system.os.arch", () -> {
         return System.getProperty("os.arch");
      }, UNKNOWN);
      this.registerPlaceholder("system.os.version", () -> {
         return System.getProperty("os.version");
      }, UNKNOWN);
      this.registerPlaceholder("system.user.name", () -> {
         return System.getProperty("user.name");
      }, UNKNOWN);
      this.registerPlaceholder("system.user.home", () -> {
         return System.getProperty("user.home");
      }, UNKNOWN);
      this.registerPlaceholder("system.cpu.cores", () -> {
         return Runtime.getRuntime().availableProcessors();
      }, "0");
      this.registerPlaceholder("system.memory.total.mb", () -> {
         return Runtime.getRuntime().totalMemory() / 1024L / 1024L;
      }, "0");
      this.registerPlaceholder("system.memory.free.mb", () -> {
         return Runtime.getRuntime().freeMemory() / 1024L / 1024L;
      }, "0");
      this.registerPlaceholder("system.memory.used.mb", () -> {
         return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L;
      }, "0");
      this.registerPlaceholder("system.memory.max.mb", () -> {
         return Runtime.getRuntime().maxMemory() / 1024L / 1024L;
      }, "0");
      this.registerPlaceholder("jvm.name", () -> {
         return System.getProperty("java.vm.name");
      }, UNKNOWN);
      this.registerPlaceholder("jvm.vendor", () -> {
         return System.getProperty("java.vm.vendor");
      }, UNKNOWN);
      this.registerPlaceholder("jvm.version", () -> {
         return System.getProperty("java.vm.version");
      }, UNKNOWN);
      this.registerPlaceholder("jvm.uptime.ms", () -> {
         return ManagementFactory.getRuntimeMXBean().getUptime();
      }, "0");
      this.registerPlaceholder("jvm.threads.count", () -> {
         return ManagementFactory.getThreadMXBean().getThreadCount();
      }, "0");
      this.registerPlaceholder("jvm.threads.daemon", () -> {
         return ManagementFactory.getThreadMXBean().getDaemonThreadCount();
      }, "0");
      this.registerPlaceholder("jvm.gc.count", () -> {
         return ManagementFactory.getGarbageCollectorMXBeans().stream().mapToLong(GarbageCollectorMXBean::getCollectionCount).sum();
      }, "0");
      this.registerPlaceholder("jvm.gc.time.ms", () -> {
         return ManagementFactory.getGarbageCollectorMXBeans().stream().mapToLong(GarbageCollectorMXBean::getCollectionTime).sum();
      }, "0");
      this.registerPlaceholder("java.version", () -> {
         return System.getProperty("java.version");
      }, UNKNOWN);
      this.registerPlaceholder("world.difficulty", () -> {
         return CactusConstants.mc.field_1687.method_28104().method_207().method_5463().getString();
      });
      this.registerPlaceholder("world.time", () -> {
         return CactusConstants.mc.field_1687.method_28104().method_188();
      }, "0");
      this.registerPlaceholder("world.time.sec", () -> {
         return Utils.getTimeUnits(CactusConstants.mc.field_1687.method_28104().method_188()).seconds();
      }, "0");
      this.registerPlaceholder("world.time.min", () -> {
         return Utils.getTimeUnits(CactusConstants.mc.field_1687.method_28104().method_188()).minutes();
      }, "0");
      this.registerPlaceholder("world.time.hour", () -> {
         return Utils.getTimeUnits(CactusConstants.mc.field_1687.method_28104().method_188()).hours();
      }, "0");
      this.registerPlaceholder("world.time.day", () -> {
         return Utils.getTimeUnits(CactusConstants.mc.field_1687.method_28104().method_188()).days();
      }, "0");
      this.registerPlaceholder("world.time.week", () -> {
         return Utils.getTimeUnits(CactusConstants.mc.field_1687.method_28104().method_188()).weeks();
      }, "0");
      this.registerPlaceholder("world.time.formatted", () -> {
         long[] units = Utils.getTimeUnits(CactusConstants.mc.field_1687.method_28104().method_188()).toArray();
         String[][] names = new String[][]{{"w", "week"}, {"d", "day"}, {"h", "h"}, {"m", "m"}, {"s", "s"}};

         int first;
         for(first = 0; first < units.length && units[first] == 0L; ++first) {
         }

         if (first >= units.length) {
            return "0s";
         } else {
            int second;
            for(second = first + 1; second < units.length && units[second] == 0L; ++second) {
            }

            StringBuilder sb = new StringBuilder();
            sb.append(units[first]).append(names[first][0]);
            if (second < units.length) {
               sb.append(" ").append(units[second]).append(names[second][0]);
            }

            return sb.toString();
         }
      }, "0s");
      this.registerPlaceholder("world.day.ticks", () -> {
         return CactusConstants.mc.field_1687.method_28104().method_217();
      }, "");
      this.registerPlaceholder("world.time", () -> {
         long time = CactusConstants.mc.field_1687.method_28104().method_217();
         if (time >= 0L && time < 6000L) {
            return "Day";
         } else if (time >= 6000L && time < 12000L) {
            return "Noon";
         } else {
            return time >= 12000L && time < 18000L ? "Night" : "Midnight";
         }
      });
      this.registerPlaceholder("world.biome", () -> {
         return CactusConstants.mc.field_1687.method_23753(CactusConstants.mc.field_1724.method_24515()).method_40230().map(class_5321::method_29177).map((ResourceLocation) -> {
            String[] parts = ResourceLocation.toString().split(":");
            String name = parts.length > 1 ? parts[1] : parts[0];
            String var10000 = name.substring(0, 1).toUpperCase();
            return var10000 + name.substring(1).toLowerCase();
         }).orElseThrow();
      }, UNKNOWN);
      this.registerPlaceholder("world.weather", () -> {
         if (CactusConstants.mc.field_1687.method_8546()) {
            return "Thunderstorm";
         } else {
            return CactusConstants.mc.field_1687.method_8419() ? "Rain" : "Clear";
         }
      }, "Clear");
      this.registerPlaceholder("world.weather.rain", () -> {
         return CactusConstants.mc.field_1687.method_8419();
      }, "false");
      this.registerPlaceholder("world.weather.thunder", () -> {
         return CactusConstants.mc.field_1687.method_8546();
      }, "false");
      this.registerPlaceholder("client.fps", () -> {
         return CactusConstants.mc.method_47599();
      }, "100");
      this.registerPlaceholder("client.time", () -> {
         return sdfDate.format(System.currentTimeMillis());
      });
      this.registerPlaceholder("client.version", () -> {
         return CactusConstants.mc.method_1515();
      }, UNKNOWN);
      this.registerPlaceholder("client.protocol", class_155::method_31372, "0");
      this.registerPlaceholder("client.brand", () -> {
         return CactusConstants.mc.method_1547();
      }, UNKNOWN);
      this.registerPlaceholder("client.is_singleplayer", () -> {
         return CactusConstants.mc.method_1542();
      }, "false");
      this.registerPlaceholder("client.render.distance", () -> {
         return CactusConstants.mc.field_1690.method_42503().method_41753();
      }, "0");
      this.registerPlaceholder("client.gui.scale", () -> {
         return CactusConstants.mc.field_1690.method_42474().method_41753();
      }, "0");
      this.registerPlaceholder("client.vsync", () -> {
         return CactusConstants.mc.field_1690.method_42433().method_41753();
      }, "false");
      this.registerPlaceholder("client.fullscreen", () -> {
         return CactusConstants.mc.method_22683().method_4498();
      }, "false");
      this.registerPlaceholder("client.window.width", () -> {
         return CactusConstants.mc.method_22683().method_4489();
      }, "0");
      this.registerPlaceholder("client.window.height", () -> {
         return CactusConstants.mc.method_22683().method_4506();
      }, "0");
      this.registerPlaceholder("server.tps", () -> {
         return Utils.isInWorld() ? String.format(Locale.ROOT, "%.2f", TickRateProcessor.INSTANCE.getTickRate()) : null;
      }, "20.0");
      this.registerPlaceholder("server.ping", ServerUtils::ping, "0");
      this.registerPlaceholder("server.name", () -> {
         if (CactusConstants.mc.method_1558() != null) {
            return CactusConstants.mc.method_1558().field_3752;
         } else {
            return CactusConstants.mc.method_1496() ? Utils.clearFormattingChars(CactusConstants.mc.method_1576().method_27728().method_150()) : class_1074.method_4662("selectServer.defaultName", new Object[0]);
         }
      });
      this.registerPlaceholder("server.address", () -> {
         return CactusConstants.mc.method_1558() != null ? CactusConstants.mc.method_1558().field_3761 : Utils.clearFormattingChars(CactusConstants.mc.method_1576().method_27728().method_150());
      }, "cactusmod.xyz");
      this.registerPlaceholder("server.brand", () -> {
         return CactusConstants.mc.method_1562().method_52790();
      }, UNKNOWN);
      this.registerPlaceholder("server.brand.short", () -> {
         return CactusConstants.mc.method_1562().method_52790().split(" ")[0];
      }, UNKNOWN);
      this.registerPlaceholder("server.packets.in", () -> {
         return Math.round(CactusConstants.mc.method_1562().method_48296().method_10762());
      }, "0");
      this.registerPlaceholder("server.packets.out", () -> {
         return Math.round(CactusConstants.mc.method_1562().method_48296().method_10745());
      }, "0");
      CactusClient.getInstance().getAddonHandler().getRegistryBus().completeAndTake(PlaceholderHandler.PlaceholderRegistration.class, (r) -> {
         this.registerPlaceholder(r.name(), r.value());
      });
   }

   private void registerPlaceholder(String name, PlaceholderHandler.PlaceholderGetter valSupplier) {
      this.registerPlaceholder(name, valSupplier, (String)null);
   }

   private void registerPlaceholder(String name, PlaceholderHandler.PlaceholderGetter valSupplier, @Nullable String fallback) {
      this.registerPlaceholder(name, valSupplier, fallback, () -> {
         return true;
      });
   }

   private void registerPlaceholder(String name, PlaceholderHandler.PlaceholderGetter valSupplier, @Nullable String fallback, PlaceholderHandler.PlaceholderValidator validator) {
      this.registerPlaceholder(name, (PlaceholderHandler.Placeholder)(new PlaceholderHandler.StaticPlaceholderValue(valSupplier, (String)Utils.orElse(fallback, name), validator)));
   }

   private void registerPlaceholder(String name, PlaceholderHandler.Placeholder value) {
      this.placeholderStaticRegistry.put(name, value);
   }

   public void addOrUpdateDynamic(class_2960 id, String content, String requiredState) {
      this.placeholderStaticRegistry.compute(id.toString(), (n, c) -> {
         if (c == null) {
            c = new PlaceholderHandler.DynamicPlaceholderValue(content, requiredState);
         } else {
            if (!(c instanceof PlaceholderHandler.DynamicPlaceholderValue)) {
               throw new IllegalArgumentException("Identifier '%s' conflicts with existing non-dynamic placeholder");
            }

            PlaceholderHandler.DynamicPlaceholderValue dynamic = (PlaceholderHandler.DynamicPlaceholderValue)c;
            dynamic.setContent(content);
         }

         return (PlaceholderHandler.Placeholder)c;
      });
   }

   public void clearDynamic() {
      this.placeholderStaticRegistry.entrySet().removeIf((entry) -> {
         return entry.getValue() instanceof PlaceholderHandler.DynamicPlaceholderValue;
      });
   }

   public String getPlaceholder(String name) {
      return (String)Utils.orElse((PlaceholderHandler.Placeholder)this.placeholderStaticRegistry.get(name), PlaceholderHandler.Placeholder::get, name);
   }

   public String replaceConditionalPlaceholders(String text) {
      return this.replaceConditionalPlaceholders(text, DEFAULT_FORMAT);
   }

   public String replaceConditionalPlaceholders(String text, String format) {
      String filtered = RequirementChecker.processConditionalString(text);
      return (String)Utils.orElse(filtered, (s) -> {
         return this.replacePlaceholders(text, format);
      }, (Object)null);
   }

   public String replacePlaceholders(String text) {
      return this.replacePlaceholders(text, DEFAULT_FORMAT);
   }

   public String replacePlaceholders(String text, String format) {
      Iterator var3 = this.placeholderStaticRegistry.keySet().iterator();

      while(var3.hasNext()) {
         String name = (String)var3.next();

         try {
            text = text.replace(format.formatted(new Object[]{name}), this.getPlaceholder(name));
         } catch (Exception var6) {
         }
      }

      return text;
   }

   @FunctionalInterface
   public interface PlaceholderGetter {
      Object get() throws Exception;
   }

   public static record PlaceholderRegistration(String name, PlaceholderHandler.Placeholder value) {
      public PlaceholderRegistration(String name, PlaceholderHandler.Placeholder value) {
         this.name = name;
         this.value = value;
      }

      public String name() {
         return this.name;
      }

      public PlaceholderHandler.Placeholder value() {
         return this.value;
      }
   }

   public interface PlaceholderValidator {
      boolean get() throws Exception;
   }

   public static class StaticPlaceholderValue implements PlaceholderHandler.Placeholder {
      private final PlaceholderHandler.PlaceholderGetter getter;
      private final String fallback;
      private final PlaceholderHandler.PlaceholderValidator validator;

      public StaticPlaceholderValue(PlaceholderHandler.PlaceholderGetter getter, String fallback, PlaceholderHandler.PlaceholderValidator validator) {
         this.getter = getter;
         this.fallback = fallback;
         this.validator = validator;
      }

      public String get() {
         try {
            Object o = this.getter.get();
            String var10000;
            if (o instanceof String) {
               String s = (String)o;
               var10000 = s;
            } else {
               var10000 = o == null ? this.fallback : o.toString();
            }

            return var10000;
         } catch (Exception var3) {
            return this.fallback;
         }
      }

      public boolean isSet() {
         try {
            return this.validator.get();
         } catch (Exception var2) {
            return false;
         }
      }
   }

   public interface Placeholder {
      String get();

      boolean isSet();
   }

   private static class DynamicPlaceholderValue implements PlaceholderHandler.Placeholder {
      public String content;
      private final String requiredState;

      public DynamicPlaceholderValue(String content, String requiredState) {
         this.content = content;
         this.requiredState = requiredState;
      }

      public void setContent(String content) {
         this.content = content;
      }

      public String get() {
         return this.content;
      }

      public boolean isSet() {
         return "SERVER_STATE".equals(this.requiredState);
      }
   }

   private static class ValueUtils {
      public static int getOpposite(int pos) {
         if (CactusConstants.mc.field_1687 != null) {
            double v = CactusConstants.mc.field_1687.method_8597().comp_646();
            if (v == 8.0D) {
               return pos * 8;
            }

            if (v == 1.0D) {
               return pos / 8;
            }
         }

         return pos;
      }
   }
}
