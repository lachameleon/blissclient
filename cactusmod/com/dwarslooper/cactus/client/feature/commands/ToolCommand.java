package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.event.CRunnableClickEvent;
import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.ActionEvent;
import com.dwarslooper.cactus.client.event.impl.InteractBlockEvent;
import com.dwarslooper.cactus.client.event.impl.InteractEntityEvent;
import com.dwarslooper.cactus.client.event.impl.InteractItemEvent;
import com.dwarslooper.cactus.client.event.impl.MessageReceiveEvent;
import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.command.arguments.MacroArgumentType;
import com.dwarslooper.cactus.client.feature.macro.Macro;
import com.dwarslooper.cactus.client.feature.macro.MacroManager;
import com.dwarslooper.cactus.client.gui.screen.impl.MacroListScreen;
import com.dwarslooper.cactus.client.systems.SingleInstance;
import com.dwarslooper.cactus.client.systems.config.settings.impl.StringListSetting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.dwarslooper.cactus.client.util.game.PositionUtils;
import com.dwarslooper.cactus.client.util.game.ServerUtils;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.class_124;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_156;
import net.minecraft.class_1799;
import net.minecraft.class_2172;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2588;
import net.minecraft.class_2680;
import net.minecraft.class_2769;
import net.minecraft.class_3965;
import net.minecraft.class_437;
import net.minecraft.class_5250;
import net.minecraft.class_7417;
import net.minecraft.class_746;
import net.minecraft.class_9279;
import net.minecraft.class_9290;
import net.minecraft.class_9326;
import net.minecraft.class_9334;
import org.lwjgl.glfw.GLFW;

public class ToolCommand extends Command {
   public static ToolCommand.Handler HANDLER = new ToolCommand.Handler();
   private static final SimpleCommandExceptionType NO_ITEM = new SimpleCommandExceptionType(class_2561.method_43471("commands.tool.noItem"));
   private static final SimpleCommandExceptionType CREATIVE_REQUIRED = new SimpleCommandExceptionType(class_2561.method_43471("command.creativeModeRequired"));

   public ToolCommand() {
      super("tool");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(literal("unbind").executes((context) -> {
         class_1799 stack = ToolCommand.Registry.requireItemAndConditionsOrThrow();
         stack.method_59692(class_9326.field_49588);
         return 1;
      }));
      LiteralArgumentBuilder<class_2172> bindBuilder = literal("bind");
      ToolCommand.Registry.appendAll(bindBuilder);
      builder.then(bindBuilder);
   }

