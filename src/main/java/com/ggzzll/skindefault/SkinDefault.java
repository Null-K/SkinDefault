package com.ggzzll.skindefault;

import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.VersionProvider;
import net.skinsrestorer.api.property.InputDataResult;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

import static java.lang.System.in;

public final class SkinDefault extends JavaPlugin implements Listener {

    private int Start = 0;
    private SkinsRestorer SkinsRestorerAPI;

    @Override
    public void onEnable() {
        getLogger().info(getDescription().getName() + " 已启用！");

        if (!VersionProvider.isCompatibleWith("15")) {
            getLogger().info("插件最好使用15以上的版本，不然可能会存在些许问题，你现在安装的版本是" + VersionProvider.getVersionInfo());
        }

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File File = new File(getDataFolder(), "Config.yml");
        if (!File.exists()) {
            saveResource("Config.yml", false);
        }

        SkinsRestorerAPI = SkinsRestorerProvider.get();
        getLogger().info(getDescription().getName() + " 初始化完成！");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player Player = event.getPlayer();

        if (!SkinsRestorerAPI.getPlayerStorage().getSkinOfPlayer(Player.getUniqueId()).isPresent()){

            File File = new File(getDataFolder(), "Config.yml");
            YamlConfiguration Config = YamlConfiguration.loadConfiguration(File);

            Optional<InputDataResult> SteveSkin = SkinsRestorerAPI.getSkinStorage().findSkinData(Config.getString("SkinDefault"));

            if (!SteveSkin.isPresent()) {
                getLogger().info(" 玩家 " + Player.getName() + " 在进入服务器时设置 " + Config.getString("SkinDefault") + "皮肤失败，原因：皮肤ID不存在");
                return;
            }

            SkinsRestorerAPI.getPlayerStorage().setSkinIdOfPlayer(Player.getUniqueId(), SteveSkin.get().getIdentifier());
            getLogger().info(" 玩家 " + Player.getName() + " 在进入服务器时设置 " + Config.getString("SkinDefault") + "皮肤成功");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info(getDescription().getName() + " 感谢您的使用，如果可以请给该项目点一个Star吧！！！期待您的下次使用");
    }
}
