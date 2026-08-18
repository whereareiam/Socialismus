package me.whereareiam.socialismus.module.essentials.feature.dialogue.model;

import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.conversation.ConversationKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ConversationKeyTest {

@Test
void shouldCreateConsistentKeyRegardlessOfOrder() {
String player1 = "alice";
String player2 = "bob";

ConversationKey key1 = ConversationKey.of(player1, player2);
ConversationKey key2 = ConversationKey.of(player2, player1);

assertEquals(key2, key1);
assertEquals(key2.getKey(), key1.getKey());
assertEquals(key2.hashCode(), key1.hashCode());
}

@ParameterizedTest
@MethodSource("caseInsensitiveTestData")
void shouldHandleCaseInsensitiveNames(String name1, String name2, String expectedKey) {
ConversationKey key = ConversationKey.of(name1, name2);
assertEquals(expectedKey, key.getKey());
}

private static Stream<Arguments> caseInsensitiveTestData() {
return Stream.of(
Arguments.of("Alice", "Bob", "alice:bob"),
Arguments.of("ALICE", "bob", "alice:bob"),
Arguments.of("alice", "BOB", "alice:bob"),
Arguments.of("AlIcE", "BoB", "alice:bob")
);
}

@Test
void shouldGenerateValidKeyFromSet() {
Set<String> participants = Set.of("Charlie", "Alice", "Bob");
ConversationKey key = ConversationKey.of(participants);
assertEquals("alice:bob:charlie", key.getKey());
}

@Test
void shouldGenerateConsistentKeyFromSetRegardlessOfOrder() {
Set<String> participants1 = Set.of("bob", "alice");
Set<String> participants2 = Set.of("alice", "bob");

ConversationKey key1 = ConversationKey.of(participants1);
ConversationKey key2 = ConversationKey.of(participants2);

assertEquals(key2, key1);
assertEquals("alice:bob", key1.getKey());
}

@Test
void shouldHandleEmptyAndSingleParticipant() {
ConversationKey singleKey = ConversationKey.of(Set.of("alice"));
ConversationKey emptyKey = ConversationKey.of(Set.of());

assertEquals("alice", singleKey.getKey());
assertTrue(emptyKey.getKey().isEmpty());
}

@Test
void shouldImplementEqualsAndHashCodeCorrectly() {
ConversationKey key1 = ConversationKey.of("alice", "bob");
ConversationKey key2 = ConversationKey.of("alice", "bob");
ConversationKey key3 = ConversationKey.of("alice", "charlie");

assertEquals(key2, key1);
assertNotEquals(key3, key1);
assertNotEquals(null, key1);
assertNotEquals("some string", key1);

assertEquals(key2.hashCode(), key1.hashCode());
assertNotEquals(key3.hashCode(), key1.hashCode());
}

@Test
void shouldImplementToStringCorrectly() {
ConversationKey key = ConversationKey.of("alice", "bob");
String toString = key.toString();
assertEquals("ConversationKey(key=alice:bob)", toString);
}
}