   public static enum Registry {
      SIMPLE_COMMAND("command", (me, builder) -> {
         ((LiteralArgumentBuilder)builder.executes((context) -> {
            class_1799 stack = requireItemAndConditionsOrThrow();
            class_2487 cactusToolData = getToolData(stack);
            if (cactusToolData.method_10545("command")) {
               ChatUtils.info((class_2561)getTranslatable(me, "lookupSuccess", (String)null, cactusToolData.method_10558("command")));
            } else {
               ChatUtils.error((class_2561)getTranslatable(me, "lookupFail", (String)null));
            }

            return 1;
         })).then(ToolCommand.argument("command", StringArgumentType.greedyString()).executes((context) -> {
            class_1799 stack = requireItemAndConditionsOrThrow();
            class_2487 compound = new class_2487();
            compound.method_10582("command", StringArgumentType.getString(context, "command"));
            appendToItem(stack, compound, me, Collections.emptyList());
            return 1;
         }));
      }, (compound, event, me) -> {
         assert CactusConstants.mc.field_1724 != null;

         String command = compound.method_68564("command", "");
         class_3965 targetBlock = tryFindTargetBlock(event, compound);
         if (targetBlock != null) {
            boolean shiftDown = GLFW.glfwGetKey(CactusConstants.mc.method_22683().method_4490(), 340) == 1 || GLFW.glfwGetKey(CactusConstants.mc.method_22683().method_4490(), 344) == 1;
            CactusConstants.mc.field_1724.field_3944.method_45730("execute positioned %s run %s".formatted(new Object[]{PositionUtils.toString(shiftDown ? targetBlock.method_17777() : targetBlock.method_17777().method_10093(targetBlock.method_17780())), command}));
         } else {
            CactusConstants.mc.field_1724.field_3944.method_45730(command);
         }

         return true;
      }),
      MACRO("macro", (me, builder) -> {
         builder.then(ToolCommand.argument("macro", MacroArgumentType.macroEntry()).executes((context) -> {
            class_1799 stack = requireItemAndConditionsOrThrow();
            class_2487 compound = new class_2487();
            Macro macro = MacroArgumentType.getMacroEntry(context);
            compound.method_10582("macro", macro.getName());
            appendToItem(stack, compound, me, ImmutableList.of(getTranslatable(me, "macroType", (String)null, macro.getName(), Integer.toString(macro.getActions().size())).method_27694(ToolCommand.Registry::applyDefaultStyle)));
            return 1;
         }));
      }, (compound, event, me) -> {
         String macroName = compound.method_68564("macro", "");
         Optional<Macro> optionalMacro = MacroManager.get().getMacro(macroName);
         optionalMacro.ifPresentOrElse(Macro::run, () -> {
            ChatUtils.error((class_2561)class_2561.method_43470("The macro specified on this tool item doesn't seem to exist. You can ").method_10852(class_2561.method_43470("create a macro here").method_27692(class_124.field_1073).method_27694((style) -> {
               return style.method_10958(new CRunnableClickEvent(() -> {
                  CactusConstants.mc.method_1507(new MacroListScreen((class_437)null));
               }));
            })).method_27693("."));
         });
         return true;
      }),
      DISPLAY_CONVERTER("block_to_display", (me, builder) -> {
         ((LiteralArgumentBuilder)builder.executes((context) -> {
            ToolCommand.Helper.handleDisplayConverter(me, false, (String)null);
            return 1;
         })).then(((RequiredArgumentBuilder)ToolCommand.argument("auto_origin", BoolArgumentType.bool()).executes((context) -> {
            ToolCommand.Helper.handleDisplayConverter(me, BoolArgumentType.getBool(context, "auto_origin"), (String)null);
            return 1;
         })).then(ToolCommand.argument("tag", StringArgumentType.word()).executes((context) -> {
            ToolCommand.Helper.handleDisplayConverter(me, BoolArgumentType.getBool(context, "auto_origin"), StringArgumentType.getString(context, "tag"));
            return 1;
         })));
      }, (compound, event, me) -> {
         if (event instanceof InteractBlockEvent) {
            InteractBlockEvent blockEvent = (InteractBlockEvent)event;
            if (CactusConstants.mc.field_1724 != null && CactusConstants.mc.field_1687 != null) {
               class_1799 stack = CactusConstants.mc.field_1724.method_6047();
               class_2338 blockPos = blockEvent.getHitResult().method_17777();
               int[] origin = (int[])compound.method_10561("origin").orElse((Object)null);
               boolean isOriginSet = origin != null && origin.length == 3;
               class_2338 originPos = isOriginSet ? new class_2338(origin[0], origin[1], origin[2]) : null;
               String tag = compound.method_68564("tag", "");
               if (!tag.isEmpty()) {
                  tag = "\"%s\"".formatted(new Object[]{tag});
               }

               class_2680 state = CactusConstants.mc.field_1687.method_8320(blockPos);
               StringBuilder propertiesBuilder = new StringBuilder();
               List<Entry<class_2769<?>, Comparable<?>>> entrySet = state.method_11656().entrySet().stream().toList();

               class_2769 property;
               String literalPos;
               for(Iterator var13 = entrySet.iterator(); var13.hasNext(); propertiesBuilder.append(property.method_11899()).append(":\"").append(literalPos).append('"')) {
                  Entry<class_2769<?>, Comparable<?>> entry = (Entry)var13.next();
                  property = (class_2769)entry.getKey();
                  literalPos = class_156.method_650(property, entry.getValue());
                  if (entrySet.indexOf(entry) > 0) {
                     propertiesBuilder.append(',');
                  }
               }

               String blockIdentifier = ServerUtils.getBlockIdStr(state.method_26204());
               String properties = propertiesBuilder.toString();
               String transformation = ",transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[1f,1f,1f],translation:[%sf,%sf,%sf]}".formatted(new Object[]{(double)(isOriginSet ? blockPos.method_10263() - origin[0] : 0) - 0.5D, (double)(isOriginSet ? blockPos.method_10264() - origin[1] : 0) - 0.5D, (double)(isOriginSet ? blockPos.method_10260() - origin[2] : 0) - 0.5D});
               literalPos = PositionUtils.toString(blockPos);
               String command = "summon block_display %s {block_state:{Name:\"%s\",Properties:{%s}}%s,Tags:[%s]}".formatted(new Object[]{PositionUtils.toStringCentered(!isOriginSet ? blockPos : originPos), blockIdentifier, properties, transformation, tag});
               ChatUtils.info((class_2561)class_2561.method_43469("commands.tool.type.display_converter.success", new Object[]{blockIdentifier.replaceAll("^[^:]*:\n", ""), literalPos}).method_27694(ToolCommand.Registry::applyDefaultStyle));
               CactusConstants.mc.field_1724.field_3944.method_45730("setblock %s air".formatted(new Object[]{literalPos}));
               CactusConstants.mc.field_1724.field_3944.method_45730(command);
               if (origin != null && origin.length == 0) {
                  compound.method_10539("origin", new int[]{blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260()});
                  class_9290 lore = (class_9290)stack.method_58694(class_9334.field_49632);
                  if (lore != null) {
                     lore.comp_2400().remove(1);
                     lore.comp_2400().add(1, class_2561.method_43469("commands.tool.type.display_converter.autoOrigin", new Object[]{literalPos}).method_27694(ToolCommand.Registry::applyDefaultStyle));
                     stack.method_57379(class_9334.field_49632, lore);
                  }
               }
            }
         }

         return true;
      }),
      TAG_TOOL("tag_editor", (me, builder) -> {
         builder.executes((context) -> {
            class_1799 stack = requireItemAndConditionsOrThrow();
            class_2487 compound = new class_2487();
            appendToItem(stack, compound, me, Collections.emptyList());
            return 1;
         });
      }, (compound, event, me) -> {
         if (event instanceof InteractEntityEvent) {
            InteractEntityEvent entityEvent = (InteractEntityEvent)event;
            class_1297 entity = entityEvent.getEntity();
            class_746 player = entityEvent.getPlayer();
            UUID uuid = entity.method_5667();
            ToolCommand.Helper.tagListCallback = (originalListUnmodifiable) -> {
               CactusConstants.mc.method_1507(new StringListSetting.SelectionScreen(originalListUnmodifiable, class_2561.method_43471("commands.tool.type.tag_tool.windowTitle"), (modified) -> {
                  ToolCommand.Helper.copyAndRemove(originalListUnmodifiable, modified).forEach((tag) -> {
                     player.field_3944.method_45730("tag %s remove %s".formatted(new Object[]{uuid, tag}));
                  });
                  ToolCommand.Helper.copyAndRemove(modified, originalListUnmodifiable).forEach((tag) -> {
                     player.field_3944.method_45730("tag %s add %s".formatted(new Object[]{uuid, tag}));
                  });
               }) {
                  {
                     super(list, windowTitle, callback);
                  }

                  public boolean method_25421() {
                     return false;
                  }
               });
            };
            player.field_3944.method_45730("tag %s list".formatted(new Object[]{uuid}));
            Utils.unsafeDelayed(() -> {
               if (ToolCommand.Helper.tagListCallback != null) {
                  ChatUtils.warning((class_2561)class_2561.method_43471("commands.tool.type.tag_tool.responseTimeout"));
                  ToolCommand.Helper.tagListCallback = null;
               }

            }, Math.max(100L, (long)ServerUtils.ping() * 2L));
         }

         return true;
      }),
      MEASURING_TAPE("measuring_tape", (me, builder) -> {
         ((LiteralArgumentBuilder)builder.executes((context) -> {
            class_1799 stack = requireItemAndConditionsOrThrow();
            class_2487 compound = new class_2487();
            appendToItem(stack, compound, me, Collections.emptyList());
            return 1;
         })).then(ToolCommand.argument("autoReset", BoolArgumentType.bool()).executes((context) -> {
            class_1799 stack = requireItemAndConditionsOrThrow();
            class_2487 compound = new class_2487();
            compound.method_10556("autoReset", BoolArgumentType.getBool(context, "autoReset"));
            appendToItem(stack, compound, me, Collections.emptyList());
            return 1;
         }));
      }, (compound, event, me) -> {
         class_3965 hitResult = tryFindTargetBlock(event, compound);
         if (hitResult == null) {
            return true;
         } else {
            class_2338 pos = hitResult.method_17777();
            if (!CactusConstants.mc.field_1687.method_22347(pos)) {
               if (!compound.method_10545("firstPos")) {
                  compound.method_10539("firstPos", new int[]{pos.method_10263(), pos.method_10264(), pos.method_10260()});
                  ChatUtils.info((class_2561)getTranslatable(me, "firstPosSet", (String)null, PositionUtils.toString(pos)).method_27694(ToolCommand.Registry::applyDefaultStyle));
               } else {
                  int[] first = (int[])compound.method_10561("firstPos").orElse(new int[]{pos.method_10263(), pos.method_10264(), pos.method_10260()});
                  class_2338 firstPos = new class_2338(first[0], first[1], first[2]);
                  double distance = firstPos.method_10262(pos);
                  ChatUtils.info((class_2561)getTranslatable(me, "distance", (String)null, class_2561.method_43470(PositionUtils.toString(firstPos)).method_27692(class_124.field_1060), class_2561.method_43470(PositionUtils.toString(pos)).method_27692(class_124.field_1060), class_2561.method_43470(String.format("%.2f", Math.sqrt(distance))).method_27692(class_124.field_1060)).method_27694(ToolCommand.Registry::applyDefaultStyle));
                  if (compound.method_68566("autoReset", false)) {
                     compound.method_10551("firstPos");
                  }
               }
            }

            return true;
         }
      });

