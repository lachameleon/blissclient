package com.dwarslooper.cactus.client.util.generic;

import com.dwarslooper.cactus.client.CactusClient;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class CopyImageToClipBoard implements ClipboardOwner {
   private static File lastScreenshot = null;
   private static CopyImageToClipBoard instance;

   public static CopyImageToClipBoard getInstance() {
      if (instance == null) {
         instance = new CopyImageToClipBoard();
      }

      return instance;
   }

   public void setLastScreenshot(File screenshot) {
      lastScreenshot = screenshot;
   }

   public static File getLastScreenshot() {
      return lastScreenshot;
   }

   public boolean isLastScreenshot(File screenshot) {
      return screenshot.equals(lastScreenshot);
   }

   public boolean copyImage(File screenshot) {
      try {
         BufferedImage bufferedImage = ImageIO.read(screenshot);
         CopyImageToClipBoard.TransferableImage trans = new CopyImageToClipBoard.TransferableImage(bufferedImage);
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(trans, this);
         return true;
      } catch (IOException var5) {
         CactusClient.getLogger().error("Failed to copy image to clipboard", var5);
         return false;
      }
   }

   public boolean copyLastScreenshot() {
      return lastScreenshot != null ? this.copyImage(lastScreenshot) : false;
   }

   public void lostOwnership(Clipboard clip, Transferable trans) {
      CactusClient.getLogger().info("Lost Clipboard Ownership");
   }

   public static record TransferableImage(Image i) implements Transferable {
      public TransferableImage(Image i) {
         this.i = i;
      }

      public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
         if (flavor.equals(DataFlavor.imageFlavor) && this.i != null) {
            return this.i;
         } else {
            throw new UnsupportedFlavorException(flavor);
         }
      }

      public DataFlavor[] getTransferDataFlavors() {
         DataFlavor[] flavors = new DataFlavor[]{DataFlavor.imageFlavor};
         return flavors;
      }

      public boolean isDataFlavorSupported(DataFlavor flavor) {
         DataFlavor[] flavors = this.getTransferDataFlavors();
         DataFlavor[] var3 = flavors;
         int var4 = flavors.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            DataFlavor dataFlavor = var3[var5];
            if (flavor.equals(dataFlavor)) {
               return true;
            }
         }

         return false;
      }

      public Image i() {
         return this.i;
      }
   }
}
