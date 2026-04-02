package com.dwarslooper.cactus.client.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.class_2561;

public class LinkCodeArgumentType implements ArgumentType<String> {
   private static final SimpleCommandExceptionType MALFORMED_CODE = new SimpleCommandExceptionType(class_2561.method_43471("command.arg.linkCode"));

   public static LinkCodeArgumentType linkCode() {
      return new LinkCodeArgumentType();
   }

   public static String getCode(CommandContext<?> context, String name) {
      return (String)context.getArgument(name, String.class);
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      String code = reader.readString();
      if (!code.matches("\\b[0-9]\\d{3}-[0-9]\\d{3}-[0-9]\\d{3}-[0-9]\\d{3}\\b")) {
         throw MALFORMED_CODE.createWithContext(reader);
      } else {
         return code;
      }
   }
}
