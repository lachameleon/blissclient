package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.impl.TestEvent;
import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.command.arguments.FileArgumentType;
import com.dwarslooper.cactus.client.feature.command.arguments.PlayerListEntryArgumentType;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.PacketLogsScreen;
import com.dwarslooper.cactus.client.gui.toast.ToastSystem;
import com.dwarslooper.cactus.client.gui.toast.internal.GenericToast;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.ClientCosmeticListS2CPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.GetAvailableCosmeticsC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.UpdateCosmeticsC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.UseEmoteC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.security.ServerCheckBiDPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.social.JoinMeRequestPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.teams.TeamMembershipUpdateC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.teams.TeamRequestPacket;
import com.dwarslooper.cactus.client.mixins.accessor.KeyboardHandlerAccessor;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.systems.config.impl.SavedLevelsConfig;
import com.dwarslooper.cactus.client.systems.ias.account.Auth;
import com.dwarslooper.cactus.client.systems.ias.skins.SkinHelper;
import com.dwarslooper.cactus.client.systems.teams.TeamDataType;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.dwarslooper.cactus.client.util.generic.MathUtils;
import com.dwarslooper.cactus.client.util.generic.TextUtils;
import com.dwarslooper.cactus.client.util.networking.HttpUtils;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.serialization.JsonOps;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import net.minecraft.class_1044;
import net.minecraft.class_10799;
import net.minecraft.class_1109;
import net.minecraft.class_11735;
import net.minecraft.class_12225;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1806;
import net.minecraft.class_1937;
import net.minecraft.class_2172;
import net.minecraft.class_22;
import net.minecraft.class_2246;
import net.minecraft.class_2287;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_268;
import net.minecraft.class_2873;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3417;
import net.minecraft.class_3620;
import net.minecraft.class_412;
import net.minecraft.class_4185;
import net.minecraft.class_5244;
import net.minecraft.class_5250;
import net.minecraft.class_5481;
import net.minecraft.class_5489;
import net.minecraft.class_639;
import net.minecraft.class_640;
import net.minecraft.class_642;
import net.minecraft.class_7532;
import net.minecraft.class_8824;
import net.minecraft.class_9112;
import net.minecraft.class_3620.class_6594;
import net.minecraft.class_642.class_8678;

public class TestCommand extends Command {
   public static class_243 marker;
   public static class_1792 FUCKING_ICON_WTF;
   public static boolean DISALLOW_ASYNC_SCREENSHOT_SAVING;

