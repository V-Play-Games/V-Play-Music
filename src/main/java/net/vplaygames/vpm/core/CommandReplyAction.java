package net.vplaygames.vpm.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import net.dv8tion.jda.api.utils.AttachmentOption;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
public class CommandReplyAction {
    private boolean isReply;
    private ReplyAction reply;
    private MessageAction message;
    private String content = "";
    private Runnable afterQueueTasks;

    public CommandReplyAction(Message message) {
        this(null, message);
    }

    public CommandReplyAction(Interaction interaction) {
        this(interaction, null);
    }

    public CommandReplyAction(Interaction interaction, Message message) {
        this(interaction, message, null);
    }

    public CommandReplyAction(Interaction interaction, Message message, Runnable afterQueueTasks) {
        this.afterQueueTasks = afterQueueTasks;
        if (interaction != null) {
            this.reply = interaction.deferReply();
            isReply = true;
        } else {
            this.message = message.reply(" ");
            isReply = false;
        }
    }

    public String getContent() {
        return content;
    }

    public CommandReplyAction setContent(String content) {
        if (isReply) {
            reply.setContent(content);
        } else {
            message.content(content);
        }
        this.content = content;
        return this;
    }

    public CommandReplyAction setEphemeral(boolean ephemeral) {
        if (isReply) {
            reply.setEphemeral(ephemeral);
        }
        return this;
    }

    public CommandReplyAction addFile(InputStream data, String name, AttachmentOption... options) {
        if (isReply) {
            reply.addFile(data, name, options);
        } else {
            message.addFile(data, name, options);
        }
        return this;
    }

    public CommandReplyAction addEmbeds(Collection<? extends MessageEmbed> embeds) {
        if (isReply) {
            reply.addEmbeds(embeds);
        } else {
            message.setEmbeds(embeds);
        }
        return this;
    }

    public CommandReplyAction addActionRows(ActionRow... rows) {
        if (isReply) {
            reply.addActionRows(rows);
        } else {
            message.setActionRows(rows);
        }
        return this;
    }

    public CommandReplyAction timeout(long timeout, TimeUnit unit) {
        if (isReply) {
            reply.timeout(timeout, unit);
        } else {
            message.timeout(timeout, unit);
        }
        return this;
    }

    public CommandReplyAction deadline(long timestamp) {
        if (isReply) {
            reply.deadline(timestamp);
        } else {
            message.deadline(timestamp);
        }
        return this;
    }

    public CommandReplyAction setTTS(boolean isTTS) {
        if (isReply) {
            reply.setTTS(isTTS);
        } else {
            message.tts(isTTS);
        }
        return this;
    }

    public CommandReplyAction mentionRepliedUser(boolean mention) {
        if (isReply) {
            reply.mentionRepliedUser(mention);
        } else {
            message.mentionRepliedUser(mention);
        }
        return this;
    }

    public CommandReplyAction allowedMentions(Collection<Message.MentionType> allowedMentions) {
        if (isReply) {
            reply.allowedMentions(allowedMentions);
        } else {
            message.allowedMentions(allowedMentions);
        }
        return this;
    }

    public CommandReplyAction mention(IMentionable... mentions) {
        if (isReply) {
            reply.mention(mentions);
        } else {
            message.mention(mentions);
        }
        return this;
    }

    public CommandReplyAction mentionUsers(String... userIds) {
        if (isReply) {
            reply.mentionUsers(userIds);
        } else {
            message.mentionUsers(userIds);
        }
        return this;
    }

    public CommandReplyAction mentionRoles(String... roleIds) {
        if (isReply) {
            reply.mentionRoles(roleIds);
        } else {
            message.mentionRoles(roleIds);
        }
        return this;
    }

    public CommandReplyAction addEmbeds(MessageEmbed... embeds) {
        if (isReply) {
            reply.addEmbeds(embeds);
        } else {
            message.setEmbeds(embeds);
        }
        return this;
    }

    public CommandReplyAction addActionRow(Component... components) {
        if (isReply) {
            reply.addActionRow(components);
        } else {
            message.setActionRow(components);
        }
        return this;
    }

