package com.dwarslooper.cactus.client.mixins.accessor;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1071;
import net.minecraft.class_310;
import net.minecraft.class_312;
import net.minecraft.class_320;
import net.minecraft.class_5520;
import net.minecraft.class_7497;
import net.minecraft.class_7574;
import net.minecraft.class_7853;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_310.class})
public interface MinecraftAccessor {
   @Accessor("field_1726")
   @Mutable
   void setUser(class_320 var1);

   @Accessor("field_26902")
   @Mutable
   void setUserApiService(UserApiService var1);

   @Accessor("field_62106")
   @Mutable
   void setServices(class_7497 var1);

   @Accessor("field_26842")
   @Mutable
   void setPlayerSocialManager(class_5520 var1);

   @Accessor("field_39068")
   @Mutable
   void setProfileKeyPairManager(class_7853 var1);

   @Accessor("field_39492")
   @Mutable
   void setReportingContext(class_7574 var1);

   @Accessor("field_1707")
   @Mutable
   void setSkinManager(class_1071 var1);

   @Accessor("field_45899")
   @Mutable
   void setProfileFuture(CompletableFuture<ProfileResult> var1);

   @Accessor("field_1729")
   @Mutable
   class_312 getMouseHandler();
}