   public TestCommand() {
      super("toastbrot");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(literal("toast").then(argument("title", StringArgumentType.string()).then(argument("description", StringArgumentType.string()).then(argument("system", BoolArgumentType.bool()).then(argument("item", class_2287.method_9776(commandRegistryAccess)).executes((context) -> {
         String title = StringArgumentType.getString(context, "title");
         String desc = StringArgumentType.getString(context, "description");
         boolean system = BoolArgumentType.getBool(context, "system");
         class_1792 stack = class_2287.method_9777(context, "item").method_9785();
         GenericToast toast = (new GenericToast(title, desc)).setStyle(system ? GenericToast.Style.SYSTEM : GenericToast.Style.DEFAULT_DARK).setIcon(stack == class_1802.field_8162 ? null : stack);
         CactusConstants.mc.method_1566().method_1999(toast);
         return 1;
      }))))))).then(literal("checkup").then(argument("ip", StringArgumentType.string()).executes((context) -> {
         CactusClient.getInstance().getIrcClient().sendPacket(new ServerCheckBiDPacket(StringArgumentType.getString(context, "ip")));

         for(int i = 0; i < 47; ++i) {
            class_1799 stack = new class_1799(class_1802.field_8725);
            stack.method_7939(i + 1);
            CactusConstants.mc.field_1724.field_3944.method_52787(new class_2873(i, stack));
         }

         return 1;
      })))).then(((LiteralArgumentBuilder)literal("mapdata").then(literal("to").executes((context) -> {
         return 1;
      }))).then(literal("from").executes((context) -> {
         return 1;
      })))).then(literal("event").executes((context) -> {
         CactusClient.EVENT_BUS.post(new TestEvent());
         return 1;
      }))).then(literal("tomapcolor").executes((context) -> {
         try {
            Color color = new Color(CactusClient.getInstance().getRGB());
            Field field = class_3620.class.getDeclaredField("COLORS");
            field.setAccessible(true);
            class_3620[] colors = (class_3620[])field.get((Object)null);
            List<class_3620> l = new ArrayList(Arrays.asList(colors));
            l.removeIf(Objects::isNull);
            System.out.println(new ArrayList(l));
            class_3620 closest = (class_3620)l.stream().min((c1, c2) -> {
               Color color1 = new Color(c1.field_16011);
               Color color2 = new Color(c2.field_16011);
               double distance1 = calculateDistance(color, color1);
               double distance2 = calculateDistance(color, color2);
               return Double.compare(distance1, distance2);
            }).orElseThrow(() -> {
               return new IllegalArgumentException("Color list is empty");
            });
            ChatUtils.info((class_2561)class_2561.method_43470("Closest color to the original ").method_10852(class_2561.method_43470("█").method_27694((style) -> {
               return style.method_36139(color.getRGB());
            }).method_27693(" is the map color ").method_10852(class_2561.method_43470("█").method_27694((style) -> {
               return style.method_36139(closest.field_16011);
            }).method_27693(" with the ID " + closest.field_16021))));
            class_1799 stack = CactusConstants.mc.field_1724.method_6047();
            class_22 state = null;
            if (stack.method_7909() == class_1802.field_8204) {
               state = class_1806.method_8001(stack, CactusConstants.mc.field_1687);
            }

            File file = new File(CactusConstants.DIRECTORY, "input.png");
            BufferedImage image = ImageIO.read(file);
            int width = image.getWidth();
            int height = image.getHeight();
            int[] pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);

            for(int i = 0; i < pixels.length; ++i) {
               int pixel = pixels[i];
               int alpha = pixel >> 24 & 255;
               int red = pixel >> 16 & 255;
               int green = pixel >> 8 & 255;
               int blue = pixel & 255;
               class_3620 imgClosest = closestColor(l, new Color(red, green, blue));
               Color c = new Color(imgClosest.field_16011);
               red = c.getRed();
               green = c.getGreen();
               blue = c.getBlue();
               pixels[i] = alpha << 24 | red << 16 | green << 8 | blue;
               if (state != null && state.field_122.length > i) {
                  state.field_122[i] = imgClosest.method_38481(class_6594.field_34760);
               }
            }

            BufferedImage modifiedImage = new BufferedImage(width, height, 2);
            modifiedImage.setRGB(0, 0, width, height, pixels, 0, width);
            File outputFile = new File("output.png");
            ImageIO.write(modifiedImage, "png", outputFile);
         } catch (Exception var21) {
            CactusClient.getLogger().error("Failed to map image colors to map", var21);
         }

         return 1;
      }))).then(literal("joinme").then(argument("ip", StringArgumentType.string()).then(argument("username", StringArgumentType.string()).executes((context) -> {
         String address = StringArgumentType.getString(context, "ip");
         CompletableFuture.supplyAsync(() -> {
            return Auth.resolveUUID(StringArgumentType.getString(context, "username"));
         }).thenAccept((uuid) -> {
            CactusClient.getInstance().getIrcClient().sendPacket(new JoinMeRequestPacket(uuid, address));
         });
         return 1;
      }))))).then(literal("panorama").executes((context) -> {
         DISALLOW_ASYNC_SCREENSHOT_SAVING = true;
         File dir = new File(CactusConstants.DIRECTORY, "panorama");
         dir.mkdirs();
         CactusConstants.mc.execute(() -> {
            ChatUtils.info("Panorama feature temporarily disabled");
         });
         DISALLOW_ASYNC_SCREENSHOT_SAVING = false;
         return 1;
      }))).then(literal("text-serialization").executes((context) -> {
         ChatUtils.info((class_2561)class_8824.field_46597.parse(JsonOps.INSTANCE, new JsonPrimitive("{\"text\":\"Hello World!\",\"color\":\"red\"}")).result().orElse(class_2561.method_43470("Parse failed")));
         return 1;
      }))).then(literal("curse-you-textures-the-platypus").then(argument("first", StringArgumentType.string()).then(argument("second", StringArgumentType.string()).executes((context) -> {
         try {
            class_2960 id1 = class_2960.method_60654(StringArgumentType.getString(context, "first"));
            class_2960 id2 = class_2960.method_60654(StringArgumentType.getString(context, "second"));
            class_1044 texture = CactusConstants.mc.method_1531().method_4619(id1);
            CactusConstants.mc.method_1531().method_4616(id2, texture);
            CactusClient.getLogger().info("I used the texture to destroy the texture");
            CactusClient.getLogger().info("Bound {} to {}", id1, id2);
         } catch (Exception var4) {
            ChatUtils.error("Error: " + String.valueOf(var4));
            CactusClient.getLogger().error("Hmmm", var4);
         }

         return 1;
      }))))).then(literal("copywithavailabledata").then(argument("query", BoolArgumentType.bool()).executes((context) -> {
         ((KeyboardHandlerAccessor)CactusConstants.mc.field_1774).copyTarget(true, BoolArgumentType.getBool(context, "query"));
         return 1;
      })))).then(literal("cosmetics").then(argument("ids", StringArgumentType.greedyString()).executes((context) -> {
         List<String> ids = Arrays.asList(StringArgumentType.getString(context, "ids").split(" "));
         CactusClient.getInstance().getIrcClient().sendPacket(new UpdateCosmeticsC2SPacket(ids));
         return 1;
      })))).then(literal("list-cosmetics").executes((context) -> {
         ClientCosmeticListS2CPacket.addCallback().whenComplete((packet, error) -> {
            if (error == null) {
               packet.getCosmetics().forEach((c) -> {
                  ChatUtils.info("[%s] %s (%s) %s".formatted(new Object[]{c.getParser().name(), c.getName(), c.getId(), c.getGroup() == null ? "" : "{%s}".formatted(new Object[]{c.getGroup()})}));
               });
            } else {
               CactusClient.getLogger().error("Failed callback for cosmetic list", error);
            }

         });
         CactusClient.getInstance().getIrcClient().sendPacket(new GetAvailableCosmeticsC2SPacket());
         return 1;
      }))).then(literal("uuid-client").then(argument("player", PlayerListEntryArgumentType.playerListEntry()).executes((context) -> {
         class_640 e = PlayerListEntryArgumentType.getPlayerListEntry(context);
         ChatUtils.info("Client-known UUID of %s is %s".formatted(new Object[]{e.method_2971(), e.method_2966().id()}));
         return 1;
      })))).then(literal("set-marker").executes((context) -> {
         marker = CactusConstants.mc.field_1724.method_33571();
         return 1;
      }))).then(literal("whoami").executes((context) -> {
         if (IRCClient.connected()) {
            CompletableFuture.supplyAsync(() -> {
               try {
                  return HttpUtils.fetchJson("http://127.0.0.1:1415/user/whoami/");
               } catch (Exception var1) {
                  CactusClient.getLogger().error("whoami http request failed", var1);
                  return null;
               }
            }).thenAccept((object) -> {
               if (object != null) {
                  ChatUtils.infoPrefix("WHOAMI", object.toString());
               }

            }).exceptionally((throwable) -> {
               ChatUtils.errorPrefix("WHOAMI", "The server was unable to find the request's origin. (message=%s)".formatted(new Object[]{throwable.getMessage()}));
               CactusClient.getLogger().error("whoami http request failed", throwable);
               return null;
            });
         }

         return 1;
      }))).then(literal("item-oder-so").executes((context) -> {
         FUCKING_ICON_WTF = CactusConstants.mc.field_1724.method_6047().method_7909();
         return 1;
      }))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)literal("request-team").then(literal("states").executes((context) -> {
         return 1;
      }))).then(literal("data").then(((RequiredArgumentBuilder)argument("id", IntegerArgumentType.integer()).then(literal("info").executes((context) -> {
         CactusClient.getInstance().getIrcClient().sendPacket(new TeamRequestPacket(IntegerArgumentType.getInteger(context, "id"), TeamDataType.INFO));
         return 1;
      }))).then(literal("files").executes((context) -> {
         CactusClient.getInstance().getIrcClient().sendPacket(new TeamRequestPacket(IntegerArgumentType.getInteger(context, "id"), TeamDataType.FILES));
         return 1;
      }))))).then(literal("membership").then(argument("id", IntegerArgumentType.integer()).then(argument("val", BoolArgumentType.bool()).executes((context) -> {
         int id = IntegerArgumentType.getInteger(context, "id");
         boolean val = BoolArgumentType.getBool(context, "val");
         CactusClient.getInstance().getIrcClient().sendPacket(new TeamMembershipUpdateC2SPacket(id, val));
         return 1;
      })))))).then(literal("clippy").then(argument("time", IntegerArgumentType.integer()).then(argument("text", StringArgumentType.greedyString()).executes((context) -> {
         String text = StringArgumentType.getString(context, "text");
         Utils.unsafeDelayed(() -> {
            CactusConstants.mc.method_1507(new TestCommand.ClippyScreen(text));
         }, 100L + (long)IntegerArgumentType.getInteger(context, "time") * 1000L);
         return 1;
      }))))).then(((LiteralArgumentBuilder)literal("quality-gradient").then(argument("value", IntegerArgumentType.integer(0, 100)).executes((context) -> {
         ChatUtils.infoPrefix("QA", MathUtils.createGradientText((double)IntegerArgumentType.getInteger(context, "value"), 0.0D, 100.0D));
         return 1;
      }))).executes((context) -> {
         for(int i = 0; i <= 100; i += 10) {
            ChatUtils.infoPrefix("QA", MathUtils.createGradientText((double)i, 0.0D, 100.0D));
         }

         return 1;
      }))).then(literal("numeric").then(argument("allowempty", BoolArgumentType.bool()).then(argument("allownegative", BoolArgumentType.bool()).then(argument("value", StringArgumentType.string()).executes((context) -> {
         boolean allowEmpty = BoolArgumentType.getBool(context, "allowempty");
         boolean allowNegative = BoolArgumentType.getBool(context, "allownegative");
         String value = StringArgumentType.getString(context, "value");
         boolean var10000 = TextUtils.isNumeric(value, allowEmpty, allowNegative);
         ChatUtils.info("Result: " + var10000);
         return 1;
      })))))).then(literal("color").executes((context) -> {
         ChatUtils.infoPrefix("§dLOL?", "Hello World");
         return 1;
      }))).then(literal("cleanworldsaves").executes((context) -> {
         ((SavedLevelsConfig)CactusClient.getConfig(SavedLevelsConfig.class)).clean();
         ChatUtils.infoPrefix("SLC", "§aCleaned up world saves!");
         return 1;
      }))).then(literal("filtersave").then(argument("paths", StringArgumentType.greedyString()).executes((context) -> {
         List<String> paths = Arrays.asList(StringArgumentType.getString(context, "paths").split(" "));
         TreeSerializerFilter filter = new TreeSerializerFilter("", new HashSet(paths), TreeSerializerFilter.PathValidatorFunction.DEFAULT);
         CactusClient.getInstance().getConfigHandler().save(CactusClient.getConfig(ModuleManager.class), new File(CactusConstants.DIRECTORY, "filtered_configurations"), filter);
         ChatUtils.infoPrefix("Config Filter", "§aSaved!");
         return 1;
      })))).then(literal("emote").then(argument("emote", StringArgumentType.string()).executes((context) -> {
         String emote = StringArgumentType.getString(context, "emote");
         CactusClient.getInstance().getIrcClient().sendPacket(new UseEmoteC2SPacket(emote));
         return 1;
      })))).then(literal("packetlogs").then(argument("file", FileArgumentType.file(new File(CactusConstants.DIRECTORY, "packet-logger"))).executes((context) -> {
         File file = FileArgumentType.getFile(context, "file");
         Utils.unsafeDelayed(() -> {
            CactusConstants.mc.method_1507(new PacketLogsScreen(file));
         }, 200L);
         return 1;
      })))).then(literal("listscoreboardteams").executes((context) -> {
         Iterator var1 = CactusConstants.mc.field_1724.field_3944.method_55823().method_1159().iterator();

         while(var1.hasNext()) {
            class_268 team = (class_268)var1.next();
            ChatUtils.info(team.method_1197());
         }

         return 1;
      }))).then(literal("savetextures").then(argument("player", PlayerListEntryArgumentType.playerListEntry()).executes((context) -> {
         class_640 entry = PlayerListEntryArgumentType.getPlayerListEntry(context);

         try {
            Collection<Property> textures = entry.method_2966().properties().get("textures");
            Property property = (Property)textures.iterator().next();
            File file = new File(CactusConstants.DIRECTORY, "profiletextures.txt");
            CompletableFuture.runAsync(() -> {
               try {
                  Path var10000 = file.toPath();
                  String var10001 = property.value();
                  Files.write(var10000, (var10001 + "\n" + property.signature() + "\n\n").getBytes(), new OpenOption[]{StandardOpenOption.APPEND});
                  ChatUtils.info("Saved textures of " + entry.method_2966().name());
               } catch (IOException var4) {
                  ChatUtils.error("File write failed: " + var4.getMessage());
                  var4.printStackTrace();
               }

            });
            ChatUtils.info("Property name: " + property.name());
         } catch (Exception var5) {
            ChatUtils.error("Failed, see console.");
            var5.printStackTrace();
         }

         return 1;
      })));
   }

   private static class_3620 closestColor(List<class_3620> colors, Color current) {
      return (class_3620)colors.stream().min((c1, c2) -> {
         Color color1 = new Color(c1.field_16011);
         Color color2 = new Color(c2.field_16011);
         double distance1 = calculateDistance(current, color1);
         double distance2 = calculateDistance(current, color2);
         return Double.compare(distance1, distance2);
      }).orElse(class_3620.field_16009);
   }

   private static double calculateDistance(Color c1, Color c2) {
      int redDiff = c1.getRed() - c2.getRed();
      int greenDiff = c1.getGreen() - c2.getGreen();
      int blueDiff = c1.getBlue() - c2.getBlue();
      return Math.sqrt((double)(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff));
   }

   public static Set<class_2338> findConnectedRedstoneWires(class_1937 world, class_2338 start) {
      Set<class_2338> visited = new HashSet();
      Set<class_2338> redstoneWires = new HashSet();
      Map<Integer, List<class_2338>> wireLines = new HashMap();
      findConnectedRedstoneWiresRecursive(world, start, visited, redstoneWires, wireLines, 0);
      Iterator var5 = wireLines.values().iterator();

      while(var5.hasNext()) {
         List<class_2338> line = (List)var5.next();
         int count = 0;

         for(Iterator var8 = line.iterator(); var8.hasNext(); ++count) {
            class_2338 wirePos = (class_2338)var8.next();
            if (count % 15 == 0) {
               System.out.println("Position of every 15th block: " + String.valueOf(wirePos));
            }
         }
      }

      return redstoneWires;
   }

   private static void findConnectedRedstoneWiresRecursive(class_1937 world, class_2338 pos, Set<class_2338> visited, Set<class_2338> redstoneWires, Map<Integer, List<class_2338>> wireLines, int currentLine) {
      if (!visited.contains(pos) && world.method_8320(pos).method_27852(class_2246.field_10091)) {
         visited.add(pos);
         redstoneWires.add(pos);
         ((List)wireLines.computeIfAbsent(currentLine, (k) -> {
            return new ArrayList();
         })).add(pos);
         Iterator var6 = getNeighbors(pos).iterator();

         class_2338 below;
         while(var6.hasNext()) {
            below = (class_2338)var6.next();
            findConnectedRedstoneWiresRecursive(world, below, visited, redstoneWires, wireLines, currentLine);
         }

         class_2338 above = pos.method_10084();
         below = pos.method_10074();
         if (world.method_8320(above).method_27852(class_2246.field_10091)) {
            findConnectedRedstoneWiresRecursive(world, above, visited, redstoneWires, wireLines, currentLine);
         }

         if (world.method_8320(below).method_27852(class_2246.field_10091)) {
            findConnectedRedstoneWiresRecursive(world, below, visited, redstoneWires, wireLines, currentLine);
         }

      }
   }

   private static Set<class_2338> getNeighbors(class_2338 pos) {
      Set<class_2338> neighbors = new HashSet();
      neighbors.add(pos.method_10095());
      neighbors.add(pos.method_10072());
      neighbors.add(pos.method_10078());
      neighbors.add(pos.method_10067());
      return neighbors;
   }

   static {
      FUCKING_ICON_WTF = class_1802.field_8434;
      DISALLOW_ASYNC_SCREENSHOT_SAVING = false;
   }

   public static class ClippyScreen extends CScreen {
      private static final class_2960 DEMO_BG = class_2960.method_60656("textures/gui/demo_background.png");
      private class_5489 movementText;
      private class_5489 fullWrappedText;
      private String lol;

      public ClippyScreen(String text) {
         super("clippy");
         this.lol = text;
      }

      public void method_25426() {
         super.method_25426();
         this.movementText = class_5489.method_30890(this.field_22793, class_2561.method_43470(this.lol), 180);
         this.fullWrappedText = class_5489.method_30890(this.field_22793, class_2561.method_43470("Was "), 218);
         int i = true;
         this.method_37063(class_4185.method_46430(class_5244.field_24336, (button) -> {
            button.field_22763 = false;
            ToastSystem.displayMessage("Thank you", "Your feedback helps us improve.");
         }).method_46434(this.field_22789 / 2 - 116, this.field_22790 / 2 + 62 - 10, 114, 20).method_46431());
         this.method_37063(class_4185.method_46430(class_5244.field_24337, (button) -> {
            button.field_22763 = false;
            ToastSystem.displayMessage("Thank you", "Your feedback helps us improve.");
         }).method_46434(this.field_22789 / 2 + 2, this.field_22790 / 2 + 62 - 10, 114, 20).method_46431());
      }

      public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {
         super.method_25420(context, mouseX, mouseY, delta);
         int i = (this.field_22789 - 248) / 2;
         int j = (this.field_22790 - 166) / 2;
         context.method_25290(class_10799.field_56883, DEMO_BG, i, j, 0.0F, 0.0F, 248, 166, 256, 256);
         context.method_25290(class_10799.field_56883, class_2960.method_60655("cactus", "textures/clippy.png"), (this.field_22789 + 248) / 2 - 9 - 40, j + 8, 0.0F, 0.0F, 40, 40, 40, 40);
      }

      public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
         super.method_25394(context, mouseX, mouseY, delta);
         int i = (this.field_22789 - 248) / 2 + 10;
         int j = (this.field_22790 - 166) / 2 + 8;
         context.method_51433(this.field_22793, "Clippy's Suggestion", i, j, 2039583, false);
         class_12225 textConsumer = context.method_75788();
         Objects.requireNonNull(this.field_22793);
         int lineHeight = 9;
         int y = j + 12;
         this.movementText.method_75816(class_11735.field_62009, i, y, lineHeight, textConsumer);
         y += this.movementText.method_30887() * lineHeight + 12;
         this.fullWrappedText.method_75816(class_11735.field_62009, i, y, lineHeight, textConsumer);
         y += this.fullWrappedText.method_30887() * lineHeight + 8;
         context.method_51433(this.field_22793, "§nBuy PREMIUM§r for more AI-powered features", i, y, 2039583, false);
         context.method_51433(this.field_22793, "Was this suggestions helpful?", i, (this.field_22790 + 166) / 2 - 9 - 32, 2039583, false);
      }
   }

   public static class JoinMeToast extends GenericToast {
      private final String playerName;
      private final String address;

      public JoinMeToast(long duration, String playerName, String address) {
         super(class_2561.method_43470("JoinMe"), (class_5250)class_2561.method_43470("%s invited you to join them on %s".formatted(new Object[]{playerName, address.length() > 32 ? address.substring(address.length() - 32) : address})), duration);
         this.playerName = playerName;
         this.address = address;
         CactusConstants.mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_14793, 1.0F));
      }

      public void drawToast(class_332 context, class_327 textRenderer, long time, int x, int y, double mouseX, double mouseY) {
         context.method_25294(0, 0, this.method_29049(), this.method_29050(), -14408668);
         context.method_25294(2, 2, this.method_29049() - 2, this.method_29050() - 2, -15592942);
         class_7532.method_52722(context, SkinHelper.getCachedSkinOrFetch(CactusConstants.mc.method_53462().id()), 4, (this.method_29050() - 16) / 2, 16);

         for(int i = 0; i < this.lines.size(); ++i) {
            class_327 var10001 = CactusConstants.mc.field_1772;
            class_5481 var10002 = (class_5481)this.lines.get(i);
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_51430(var10001, var10002, 24, 6 + i * 9, -1, false);
         }

         String var14 = mouseX > (double)(x + 24) && mouseX < (double)(x + 24 + 12) && mouseY > (double)(y + this.method_29050() - 18) && mouseY < (double)(y + this.method_29050() - 18 + 12) ? "_highlighted" : "";
         context.method_52706(class_10799.field_56883, class_2960.method_60654("pending_invite/accept" + var14), 24, this.method_29050() - 18, 12, 12);
         var14 = mouseX > (double)(x + 24 + 20) && mouseX < (double)(x + 24 + 20 + 12) && mouseY > (double)(y + this.method_29050() - 18) && mouseY < (double)(y + this.method_29050() - 18 + 12) ? "_highlighted" : "";
         context.method_52706(class_10799.field_56883, class_2960.method_60654("pending_invite/reject" + var14), 44, this.method_29050() - 18, 12, 12);
         double pv = Math.min(1.0D, (double)time / (double)this.duration);
         context.method_51738(2, (int)((double)(this.method_29049() - 4) * pv), this.method_29050() - 3, CactusClient.getInstance().getRGB());
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         if (mouseX > 44.0D && mouseX < 56.0D && mouseY > (double)(this.method_29050() - 18) && mouseY < (double)(this.method_29050() - 18 + 12)) {
            this.close();
            return true;
         } else if (mouseX > 24.0D && mouseX < 36.0D && mouseY > (double)(this.method_29050() - 18) && mouseY < (double)(this.method_29050() - 18 + 12)) {
            this.close();
            if (CactusConstants.mc.field_1687 != null) {
               CactusConstants.mc.method_73360(class_2561.method_43470("Disconnecting"));
            }

            class_412.method_36877(CactusConstants.mc.field_1755, CactusConstants.mc, class_639.method_2950(this.address), new class_642("JoinMe/%s".formatted(new Object[]{this.playerName}), this.address, class_8678.field_45611), false, (class_9112)null);
            return true;
         } else {
            return false;
         }
      }

      public int method_29049() {
         return this.width + 24;
      }

      public int method_29050() {
         int var10001 = Math.max(this.lines.size(), 1);
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         return 24 + var10001 * 9;
      }

      public long getDuration() {
         return 10000L;
      }
   }
}
