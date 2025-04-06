package main.java.com.abdulmuhg.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MusicBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(MusicBot.class);

    public static void main(String[] args) {
        try {
            // Load properties file
            Properties props = new Properties();
            InputStream input = MusicBot.class.getClassLoader().getResourceAsStream("config.properties");

            if (input == null) {
                LOGGER.error("Unable to find config.properties, please create this file with your bot token");
                return;
            }

            props.load(input);
            String token = props.getProperty("bot.token");

            if (token == null || token.isEmpty()) {
                LOGGER.error("Bot token is missing from config.properties");
                return;
            }

            // Initialize JDA
            JDA jda = JDABuilder.createDefault(token)
                    .enableIntents(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_MEMBERS)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .setActivity(Activity.listening("!play commands"))
                    .addEventListeners(new CommandManager())
                    .build();

            jda.awaitReady();
            LOGGER.info("Bot is online and ready!");

        } catch (LoginException e) {
            LOGGER.error("Invalid bot token", e);
        } catch (IOException e) {
            LOGGER.error("Error loading config file", e);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for JDA to be ready", e);
        }
    }
}