package me.whereareiam.socialismus.model.condition;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.conditia.model.Node;
import me.whereareiam.conditia.model.Requirements;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Builds the {@code requires:} lists of default configs in code, as an admin would write them:
 *
 * <pre>{@code
 * ConditionLists.of("permission", "socialismus.chat.local")   // - permission: socialismus.chat.local
 * ConditionLists.of("chat", "fallback", "local")               // - chat: [fallback, local]
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConditionLists {
	/**
	 * A list of one condition whose value is the texts given.
	 *
	 * @param condition the condition's name, such as {@code permission}
	 * @param values    its value; one becomes a bare value, several a list
	 * @return the list
	 */
	public static @NotNull Requirements of(@NotNull String condition, @NotNull String... values) {
		Node value = values.length == 1
				? new Node.Scalar(values[0])
				: new Node.Sequence(Arrays.stream(values).<Node>map(Node.Scalar::new).toList());

		return Requirements.of(new Node.Sequence(List.of(Node.Mapping.of(condition, value))));
	}

	/**
	 * A list of one {@code permission} condition.
	 *
	 * @param permissions the permissions a player must have
	 * @return the list
	 */
	public static @NotNull Requirements permission(@NotNull String... permissions) {
		return of("permission", permissions);
	}
}
