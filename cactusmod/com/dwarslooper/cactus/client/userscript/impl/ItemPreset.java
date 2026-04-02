package com.dwarslooper.cactus.client.userscript.impl;

import com.dwarslooper.cactus.client.userscript.ScriptExecutable;
import com.dwarslooper.cactus.client.userscript.Userscript;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.dwarslooper.cactus.client.util.game.ItemUtils;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import net.minecraft.class_2509;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import net.minecraft.class_9326;

public class ItemPreset extends Userscript implements ScriptExecutable {
   public ItemPreset(JsonObject json) {
      super(json);
   }

   public void load() {
      ChatUtils.info("Loaded Item Preset");
   }

   public void unload() {
      ChatUtils.warning("Unloaded item preset");
   }

   public void run(JsonObject data) throws CommandSyntaxException {
      class_1792 item = (class_1792)class_7923.field_41178.method_63535(class_2960.method_60654(data.get("id").getAsString()));
      int count = data.get("count").getAsInt();
      class_1799 stack = item.method_7854();
      class_9326 changes = (class_9326)((Pair)class_9326.field_49589.decode(class_2509.field_11560, new class_2487()).getOrThrow()).getFirst();
      stack.method_59692(changes);
      stack.method_7939(count);
      ItemUtils.giveItem(stack);
      ChatUtils.info("§aItem created");
   }
}