      private final String commandName;
      private final BiConsumer<ToolCommand.Registry, LiteralArgumentBuilder<class_2172>> buildContext;
      private final ToolCommand.ActionHandler handler;
      private static boolean commandBuilt = false;

      private Registry(String subCommandName, BiConsumer<ToolCommand.Registry, LiteralArgumentBuilder<class_2172>> buildContext, ToolCommand.ActionHandler handler) {
         this.commandName = subCommandName;
         this.buildContext = buildContext;
         this.handler = handler;
      }

      public boolean pass(class_2487 component, ActionEvent event) {
         return this.handler.handle(component, event, this);
      }

      public LiteralArgumentBuilder<class_2172> appendToCommand(LiteralArgumentBuilder<class_2172> builder) {
         this.buildContext.accept(this, builder);
         return builder;
      }

      private static class_1799 requireItemAndConditionsOrThrow() throws CommandSyntaxException {
         assert CactusConstants.mc.field_1724 != null;

         if (CactusConstants.mc.field_1724.method_31549().field_7477) {
            class_1799 stack = CactusConstants.mc.field_1724.method_6047();
            if (stack.method_7960()) {
               throw ToolCommand.NO_ITEM.create();
            } else {
               return stack;
            }
         } else {
            throw ToolCommand.CREATIVE_REQUIRED.create();
         }
      }

