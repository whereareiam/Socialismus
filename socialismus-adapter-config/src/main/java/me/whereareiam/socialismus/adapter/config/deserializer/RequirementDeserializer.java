package me.whereareiam.socialismus.adapter.config.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.requirement.Requirement;
import me.whereareiam.socialismus.api.model.requirement.type.*;
import me.whereareiam.socialismus.api.type.requirement.RequirementConditionType;

import java.io.IOException;
import java.util.List;

@Singleton
public class RequirementDeserializer extends JsonDeserializer<Requirement> {
	private static final List<RequirementConditionType> SIMPLE_CONDITIONS = List.of(
			RequirementConditionType.EQUALS,
			RequirementConditionType.CONTAINS
	);

	private static final List<RequirementConditionType> PLACEHOLDER_CONDITIONS = List.of(
			RequirementConditionType.EQUALS,
			RequirementConditionType.GREATER_THAN,
			RequirementConditionType.LESS_THAN,
			RequirementConditionType.GREATER_THAN_OR_EQUALS,
			RequirementConditionType.LESS_THAN_OR_EQUALS
	);

	private static final List<RequirementConditionType> PERMISSION_CONDITIONS = List.of(
			RequirementConditionType.HAS,
			RequirementConditionType.CONTAINS
	);

	@Override
	public Requirement deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
		ObjectCodec codec = parser.getCodec();
		JsonNode root = codec.readTree(parser);

		if (root.has("servers"))
			return handleRequirement(codec, root, ServerRequirement.class, SIMPLE_CONDITIONS, RequirementConditionType.CONTAINS);

		if (root.has("worlds"))
			return handleRequirement(codec, root, WorldRequirement.class, SIMPLE_CONDITIONS, RequirementConditionType.CONTAINS);

		if (root.has("chatIdentifiers"))
			return handleRequirement(codec, root, ChatRequirement.class, SIMPLE_CONDITIONS, RequirementConditionType.CONTAINS);

		if (root.has("placeholders"))
			return handleRequirement(codec, root, PlaceholderRequirement.class, PLACEHOLDER_CONDITIONS, RequirementConditionType.EQUALS);

		if (root.has("permissions"))
			return handleRequirement(codec, root, PermissionRequirement.class, PERMISSION_CONDITIONS, RequirementConditionType.HAS);

		return null;
	}

	private <T extends Requirement> T handleRequirement(ObjectCodec codec, JsonNode root, Class<T> requirementClass, List<RequirementConditionType> validConditions, RequirementConditionType defaultCondition) throws IOException {
		T requirement = codec.treeToValue(root, requirementClass);
		if (!validConditions.contains(requirement.getCondition())) {
			requirement.setCondition(defaultCondition);
		}
		return requirement;
	}
}
