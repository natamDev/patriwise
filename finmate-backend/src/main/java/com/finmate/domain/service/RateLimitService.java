package com.finmate.domain.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service de rate limiting basé sur Bucket4j.
 * Stockage en mémoire pour commencer (peut être migré vers Redis ultérieurement).
 */
@ApplicationScoped
public class RateLimitService {

    // Stockage en mémoire des buckets par clé
    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<UUID, Bucket> aiBuckets = new ConcurrentHashMap<>();
    private final Map<UUID, Bucket> globalApiBuckets = new ConcurrentHashMap<>();

    /**
     * Vérifie si une tentative de login est autorisée.
     * Limite : 5 tentatives par email toutes les 15 minutes.
     */
    public boolean allowLoginAttempt(String email) {
        Bucket bucket = loginBuckets.computeIfAbsent(email, k -> createLoginBucket());
        return bucket.tryConsume(1);
    }

    /**
     * Vérifie si une requête AI est autorisée.
     * Limite : 20 requêtes par user toutes les heures.
     */
    public boolean allowAiRequest(UUID userId) {
        Bucket bucket = aiBuckets.computeIfAbsent(userId, k -> createAiBucket());
        return bucket.tryConsume(1);
    }

    /**
     * Vérifie si une requête API globale est autorisée.
     * Limite : 100 requêtes par user par minute.
     */
    public boolean allowGlobalApiRequest(UUID userId) {
        Bucket bucket = globalApiBuckets.computeIfAbsent(userId, k -> createGlobalApiBucket());
        return bucket.tryConsume(1);
    }

    /**
     * Réinitialise le compteur de login pour un email (utilisé après login réussi).
     */
    public void resetLoginAttempts(String email) {
        loginBuckets.remove(email);
    }

    private Bucket createLoginBucket() {
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(15)));
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createAiBucket() {
        Bandwidth limit = Bandwidth.classic(20, Refill.intervally(20, Duration.ofHours(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createGlobalApiBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
