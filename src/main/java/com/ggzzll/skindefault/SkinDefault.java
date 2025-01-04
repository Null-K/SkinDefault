package com.ggzzll.skindefault;

import com.alibaba.fastjson.JSON;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.MojangSkinDataResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SkinDefault extends JavaPlugin implements Listener {

    private static SkinsRestorer skinsRestorerAPI;
    private List<String> skinList;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        skinsRestorerAPI = SkinsRestorerProvider.get();
        skinList = getConfig().getStringList("SkinDefault");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = event.getPlayer().getUniqueId();

        if (skinsRestorerAPI.getPlayerStorage().getSkinOfPlayer(uuid).isPresent()) { return; }

        String skinName = skinList.get(new Random().nextInt(skinList.size()));
        Optional<InputDataResult> playerSkin = skinsRestorerAPI.getSkinStorage().findSkinData(skinName);

        if (playerSkin.isPresent()) {
            applySkin(uuid,playerSkin.get(),event.getPlayer());
            return;
        }

        try {
            Optional<MojangSkinDataResult> skinOpt = skinsRestorerAPI.getSkinStorage().getPlayerSkin(skinName, true);
            if (skinOpt.isEmpty()) { return; }

            MojangSkinDataResult mojangSkin = skinOpt.get();
            String apiUrl = "https://api.mojang.com/user/profile/" + mojangSkin.getUniqueId();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(apiUrl).openConnection().getInputStream()))) {
                String mojangPlayerName = JSON.parseObject(reader.lines().collect(Collectors.joining())).getString("name");
                skinsRestorerAPI.getSkinStorage().setPlayerSkinData(mojangSkin.getUniqueId(), mojangPlayerName, mojangSkin.getSkinProperty(), System.currentTimeMillis());
                playerSkin = skinsRestorerAPI.getSkinStorage().findSkinData(mojangPlayerName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        playerSkin.ifPresent(skin -> applySkin(uuid, skin, player));
    }

    private void applySkin(UUID uuid, InputDataResult skin, Player player) {
        try {
            skinsRestorerAPI.getPlayerStorage().setSkinIdOfPlayer(uuid, skin.getIdentifier());
            skinsRestorerAPI.getSkinApplier(Player.class).applySkin(player);
        } catch (DataRequestException e) {
            getLogger().warning("玩家 " + player.getName() + " 应用皮肤时出错: " + e.getMessage());
        }
    }
}
