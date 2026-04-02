package com.dwarslooper.cactus.client.systems.hdb;

import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.networking.HttpUtils;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class HeadDataHandler {
   public static final HeadDataHandler INSTANCE = new HeadDataHandler();
   public static final File DIRECTORY;
   private static final ExecutorService EXECUTOR;
   private static final String API_URL = "https://minecraft-heads.com/scripts/api.php";

   public CompletableFuture<Void> downloadAndSaveAll(BiConsumer<Float, TagCategory> callback) {
      return this.downloadAndSave(Arrays.asList(TagCategory.values()), callback);
   }

   public CompletableFuture<Void> downloadAndSave(List<TagCategory> categories, BiConsumer<Float, TagCategory> callback) {
      CompletableFuture<Void> future = new CompletableFuture();
      if (!DIRECTORY.exists()) {
         DIRECTORY.mkdirs();
      }

      EXECUTOR.submit(() -> {
         Iterator var2 = categories.iterator();

         while(var2.hasNext()) {
            TagCategory category = (TagCategory)var2.next();
            callback.accept(-1.0F, category);
            HttpUtils.downloadAndReturnProgress("%s?tags=true&cat=%s".formatted(new Object[]{"https://minecraft-heads.com/scripts/api.php", category.getId()}), (f) -> {
               callback.accept(f, category);
            }, DIRECTORY, category.getId() + ".json");
         }

      });
      return future;
   }

   static {
      DIRECTORY = new File(CactusConstants.DIRECTORY, "hdb");
      EXECUTOR = Executors.newSingleThreadExecutor((r) -> {
         return new Thread(r, "HDB Download Executor");
      });
   }
}
