package com.dwarslooper.cactus.client.systems;

public enum UpdateReason {
   USER_INPUT_EXPECT_FEEDBACK(true),
   USER_INPUT_SILENT(false),
   UPDATE_STATE_FROM_CONFIG(false),
   OTHER(true);

   private final boolean feedback;

   private UpdateReason(boolean feedback) {
      this.feedback = feedback;
   }

   public boolean showsFeedback() {
      return this.feedback;
   }

   // $FF: synthetic method
   private static UpdateReason[] $values() {
      return new UpdateReason[]{USER_INPUT_EXPECT_FEEDBACK, USER_INPUT_SILENT, UPDATE_STATE_FROM_CONFIG, OTHER};
   }
}
