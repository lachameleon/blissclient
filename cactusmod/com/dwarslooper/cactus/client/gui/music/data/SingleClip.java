package com.dwarslooper.cactus.client.gui.music.data;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_2960;

public record SingleClip(String name, AtomicReference<class_2960> compiledImage, int totalWidth, String instrument, int ticksPerBeat, int totalBeats, Map<Integer, List<PlacedNote>> lines, Color color, AtomicReference<SingleClip> replacement) {
   public SingleClip(String name, AtomicReference<class_2960> compiledImage, int totalWidth, String instrument, int ticksPerBeat, int totalBeats, Map<Integer, List<PlacedNote>> lines, Color color, AtomicReference<SingleClip> replacement) {
      this.name = name;
      this.compiledImage = compiledImage;
      this.totalWidth = totalWidth;
      this.instrument = instrument;
      this.ticksPerBeat = ticksPerBeat;
      this.totalBeats = totalBeats;
      this.lines = lines;
      this.color = color;
      this.replacement = replacement;
   }

   public class_2960 getCompiledImage() {
      if (this.compiledImage.get() == null) {
         class_1011 tex = new class_1011(this.totalWidth, 25, true);
         Iterator var2 = this.lines.entrySet().iterator();

         while(var2.hasNext()) {
            Entry<Integer, List<PlacedNote>> entry = (Entry)var2.next();
            ((List)entry.getValue()).forEach((note) -> {
               if (note.timestamp() < 0) {
                  CactusClient.getLogger().warn("Invalid note timestamp: {}", note.timestamp());
               } else {
                  tex.method_4305(note.timestamp(), 24 - (Integer)entry.getKey(), -16777216);
               }
            });
         }

         class_1043 texture = new class_1043(() -> {
            return "NoteClip";
         }, tex);
         String var10001 = this.name.toLowerCase();
         class_2960 identifier = class_2960.method_60655("cactus", var10001 + (new Random()).nextInt(9999999));
         CactusConstants.mc.method_1531().method_4616(identifier, texture);
         this.compiledImage.set(identifier);
      }

      return (class_2960)this.compiledImage.get();
   }

   public String name() {
      return this.name;
   }

   public AtomicReference<class_2960> compiledImage() {
      return this.compiledImage;
   }

   public int totalWidth() {
      return this.totalWidth;
   }

   public String instrument() {
      return this.instrument;
   }

   public int ticksPerBeat() {
      return this.ticksPerBeat;
   }

   public int totalBeats() {
      return this.totalBeats;
   }

   public Map<Integer, List<PlacedNote>> lines() {
      return this.lines;
   }

   public Color color() {
      return this.color;
   }

   public AtomicReference<SingleClip> replacement() {
      return this.replacement;
   }
}
