package com.ggzzll.skindefault;

import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.InputDataResult;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

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
        UUID uuid = event.getPlayer().getUniqueId();

        if (skinsRestorerAPI.getPlayerStorage().getSkinOfPlayer(uuid).isEmpty()){
            Optional<InputDataResult> defaultSkin = skinsRestorerAPI.getSkinStorage().findSkinData(skinList.get(new Random().nextInt(skinList.size())));
            if (defaultSkin.isEmpty()) { return; }
            skinsRestorerAPI.getPlayerStorage().setSkinIdOfPlayer(uuid, defaultSkin.get().getIdentifier());
        }
    }
}
