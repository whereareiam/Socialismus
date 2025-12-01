package me.whereareiam.socialismus.common.chat.worker.base;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.AllArgsConstructor;
import me.whereareiam.socialismus.Logger;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.common.requirement.RequirementEvaluator;
import me.whereareiam.socialismus.input.WorkerProcessor;
import me.whereareiam.socialismus.input.container.ChatContainerService;
import me.whereareiam.socialismus.input.event.chat.ChatResolvedEvent;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.ChatTrigger;
import me.whereareiam.socialismus.model.chat.InternalChat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.trigger.RegexChatTrigger;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.type.chat.Participants;
import me.whereareiam.socialismus.util.ComponentUtil;
import me.whereareiam.socialismus.util.EventUtil;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class ChatSelector {
	private final ChatContainerService containerService;
	private final RequirementEvaluator requirementEvaluator;
	private final Provider<ChatMessages> chatMessagesProvider;
	private final Provider<ChatSettings> chatSettingsProvider;

	private static final Comparator<InternalChat> BY_PRIORITY_DESC =
			Comparator.comparingInt(InternalChat::getPriority).reversed();

	@Inject
	public ChatSelector(
			ChatContainerService containerService,
			RequirementEvaluator requirementEvaluator,
			Provider<ChatMessages> chatMessages,
			Provider<ChatSettings> chatSettings,
			WorkerProcessor<ChatMessage> workerProcessor
	) {
		this.containerService = containerService;
		this.requirementEvaluator = requirementEvaluator;
		this.chatMessagesProvider = chatMessages;
		this.chatSettingsProvider = chatSettings;

		workerProcessor.addWorker(new Worker<>(this::selectChat, 50, true, false));
	}

	public ChatMessage selectChat(ChatMessage message) {
		if (message.getChat() != null) {
			Logger.debug("Chat already selected for user " + message.getSender().getUsername());
			return message;
		}

		final ChatSettings settings = chatSettingsProvider.get();
		final String plain = ComponentUtil.toPlain(message.getContent());
		Logger.debug("Selecting chat for user " + message.getSender().getUsername());

		// Pre-detect leading symbol without mutating content yet.
		final Optional<String> leadingSymbol = detectLeadingSymbol(plain);

		// 1) Try symbol trigger using leading character (if present and mapped)
		ChatMatch match = leadingSymbol
				.flatMap(sym -> findSymbolChat(sym).map(chat ->
						new ChatMatch(chat, findSymbolTrigger(chat, sym).orElse(null), true)))
				.orElse(null);

		// 2) Try regex triggers if no symbol chat matched
		if (match == null) {
			match = findRegexChat(plain, settings)
					.map(p -> {
						InternalChat chat = p.chat();
						RegexChatTrigger trig = p.trigger();
						return new ChatMatch(chat, trig, false);
					})
					.orElse(null);
		}

		// 3) Try default symbol trigger (empty symbol)
		if (match == null) {
			match = findDefaultSymbolChat(settings)
					.map(chat -> new ChatMatch(chat, findDefaultSymbolTrigger(chat).orElse(null), false))
					.orElse(null);
		}

		// If nothing or requirements fail → alternative / fallback / notify
		if (match == null || !meetsSenderRequirements(match.chat, message)) {
			ChatMatch alternative = findAlternativeOrFallback(leadingSymbol, match != null ? match.chat : null, message, settings);
			if (alternative == null || alternative.chat == null) {
				notifyNoMatchOrFallback(message, settings, alternative == ChatMatch.FALLBACK_MISSING);
				message.setCancelled(true);
				return message;
			}
			match = alternative;
		}

		// Apply content changes (strip symbol and/or regex) once we know the final trigger.
		applyStripping(message, match, plain);

		// Fire event and set chat
		ChatResolvedEvent event = new ChatResolvedEvent(message, match.chat, message.isCancelled());
		EventUtil.callEvent(event, () -> message.setChat(event.getChat()));
		message.setTrigger(match.trigger);
		Logger.debug("Selected chat: " + match.chat);

		return message;
	}

	private Optional<String> detectLeadingSymbol(String plain) {
		if (plain == null || plain.isEmpty()) return Optional.empty();
		String first = String.valueOf(plain.charAt(0));

		return containerService.hasChatBySymbol(first) ? Optional.of(first) : Optional.empty();
	}

	private Optional<InternalChat> findSymbolChat(String symbol) {
		List<InternalChat> candidates = containerService.getChatBySymbol(symbol);

		return candidates.stream().min(BY_PRIORITY_DESC);
	}

	private Optional<InternalChat> findDefaultSymbolChat(ChatSettings settings) {
		String fallbackId = settings.getFallback().getChatId();

		return containerService.getChats().stream()
				.filter(c -> !Objects.equals(c.getId(), fallbackId))
				.sorted(BY_PRIORITY_DESC)
				.filter(c -> c.getTriggers() != null && c.getTriggers().stream().anyMatch(this::isDefaultSymbolTrigger))
				.findFirst();
	}

	private Optional<SymbolChatTrigger> findSymbolTrigger(InternalChat chat, String symbol) {
		return chat.getTriggers().stream()
				.filter(this::isSymbolTrigger)
				.map(t -> (SymbolChatTrigger) t)
				.filter(t -> symbol.equals(t.getSymbol()))
				.findFirst();
	}

	private Optional<SymbolChatTrigger> findDefaultSymbolTrigger(InternalChat chat) {
		return chat.getTriggers().stream()
				.filter(this::isDefaultSymbolTrigger)
				.map(t -> (SymbolChatTrigger) t)
				.findFirst();
	}

	private Optional<Pair> findRegexChat(String plain, ChatSettings settings) {
		String fallbackId = settings.getFallback().getChatId();
		return containerService.getChats().stream()
				.filter(c -> !Objects.equals(c.getId(), fallbackId))
				.sorted(BY_PRIORITY_DESC)
				.filter(c -> c.getTriggers() != null && !c.getTriggers().isEmpty())
				.map(chat -> {
					RegexChatTrigger trig = chat.getTriggers().stream()
							.filter(this::isRegexTrigger)
							.map(t -> (RegexChatTrigger) t)
							.filter(t -> safeMatches(plain, t.getPattern()))
							.findFirst()
							.orElse(null);
					return trig != null ? new Pair(chat, trig) : null;
				})
				.filter(Objects::nonNull)
				.findFirst();
	}

	private boolean safeMatches(String input, String pattern) {
		try {
			return input != null && input.matches(pattern);
		} catch (Exception e) {
			Logger.warn("Invalid regex pattern in trigger: " + pattern);
			return false;
		}
	}

	private boolean isSymbolTrigger(ChatTrigger trigger) {
		return trigger instanceof SymbolChatTrigger && ((SymbolChatTrigger) trigger).getSymbol() != null;
	}

	private boolean isDefaultSymbolTrigger(ChatTrigger t) {
		if (!(t instanceof SymbolChatTrigger)) return false;
		String symbol = ((SymbolChatTrigger) t).getSymbol();

		return symbol == null || symbol.isEmpty();
	}

	private boolean isRegexTrigger(ChatTrigger trigger) {
		return trigger instanceof RegexChatTrigger;
	}

	private boolean meetsSenderRequirements(InternalChat chat, ChatMessage message) {
		if (chat.getRequirements() == null) return true;
		if (chat.getRequirements().get(Participants.SENDER) == null) return true;

		return requirementEvaluator.check(chat.getRequirements().get(Participants.SENDER), message.getSender());
	}

	/**
	 * Try:
	 * 1) Another chat bound to the same symbol (if available) that meets requirements (highest priority first).
	 * 2) Fallback chat (if enabled and available).
	 * Returns null if we should notify "no chat match".
	 * Returns ChatMatch.FALLBACK_MISSING sentinel if fallback is missing/misconfigured.
	 */
	private ChatMatch findAlternativeOrFallback(Optional<String> leadingSymbol, InternalChat current, ChatMessage msg, ChatSettings settings) {
		// 1) Try alternate chats for the same symbol (without stripping again).
		if (leadingSymbol.isPresent()) {
			String symbol = leadingSymbol.get();
			List<InternalChat> sameSymbol = containerService.getChatBySymbol(symbol);
			InternalChat alt = sameSymbol.stream()
					.filter(c -> !Objects.equals(c, current))
					.filter(c -> !Objects.equals(c.getId(), settings.getFallback().getChatId()))
					.sorted(BY_PRIORITY_DESC)
					.filter(c -> meetsSenderRequirements(c, msg))
					.findFirst()
					.orElse(null);
			if (alt != null) {
				SymbolChatTrigger trig = findSymbolTrigger(alt, symbol).orElse(null);
				return new ChatMatch(alt, trig, true);
			}
		}

		// 2) Fallback chat
		ChatSettings.FallbackChatSettings fb = settings.getFallback();
		if (!fb.isEnabled()) return null;

		Optional<InternalChat> fallback = containerService.getChat(fb.getChatId());
		if (fallback.isEmpty())
			return ChatMatch.FALLBACK_MISSING;

		InternalChat fbChat = fallback.get();
		if (!meetsSenderRequirements(fbChat, msg))
			// Fallback exists but still not allowed → treat as no match
			return null;

		// Fallback shouldn't strip anything.
		return new ChatMatch(fbChat, findDefaultSymbolTrigger(fbChat).orElse(null), /*stripLeadingSymbol*/ false);
	}

	private void notifyNoMatchOrFallback(ChatMessage message, ChatSettings settings, boolean fallbackMissing) {
		if (!settings.isNotifyNoChat()) return;

		ChatMessages msgs = chatMessagesProvider.get();

		if (fallbackMissing) {
			Logger.warn("Fallback chat is not available");
			message.getSender().sendMessage(Serializer.serialize(message.getSender(), msgs.getNoFallbackChat()));
		} else {
			message.getSender().sendMessage(Serializer.serialize(message.getSender(), msgs.getNoChatMatch()));
		}
	}

	private void applyStripping(ChatMessage message, ChatMatch match, String originalPlain) {
		// Strip leading symbol if we selected a symbol-bound chat via leading char.
		if (match.stripLeadingSymbol && originalPlain != null && !originalPlain.isEmpty()) {
			String first = String.valueOf(originalPlain.charAt(0));
			message.setContent(message.getContent().replaceText(
					TextReplacementConfig.builder()
							.matchLiteral(first)
							.replacement("")
							.once()
							.build()
			));
			Logger.debug("Stripped leading chat symbol: " + first);
		}

		// If regex trigger requested strip, apply its pattern now.
		if (match.trigger instanceof RegexChatTrigger regex && regex.isStrip()) {
			String pattern = regex.getPattern();
			message.setContent(message.getContent().replaceText(
					TextReplacementConfig.builder()
							.match(pattern)
							.replacement("")
							.once()
							.build()
			));
			Logger.debug("Stripped regex pattern from message content: " + pattern);
		}
	}

	@AllArgsConstructor
	private static final class ChatMatch {
		static final ChatMatch FALLBACK_MISSING = new ChatMatch(null, null, false);

		private final InternalChat chat;
		private final ChatTrigger trigger;
		private final boolean stripLeadingSymbol;
	}

	/**
	 * Pair of (chat, regexTrigger) for internal use.
	 */
	private record Pair(InternalChat chat, RegexChatTrigger trigger) {
	}
}
