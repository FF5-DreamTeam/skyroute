package com.skyroute.skyroute.security.jwt;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void addToBlacklist(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public void removeFromBlacklist(String token) {
        blacklistedTokens.remove(token);
    }

    public void clearBlacklist() {
        blacklistedTokens.clear();
    }
}
