package com.ggzzll.skindefault;

import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.InputDataResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;
import java.util.Random;

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

        if (skinsRestorerAPI.getPlayerStorage().getSkinOfPlayer(player.getUniqueId()).isEmpty()){
            Optional<InputDataResult> defaultSkin = skinsRestorerAPI.getSkinStorage().findSkinData(skinList.get(new Random().nextInt(skinList.size())));
            if (defaultSkin.isEmpty()) { return; }
            skinsRestorerAPI.getPlayerStorage().setSkinIdOfPlayer(player.getUniqueId(), defaultSkin.get().getIdentifier());
        }
    }
}
