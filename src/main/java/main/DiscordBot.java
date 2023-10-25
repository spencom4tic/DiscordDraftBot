package main;

import model.Pokemon;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.lang.Object;


public class DiscordBot extends ListenerAdapter {
    static ArrayList<String> listOfPokemonString = new ArrayList<String>();
    static Dictionary<String, Pokemon> listOfPokemon = new Hashtable<>();
    static Dictionary<String, Pokemon[]>  userParties = new Hashtable<>();
    static final Object lock = new Object();



    public static void main(String[] args) throws LoginException, FileNotFoundException {

        Scanner s = new Scanner(new File("C:\\Users\\spenc\\OneDrive\\Desktop\\bot shit\\Discord-Bot\\src\\main\\PokemonNames"));

        while (s.hasNextLine()){
            listOfPokemonString.add(s.nextLine().toLowerCase());
        }
        for(String x: listOfPokemonString) {
            listOfPokemon.put(x, new Pokemon(x));

        }
        JDA bot = JDABuilder.createDefault("OTI5MTE1MDA3Mjg1ODY2NTc2.GmZeTs.EPxhE1T2RmOYi1x0uPz9LHaUd4nxNz_Y8bv7PI")
                .setActivity(Activity.customStatus("Spence Bot Test"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new DiscordBot())
                .build();




    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String command = event.getMessage().getContentRaw();
        String period = ".";
        if(command.charAt(0) == period.charAt(0)) {
            command = command.substring(1);
            System.out.println(command);
            if (command.equalsIgnoreCase("List")) {
                for(Enumeration x = listOfPokemon.keys(); x.hasMoreElements();) {
                    event.getChannel().sendMessage(x.nextElement().toString()).queue();
                }
            } else if (command.contains("addUser".toLowerCase())) {
                String userId = command.substring(command.indexOf(" ") + 1);
                String validIdChecker = "<@>";
                if (userId.length() == 21
                        && userId.charAt(0) == validIdChecker.charAt(0)
                        && userId.charAt(1) == validIdChecker.charAt(1)
                        && userId.charAt(20) == validIdChecker.charAt(2)) {
                    event.getChannel().sendMessage("Added: " + userId).queue();
                    userParties.put(userId, new Pokemon[6]);
                } else {
                    event.getChannel().sendMessage("User ID is not valid!").queue();
                }
            } else if (command.equalsIgnoreCase("listUsers")) {
                for(Enumeration x = userParties.keys(); x.hasMoreElements();) {
                    event.getChannel().sendMessage(x.nextElement().toString()).queue();
                }
            } else if (command.equalsIgnoreCase("startDraft")) {
                try {
                    startDraft(event);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else if (command.equalsIgnoreCase("myList")) {
                String id = event.getAuthor().getId();
                Pokemon[] list = userParties.get("<@" + id + ">");
                event.getChannel().sendMessage("Your roster:").queue();
                for(Pokemon x: list) {
                    event.getChannel().sendMessage(x.getPokemonName()).queue();
                }
            }
        }
    }
    public void startDraft (@NotNull MessageReceivedEvent event) throws InterruptedException {
        int upOrDown = 1;
        int round = 0;
        int draftNumber = 0;
        String channel = event.getChannel().toString();
        List<String> draftOrder = new ArrayList<>(userParties.size());
        for(Enumeration x = userParties.keys(); x.hasMoreElements();) {
            draftOrder.add(x.nextElement().toString());
        }
        Collections.shuffle(draftOrder);
        String activeDrafter = draftOrder.get(0);
        event.getChannel().sendMessage("The list is:").queue();
        for(String s: draftOrder) {
            event.getChannel().sendMessage(s).queue();
        }
        while(round < userParties.get(activeDrafter).length) {
            activeDrafter = draftOrder.get(draftNumber);
            event.getChannel().sendMessage("It is now your turn " + activeDrafter).queue();
            event.getJDA().addEventListener(new DraftPicker(event.getChannel(), activeDrafter, round));


            if((draftNumber+1 == draftOrder.size() && upOrDown == 1) || (draftNumber-1 == -1 && upOrDown == -1)) {
                round++;
                upOrDown = upOrDown*-1;
            } else {
                draftNumber = draftNumber + upOrDown;
            }

        }
        System.out.println("out of the while loop");

    }

}