      private static void appendToItem(class_1799 stack, class_2487 compound, ToolCommand.Registry registry, List<class_2561> tooltipLines) {
         class_2487 root = new class_2487();
         compound.method_10582("type", registry.name().toLowerCase());
         root.method_10566("cactusToolItem", compound);
         stack.method_57379(class_9334.field_49628, class_9279.method_57456(root));
         stack.method_57379(class_9334.field_49641, true);
         stack.method_57379(class_9334.field_49631, class_2561.method_43473().method_10852(class_2561.method_43470("§8[§a\ud83c\udf35§8] §6» ").method_10852(getTranslatable(registry, "name", registry.name().toUpperCase()).method_27694(ToolCommand.Registry::applyDefaultStyle))));
         List<class_2561> tooltipLines = new ArrayList(tooltipLines);
         tooltipLines.addFirst(class_2561.method_43469("commands.tool.tooltip.type", new Object[]{registry.name().toUpperCase()}).method_27694(ToolCommand.Registry::applyDefaultStyle));
         tooltipLines.addLast(getTranslatable(registry, "description", (String)null).method_27694((style) -> {
            return applyDefaultStyle(style).method_10977(class_124.field_1080);
         }));
         stack.method_57379(class_9334.field_49632, new class_9290(tooltipLines));
      }

