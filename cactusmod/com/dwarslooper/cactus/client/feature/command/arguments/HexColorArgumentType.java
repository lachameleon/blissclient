package com.dwarslooper.cactus.client.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.awt.Color;
import net.minecraft.class_2561;

public class HexColorArgumentType implements ArgumentType<Color> {
   private static final SimpleCommandExceptionType MALFORMED_HEX = new SimpleCommandExceptionType(class_2561.method_43471("command.arg.color"));

   public static HexColorArgumentType hexColorArgument() {
      return new HexColorArgumentType();
   }

   public static Color getColor(CommandContext<?> context, String name) {
      return (Color)context.getArgument(name, Color.class);
   }

   public Color parse(StringReader reader) throws CommandSyntaxException {
      if (reader.read() != '#') {
         throw MALFORMED_HEX.createWithContext(reader);
      } else {
         String hex = reader.readUnquotedString();
         if (hex.length() != 6) {
            throw MALFORMED_HEX.createWithContext(reader);
         } else {
            try {
               return new Color(Integer.valueOf(hex.substring(0, 2), 16), Integer.valueOf(hex.substring(2, 4), 16), Integer.valueOf(hex.substring(4, 6), 16));
            } catch (NumberFormatException var4) {
               throw MALFORMED_HEX.createWithContext(reader);
            }
         }
      }
   }
}
