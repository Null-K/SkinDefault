package com.ggzzll.skindefault;

import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.InputDataResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class SkinDefault extends JavaPlugin implements Listener {

    private static SkinsRestorer SkinsRestorerAPI;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        SkinsRestorerAPI = SkinsRestorerProvider.get();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (SkinsRestorerAPI.getPlayerStorage().getSkinOfPlayer(player.getUniqueId()).isEmpty()){
            Optional<InputDataResult> SteveSkin = SkinsRestorerAPI.getSkinStorage().findSkinData(getConfig().getString("SkinDefault"));
            if (SteveSkin.isEmpty()) { return; }
            SkinsRestorerAPI.getPlayerStorage().setSkinIdOfPlayer(player.getUniqueId(), SteveSkin.get().getIdentifier());
        }
    }
}
