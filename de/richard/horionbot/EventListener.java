package de.richard.horionbot;

import de.richard.horionbot.utils.Suggestions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static de.richard.horionbot.utils.Suggestions.*;

public class EventListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if (e.getTextChannel().equals(Suggestions.SuggestionChannel)) {
            Message message = e.getTextChannel().retrieveMessageById(e.getMessageId()).complete();
            String SuggestionID = Objects.requireNonNull(Objects.requireNonNull(message.getEmbeds().get(0).getFooter()).getText()).substring(14);
            SuggestionID = SuggestionID.substring(0, 36);
            String authorID;
            try {
                Properties prop = new Properties();
                InputStream is = new FileInputStream("suggestions/" + SuggestionID + ".xml");
                prop.loadFromXML(is);
                authorID = prop.getProperty("authorID");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            for (int x = 0; x < message.getReactions().size(); x++) {
                List<User> ReactionUsers = message.getReactions().get(x).retrieveUsers().complete();
                if (ReactionUsers.contains(e.getUser()) && !message.getReactions().get(x).getReactionEmote().equals(e.getReaction().getReactionEmote()) && !e.getUser().isBot()) {
                    e.getReaction().removeReaction(e.getUser()).queue();
                }
                if (message.getReactions().get(x).retrieveUsers().complete().size() > downvoteLimit && e.getReaction().getReactionEmote().getName().contains("deny")) {
                    try {
                        denySuggestion(SuggestionID, e.getJDA().getSelfUser());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    e.getTextChannel().deleteMessageById(e.getMessageId()).queue();
                }
            }

            if (Objects.requireNonNull(e.getMember()).hasPermission(Permission.ADMINISTRATOR) && !e.getUser().isBot()) {
                if (e.getReactionEmote().getName().contains("accept")) {
                    try {
                        acceptSuggestion(SuggestionID, e.getUser());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    e.getTextChannel().deleteMessageById(e.getMessageId()).queue();
                } else if (e.getReactionEmote().getName().contains("deny")) {
                    try {
                        denySuggestion(SuggestionID, e.getUser());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    e.getTextChannel().deleteMessageById(e.getMessageId()).queue();
                }
            } else if (e.getUser().getId().equals(authorID)) {
                if (e.getReactionEmote().getName().contains("deny")) {
                    e.getTextChannel().deleteMessageById(e.getMessageId()).queue();
                    File save = new File("suggestion/" + SuggestionID + ".xml");
                    save.delete();
                } else {
                    e.getReaction().removeReaction(e.getUser()).queue();
                }
            }
        }
        if (e.getTextChannel().equals(Suggestions.acceptedSuggestionsChannel)) {
            Message message = Suggestions.acceptedSuggestionsChannel.retrieveMessageById(e.getMessageId()).complete();
            MessageEmbed oldembed = message.getEmbeds().get(0);
            String SuggestionID = Objects.requireNonNull(Objects.requireNonNull(oldembed.getFooter()).getText()).substring(14);
            SuggestionID = SuggestionID.substring(0, 36);
            if(e.getMember().hasPermission(Permission.ADMINISTRATOR) && !e.getUser().isBot()) {
                if(e.getReactionEmote().getName().contains("accept")) {
                    MessageEmbed newembed = new EmbedBuilder()
                            .setColor(new Color(0x84D26A))
                            .setTitle(oldembed.getTitle() + " (DONE)")
                            .setDescription(oldembed.getDescription())
                            .setFooter(oldembed.getFooter().getText(), oldembed.getFooter().getIconUrl())
                            .build();
                    message.editMessage(newembed).queue();
                    message.clearReactions().queue();
                    File save = new File("suggestions/" + SuggestionID + ".xml");
                    save.delete();
                }
            } else {
                if(!e.getUser().getId().equalsIgnoreCase("605822602400890903")) e.getReaction().removeReaction(e.getUser()).queue();
            }
        }
    }
}
