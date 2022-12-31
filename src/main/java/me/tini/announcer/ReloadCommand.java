package me.tini.announcer;

import eu.mcdb.universal.command.UniversalCommandSender;
import eu.mcdb.universal.command.api.Command;
import me.tini.announcer.config.Config;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super(
            "banannouncer-reload", // main command
            "banannouncer.reload", // required permission
            "bareload"             // alias command
        );
        super.setCommandHandler(this::handle);
    }

    public boolean handle(UniversalCommandSender sender) {
        Config.getInstance().reload();
        sender.sendMessage("Successfully reloaded BanAnnouncer");
        return true;
    }
}
