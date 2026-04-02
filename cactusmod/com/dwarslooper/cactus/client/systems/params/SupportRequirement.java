package com.dwarslooper.cactus.client.systems.params;

import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.function.Predicate;

public enum SupportRequirement {
   COMPAT((s) -> {
      return true;
   }),
   STATE("SERVER_STATE"::equals),
   ADDRESS((s) -> {
      return CactusConstants.mc.method_1558() != null && CactusConstants.mc.method_1558().field_3761.equalsIgnoreCase(s);
   });

   private final Predicate<String> resolver;

   private SupportRequirement(Predicate<String> resolver) {
      this.resolver = resolver;
   }

   public boolean check(String value) {
      return this.resolver.test(value);
   }

   // $FF: synthetic method
   private static SupportRequirement[] $values() {
      return new SupportRequirement[]{COMPAT, STATE, ADDRESS};
   }
}
