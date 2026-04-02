package com.dwarslooper.cactus.client.feature.command.arguments;

import com.dwarslooper.cactus.client.util.CactusConstants;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LitematicArgumentType implements ArgumentType<File> {
   private final File startFolder;

   public LitematicArgumentType() {
      this.startFolder = new File(CactusConstants.DIRECTORY.getParentFile(), "schematics");
   }

   public static LitematicArgumentType litematic() {
      return new LitematicArgumentType();
   }

   public static File getFile(CommandContext<?> context, String name) {
      return (File)context.getArgument(name, File.class);
   }

   public File parse(StringReader reader) throws CommandSyntaxException {
      String argument = reader.readQuotedString();
      File file = new File(this.startFolder, argument);
      if (file.exists() && !file.isDirectory()) {
         return file;
      } else {
         throw FileArgumentType.NO_SUCH_FILE.create(file.getName());
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      Collection<String> suggestions = this.getFiles(this.startFolder, "");
      Iterator var4 = suggestions.iterator();

      while(var4.hasNext()) {
         String suggestion = (String)var4.next();
         builder.suggest("\"" + suggestion + "\"");
      }

      return builder.buildFuture();
   }

   private Collection<String> getFiles(File folder, String path) {
      List<String> fileNames = new ArrayList();
      if (!folder.exists()) {
         return fileNames;
      } else {
         if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
               File[] var5 = files;
               int var6 = files.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  File file = var5[var7];
                  String subPath;
                  if (file.isFile()) {
                     subPath = path.isEmpty() ? file.getName() : path + "/" + file.getName();
                     fileNames.add(subPath);
                  } else if (file.isDirectory()) {
                     subPath = path.isEmpty() ? file.getName() : path + "/" + file.getName();
                     fileNames.addAll(this.getFiles(file, subPath));
                  }
               }
            }
         }

         return fileNames;
      }
   }
}
