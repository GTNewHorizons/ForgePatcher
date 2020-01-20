package ru.denull.BugPatch.coremod;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

import java.util.Collections;

public class BugPatchDummyContainer extends DummyModContainer {

    public BugPatchDummyContainer() {
        super(new ModMetadata());
        ModMetadata myMeta = super.getMetadata();
        myMeta.authorList = Collections.singletonList("deNULL");
        myMeta.modId = "bugpatch-core";
        myMeta.version = "@VERSION@";
        myMeta.name = "BugPatchCore";
    }

    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
}
