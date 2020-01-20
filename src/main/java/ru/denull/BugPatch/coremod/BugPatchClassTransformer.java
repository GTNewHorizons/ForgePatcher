package ru.denull.BugPatch.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.common.config.Configuration;
import ru.denull.BugPatch.BugPatchSettings;
import ru.denull.BugPatch.coremod.patchers.AbstractPatcher;
import ru.denull.BugPatch.coremod.patchers.BoatDesyncFixPatcher_Extra;
import ru.denull.BugPatch.coremod.patchers.BoatDesyncFixPatcher_Main;
import ru.denull.BugPatch.coremod.patchers.ChickenLureTweakPatcher;
import ru.denull.BugPatch.coremod.patchers.HeartBlinkFixPatcher;
import ru.denull.BugPatch.coremod.patchers.HeartFlashFixPatcher;
import ru.denull.BugPatch.coremod.patchers.ItemHopperBounceFixPatcher;
import ru.denull.BugPatch.coremod.patchers.ItemStairBounceFixPatcher;
import ru.denull.BugPatch.coremod.patchers.SnowballFixPatcher;
import ru.denull.BugPatch.coremod.patchers.VillageAnvilTweakPatcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by Vincent on 3/10/14.
 */
public class BugPatchClassTransformer implements IClassTransformer {

    public static BugPatchClassTransformer instance;
    public File settingsFile;
    private boolean hasInit = false;
    protected BugPatchSettings settings;
    private ArrayList<AbstractPatcher> patchers;
    public Logger logger = LogManager.getLogger("BugPatch");

    public BugPatchClassTransformer() {

        if (instance != null) {
            throw new RuntimeException("Only one transformer may exist!");
        } else {
            instance = this;
        }
    }

    public void initialize(Boolean isObf) {
        if (!hasInit) {
            Configuration config = new Configuration(settingsFile);
            config.load();
            settings = new BugPatchSettings();


            settings.ItemHopperBounceFixEnabled = config.get("COMMON", "ItemHopperBounceFixEnabled", false).getBoolean(false);
            settings.ItemStairBounceFixEnabled = config.get("COMMON", "ItemStairBounceFixEnabled", false).getBoolean(false);
            settings.SnowballFixEnabled = config.get("COMMON", "SnowballFixEnabled", true).getBoolean(true);

            settings.ChickenLureTweakEnabled = config.get("TWEAKS", "ChickenLureTweakEnabled", false).getBoolean(false);
            settings.VillageAnvilTweakEnabled = config.get("TWEAKS", "VillageAnvilTweakEnabled", false).getBoolean(false);

            settings.BoatDesyncFixEnabled = config.get("CLIENT", "BoatDesyncFixEnabled", true).getBoolean(true);
            settings.HeartBlinkFixEnabled = config.get("CLIENT", "HeartBlinkFixEnabled", true).getBoolean(true);
            settings.HeartFlashFixEnabled = config.get("CLIENT", "HeartFlashFixEnabled", true).getBoolean(true);

            if (!Arrays.asList(new File(new File(settingsFile.getParent()).getParent()).list()).contains("saves")) {
                logger.info("You probably are on a dedicated server. Disabling client fixes");
                settings.BoatDesyncFixEnabled = false;
                settings.HeartBlinkFixEnabled = false;
                settings.HeartFlashFixEnabled = false;
            }

            config.save();
            MappingRegistry.init(isObf);
            setupPatchers();
            hasInit = true;
        }
    }


    public byte[] transform(String par1, String par2, byte[] bytes) {
        if (hasInit) {
            for (AbstractPatcher p : patchers) {
                bytes = p.patch(bytes);
            }
        }
        return bytes;
    }


