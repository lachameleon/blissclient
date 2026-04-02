package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.command.arguments.HexColorArgumentType;
import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.awt.Color;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_5251;

public class RGBCommand extends Command implements ISerializable<RGBCommand> {
   private static int speed = 10;
   private static boolean fadeToggle = false;

   public RGBCommand() {
      super("color");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(literal("toggle").executes((context) -> {
         fadeToggle = !fadeToggle;
         ChatUtils.info((class_2561)class_2561.method_43471(fadeToggle ? "commands.color.toggle.activated" : "commands.color.toggle.deactivated"));
         return 1;
      }))).then(literal("speed").then(argument("speed", IntegerArgumentType.integer(1, 20)).executes((context) -> {
         speed = IntegerArgumentType.getInteger(context, "speed");
         ChatUtils.info((class_2561)class_2561.method_43469("commands.color.speed", new Object[]{speed}));
         return 1;
      })))).then(literal("set").then(argument("hexcolor", HexColorArgumentType.hexColorArgument()).executes((context) -> {
         Setting<ColorSetting.ColorValue> setting = CactusSettings.get().accentColor;
         Color color = HexColorArgumentType.getColor(context, "hexcolor");
         CactusSettings.get().accentColor.set(new ColorSetting.ColorValue(color, ((ColorSetting.ColorValue)setting.get()).usesRgb()));
         ChatUtils.info((class_2561)class_2561.method_43471("commands.color.change.1").method_10852(class_2561.method_43470("█").method_27694((style) -> {
            return style.method_27703(class_5251.method_27717(color.getRGB()));
         })).method_10852(class_2561.method_43471("commands.color.change.2")));
         return 1;
      })));
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      object.addProperty("fade", fadeToggle);
      object.addProperty("speed", speed);
      return object;
   }

   public RGBCommand fromJson(JsonObject object) {
      fadeToggle = object.get("fade").getAsBoolean();
      speed = object.get("speed").getAsInt();
      return this;
   }
}
