package dev.stardust.mixin.accessor;

import javax.annotation.Nullable;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Connection.class)
public interface ClientConnectionAccessor {
    @Invoker("sendImmediately")
    void invokeSendImmediately(Packet<?> packet, @Nullable PacketSendListener callbacks, boolean flush);
}
