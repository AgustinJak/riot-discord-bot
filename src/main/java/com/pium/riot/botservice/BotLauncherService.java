package com.pium.riot.botservice;

import com.pium.riot.commandservice.riotcommandmanagerservice.RiotCommandManagerService;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class BotLauncherService {
    public void start() throws InterruptedException {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String token = dotenv.get("token");

        JDA api = JDABuilder.createDefault(token,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS
                )
                .addEventListeners(new RiotCommandManagerService())
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("/profile"))
                .build().awaitReady();

        new CommandRegisterService(api).registerCommands();
    }
}