package dfdyz.ef_skin;

import dfdyz.ef_skin.client.renderer.RegisterRenderer;
import dfdyz.ef_skin.storage.ClientSkinManager;
import dfdyz.ef_skin.storage.SkinFinder;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;

@Mod(value = EFSkinMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = EFSkinMod.MODID, value = Dist.CLIENT)
public class EFSkinModClient {
    public EFSkinModClient(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        EpicFightClientEventHooks.Registry.ADD_PATCHED_ENTITY.registerEvent(RegisterRenderer::register);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        SkinFinder.setupPath();
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientSkinManager.clean();
    }
}
