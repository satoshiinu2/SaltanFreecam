package com.satoshiinu.saltan.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.satoshiinu.saltan.SaltanClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Util;


public class SaltanFreecamConfigCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> commandRoot = ClientCommandManager.literal("saltan-freecam")
//                .then(genConfigCommandArg(ClientCommandManager.literal("config")))
                .then(ClientCommandManager.literal("toggle")
                        .executes(context -> {
                            boolean result = SaltanClient.toggleFreecam();

                            if(result)return (SaltanClient.isFreecamEnabled() ? 1 : 0);
                            return 0;
                        })
                        .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(context -> {
                                    boolean enabled = BoolArgumentType.getBool(context,"enabled");
                                    boolean result = SaltanClient.toggleFreecam(enabled);

                                    if(result)return (SaltanClient.isFreecamEnabled() ? 1 : 0);
                                    return 0;
                                })
                        )
                )
                .then(ClientCommandManager.literal("toggleLock")
                        .executes(context -> {
                            boolean result = SaltanClient.toggleFreecamLock();

                            if(result)return (!SaltanClient.isCameraLocked() ? 1 : 0);
                            return 0;
                        })
                        .then(ClientCommandManager.argument("locked", BoolArgumentType.bool())
                                .executes(context -> {
                                    boolean locked = BoolArgumentType.getBool(context,"locked");
                                    boolean result = SaltanClient.toggleFreecamLock(locked);

                                    if(result)return (SaltanClient.isCameraLocked() ? 1 : 0);
                                    return 0;
                                })
                        )
                )
                .then(ClientCommandManager.literal("toggleNightVision")
                        .executes(context -> {
                            boolean result = SaltanClient.toggleNightVision();

                            if(result)return (!SaltanClient.isNightVisionEnabled() ? 1 : 0);
                            return 0;
                        })
                        .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(context -> {
                                    boolean enabled = BoolArgumentType.getBool(context,"enabled");
                                    boolean result = SaltanClient.toggleNightVision(enabled);

                                    if(result)return (SaltanClient.isNightVisionEnabled() ? 1 : 0);
                                    return 0;
                                })
                        )
                );


        dispatcher.register(commandRoot);

    }
//
//    private static LiteralArgumentBuilder<FabricClientCommandSource> genConfigCommandArg(LiteralArgumentBuilder<FabricClientCommandSource> configRoot){
//        for (Config config : SaltanConfig.getConfigSet()) {
//            String saveKey = config.getSaveKey();
//            LiteralArgumentBuilder<FabricClientCommandSource> argBuilder = ClientCommandManager.literal(saveKey);
//            argBuilder.executes(context -> {
//                context.getSource().sendFeedback(Text.translatable("commands.saltan.config.query",saveKey,config.toString()));
//                return config.commandResult();
//            });
//
//            config.initCommandEntry(argBuilder);
//            configRoot.then(argBuilder);
//        }
//        configRoot.then(ClientCommandManager.literal("_load").executes(
//                context -> {
//                    SaltanConfig.loadConfig();
//                    context.getSource().sendFeedback(Text.translatable("commands.saltan.config.loaded"));
//                    return 1;
//                }
//        ));
//        configRoot.then(ClientCommandManager.literal("_save").executes(
//                context -> {
//                    SaltanConfig.saveConfig();
//                    context.getSource().sendFeedback(Text.translatable("commands.saltan.config.saved"));
//                    return 1;
//                }
//        ));
//        configRoot.then(ClientCommandManager.literal("_opendir").executes(
//                context -> {
//                    Util.getOperatingSystem().open(SaltanConfig.configDir.toPath());
//                    return 1;
//                }
//        ));
//        configRoot.then(ClientCommandManager.literal("_query").executes(
//                context -> {
//                    return printAllConfigs(context.getSource());
//                }
//        ));
//        configRoot.then(ClientCommandManager.literal("_open").executes(
//                context -> {
//                    if(FabricLoader.getInstance().isModLoaded("cloth-config")) {
//                        SaltanModMenu.openScreen(context.getSource().getClient());
//                        return 1;
//                    }
//                    return 0;
//                }
//        ));
//        return configRoot;
//    }
//
//    private static int printAllConfigs(FabricClientCommandSource source){
//        for (Config config : SaltanConfig.getConfigSet()){
//            String value = config.toString();
//            Text valueC = Text.literal(value).withColor(config.getCharColor());
//            source.sendFeedback(Text.translatable("commands.saltan.config.list",config.getSaveKey(),valueC));
//        }
//
//        return SaltanConfig.getConfigSet().size();
//    }
}
