package ninja.carter.syslf;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import ninja.carter.syslf.commands.SyslfCommand;

public class Mod implements DedicatedServerModInitializer {
    public static final String MOD_ID = "syslf";

    @Override
    public void onInitializeServer() {
        Attachements.initialize();

        CommandRegistrationCallback.EVENT.register(SyslfCommand::register);
    }
}
