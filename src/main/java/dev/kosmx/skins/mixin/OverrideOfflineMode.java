package dev.kosmx.skins.mixin;

import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.HttpURLConnection;
import java.net.URL;

@Mixin(ServerLoginNetworkHandler.class)
public class OverrideOfflineMode {


    @Redirect(method = "onHello", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isOnlineMode()Z"))
    private boolean overrideOfflineMode(MinecraftServer instance, LoginHelloC2SPacket packet) {
        var profile = packet.getProfile();
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + profile.getName());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (!(connection.getResponseCode() == HttpURLConnection.HTTP_OK)) {
                return false;
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }
        return instance.isOnlineMode();
    }
}
