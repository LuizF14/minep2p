package br.edu.unifei.minep2p.minecraft;

import com.mojang.brigadier.context.CommandContext;

import br.edu.unifei.minep2p.service.controllers.BaseController;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import java.io.IOException;
import java.net.*;

public class TransferHostCommand {
    private final BaseController<InetAddress> controller;

    public TransferHostCommand(BaseController<InetAddress> controller) {
        this.controller = controller;
    }

    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("transferhost")
                    .then(argument("target", StringArgumentType.string())
                    .executes(this::run)
                )
            );
        });
    }

    private int run(CommandContext<ServerCommandSource> context) {
        String target = StringArgumentType.getString(context, "target");

        try {
            InetAddress ip_target = InetAddress.getByName(target);
            String data = this.controller.execute(ip_target);
            context.getSource().sendFeedback(() -> Text.literal(data), false);
            return Command.SINGLE_SUCCESS;
        } catch (UnknownHostException e) {
            context.getSource().sendError(Text.literal("ERRO: O endereço '" + target + "' é inválido. "));
        } catch (IOException e) {
            context.getSource().sendError(Text.literal("⚠️ Falha ao compactar ou acessar o mundo: " + e.getMessage()));
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            context.getSource().sendError(Text.literal("Operação interrompida."));
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("ERRO INESPERADO ao executar o comando 'transferhost': " 
                                            + e.getClass().getSimpleName() + " - " + e.getMessage()));
            e.printStackTrace();
        }
        return 0;
    }
}
