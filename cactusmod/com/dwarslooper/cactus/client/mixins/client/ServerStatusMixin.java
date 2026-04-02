package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.util.mixinterface.ICactusCustomMeta;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2926;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin({class_2926.class})
public abstract class ServerStatusMixin implements ICactusCustomMeta {
   @Unique
   private JsonObject cactusCustomData;

   @Unique
   public void setCactusCustomData(JsonObject cactusCustomData) {
      this.cactusCustomData = cactusCustomData;
   }

   @Unique
   public JsonObject getCactusCustomData() {
      return this.cactusCustomData;
   }
}
