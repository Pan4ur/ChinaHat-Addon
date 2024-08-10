package thunder.addon.chinahat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thunder.addon.chinahat.modules.ChinaHat;
import thunder.hack.api.IAddon;
import thunder.hack.features.cmd.Command;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.Module;

import java.util.Arrays;
import java.util.List;

public class ChinaHatAddon implements IAddon {
    public static final Logger LOGGER = LoggerFactory.getLogger("thchinahat");

    @Override
    public void onInitialize() {
        LOGGER.info("Injecting china..");
    }

    @Override
    public List<Module> getModules() {
        return List.of(new ChinaHat());
    }

    @Override
    public List<Command> getCommands() {
        return null;
    }

    @Override
    public List<HudElement> getHudElements() {
        return null;
    }

    @Override
    public String getPackage() {
        return "thunder.addon.chinahat";
    }

    @Override
    public String getName() {
        return "ChinaHat";
    }

    @Override
    public String getAuthor() {
        return "Pan4ur";
    }

    @Override
    public String getRepo() {
        return "https://github.com/Pan4ur/ChinaHat-Addon";
    }

    @Override
    public String getVersion() {
        return "1.1.0";
    }
}