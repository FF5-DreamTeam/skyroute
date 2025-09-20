package com.skyroute.skyroute.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
class TokenBlacklistServiceTest {

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
    }

    @Test
    void addToBlacklist_shouldAddToken() {
        String token = "test-token-123";

        tokenBlacklistService.addToBlacklist(token);

        assertTrue(tokenBlacklistService.isBlacklisted(token));
    }

    @Test
    void isBlacklisted_shouldReturnTrue() {
        String token = "blacklisted-token";
        tokenBlacklistService.addToBlacklist(token);

        assertTrue(tokenBlacklistService.isBlacklisted(token));
    }

    @Test
    void removeFromBlacklist_shouldRemoveToken() {
        String token = "token-to-remove";
        tokenBlacklistService.addToBlacklist(token);
        assertTrue(tokenBlacklistService.isBlacklisted(token));

        tokenBlacklistService.removeFromBlacklist(token);

        assertFalse(tokenBlacklistService.isBlacklisted(token));
    }

    @Test
    void clearBlacklist_shouldRemoveAllTokens() {
        tokenBlacklistService.addToBlacklist("token1");
        tokenBlacklistService.addToBlacklist("token2");
        tokenBlacklistService.addToBlacklist("token3");

        tokenBlacklistService.clearBlacklist();

        assertFalse(tokenBlacklistService.isBlacklisted("token1"));
        assertFalse(tokenBlacklistService.isBlacklisted("token2"));
        assertFalse(tokenBlacklistService.isBlacklisted("token3"));
    }

    @Test
    void addToBlacklist_shouldHandleEmptyString() {
        String emptyToken = "";

        tokenBlacklistService.addToBlacklist(emptyToken);

        assertTrue(tokenBlacklistService.isBlacklisted(emptyToken));
    }

    @Test
    void addToBlacklist_shouldHandleDuplicateTokens() {
        String token = "duplicate-token";

        tokenBlacklistService.addToBlacklist(token);
        tokenBlacklistService.addToBlacklist(token);

        assertTrue(tokenBlacklistService.isBlacklisted(token));
    }

}