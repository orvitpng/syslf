package ninja.carter.syslf;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import ninja.carter.syslf.command.SyslfCommand;

public class ServerMod implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        Attachements.initialize();
        CommandRegistrationCallback.EVENT.register(SyslfCommand::register);
    }
}
