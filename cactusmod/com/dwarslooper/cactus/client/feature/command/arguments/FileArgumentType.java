package com.dwarslooper.cactus.client.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class FileArgumentType implements ArgumentType<File> {
   private final File startFolder;
   public static final DynamicCommandExceptionType NO_SUCH_FILE = new DynamicCommandExceptionType((o) -> {
      return class_2561.method_43469("command.arg.file", new Object[]{o});
   });

   public FileArgumentType(File startFolder) {
      this.startFolder = startFolder;
   }

   public static FileArgumentType file(File startFolder) {
      return new FileArgumentType(startFolder);
   }

   public static File getFile(CommandContext<?> context, String name) {
      return (File)context.getArgument(name, File.class);
   }

   public File parse(StringReader reader) throws CommandSyntaxException {
      String argument = reader.readString();
      File file = new File(this.startFolder, argument);
      if (file.exists() && !file.isDirectory()) {
         return file;
      } else {
         throw NO_SUCH_FILE.create(file.getName());
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return class_2172.method_9265(this.getFiles(this.startFolder), builder);
   }

   private Collection<String> getFiles(File folder) {
      List<String> fileNames = new ArrayList();
      if (folder.isDirectory()) {
         File[] files = folder.listFiles();
         if (files != null) {
            File[] var4 = files;
            int var5 = files.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               File file = var4[var6];
               if (file.isFile()) {
                  fileNames.add(file.getName());
               }
            }
         }
      }

      return fileNames;
   }
}
