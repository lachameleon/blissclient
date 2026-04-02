package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.commands.IRCCommand;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.screen.EventHandlingCScreen;
import com.dwarslooper.cactus.client.gui.toast.internal.GenericToast;
import com.dwarslooper.cactus.client.gui.widget.SubmittableTextWidget;
import com.dwarslooper.cactus.client.systems.profile.ProfileHandler;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_3674;
import net.minecraft.class_4185;
import org.jetbrains.annotations.NotNull;

public class DevOptionsScreen extends EventHandlingCScreen {
   public DevOptionsScreen() {
      super("devoptions");
   }

   public void method_25426() {
      super.method_25426();
      ((SubmittableTextWidget)this.method_37063(new SubmittableTextWidget(this.field_22793, 20, 40, 100, 20, class_2561.method_43473(), (s) -> {
         try {
            String[] address = s.split(":");
            IRCCommand.customAddress = new InetSocketAddress(address[0], Integer.parseInt(address[1]));
            CactusClient.getInstance().getIrcClient().connect();
         } catch (Exception var3) {
            this.error(var3);
         }

      }))).method_47404(class_2561.method_43470("cnet address"));
      this.method_37063(class_4185.method_46430(class_2561.method_43470("CNet dev addr"), (button) -> {
         IRCCommand.customAddress = new InetSocketAddress("127.0.0.1", 1414);
      }).method_46434(20, 64, 100, 20).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("Clear caches"), (button) -> {
         ProfileHandler.invalidateAll();
      }).method_46434(20, 88, 100, 20).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("Copy session"), (button) -> {
         (new class_3674()).method_15979(CactusConstants.mc.method_22683(), CactusConstants.mc.method_1548().method_1674());
      }).method_46434(20, 112, 100, 20).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("MATRIX!!"), (button) -> {
         CactusConstants.mc.method_1507(new DevOptionsScreen.MatrixScreen());
      }).method_46434(20, 136, 100, 20).method_46431());
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_25300(this.field_22793, "Developer Options", this.field_22789 / 2, 10, -1);
   }

   private void error(Throwable t) {
      CactusConstants.mc.method_1566().method_1999((new GenericToast("Error", t.getMessage())).setStyle(GenericToast.Style.SYSTEM));
   }

   public static class MatrixScreen extends CScreen {
      private static final String CHARLIST = "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン日月火水木金土上下左右中大小人光電空神夢愛心魂影零黒白赤青緑金銀風雨雪雷ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ０１２３４５６７８９！＠＃＄％＾＆＊（）";
      private static final List<DevOptionsScreen.MatrixScreen.Matrix> matrixColumns = new ArrayList();
      private static final Random random = new Random();

      public MatrixScreen() {
         super("matrix");
      }

      public void method_25426() {
         super.method_25426();
         float count = 100.0F;
         float sizeX = (float)this.field_22789 / count;
         if (matrixColumns.isEmpty()) {
            for(int i = 0; (float)i < count; ++i) {
               float x = (float)i * sizeX;
               if (x > (float)this.field_22789) {
                  break;
               }

               matrixColumns.add(new DevOptionsScreen.MatrixScreen.Matrix(x, random.nextFloat() * -500.0F, random.nextFloat() * 200.0F + 100.0F, random.nextInt(20) + 5, 65280, random.nextFloat() * 0.5F + 0.5F));
            }
         }

      }

      public void method_25410(int width, int height) {
         super.method_25410(width, height);
         matrixColumns.clear();
         this.method_41843();
      }

      public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {
         context.method_25294(0, 0, this.field_22789, this.field_22790, -1442840576);
         super.method_25420(context, mouseX, mouseY, delta);
      }

      public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
         super.method_25394(context, mouseX, mouseY, delta);
         Iterator var5 = matrixColumns.iterator();

         while(var5.hasNext()) {
            DevOptionsScreen.MatrixScreen.Matrix mtx = (DevOptionsScreen.MatrixScreen.Matrix)var5.next();
            mtx.y += delta / 100.0F * mtx.speed;
            float var10000 = mtx.y;
            int var10001 = mtx.length;
            Objects.requireNonNull(this.field_22793);
            if (var10000 - (float)(var10001 * 9) > (float)this.field_22790) {
               mtx.y = random.nextFloat() * -500.0F;
               mtx.speed = random.nextFloat() * 200.0F + 100.0F;
               mtx.length = random.nextInt(20) + 5;
               mtx.reshuffleChars();
            }

            int size = mtx.chars.size();

            for(int i = 0; i < size; ++i) {
               if ((double)random.nextFloat() < 0.005D) {
                  mtx.chars.set(i, randomChar());
               }

               char c = (Character)mtx.chars.get(i);
               float alpha = 1.0F - (float)i / (float)size;
               int fmtp = i == 0 ? 1 : 0;
               int color = Math.round(alpha * 255.0F) << 24 | fmtp * 255 << 16 | Math.max(fmtp, (int)(mtx.brightness * 255.0F)) << 8 | fmtp * 255;
               String var13 = String.valueOf(c);
               float var10002 = mtx.x;
               float var10003 = mtx.y;
               Objects.requireNonNull(CactusConstants.mc.field_1772);
               RenderUtils.drawText(context, var13, var10002, var10003 - (float)(i * 9), color);
            }
         }

      }

      private static char randomChar() {
         return "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン日月火水木金土上下左右中大小人光電空神夢愛心魂影零黒白赤青緑金銀風雨雪雷ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ０１２３４５６７８９！＠＃＄％＾＆＊（）".charAt(random.nextInt("アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン日月火水木金土上下左右中大小人光電空神夢愛心魂影零黒白赤青緑金銀風雨雪雷ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ０１２３４５６７８９！＠＃＄％＾＆＊（）".length()));
      }

      private static class Matrix {
         private float x;
         private float y;
         private float brightness;
         private float speed;
         private int length;
         private int color;
         private final List<Character> chars = new ArrayList();

         public Matrix(float x, float y, float speed, int length, int color, float brightness) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.length = length;
            this.color = color;
            this.brightness = brightness;
            this.reshuffleChars();
         }

         public void reshuffleChars() {
            this.chars.clear();

            for(int i = 0; i < this.length; ++i) {
               this.chars.add(DevOptionsScreen.MatrixScreen.randomChar());
            }

         }
      }
   }
}
