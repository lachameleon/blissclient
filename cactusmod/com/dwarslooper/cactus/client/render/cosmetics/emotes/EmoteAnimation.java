package com.dwarslooper.cactus.client.render.cosmetics.emotes;

import com.dwarslooper.cactus.client.util.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_630;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class EmoteAnimation {
   private final List<EmoteAnimation.EmoteSegment> segments;
   private final Map<String, EmoteAnimation.PartPosition> initialPositions;
   private long startTime;
   private long currentTime;
   private long maxTime;

   public EmoteAnimation() {
      this.startTime = System.currentTimeMillis();
      this.currentTime = 0L;
      this.maxTime = 0L;
      this.segments = new ArrayList();
      this.initialPositions = new HashMap();
   }

   public EmoteAnimation(byte[] data) {
      this();
      this.load(Arrays.asList((new String(data)).split("\n")));
   }

   public void loadFromFile(File file) throws IOException {
      this.load(Files.readAllLines(file.toPath()));
   }

   public void load(List<String> lines) {
      try {
         Pattern pattern = Pattern.compile("(\\d+)\\s+(\\d+)\\s+(\\w+)\\s+((?:[PRS]\\[[0-9.,\\-]+]\\s*)+)\\s+(\\d+)\\s+(\\w+)");
         Iterator var3 = lines.iterator();

         while(true) {
            String line;
            Matcher matcher;
            do {
               if (!var3.hasNext()) {
                  return;
               }

               line = (String)var3.next();
               matcher = pattern.matcher(line.trim());
            } while(!matcher.matches());

            int startTime = Integer.parseInt(matcher.group(1));
            int endTime = Integer.parseInt(matcher.group(2));
            String partName = matcher.group(3);
            String arrayGroup = matcher.group(4);
            int smoothness = Integer.parseInt(matcher.group(5));
            String smoothType = matcher.group(6);
            Vector3f pivot = null;
            Vector3f rotation = null;
            Vector3f scale = null;
            Pattern arrayPattern = Pattern.compile("([PRS])\\[([0-9.,\\-]+)]");
            Matcher arrayMatcher = arrayPattern.matcher(arrayGroup);

            while(arrayMatcher.find()) {
               String type = arrayMatcher.group(1);
               String[] values = arrayMatcher.group(2).split(",");
               if (values.length != 3) {
                  System.err.println("Invalid vector length in line: " + line);
               } else {
                  float x = Float.parseFloat(values[0]);
                  float y = Float.parseFloat(values[1]);
                  float z = Float.parseFloat(values[2]);
                  Vector3f vector = new Vector3f(x, y, z);
                  byte var24 = -1;
                  switch(type.hashCode()) {
                  case 80:
                     if (type.equals("P")) {
                        var24 = 0;
                     }
                  case 81:
                  default:
                     break;
                  case 82:
                     if (type.equals("R")) {
                        var24 = 1;
                     }
                     break;
                  case 83:
                     if (type.equals("S")) {
                        var24 = 2;
                     }
                  }

                  switch(var24) {
                  case 0:
                     pivot = vector;
                     break;
                  case 1:
                     rotation = vector;
                     break;
                  case 2:
                     scale = vector;
                  }
               }
            }

            this.segments.add(new EmoteAnimation.EmoteSegment(startTime, endTime, partName, new EmoteAnimation.PartPosition(pivot, scale, rotation), smoothness, EmoteAnimation.EaseType.valueOf(smoothType)));
            this.maxTime = Math.max(this.maxTime, (long)endTime);
         }
      } catch (Exception var25) {
         var25.printStackTrace();
      }
   }

   public void setCurrentTime(long time) {
      this.currentTime = time;
   }

   public long getCurrentTime() {
      return this.currentTime;
   }

   public long getMaxTime() {
      return this.maxTime;
   }

   public boolean isDone() {
      return System.currentTimeMillis() - this.startTime > this.maxTime;
   }

   public void getPose(class_630 modelPart, String id) {
      long time = this.currentTime - this.startTime;
      boolean hasAnimation = this.segments.stream().anyMatch((segmentx) -> {
         return this.isAffected(segmentx, id) && time <= (long)segmentx.endTime;
      });
      if (hasAnimation) {
         if (!this.initialPositions.containsKey(id)) {
            this.initialPositions.put(id, EmoteAnimation.PartPosition.fromModelPart(modelPart));
         }

         EmoteAnimation.PartPosition currentPose = (EmoteAnimation.PartPosition)this.initialPositions.get(id);
         EmoteAnimation.PartPosition fromPosition = currentPose.copy();
         EmoteAnimation.EmoteSegment activeSegment = null;
         Iterator var9 = this.segments.iterator();

         while(var9.hasNext()) {
            EmoteAnimation.EmoteSegment segment = (EmoteAnimation.EmoteSegment)var9.next();
            if (this.isAffected(segment, id)) {
               if (time >= (long)segment.startTime && time <= (long)segment.endTime) {
                  activeSegment = segment;
                  fromPosition = this.getPositionAtTime(id, segment.startTime);
                  break;
               }

               if (time > (long)segment.endTime) {
                  fromPosition = this.createReplaceMissing(segment.targetPosition(), (EmoteAnimation.PartPosition)null);
               }
            }
         }

         EmoteAnimation.PartPosition target = fromPosition;
         float easedProgress;
         if (activeSegment != null) {
            float progress = (float)(time - (long)activeSegment.startTime) / (float)(activeSegment.endTime - activeSegment.startTime);
            easedProgress = this.applyEasing(progress, activeSegment.easeType);
            target = activeSegment.targetPosition();
         } else {
            easedProgress = 1.0F;
         }

         if (target.pivot() != null && fromPosition.pivot() != null) {
            target.pivot(modelPart, (v) -> {
               return this.lerpVec(fromPosition.pivot(), v, easedProgress);
            });
         }

         if (target.scale() != null && fromPosition.scale() != null) {
            target.scale(modelPart, (v) -> {
               return this.lerpVec(fromPosition.scale(), v, easedProgress);
            });
         }

         if (target.rotation() != null && fromPosition.rotation() != null) {
            target.rotate(modelPart, (v) -> {
               return this.lerpVec(fromPosition.rotation(), v, easedProgress).mul(0.017453292F);
            });
         }

      }
   }

   private EmoteAnimation.PartPosition getPositionAtTime(String partName, int targetTime) {
      EmoteAnimation.PartPosition position = (EmoteAnimation.PartPosition)this.initialPositions.get(partName);
      Iterator var4 = this.segments.iterator();

      while(var4.hasNext()) {
         EmoteAnimation.EmoteSegment segment = (EmoteAnimation.EmoteSegment)var4.next();
         if (this.isAffected(segment, partName) && targetTime >= segment.endTime) {
            position = this.createReplaceMissing(segment.targetPosition(), (EmoteAnimation.PartPosition)null);
         }
      }

      return position;
   }

   private EmoteAnimation.PartPosition createReplaceMissing(EmoteAnimation.PartPosition priority, @Nullable EmoteAnimation.PartPosition fallback) {
      return new EmoteAnimation.PartPosition((Vector3f)Utils.orElse(priority.pivot(), (Vector3f)Utils.orElse(fallback, EmoteAnimation.PartPosition::pivot, (Object)null)), (Vector3f)Utils.orElse(priority.scale(), (Vector3f)Utils.orElse(fallback, EmoteAnimation.PartPosition::scale, (Object)null)), (Vector3f)Utils.orElse(priority.rotation(), (Vector3f)Utils.orElse(fallback, EmoteAnimation.PartPosition::rotation, (Object)null)));
   }

   private Vector3f lerpVec(Vector3f a, Vector3f b, float t) {
      return new Vector3f(this.lerp(a.x(), b.x(), t), this.lerp(a.y(), b.y(), t), this.lerp(a.z(), b.z(), t));
   }

   private float lerp(float start, float end, float t) {
      return start + (end - start) * t;
   }

   private float applyEasing(float t, EmoteAnimation.EaseType easeType) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      return this.easeQuad(t, (f) -> {
         boolean var10000;
         switch(easeType.ordinal()) {
         case 1:
            var10000 = f < 0.5F;
            break;
         case 2:
            var10000 = f > 0.5F;
            break;
         case 3:
            var10000 = true;
            break;
         default:
            var10000 = false;
         }

         return var10000;
      });
   }

   private float easeQuad(float t, Predicate<Float> rangeValidator) {
      if (!rangeValidator.test(t)) {
         return t;
      } else {
         return (double)t < 0.5D ? 2.0F * t * t : -1.0F + (4.0F - 2.0F * t) * t;
      }
   }

   private boolean isAffected(EmoteAnimation.EmoteSegment segment, String id) {
      return segment.partName().equals(id) || "*".equals(id);
   }

   public void play(long time) {
      this.startTime = System.currentTimeMillis();
      this.setCurrentTime(time);
   }

   public void reset() {
      this.initialPositions.clear();
      this.startTime = System.currentTimeMillis();
      this.currentTime = 0L;
   }

   private static record EmoteSegment(int startTime, int endTime, String partName, EmoteAnimation.PartPosition targetPosition, int easeStrength, EmoteAnimation.EaseType easeType) {
      private EmoteSegment(int startTime, int endTime, String partName, EmoteAnimation.PartPosition targetPosition, int easeStrength, EmoteAnimation.EaseType easeType) {
         this.startTime = startTime;
         this.endTime = endTime;
         this.partName = partName;
         this.targetPosition = targetPosition;
         this.easeStrength = easeStrength;
         this.easeType = easeType;
      }

      public int startTime() {
         return this.startTime;
      }

      public int endTime() {
         return this.endTime;
      }

      public String partName() {
         return this.partName;
      }

      public EmoteAnimation.PartPosition targetPosition() {
         return this.targetPosition;
      }

      public int easeStrength() {
         return this.easeStrength;
      }

      public EmoteAnimation.EaseType easeType() {
         return this.easeType;
      }
   }

   private static record PartPosition(@Nullable Vector3f pivot, @Nullable Vector3f scale, @Nullable Vector3f rotation) {
      private PartPosition(@Nullable Vector3f pivot, @Nullable Vector3f scale, @Nullable Vector3f rotation) {
         this.pivot = pivot;
         this.scale = scale;
         this.rotation = rotation;
      }

      public void pivot(class_630 modelPart, Function<Vector3f, Vector3f> transform) {
         if (this.pivot != null) {
            modelPart.method_41920((Vector3f)transform.apply(this.pivot));
         }

      }

      public void scale(class_630 modelPart, Function<Vector3f, Vector3f> transform) {
         if (this.scale != null) {
            Vector3f vec = (Vector3f)transform.apply(this.scale);
            modelPart.field_37938 = vec.x();
            modelPart.field_37939 = vec.y();
            modelPart.field_37940 = vec.z();
         }

      }

      public void rotate(class_630 modelPart, Function<Vector3f, Vector3f> transform) {
         if (this.rotation != null) {
            Vector3f vec = (Vector3f)transform.apply(this.rotation);
            modelPart.method_33425(vec.x(), vec.y(), vec.z());
         }

      }

      public EmoteAnimation.PartPosition copy() {
         return new EmoteAnimation.PartPosition((Vector3f)Utils.orElse(this.pivot, Vector3f::new, (Object)null), (Vector3f)Utils.orElse(this.scale, Vector3f::new, (Object)null), (Vector3f)Utils.orElse(this.rotation, Vector3f::new, (Object)null));
      }

      public static EmoteAnimation.PartPosition fromModelPart(class_630 modelPart) {
         return new EmoteAnimation.PartPosition(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(modelPart.field_37938, modelPart.field_37939, modelPart.field_37940), new Vector3f((float)Math.toDegrees((double)modelPart.field_3654), (float)Math.toDegrees((double)modelPart.field_3675), (float)Math.toDegrees((double)modelPart.field_3674)));
      }

      @Nullable
      public Vector3f pivot() {
         return this.pivot;
      }

      @Nullable
      public Vector3f scale() {
         return this.scale;
      }

      @Nullable
      public Vector3f rotation() {
         return this.rotation;
      }
   }

   private static enum EaseType {
      NONE,
      IN,
      OUT,
      IN_OUT;

      // $FF: synthetic method
      private static EmoteAnimation.EaseType[] $values() {
         return new EmoteAnimation.EaseType[]{NONE, IN, OUT, IN_OUT};
      }
   }
}
