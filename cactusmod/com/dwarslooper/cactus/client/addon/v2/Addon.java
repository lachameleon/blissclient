package com.dwarslooper.cactus.client.addon.v2;

import java.util.List;
import net.fabricmc.loader.api.metadata.ModMetadata;

public record Addon(String id, String name, List<String> authors, ModMetadata metadata, ICactusAddon lifecycle) {
   public Addon(String id, String name, List<String> authors, ModMetadata metadata, ICactusAddon lifecycle) {
      this.id = id;
      this.name = name;
      this.authors = authors;
      this.metadata = metadata;
      this.lifecycle = lifecycle;
   }

   public String id() {
      return this.id;
   }

   public String name() {
      return this.name;
   }

   public List<String> authors() {
      return this.authors;
   }

   public ModMetadata metadata() {
      return this.metadata;
   }

   public ICactusAddon lifecycle() {
      return this.lifecycle;
   }
}
