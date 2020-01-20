package ru.denull.BugPatch.mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

/**
 * For non-ASM bugfixes
 */
@Mod(name = "BugPatch", modid = "bugpatch", version = "@VERSION@")
public class BugPatchProper {
    @Mod.Instance
    public static BugPatchProper instance;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent evt) {
        FMLCommonHandler.instance().bus().register(new CommonEvents());
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
        if (evt.getSide() == Side.CLIENT) {
            FMLCommonHandler.instance().bus().register(new ClientEvents());
            MinecraftForge.EVENT_BUS.register(new ClientEvents());
        }
    }
    
}
