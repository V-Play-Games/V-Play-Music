package net.vplaygames.TheChaosTrilogy.commands.music;

import net.vplaygames.TheChaosTrilogy.commands.SharedImplementationCommand;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;

public class JoinCommand extends SharedImplementationCommand {
    public JoinCommand() {
        super("join", "Joins the audio channel you are currently connected in");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (e.getArgs().size() == 2 && e.getAuthor().getIdLong() == Bot.BOT_OWNER) {

        }
        if (!Util.canJoinVC(e)) {
            return;
        }
        e.send("Connected to " + e.getMember().getVoiceState().getChannel().getAsMention()).queue();
    }
}
