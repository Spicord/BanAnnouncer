package me.tini.announcer;

import me.tini.announcer.config.Config;
import me.tini.command.ICommandExecutor;
import me.tini.command.ICommandSender;

public class ReloadCommand implements ICommandExecutor {

    @Override
    public boolean handle(ICommandSender sender, String[] args) {
        if (!sender.hasPermission("banannouncer.reload")) {
            sender.sendMessage("You do not have permission to run this command.");
            return false;
        }

        Config.getInstance().reload();
        sender.sendMessage("Successfully reloaded BanAnnouncer");
        return true;
    }
}
