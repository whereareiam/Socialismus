package me.whereareiam.socialismus.common.provider;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.chat.render.ChatPlaceholderResolver;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class ChatPlaceholderResolverProvider implements Provider<Set<ChatPlaceholderResolver>>, Registry<ChatPlaceholderResolver> {
	private final Set<ChatPlaceholderResolver> resolvers = new HashSet<>();

	@Override
	public void register(ChatPlaceholderResolver resolver) {
		if (resolver != null) resolvers.add(resolver);
	}

	@Override
	public Set<ChatPlaceholderResolver> get() {
		return resolvers;
	}
}