    private void setupPatchers() {
        if (patchers != null) {
            logger.warn("Patcher already initialized!!");
        } else {
            patchers = new ArrayList<AbstractPatcher>();


            if (settings.BoatDesyncFixEnabled) {
                patchers.add(new BoatDesyncFixPatcher_Main(
                    "BoatDesyncFix",
                    MappingRegistry.getClassNameFor("net/minecraft/entity/item/EntityBoat"),
                    MappingRegistry.getMethodNameFor("EntityBoat.setBoatIsEmpty"),
                    "(Z)V"
                ));
                patchers.add(new BoatDesyncFixPatcher_Extra(
                    "BoatDesyncFix|Extra",
                    MappingRegistry.getClassNameFor("net/minecraft/entity/item/EntityBoat"),
                    MappingRegistry.getMethodNameFor("EntityBoat.setPositionAndRotation2"),
                    "(DDDFFI)V"
                ));
            }

            if (settings.ChickenLureTweakEnabled) {
                patchers.add(new ChickenLureTweakPatcher(
                        "ChickenLureTweak",
                        MappingRegistry.getClassNameFor("net/minecraft/entity/passive/EntityChicken"),
                        "<init>",
                        "(L" + MappingRegistry.getClassNameFor("net/minecraft/world/World") + ";)V"
                ));
            }

            if (settings.HeartBlinkFixEnabled) {
                patchers.add(new HeartBlinkFixPatcher(
                    "HeartBlinkFix",
                    MappingRegistry.getClassNameFor("net/minecraft/client/entity/EntityPlayerSP"),
                    MappingRegistry.getMethodNameFor("EntityPlayerSP.setPlayerSPHealth"),
                    "(F)V"
                ));
            }

            if (settings.HeartFlashFixEnabled) {
                patchers.add(new HeartFlashFixPatcher(
                        "HeartFlashFix",
                        MappingRegistry.getClassNameFor("net/minecraft/client/entity/EntityClientPlayerMP"),
                        MappingRegistry.getMethodNameFor("EntityClientPlayerMP.attackEntityFrom"),
                        "(L" + MappingRegistry.getClassNameFor("net/minecraft/util/DamageSource") + ";F)Z"
                ));

            }

            if (settings.HeartBlinkFixEnabled && settings.HeartFlashFixEnabled) {
//                patchers.add(new HeartFlashFixCompatPatcher(
//                        "HeartFlashFix|Compat",
//                        MappingRegistry.getClassNameFor("net/minecraft/client/entity/EntityClientPlayerMP"),
//                        MappingRegistry.getMethodNameFor("EntityClientPlayerMP.setPlayerSPHealth"),
//                        "(F)V"
//                ));
            }

            if (settings.ItemHopperBounceFixEnabled) {
                patchers.add(new ItemHopperBounceFixPatcher(
                        "ItemHopperBounceFix",
                        MappingRegistry.getClassNameFor("net/minecraft/block/BlockHopper"),
                        MappingRegistry.getMethodNameFor("BlockHopper.addCollisionBoxesToList"),
                        "(L" +
                                MappingRegistry.getClassNameFor("net/minecraft/world/World") +
                                ";IIIL" +
                                MappingRegistry.getClassNameFor("net/minecraft/util/AxisAlignedBB") +
                                ";Ljava/util/List;L" +
                                MappingRegistry.getClassNameFor("net/minecraft/entity/Entity")
                                + ";)V"
                ));
            }

            if (settings.ItemStairBounceFixEnabled) {
                patchers.add(new ItemStairBounceFixPatcher(
                        "ItemStairBounceFix",
                        MappingRegistry.getClassNameFor("net/minecraft/block/BlockStairs"),
                        MappingRegistry.getMethodNameFor("BlockStairs.addCollisionBoxesToList"),
                        "(L" +
                                MappingRegistry.getClassNameFor("net/minecraft/world/World") +
                                ";IIIL" +
                                MappingRegistry.getClassNameFor("net/minecraft/util/AxisAlignedBB") +
                                ";Ljava/util/List;L" +
                                MappingRegistry.getClassNameFor("net/minecraft/entity/Entity")
                                + ";)V"
                ));
            }

            if (settings.SnowballFixEnabled) {
                patchers.add(new SnowballFixPatcher(
                        "SnowballFix",
                        MappingRegistry.getClassNameFor("net/minecraft/entity/player/EntityPlayer"),
                        MappingRegistry.getMethodNameFor("EntityPlayer.attackEntityFrom"),
                        "(L" + MappingRegistry.getClassNameFor("net/minecraft/util/DamageSource") + ";F)Z"
                ));
            }

            String sig2 = "(L" + MappingRegistry.getClassNameFor("net/minecraft/world/World") + ";"
                    + "Ljava/util/Random;"
                    + "L" + MappingRegistry.getClassNameFor("net/minecraft/world/gen/structure/StructureBoundingBox") + ";)Z";

            if (settings.VillageAnvilTweakEnabled) {
                patchers.add(new VillageAnvilTweakPatcher(
                        "VillageAnvilTweak",
                        MappingRegistry.getClassNameFor("net/minecraft/world/gen/structure/StructureVillagePieces$House2"),
                        MappingRegistry.getMethodNameFor("StructureVillagePieces$House2.addComponentParts"),
                        sig2 // break out into separate block above for readability
                ));
            }
        }
    }
}