      private static class_2583 applyDefaultStyle(class_2583 style) {
         return style.method_10978(false).method_36139(-37120);
      }

      private static class_5250 getTranslatable(ToolCommand.Registry registry, String key, String fallback, Object... arguments) {
         return class_2561.method_48322("commands.tool.type.%s.%s".formatted(new Object[]{registry.name().toLowerCase(), key}), fallback, arguments);
      }

      private static class_3965 tryFindTargetBlock(ActionEvent event, class_2487 compound) {
         int distance = compound.method_68083("distance", 0);
         if (distance == -1) {
            distance = Integer.MAX_VALUE;
         }

         if (event instanceof InteractBlockEvent) {
            InteractBlockEvent blockEvent = (InteractBlockEvent)event;
            class_3965 var5 = blockEvent.getHitResult();
            if (var5 instanceof class_3965 && !blockEvent.isInAir()) {
               return var5;
            }
         }

         if (distance > 0) {
            class_239 result = CactusConstants.mc.field_1724.method_5745((double)distance, CactusConstants.mc.method_61966().method_60637(false), false);
            if (result instanceof class_3965) {
               class_3965 bhr = (class_3965)result;
               return bhr;
            }
         }

         return null;
      }

      private static class_2487 getRootCustomData(class_1799 stack) {
         return ((class_9279)stack.method_58695(class_9334.field_49628, class_9279.field_49302)).method_57461();
      }

      private static class_2487 getToolData(class_1799 stack) {
         return getToolData(getRootCustomData(stack));
      }

      private static class_2487 getToolData(class_2487 root) {
         return root.method_68568("cactusToolItem");
      }

      private static void setToolData(class_1799 stack, class_2487 toolData) {
         class_2487 root = getRootCustomData(stack);
         root.method_10566("cactusToolItem", toolData);
         stack.method_57379(class_9334.field_49628, class_9279.method_57456(root));
      }

      public String getCommandName() {
         return this.commandName;
      }

      public static void appendAll(LiteralArgumentBuilder<class_2172> builder) {
         if (!commandBuilt) {
            Arrays.stream(values()).forEach((registry) -> {
               LiteralArgumentBuilder<class_2172> withName = (LiteralArgumentBuilder)builder.then(registry.appendToCommand(ToolCommand.literal(registry.getCommandName())));
            });
            commandBuilt = true;
         } else {
            throw new IllegalStateException("Command has already been built");
         }
      }

      // $FF: synthetic method
      private static ToolCommand.Registry[] $values() {
         return new ToolCommand.Registry[]{SIMPLE_COMMAND, MACRO, DISPLAY_CONVERTER, TAG_TOOL, MEASURING_TAPE};
      }
   }

   public static class Handler {
      public static ToolCommand.Handler get() {
         return (ToolCommand.Handler)SingleInstance.of(ToolCommand.Handler.class, ToolCommand.Handler::new);
      }

