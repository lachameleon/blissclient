package com.dwarslooper.cactus.client.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import net.minecraft.class_2561;

public class UrlArgumentType implements ArgumentType<URL> {
   private static final SimpleCommandExceptionType MALFORMED_URL = new SimpleCommandExceptionType(class_2561.method_43471("command.arg.url"));

   public static UrlArgumentType urlArgument() {
      return new UrlArgumentType();
   }

   public URL parse(StringReader reader) throws CommandSyntaxException {
      try {
         StringBuilder line = new StringBuilder();

         while(reader.canRead() && reader.peek() != ' ') {
            line.append(reader.read());
         }

         return (new URI(line.toString())).toURL();
      } catch (MalformedURLException | URISyntaxException var3) {
         throw MALFORMED_URL.createWithContext(reader);
      }
   }

   public static URL getURL(CommandContext<?> context, String name) {
      return (URL)context.getArgument(name, URL.class);
   }
}
