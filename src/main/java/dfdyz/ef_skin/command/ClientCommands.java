package dfdyz.ef_skin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dfdyz.ef_skin.EFSkinMod;
import dfdyz.ef_skin.storage.ClientSkinManager;
import dfdyz.ef_skin.storage.SkinFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = EFSkinMod.MODID, value = Dist.CLIENT)
public class ClientCommands {

    public static void MSGClient(String str){
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.displayClientMessage(Component.nullToEmpty(str),false);
        }
    }

    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event){
        event.getDispatcher().register(init());
    }

    public static int loadModel(CommandContext<CommandSourceStack> ctx){
        var ls = SkinFinder.getSkinList();
        var name = StringArgumentType.getString(ctx, "model_name");
        var info = ls.get(name);

        var pp = EpicFightCapabilities.getEntityPatch(Minecraft.getInstance().player, PlayerPatch.class);
        ClientSkinManager.loadSkinForEntity(pp, info);

        return Command.SINGLE_SUCCESS;
    }

    public static int debug(CommandContext<CommandSourceStack> ctx){
        return Command.SINGLE_SUCCESS;
    }

    public static int list(CommandContext<CommandSourceStack> ctx){
        var ls = SkinFinder.getSkinList();
        StringBuilder l = new StringBuilder();
        for (String k : ls.keySet()) {
            l.append(k).append("\n");
        }
        MSGClient(l.toString());
        return Command.SINGLE_SUCCESS;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> init(){
        var root = Commands.literal(EFSkinMod.MODID);
        root.executes(ClientCommands::debug);
        root.then(Commands
                .literal("ls")
                .executes(ClientCommands::list));
        root.then(Commands
                .literal("load")
                .then(Commands.argument("model_name", StringArgumentType.string())
                        .executes(ClientCommands::loadModel)
                )
        );
        root.then(Commands.literal("clr").executes((etx) -> {
            ClientSkinManager.clean();
            return Command.SINGLE_SUCCESS;
        }));
        return root;
    }
}
