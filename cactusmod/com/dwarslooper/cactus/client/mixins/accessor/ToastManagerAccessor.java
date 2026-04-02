package com.dwarslooper.cactus.client.mixins.accessor;

import java.util.List;
import net.minecraft.class_368;
import net.minecraft.class_374;
import net.minecraft.class_368.class_369;
import net.minecraft.class_374.class_375;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_374.class})
public interface ToastManagerAccessor {
   @Accessor("field_2239")
   List<class_375<class_368>> getVisibleToasts();

   @Mixin({class_375.class})
   public interface EntryAccessor {
      @Accessor("field_52787")
      int getFirstSlotIndex();

      @Accessor("field_2244")
      void setVisibility(class_369 var1);

      @Accessor("field_2241")
      class_368 getToast();
   }
}
