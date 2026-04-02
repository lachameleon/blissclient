package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.command.arguments.HexColorArgumentType;
import com.dwarslooper.cactus.client.feature.command.arguments.WaypointArgumentType;
import com.dwarslooper.cactus.client.systems.waypoints.WaypointEntry;
import com.dwarslooper.cactus.client.systems.waypoints.WaypointManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.dwarslooper.cactus.client.util.game.PositionUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_5244;
import net.minecraft.class_634;
import net.minecraft.class_746;

public class WaypointCommand extends Command {
   public WaypointCommand() {
      super("waypoint");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(literal("list").executes((context) -> {
         if (WaypointManager.get().getInstance().getWaypointEntryList().isEmpty()) {
            ChatUtils.error(this.getTranslatableElement("none", new Object[0]));
         } else {
            ChatUtils.info(this.getTranslatableElement("list", new Object[0]));
            WaypointManager.get().getInstance().getWaypointEntryList().forEach((waypointEntry) -> {
               ChatUtils.info((class_2561)waypointEntry.createFormattedName());
            });
         }

         return 1;
      }))).then(literal("add").then(argument("name", StringArgumentType.greedyString()).executes((context) -> {
         if (WaypointManager.get().addIfAbsent(WaypointEntry.createDefault(StringArgumentType.getString(context, "name"), ((class_746)Objects.requireNonNull(CactusConstants.mc.field_1724)).method_24515()))) {
            ChatUtils.info(this.getTranslatableElement("add", new Object[0]));
         } else {
            ChatUtils.error(this.getTranslatableElement("add.alreadyExist", new Object[0]));
         }

         return 1;
      })))).then(literal("remove").then(argument("waypoint", WaypointArgumentType.waypointEntry()).executes((context) -> {
         WaypointEntry waypointEntry = WaypointArgumentType.getWaypointEntry(context);
         if (WaypointManager.get().getInstance().getWaypointEntryList().remove(waypointEntry)) {
            ChatUtils.info(this.getTranslatableElement("remove", new Object[]{waypointEntry.getName()}));
            return 1;
         } else {
            throw new IllegalArgumentException(this.getTranslatableElement("remove.notExist", new Object[]{waypointEntry.getName()}));
         }
      })))).then(literal("get").then(argument("waypoint", WaypointArgumentType.waypointEntry()).executes((context) -> {
         WaypointEntry waypointEntry = WaypointArgumentType.getWaypointEntry(context);
         class_2338 pos = waypointEntry.getPosition();
         ChatUtils.info((class_2561)class_2561.method_43470(this.getTranslatableElement("get.1", new Object[0])).method_10852(waypointEntry.createFormattedName()));
         ChatUtils.info((class_2561)class_2561.method_43470(this.getTranslatableElement("get.2", new Object[]{waypointEntry.getDimension()})));
         ChatUtils.info((class_2561)class_2561.method_43470(this.getTranslatableElement("get.3", new Object[]{pos.method_10263(), pos.method_10264(), pos.method_10260()})));
         ChatUtils.info((class_2561)class_2561.method_43470(this.getTranslatableElement("get.4", new Object[]{(waypointEntry.isDeath() ? class_5244.field_24336.method_27661().method_27692(class_124.field_1060) : class_5244.field_24337.method_27661().method_27692(class_124.field_1061)).getString().toLowerCase(Locale.ROOT)})));
         return 1;
      })))).then(literal("teleport").then(argument("waypoint", WaypointArgumentType.waypointEntry()).executes((context) -> {
         assert CactusConstants.mc.field_1724 != null;

         if (CactusConstants.mc.field_1724.method_7338()) {
            WaypointEntry waypointEntry = WaypointArgumentType.getWaypointEntry(context);
            ((class_634)Objects.requireNonNull(CactusConstants.mc.method_1562())).method_45730("execute in %s run teleport %s".formatted(new Object[]{waypointEntry.getDimension(), PositionUtils.toString(waypointEntry.getPosition())}));
         } else {
            ChatUtils.error(this.getTranslatableElement("teleport.permission", new Object[0]));
         }

         return 1;
      })))).then(literal("toggle").then(argument("waypoint", WaypointArgumentType.waypointEntry()).executes((context) -> {
         WaypointEntry waypointEntry = WaypointArgumentType.getWaypointEntry(context);
         if (waypointEntry.isVisible()) {
            ChatUtils.info(this.getTranslatableElement("toggle.hidden", new Object[]{waypointEntry.getName()}));
            waypointEntry.setVisible(false);
         } else {
            waypointEntry.setVisible(true);
            ChatUtils.info(this.getTranslatableElement("toggle.visible", new Object[]{waypointEntry.getName()}));
         }

         return 1;
      })))).then(literal("color").then(argument("color", HexColorArgumentType.hexColorArgument()).then(argument("waypoint", WaypointArgumentType.waypointEntry()).executes((context) -> {
         WaypointEntry waypointEntry = WaypointArgumentType.getWaypointEntry(context);
         waypointEntry.setColor(HexColorArgumentType.getColor(context, "color").getRGB());
         ChatUtils.info(this.getTranslatableElement("color", new Object[]{waypointEntry.getName()}));
         return 1;
      }))));
   }
}
