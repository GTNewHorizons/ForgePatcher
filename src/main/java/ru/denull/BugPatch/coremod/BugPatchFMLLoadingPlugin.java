package ru.denull.BugPatch.coremod;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.io.File;
import java.util.Map;

/**
 * Created by Vincent on 3/10/14.
 */
public class BugPatchFMLLoadingPlugin implements IFMLLoadingPlugin, IFMLCallHook {
    public String[] getASMTransformerClass() {
        return new String[]{BugPatchClassTransformer.class.getName()};
    }

    public String getModContainerClass() {
        return BugPatchDummyContainer.class.getName();
    }

    public String getSetupClass() {
        return BugPatchFMLLoadingPlugin.class.getName();
    }

    public void injectData(Map<String, Object> data) {
        BugPatchClassTransformer.instance.settingsFile = new File(((File) data.get("mcLocation")).getPath() + "/config/BugPatch.cfg");
        BugPatchClassTransformer.instance.initialize((Boolean) data.get("runtimeDeobfuscationEnabled"));
    }

    public String getAccessTransformerClass() {
        return null;
    }

    public Void call() {
        return null;
    }
}
