package main;

import model.Pokemon;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DraftPicker extends ListenerAdapter {
    private final long channelId;
    private final String authorId;
    private final int round;

    public DraftPicker(MessageChannel channel, String author, int round) {
        this.channelId = channel.getIdLong();
        this.authorId = author;
        this.round = round;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String draftPick = event.getMessage().getContentRaw();
        if(event.getAuthor().getId().equals(authorId.substring(2, 20)) && event.getChannel().getIdLong() == channelId) {
            System.out.println(draftPick);
            if(DiscordBot.listOfPokemonString.contains(draftPick.toLowerCase())) {

                Pokemon pick = DiscordBot.listOfPokemon.get(draftPick);
                if(pick.isTaken()) {
                    event.getChannel().sendMessage("This has been taken! Please make another choice").queue();
                } else {
                    event.getChannel().sendMessage("You have chosen " + draftPick).queue();
                    DiscordBot.userParties.get(authorId)[round] = pick;
                    pick.assignPokemon(authorId);
                    event.getJDA().removeEventListener(this);
                }

            } else {
                event.getChannel().sendMessage("This is an invalid pick! please choose again").queue();
            }

        }

    }
}
