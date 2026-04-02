package com.dwarslooper.cactus.client.addon.v2;

import java.util.function.Function;

public record PendingRegistration(Addon owner, Function<RegistrationContext, ?> factory) {
   public PendingRegistration(Addon owner, Function<RegistrationContext, ?> factory) {
      this.owner = owner;
      this.factory = factory;
   }

   public Addon owner() {
      return this.owner;
   }

   public Function<RegistrationContext, ?> factory() {
      return this.factory;
   }
}