    public CommandReplyAction addActionRow(Collection<? extends Component> components) {
        if (isReply) {
            reply.addActionRow(components);
        } else {
            message.setActionRow(components);
        }
        return this;
    }

    public CommandReplyAction addActionRows(Collection<? extends ActionRow> rows) {
        if (isReply) {
            reply.addActionRows(rows);
        } else {
            message.setActionRows(rows);
        }
        return this;
    }

    public CommandReplyAction addFile(File file, AttachmentOption... options) {
        if (isReply) {
            reply.addFile(file, options);
        } else {
            message.addFile(file, options);
        }
        return this;
    }

    public CommandReplyAction addFile(File file, String name, AttachmentOption... options) {
        if (isReply) {
            reply.addFile(file, name, options);
        } else {
            message.addFile(file, name, options);
        }
        return this;
    }

    public CommandReplyAction addFile(byte[] data, String name, AttachmentOption... options) {
        if (isReply) {
            reply.addFile(data, name, options);
        } else {
            message.addFile(data, name, options);
        }
        return this;
    }

    public CommandReplyAction append(String s) {
        content += s;
        if (isReply) {
            reply.setContent(content);
        } else {
            message.content(content);
        }
        return this;
    }

    public CompletableFuture<Object> submit(boolean shouldQueue) {
        CompletableFuture<?> future = isReply ? reply.submit(shouldQueue) : message.submit(shouldQueue);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public JDA getJDA() {
        if (isReply) {
            return reply.getJDA();
        } else {
            return message.getJDA();
        }
    }

    public BooleanSupplier getCheck() {
        if (isReply) {
            return reply.getCheck();
        } else {
            return message.getCheck();
        }
    }

    public CommandReplyAction setCheck(BooleanSupplier checks) {
        if (isReply) {
            reply.setCheck(checks);
        } else {
            message.setCheck(checks);
        }
        return this;
    }

    public Object complete(boolean shouldQueue) throws RateLimitedException {
        if (isReply) {
            return reply.complete(shouldQueue);
        } else {
            return message.complete(shouldQueue);
        }
    }

    public void queue() {
        queue(null);
    }

    public void queue(Consumer<? super Object> success) {
        queue(success, null);
    }

    public void queue(Consumer<? super Object> success, Consumer<? super Throwable> failure) {
        if (isReply) {
            reply.queue(success, failure);
        } else {
            message.queue(success, failure);
        }
        if (afterQueueTasks != null) {
            afterQueueTasks.run();
        }
    }

    public Object complete() {
        if (isReply) {
            return reply.complete();
        } else {
            return message.complete();
        }
    }

    public CompletableFuture<Object> submit() {
        return submit(true);
    }

    public ScheduledFuture<?> queueAfter(long delay, TimeUnit unit) {
        return queueAfter(delay, unit, null, null, null);
    }

    public ScheduledFuture<?> queueAfter(long delay, TimeUnit unit, Consumer<? super Object> success) {
        return queueAfter(delay, unit, success, null, null);
    }

    public ScheduledFuture<?> queueAfter(long delay, TimeUnit unit, Consumer<? super Object> success, Consumer<? super Throwable> failure) {
        return queueAfter(delay, unit, success, failure, null);
    }

    public ScheduledFuture<?> queueAfter(long delay, TimeUnit unit, ScheduledExecutorService executor) {
        return queueAfter(delay, unit, null, null, executor);
    }

    public ScheduledFuture<?> queueAfter(long delay, TimeUnit unit, Consumer<? super Object> success, ScheduledExecutorService executor) {
        return queueAfter(delay, unit, success, null, executor);
    }

    public ScheduledFuture<?> queueAfter(long delay, TimeUnit unit, Consumer<? super Object> success, Consumer<? super Throwable> failure, ScheduledExecutorService executor) {
        ScheduledFuture<?> tor;
        if (isReply) {
            tor = reply.queueAfter(delay, unit, success, failure, executor);
        } else {
            tor = message.queueAfter(delay, unit, success, failure, executor);
        }
        if (afterQueueTasks != null) {
            afterQueueTasks.run();
        }
        return tor;
    }
}
