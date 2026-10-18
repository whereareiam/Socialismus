package me.whereareiam.socialismus.common.chat.worker.base;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.AllArgsConstructor;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.common.requirement.RequirementEvaluator;
import me.whereareiam.socialismus.event.chat.ChatResolvedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.ChatTrigger;
import me.whereareiam.socialismus.model.chat.InternalChat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.trigger.RegexChatTrigger;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.model.config.message.RequirementReasons;
import me.whereareiam.socialismus.model.requirement.RequirementReason;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import me.whereareiam.socialismus.service.requirement.RequirementReasonService;
import me.whereareiam.socialismus.type.chat.Participants;
import me.whereareiam.socialismus.util.ComponentUtil;
import me.whereareiam.socialismus.util.EventUtil;
import net.kyori.adventure.text.TextReplacementConfig;
import org.intellij.lang.annotations.RegExp;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class ChatSelector {
	private final ChatContainerService containerService;
	private final RequirementEvaluator requirementEvaluator;
	private final RequirementReasonService reasonService;
	private final Provider<ChatMessages> chatMessagesProvider;
	private final Provider<ChatSettings> chatSettingsProvider;

	private static final Comparator<InternalChat> BY_PRIORITY_DESC =
			Comparator.comparingInt(InternalChat::getPriority).reversed();

	@Inject
	public ChatSelector(
			ChatContainerService containerService,
			RequirementEvaluator requirementEvaluator,
			RequirementReasonService reasonService,
			Provider<ChatMessages> chatMessages,
			Provider<ChatSettings> chatSettings,
			WorkerProcessor<ChatMessage> workerProcessor
	) {
		this.containerService = containerService;
		this.requirementEvaluator = requirementEvaluator;
		this.reasonService = reasonService;
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
		RequirementResult senderResult = match == null ? null : evaluateSender(match.chat, message);
		if (match == null || senderResult.isDenied()) {
			ChatMatch alternative = findSameSymbolAlternative(leadingSymbol, match != null ? match.chat : null, message, settings);
			if (alternative == null) {
				Fallback fallback = findFallback(message, settings);
				if (fallback.match() == null) {
					// The sender hears why the chat they wrote for refused them; without one, why the fallback did.
					if (senderResult != null) notifyRefusal(message, settings, match.chat, senderResult, fallback.missing());
					else notifyRefusal(message, settings, fallback.chat(), fallback.result(), fallback.missing());
					message.setCancelled(true);
					return message;
				}
				alternative = fallback.match();
			}
			match = alternative;
		}

		// Apply content changes (strip symbol and/or regex) once we know the final trigger.
		applyStripping(message, match, plain);
		if (isBlank(message)) {
			notifyEmptyMessage(message, settings);
			Logger.debug("Cancelling chat message for user %s because trigger stripping left no content", message.getSender().getUsername());
			message.setCancelled(true);
			return message;
		}

		// Fire event and set chat
		ChatResolvedEvent event = new ChatResolvedEvent(message, match.chat, message.isCancelled());
		EventUtil.callEvent(event, () -> message.setChat(event.getChat()));
		message.setTrigger(match.trigger);
		Logger.debug("Selected chat: %s", match.chat.getId());

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
		String fallbackId = settings.getFallback().getChat();

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
		String fallbackId = settings.getFallback().getChat();
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

	private RequirementResult evaluateSender(InternalChat chat, ChatMessage message) {
		if (chat.getRequirements() == null) return RequirementResult.passed();

		return requirementEvaluator.evaluate(chat.getRequirements().get(Participants.SENDER), message.getSender());
	}

	/**
	 * Finds another chat bound to the same symbol whose sender requirements the sender meets, highest priority first.
	 */
	private ChatMatch findSameSymbolAlternative(Optional<String> leadingSymbol, InternalChat current, ChatMessage msg, ChatSettings settings) {
		if (leadingSymbol.isEmpty()) return null;

		// Alternate chats for the same symbol, without stripping again.
		String symbol = leadingSymbol.get();
		InternalChat alt = containerService.getChatBySymbol(symbol).stream()
				.filter(c -> !Objects.equals(c, current))
				.filter(c -> !Objects.equals(c.getId(), settings.getFallback().getChat()))
				.sorted(BY_PRIORITY_DESC)
				.filter(c -> evaluateSender(c, msg).isPassed())
				.findFirst()
				.orElse(null);
		if (alt == null) return null;

		SymbolChatTrigger trig = findSymbolTrigger(alt, symbol).orElse(null);
		return new ChatMatch(alt, trig, true);
	}

	/**
	 * Finds the fallback chat, if it is enabled, exists and takes the sender.
	 */
	private Fallback findFallback(ChatMessage msg, ChatSettings settings) {
		ChatSettings.FallbackChatSettings fb = settings.getFallback();
		if (!fb.isEnabled()) return Fallback.NONE;

		Optional<InternalChat> fallback = containerService.getChat(fb.getChat());
		if (fallback.isEmpty()) {
			Logger.warn("Fallback chat is not available");
			return Fallback.MISSING;
		}

		InternalChat fbChat = fallback.get();
		RequirementResult result = evaluateSender(fbChat, msg);
		if (result.isDenied()) return new Fallback(null, fbChat, result, false);

		// Fallback shouldn't strip anything.
		return new Fallback(new ChatMatch(fbChat, findDefaultSymbolTrigger(fbChat).orElse(null), false), fbChat, result, false);
	}

	/**
	 * Tells the sender why their message went nowhere: the refusal of a chat's sender requirements as chat words
	 * it, or else the no-chat notice.
	 */
	private void notifyRefusal(
			ChatMessage message,
			ChatSettings settings,
			InternalChat chat,
			RequirementResult refusal,
			boolean fallbackMissing
	) {
		RequirementReason reason = chat == null || refusal == null ? null : reasonService.resolve(
				refusal,
				wordingOf(chatMessagesProvider.get()),
				Map.of("chat", chat.getId())
		);
		if (reason != null) {
			message.getSender().sendMessage(Serializer.serialize(reason.toContent(message.getSender())));
			return;
		}

		notifyNoMatchOrFallback(message, settings, fallbackMissing);
	}

	private static Map<String, RequirementReasons> wordingOf(ChatMessages messages) {
		return messages.getRequirements() == null ? null : messages.getRequirements().get(Participants.SENDER);
	}

	private void notifyNoMatchOrFallback(ChatMessage message, ChatSettings settings, boolean fallbackMissing) {
		if (!settings.getNotifications().isNoChat()) return;

		ChatMessages msgs = chatMessagesProvider.get();

		if (fallbackMissing) {
			message.getSender().sendMessage(Serializer.serialize(message.getSender(), msgs.getNoFallbackChat()));
		} else {
			message.getSender().sendMessage(Serializer.serialize(message.getSender(), msgs.getNoChatMatch()));
		}
	}

	private void notifyEmptyMessage(ChatMessage message, ChatSettings settings) {
		if (!settings.getNotifications().isEmptyMessage()) return;

		String emptyMessage = chatMessagesProvider.get().getEmptyMessage();
		if (emptyMessage == null || emptyMessage.isBlank()) return;

		message.getSender().sendMessage(Serializer.serialize(message.getSender(), emptyMessage));
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
			@RegExp String pattern = regex.getPattern();
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

	private boolean isBlank(ChatMessage message) {
		if (message.getContent() == null) return true;
		String plain = ComponentUtil.toPlain(message.getContent());
		return plain.isBlank();
	}

	@AllArgsConstructor
	private static final class ChatMatch {
		private final InternalChat chat;
		private final ChatTrigger trigger;
		private final boolean stripLeadingSymbol;
	}

	/**
	 * What the fallback chat makes of a message: the match when it takes the sender, else whether it is missing, or
	 * the chat and the result of its sender requirements.
	 */
	private record Fallback(ChatMatch match, InternalChat chat, RequirementResult result, boolean missing) {
		static final Fallback NONE = new Fallback(null, null, null, false);
		static final Fallback MISSING = new Fallback(null, null, null, true);
	}

	/**
	 * Pair of (chat, regexTrigger) for internal use.
	 */
	private record Pair(InternalChat chat, RegexChatTrigger trigger) {
	}
}
