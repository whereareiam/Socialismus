package me.whereareiam.socialismus.common.provider;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.chat.render.ChatMessageTransformer;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class ChatMessageTransformerProvider implements Provider<Set<ChatMessageTransformer>>, Registry<ChatMessageTransformer> {
	private final Set<ChatMessageTransformer> transformers = new HashSet<>();

	@Override
	public void register(ChatMessageTransformer transformer) {
		if (transformer != null) transformers.add(transformer);
	}

	@Override
	public Set<ChatMessageTransformer> get() {
		return transformers;
	}
}
