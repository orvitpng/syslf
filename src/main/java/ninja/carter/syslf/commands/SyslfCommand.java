package ninja.carter.syslf.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import ninja.carter.syslf.attachements.ArmorVisibility;
import ninja.carter.syslf.attachements.ArmorVisibilityUtil;

public class SyslfCommand {
    public static DynamicCommandExceptionType ERROR_INVALID =
        new DynamicCommandExceptionType(obj -> Component.literal("Unknown armor visibility: " + obj));

    public static void register(
        CommandDispatcher<CommandSourceStack> dispatcher,
        CommandBuildContext ignoredContext,
        Commands.CommandSelection ignoredEnv
    ) {
        dispatcher.register(Commands.literal("syslf")
            .executes(ctx -> {
                ServerPlayer player = ctx.getSource().getPlayerOrException();
                player.serverLevel().getChunkSource().broadcast(player, new ClientboundSetEquipmentPacket(player.getId(), List.of(
                    Pair.of(EquipmentSlot.HEAD, ItemStack.EMPTY)
                )));
                return 0;
            })
            .requires(Permissions.require("syslf.command.syslf", 0))
            .then(Commands.argument("visibility", StringArgumentType.word())
                .suggests((ctx, builder) ->
                    SharedSuggestionProvider.suggest(List.of("show", "hide"), builder))
                .executes(ctx ->
                    handle(ctx, Collections.singleton(ctx.getSource().getPlayerOrException())))
                .then(Commands.argument("target", EntityArgument.players())
                    .requires(Permissions.require("syslf.command.syslf.others", 2))
                    .executes(ctx ->
                        handle(ctx, EntityArgument.getPlayers(ctx, "target")))))
        );
    }

    public static int handle(
        CommandContext<CommandSourceStack> ctx,
        Collection<ServerPlayer> target
    ) throws CommandSyntaxException {
        setVisibility(ctx.getSource(), target, getVisibility(StringArgumentType.getString(ctx, "visibility")));
        return 0;
    }

    public static ArmorVisibility getVisibility(String string) throws CommandSyntaxException {
        if (string.equals("show")) return new ArmorVisibility(true);
        if (string.equals("hide")) return new ArmorVisibility(false);
        throw ERROR_INVALID.create(string);
    }

    public static void setVisibility(
        CommandSourceStack src,
        Collection<ServerPlayer> targets,
        ArmorVisibility visibility
    ) {
        targets.forEach(target -> {
            if (!ArmorVisibilityUtil.set(target, visibility)) return;

            src.sendSuccess(() ->
                    Component.literal("Set ")
                        .append(src.getEntity() == target
                            ? Component.literal("own")
                            : Component.empty().append(target.getDisplayName()).append("'s")
                        )
                        .append(" armor visibility to " + (visibility.visibility() ? "Shown" : "Hidden")),
                true
            );
        });
    }
}
