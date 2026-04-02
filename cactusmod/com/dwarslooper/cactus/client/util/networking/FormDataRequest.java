package com.dwarslooper.cactus.client.util.networking;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

public class FormDataRequest {
   public static final String BOUNDARY = "<cactus-%s-boundary>".formatted(new Object[]{Long.toHexString(System.currentTimeMillis())});
   private static final String LINE_END = "\r\n";
   private static final String DASH_SEPARATOR = "--";
   private final ByteArrayOutputStream outputStream;
   private final DataOutputStream writer;
   private Consumer<Throwable> errorCallback;

   public FormDataRequest(ByteArrayOutputStream outputStream) {
      this.outputStream = outputStream;
      this.writer = new DataOutputStream(outputStream);
   }

   public FormDataRequest errorHandler(Consumer<Throwable> callback) {
      this.errorCallback = callback;
      return this;
   }

   public FormDataRequest addTextField(String name, String value) {
      this.callWriter((writer) -> {
         writer.writeBytes("--" + BOUNDARY + "\r\n");
         writer.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n");
         writer.writeBytes("\r\n");
         writer.writeBytes(value + "\r\n");
      });
      return this;
   }

   public FormDataRequest addFileField(String name, File file) {
      this.callWriter((writer) -> {
         writer.writeBytes("--" + BOUNDARY + "\r\n");
         writer.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + file.getName() + "\"\r\n");
         writer.writeBytes("Content-Type: " + Files.probeContentType(file.toPath()) + "\r\n");
         writer.writeBytes("Content-Transfer-Encoding: binary\r\n");
         writer.writeBytes("\r\n");
         Files.copy(file.toPath(), this.outputStream);
         writer.writeBytes("\r\n");
      });
      return this;
   }

   public byte[] bake() {
      this.callWriter((writer) -> {
         writer.writeBytes("--" + BOUNDARY + "--\r\n");
         writer.flush();
         writer.close();
      });
      return this.outputStream.toByteArray();
   }

   public byte[] getRequestData() {
      return this.outputStream.toByteArray();
   }

   private void callWriter(FormDataRequest.WriterCallback callback) {
      try {
         callback.call(this.writer);
      } catch (IOException var3) {
         if (this.errorCallback == null) {
            throw new RuntimeException("Error writing to output stream", var3);
         }

         this.errorCallback.accept(var3);
      }

   }

   @FunctionalInterface
   private interface WriterCallback {
      void call(DataOutputStream var1) throws IOException;
   }
}
