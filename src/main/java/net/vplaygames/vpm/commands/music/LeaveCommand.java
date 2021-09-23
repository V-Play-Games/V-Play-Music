package net.vplaygames.vpm.commands.music;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.vplaygames.vpm.commands.SharedImplementationCommand;
import net.vplaygames.vpm.core.Bot;
import net.vplaygames.vpm.core.CommandReceivedEvent;
import net.vplaygames.vpm.core.Util;
import net.vplaygames.vpm.player.GuildMusicManager;
import net.vplaygames.vpm.player.PlayerManager;

import java.util.List;

public class LeaveCommand extends SharedImplementationCommand {
    public LeaveCommand() {
        super("leave", "Leaves the current VC", "destroy", "dc");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (e.getAuthor().getIdLong() == Bot.BOT_OWNER && specialBotOwnerAccess(e)) {
            return;
        }
        VoiceChannel vc = e.getSelfMember().getVoiceState().getChannel();
        if (vc == null) {
            e.send("Leave? Leave what?").queue();
            return;
        }
        List<Member> members = Util.getListeningMembers(vc);
        if (members.isEmpty() ||
            (members.size() == 1 && members.get(0).equals(e.getMember())) ||
            e.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            PlayerManager.getInstance().getMusicManager(e.getGuild()).destroy();
            e.send("Left " + vc.getAsMention()).queue();
            return;
        }
        e.send("No. I am vibin' with my people in 'ere, I can't leave 'em alone like that :(").queue();
    }

    public boolean specialBotOwnerAccess(CommandReceivedEvent e) {
        if (e.getArgs().size() != 2) {
            return false;
        }
        if (e.getArg(1).equals("all")) {
            PlayerManager.getInstance().forEach(GuildMusicManager::destroy);
            e.send("DC'ed from everywhere, just for you senpai ;)").queue();
        } else if (e.getArg(1).equals("this")) {
            PlayerManager.getInstance().getMusicManager(e.getGuild()).destroy();
            e.send("DC'ed from here, just for you senpai ;)").queue();
        } else {
            e.send("wut").queue();
        }
        return true;
    }
}