      public boolean passAll(class_1799 stack, Supplier<class_2487> component, ActionEvent event) {
         return CactusConstants.mc.field_1724.method_6047().method_7960() ? false : Arrays.stream(ToolCommand.Registry.values()).anyMatch((handler) -> {
            class_2487 toolData = (class_2487)component.get();
            if (toolData != null && handler.name().toLowerCase().equals(toolData.method_68564("type", (String)null))) {
               int preProcessHash = toolData.hashCode();
               boolean pass = handler.pass(toolData, event);
               if (toolData.hashCode() != preProcessHash) {
                  class_9279 comp = (class_9279)stack.method_58695(class_9334.field_49628, class_9279.field_49302);
                  class_2487 nbtCompound = comp.method_57461();
                  nbtCompound.method_10566("cactusToolItem", toolData);
                  stack.method_57379(class_9334.field_49628, class_9279.method_57456(nbtCompound));
               }

               return pass;
            } else {
               return false;
            }
         });
      }

      public void passEvent(ActionEvent event) {
         class_1799 stack = CactusConstants.mc.field_1724.method_6047();
         if (!stack.method_7960()) {
            boolean success = this.passAll(stack, () -> {
               return ((class_9279)stack.method_58695(class_9334.field_49628, class_9279.field_49302)).method_57461().method_68568("cactusToolItem");
            }, event);
            if (success) {
               event.setResult(class_1269.field_21466);
            }
         }

      }

      @EventHandler
      public void onInteract(InteractBlockEvent event) {
         if (event.getPlayer() == CactusConstants.mc.field_1724) {
            this.passEvent(event);
         }

      }

      @EventHandler
      public void onInteract(InteractItemEvent event) {
         if (event.getHand() == class_1268.field_5808) {
            this.passEvent(event);
         }

      }

      @EventHandler
      public void onInteract(InteractEntityEvent event) {
         if (event.getHand() == class_1268.field_5808) {
            this.passEvent(event);
         }

      }

      @EventHandler
      public void onMessage(MessageReceiveEvent event) {
         if (ToolCommand.Helper.tagListCallback != null) {
            class_7417 var3 = event.message.method_10851();
            if (var3 instanceof class_2588) {
               class_2588 ttc = (class_2588)var3;
               if (ttc.method_11022().equalsIgnoreCase("commands.tag.list.single.success")) {
                  String raw = ttc.method_29434(2).getString();
                  ToolCommand.Helper.tagListCallback.accept(Arrays.asList(raw.replace(" ", "").split(",")));
                  event.cancel();
               } else if (ttc.method_11022().equalsIgnoreCase("commands.tag.list.single.empty")) {
                  ToolCommand.Helper.tagListCallback.accept(Collections.emptyList());
                  event.cancel();
               }
            }

            ToolCommand.Helper.tagListCallback = null;
         }

      }
   }

   private interface ActionHandler {
      boolean handle(class_2487 var1, ActionEvent var2, ToolCommand.Registry var3);
   }

   private static class Helper {
      public static Consumer<List<String>> tagListCallback;

      public static void handleDisplayConverter(ToolCommand.Registry registry, boolean autoOrigin, String tag) throws CommandSyntaxException {
         class_1799 stack = ToolCommand.Registry.requireItemAndConditionsOrThrow();
         class_2487 compound = new class_2487();
         List<class_2561> lines = new ArrayList();
         if (autoOrigin) {
            compound.method_10539("origin", new int[0]);
         }

         lines.add(ToolCommand.Registry.getTranslatable(registry, "autoOrigin", (String)null, autoOrigin ? "AUTO" : "DEFAULT").method_27694(ToolCommand.Registry::applyDefaultStyle));
         if (tag != null) {
            compound.method_10582("tag", tag);
            lines.add(ToolCommand.Registry.getTranslatable(registry, "tag", (String)null, tag).method_27694(ToolCommand.Registry::applyDefaultStyle));
         }

         ToolCommand.Registry.appendToItem(stack, compound, registry, lines);
      }

      public static <T> List<T> copyAndRemove(List<T> list, List<T> remove) {
         List<T> result = new ArrayList(list);
         result.removeAll(remove);
         return result;
      }
   }
}
