package net.vplaygames.vpm.commands.music;

import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.Bot;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;

public class JoinCommand extends SharedImplementationCommand {
    public JoinCommand() {
        super("join", "Joins the audio channel you are currently connected in");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (e.getArgs().size() == 2 && e.getAuthor().getIdLong() == Bot.BOT_OWNER) {

        }
        Util.canJoinVC(e);
    }
}
