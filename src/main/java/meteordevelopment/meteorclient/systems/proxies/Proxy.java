/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.proxies;

import com.google.common.net.InetAddresses;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class Proxy implements ISerializable<Proxy> {
    public final Settings settings = new Settings();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgOptional = settings.createGroup("Optional");

    public Setting<String> name = sgGeneral.add(new StringSetting.Builder()
        .name("name")
        .description("The name of the proxy.")
        .build()
    );

    public Setting<ProxyType> type = sgGeneral.add(new EnumSetting.Builder<ProxyType>()
        .name("type")
        .description("The type of proxy.")
        .defaultValue(ProxyType.Socks5)
        .build()
    );

    public Setting<String> address = sgGeneral.add(new StringSetting.Builder()
        .name("address")
        .description("The ip address of the proxy.")
        .filter(Utils::ipFilter)
        .visible(() -> !type.get().equals(ProxyType.OpenVPN))
        .build()
    );

    public Setting<Integer> port = sgGeneral.add(new IntSetting.Builder()
        .name("port")
        .description("The port of the proxy.")
        .defaultValue(0)
        .range(0, 65535)
        .sliderMax(65535)
        .noSlider()
        .visible(() -> !type.get().equals(ProxyType.OpenVPN))
        .build()
    );

    public Setting<String> configFile = sgGeneral.add(new StringSetting.Builder()
        .name("config-file")
        .description("Path to the OpenVPN configuration file (.ovpn).")
        .visible(() -> type.get().equals(ProxyType.OpenVPN))
        .build()
    );

    public Setting<Boolean> enabled = sgGeneral.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Whether the proxy is enabled.")
        .defaultValue(true)
        .build()
    );

    // Optional

    public Setting<String> username = sgOptional.add(new StringSetting.Builder()
        .name("username")
        .description("The username of the proxy.")
        .build()
    );

    public Setting<String> password = sgOptional.add(new StringSetting.Builder()
        .name("password")
        .description("The password of the proxy.")
        .visible(() -> type.get().equals(ProxyType.Socks5) || type.get().equals(ProxyType.OpenVPN))
        .build()
    );

    public Status status = Status.UNCHECKED;
    public long latency;

    private Process openvpnProcess;

    public void startOpenVPN() {
        if (!type.get().equals(ProxyType.OpenVPN)) return;
        String configPath = configFile.get();
        if (configPath == null || configPath.isEmpty()) return;

        try {
            ProcessBuilder pb = new ProcessBuilder("openvpn", configPath);
            pb.redirectErrorStream(true);

            // Handle authentication if username and password are provided
            String username = this.username.get();
            String password = this.password.get();
            if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                // Create a temporary file for auth credentials
                java.io.File authFile = java.io.File.createTempFile("openvpn_auth", ".txt");
                authFile.deleteOnExit();
                try (java.io.PrintWriter writer = new java.io.PrintWriter(authFile)) {
                    writer.println(username);
                    writer.println(password);
                }
                pb.command().add("--auth-user-pass");
                pb.command().add(authFile.getAbsolutePath());
            }

            openvpnProcess = pb.start();
            // TODO: handle output/logging
        } catch (IOException e) {
            // Handle error
        }
    }

    public void stopOpenVPN() {
        if (openvpnProcess != null) {
            openvpnProcess.destroy();
            openvpnProcess = null;
        }
    }

    // todo
    //  - add more rigorous status checks - perhaps querying our 'mcauth.meteorclient.com' or ccbluex's 'ping.liquidproxy.net'
    //  - the isSocks methods could be used to try and detect the version of added proxies when it's unclear -
    //     the only complication would be that some ips seem to be valid for both 4 and 5

    private Proxy() {}
    public Proxy(NbtElement tag) {
        fromTag((NbtCompound) tag);
    }

    public boolean resolveAddress() {
        return Utils.resolveAddress(this.address.get(), this.port.get());
    }

    /**
     *  Return codes: <br>
     *  0: In the process of checking <br>
     *  1: The proxy is alive <br>
     *  2: The proxy is dead <br>
     *  3: The check timed out
     */
    public int checkStatus() {
        if (status == Status.CHECKING) return 0;
        status = Status.CHECKING;

        if (type.get().equals(ProxyType.OpenVPN)) {
            // For OpenVPN, check if config file exists and is readable
            String configPath = configFile.get();
            if (configPath != null && !configPath.isEmpty()) {
                java.io.File file = new java.io.File(configPath);
                if (file.exists() && file.canRead()) {
                    status = Status.ALIVE;
                    latency = 0; // No latency for OpenVPN config check
                    return 1;
                }
            }
            status = Status.DEAD;
            return 2;
        }

        boolean timeout = false;

        try {
            Instant before = Instant.now();
            if (isSocks4()) {
                status = Status.ALIVE;
                latency = Duration.between(before, Instant.now()).toMillis();
                return 1;
            }
        }
        catch (SocketTimeoutException e) {
            timeout = true;
        }
        catch (IOException ignored) {}

        try {
            Instant before = Instant.now();
            if (isSocks5()) {
                status = Status.ALIVE;
                latency = Duration.between(before, Instant.now()).toMillis();
                return 1;
            }
        }
        catch (SocketTimeoutException e) {
            timeout = true;
        }
        catch (IOException ignored) {}

        status = Status.DEAD;
        return timeout ? 3 : 2;
    }

    private boolean isSocks4() throws IOException {
        ByteBuffer bb;
        byte[] u = username.get().getBytes();

        // SOCKS4
        if (InetAddresses.isInetAddress(address.get())) {
            bb = ByteBuffer.allocate(9 + u.length)
                .put((byte) 4)
                .put((byte) 1)
                .putShort(port.get().shortValue())
                .putInt(InetAddress.getByName(address.get()).hashCode()) // :clueless:
                .put(u)
                .put((byte) 0);
        }

        // SOCKS4a
        else {
            byte[] addr = address.get().getBytes();
            bb = ByteBuffer.allocate(9 + u.length + addr.length)
                .put((byte) 4)
                .put((byte) 1)
                .putShort(port.get().shortValue())
                .put(new byte[]{0, 0, 0, 1})
                .put(u)
                .put((byte) 0)
                .put(addr)
                .put((byte) 0);
        }

        byte[] data = sendData(bb.array(), 8);

        if (data.length < 2) return false;
        return data[0] == 0 && data[1] == 90;
    }

    private boolean isSocks5() throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(4)
            .put((byte) 5)
            .put((byte) 2)
            .put((byte) 0)
            .put((byte) 2);

        byte[] data = sendData(bb.array(), 2);

        if (data.length < 2) return false;
        return data[0] == 5 && (data[1] == 0 || data[1] == 2);
    }

    private byte[] sendData(byte[] data, int read) throws IOException {
        try (Socket s = new Socket()) {
            s.setSoTimeout(Proxies.get().timeout.get());
            s.connect(new InetSocketAddress(address.get(), port.get()), Proxies.get().timeout.get());
            OutputStream out = s.getOutputStream();

            out.write(data);

            return s.getInputStream().readNBytes(read);
        }
    }

    public static class Builder {
        protected ProxyType type = ProxyType.Socks5;
        protected String address = "";
        protected int port = 0;
        protected String name = "";
        protected String username = "";
        protected String password = "";
        protected String configFile = "";
        protected boolean enabled = false;

        public Builder type(ProxyType type) {
            this.type = type;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder configFile(String configFile) {
            this.configFile = configFile;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Proxy build() {
            Proxy proxy = new Proxy();

            if (!type.equals(proxy.type.getDefaultValue())) proxy.type.set(type);
            if (!address.equals(proxy.address.getDefaultValue())) proxy.address.set(address);
            if (port != proxy.port.getDefaultValue()) proxy.port.set(port);
            if (!name.equals(proxy.name.getDefaultValue())) proxy.name.set(name);
            if (!username.equals(proxy.username.getDefaultValue())) proxy.username.set(username);
            if (!password.equals(proxy.password.getDefaultValue())) proxy.password.set(password);
            if (!configFile.equals(proxy.configFile.getDefaultValue())) proxy.configFile.set(configFile);
            if (enabled != proxy.enabled.getDefaultValue()) proxy.enabled.set(enabled);

            return proxy;
        }
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.put("settings", settings.toTag());

        return tag;
    }

    @Override
    public Proxy fromTag(NbtCompound tag) {
        tag.getCompound("settings").ifPresent(settings::fromTag);

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proxy proxy = (Proxy) o;
        if (type.get().equals(ProxyType.OpenVPN)) {
            return Objects.equals(proxy.configFile.get(), this.configFile.get());
        }
        return Objects.equals(proxy.address.get(), this.address.get()) && Objects.equals(proxy.port.get(), this.port.get());
    }

    public enum Status {
        UNCHECKED,
        CHECKING,
        DEAD,
        ALIVE;

        @Override
        public String toString() {
            return switch (this) {
                case UNCHECKED -> "";
                case CHECKING -> "...";
                case DEAD -> "X";
                case ALIVE -> "O";
            };
        }

        public Color getColor() {
            return switch (this) {
                case UNCHECKED, CHECKING -> Color.GRAY;
                case DEAD -> Color.RED;
                case ALIVE -> Color.GREEN;
            };
        }
    }
}
