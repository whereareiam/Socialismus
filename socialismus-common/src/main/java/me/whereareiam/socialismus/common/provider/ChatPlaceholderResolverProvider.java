package me.whereareiam.socialismus.common.provider;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.chat.render.ChatPlaceholderResolver;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ChatPlaceholderResolverProvider implements Provider<Set<ChatPlaceholderResolver>>, Registry<ChatPlaceholderResolver> {
	private final Set<ChatPlaceholderResolver> resolvers = ConcurrentHashMap.newKeySet();

	@Override
	public void register(ChatPlaceholderResolver resolver) {
		if (resolver != null) resolvers.add(resolver);
	}

	@Override
	public void unregister(ChatPlaceholderResolver resolver) {
		if (resolver != null) resolvers.remove(resolver);
	}

	@Override
	public Set<ChatPlaceholderResolver> get() {
		return resolvers;
	}
}
