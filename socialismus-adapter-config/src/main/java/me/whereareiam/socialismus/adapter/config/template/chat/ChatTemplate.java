package me.whereareiam.socialismus.adapter.config.template.chat;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.adapter.config.dynamic.ChatsConfig;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.chat.ChatFormat;
import me.whereareiam.socialismus.api.model.chat.ChatParameters;
import me.whereareiam.socialismus.api.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.api.model.requirement.type.PermissionRequirement;
import me.whereareiam.socialismus.api.output.DefaultConfig;
import me.whereareiam.socialismus.api.type.Participants;
import me.whereareiam.socialismus.api.type.ChatType;
import me.whereareiam.socialismus.api.type.requirement.RequirementConditionType;
import me.whereareiam.socialismus.api.type.requirement.RequirementOperatorType;
import me.whereareiam.socialismus.api.type.requirement.RequirementType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ChatTemplate implements DefaultConfig<ChatsConfig> {
    @Override
    public ChatsConfig getDefault() {
        ChatsConfig chatsConfig = new ChatsConfig();

        // Default values
        Chat fallback = new Chat(
                "fallback",
                0,
                true,
                new ChatParameters(
                        ChatType.GLOBAL,
                        "",
                        0
                ),
                List.of(
                        new ChatFormat(
                                "{clear}<gray>[F] {playerName}: <white>{message}",
                                new HashMap<>()
                        )
                ),
                new HashMap<>()
        );

        Chat local = new Chat(
                "local",
                0,
                true,
                new ChatParameters(
                        ChatType.LOCAL,
                        "",
                        200
                ),
                List.of(
                        new ChatFormat(
                                "{clear}<gray>[L] {playerName}: <white>{message}",
                                new HashMap<>()
                        ),
                        new ChatFormat(
                                "{clear}<gray>[L] {playerName}: <gold>{message}",
                                Map.of(
                                        Participants.SENDER,
                                        RequirementGroup.builder()
                                                .operator(RequirementOperatorType.AND)
                                                .groups(Map.of(
                                                        RequirementType.PERMISSION, PermissionRequirement.builder()
                                                                .permissions(List.of("socialismus.admin"))
                                                                .condition(RequirementConditionType.HAS)
                                                                .expected("true")
                                                                .build()
                                                )).build()
                                )
                        )
                ),
                Map.of(
                        Participants.SENDER, RequirementGroup.builder()
                                .operator(RequirementOperatorType.AND)
                                .groups(Map.of(
                                        RequirementType.PERMISSION, PermissionRequirement.builder()
                                                .permissions(List.of("socialismus.chat.local"))
                                                .condition(RequirementConditionType.HAS)
                                                .expected("true")
                                                .build()
                                )).build(),
                        Participants.RECIPIENT, RequirementGroup.builder()
                                .operator(RequirementOperatorType.AND)
                                .groups(Map.of(
                                        RequirementType.PERMISSION, PermissionRequirement.builder()
                                                .permissions(List.of("socialismus.chat.local"))
                                                .condition(RequirementConditionType.HAS)
                                                .expected("true")
                                                .build()
                                )).build()
                )
        );

        Chat global = new Chat(
                "global",
                1,
                true,
                new ChatParameters(
                        ChatType.GLOBAL,
                        "!",
                        0
                ),
                List.of(
                        new ChatFormat(
                                "{clear}<gray>[G] {playerName}: <white>{message}",
                                new HashMap<>()
                        ),
                        new ChatFormat(
                                "{clear}<gray>[G] {playerName}: <gold>{message}",
                                Map.of(
                                        Participants.SENDER,
                                        RequirementGroup.builder()
                                                .operator(RequirementOperatorType.AND)
                                                .groups(Map.of(
                                                        RequirementType.PERMISSION, PermissionRequirement.builder()
                                                                .permissions(List.of("socialismus.admin"))
                                                                .condition(RequirementConditionType.HAS)
                                                                .expected("true")
                                                                .build()
                                                )).build()
                                )
                        )
                ),
                new HashMap<>()
        );

        chatsConfig.getChats().addAll(List.of(fallback, local, global));

        return chatsConfig;
    }
}