package org.example;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.voice.VoiceStateUpdateEvent; // Ajoutez cette importation

import javax.security.auth.login.LoginException;

public class MyBot {

    public static void main(String[] args) throws LoginException {
        // Remplacez par votre token Discord
        String botToken = "votre_token"; // Remplacez avec votre propre token

        // Construire le bot avec les permissions nécessaires
        JDABuilder.createDefault(botToken)
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES) // Permet d'interagir avec les salons vocaux
                .addEventListeners(new BotVoiceManager()) // Ajouter un gestionnaire pour la connexion vocale
                .build();
    }
}

class BotVoiceManager extends ListenerAdapter {
    private AudioChannel channel;

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Bot démarré et prêt !");

        // ID du salon vocal à rejoindre
        String voiceChannelId = "1100158192345952378";

        // Obtenir le salon vocal
        channel = event.getJDA().getVoiceChannelById(voiceChannelId);

        if (channel != null) {
            // Obtenir le gestionnaire audio de la guilde
            AudioManager audioManager = channel.getGuild().getAudioManager();

            // Connecter le bot au salon vocal
            audioManager.openAudioConnection(channel);
            System.out.println("Bot connecté au salon vocal : " + channel.getName());
        } else {
            System.err.println("Le salon vocal avec l'ID spécifié n'existe pas.");
        }
    }

    // Méthode pour maintenir la connexion active
    @Override
    public void onVoiceStateUpdate(VoiceStateUpdateEvent event) {
        // Vérifier si le bot est toujours dans le salon vocal
        if (event.getJDA().getSelfUser().getId().equals(event.getMember().getId())) {
            // Si le bot rejoint un autre salon ou quitte, on gère la connexion
            if (event.getChannelJoined() != null && !event.getChannelJoined().equals(channel)) {
                System.out.println("Le bot a rejoint un nouveau salon vocal : " + event.getChannelJoined().getName());
                AudioManager audioManager = event.getGuild().getAudioManager();
                audioManager.openAudioConnection(event.getChannelJoined());
            } else if (event.getChannelLeft() != null && event.getChannelLeft().equals(channel)) {
                // Si le bot quitte le salon vocal, il revient
                System.out.println("Le bot a quitté le salon vocal, il va revenir.");
                AudioManager audioManager = channel.getGuild().getAudioManager();
                audioManager.openAudioConnection(channel);
            }
        }
    }
}