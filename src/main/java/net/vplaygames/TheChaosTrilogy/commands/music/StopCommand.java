package net.vplaygames.TheChaosTrilogy.commands.music;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.vplaygames.TheChaosTrilogy.commands.AbstractBotCommand;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;
import net.vplaygames.TheChaosTrilogy.player.PlayerManager;

public class StopCommand extends AbstractBotCommand {
    public StopCommand() {
        super("stop", "Stops the current song");
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e);
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e);
    }

    public void execute(CommandReceivedEvent e) {
        if (!Util.canJoinVC(e)) {
            return;
        }
        PlayerManager.getInstance().getMusicManager(e.getGuild()).stopTrack();
    }
}
