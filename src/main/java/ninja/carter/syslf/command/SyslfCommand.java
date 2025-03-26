package ninja.carter.syslf.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import ninja.carter.syslf.attachement.ArmorVisibilityUtil;

public class SyslfCommand {
    public static void register(
        CommandDispatcher<CommandSourceStack> dispatcher,
        CommandBuildContext context,
        Commands.CommandSelection env
    ) {
        dispatcher.register(Commands.literal("syslf")
            .then(Commands.literal("hide").executes(ctx ->
                handle(ctx.getSource().getPlayerOrException(), false)))
            .then(Commands.literal("show").executes(ctx ->
                handle(ctx.getSource().getPlayerOrException(), true)))
        );
    }

    public static int handle(ServerPlayer player, boolean visibility) {
        if (ArmorVisibilityUtil.get(player) == visibility) return 1;

        ArmorVisibilityUtil.set(player, visibility);
        // TODO: the rejoin issue can for sure be fixed, I'm just too lazy. probably just broadcast the packet again
        player.sendSystemMessage(
            Component.literal(
                "Set own armor visibility to "
                + (visibility ? "Shown" : "Hidden")
                + ". If changes aren't visible immediately, try reconnecting."
            )
        );
        return 0;
    }
}
