package com.pfplaybackend.api.admin.application.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Generates realistic English nicknames by combining two words
 * Pattern: Adjective + Noun (e.g., "HappyRabbit", "ShinyStars", "QuietOcean")
 */
public class NicknameGenerator {

    private static final Random RANDOM = new Random();

    // Adjectives - positive, fun, and diverse descriptive words (max 7 chars)
    private static final List<String> ADJECTIVES = Arrays.asList(
            "Happy", "Joyful", "Excited", "Calm",
            "Shiny", "Cute", "Cool", "Fancy",
            "Quiet", "Fresh", "Warm", "Soft", "Strong",
            "Brave", "Wise", "Smart", "Kind", "Sweet",
            "Active", "Gentle", "Serene",
            "Elegant", "Stylish", "Bold", "Free", "Merry",
            "Bright", "Golden", "Silver", "Crystal", "Mystic",
            "Lucky", "Noble", "Royal", "Divine", "Epic"
    );

    // Nouns - animals, nature, and fun concepts (max 7 chars)
    private static final List<String> NOUNS = Arrays.asList(
            "Rabbit", "Cat", "Puppy", "Tiger", "Lion",
            "Fox", "Wolf", "Bear", "Panda", "Koala",
            "Star", "Moon", "Sun", "Cloud", "Wind",
            "Wave", "Ocean", "Sky", "River",
            "Flower", "Tree", "Forest", "Garden", "Spring",
            "Summer", "Autumn", "Winter", "Dawn", "Dusk",
            "Phoenix", "Dragon", "Eagle", "Dolphin",
            "Rainbow", "Thunder", "Aurora", "Comet"
    );

    /**
     * Generate a random nickname by combining adjective + noun
     * Uses PascalCase format for readability
     *
     * Examples: "HappyRabbit", "ShinyStars", "QuietOcean"
     *
     * @return Generated nickname in PascalCase
     */
    public static String generate() {
        String adjective = ADJECTIVES.get(RANDOM.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(RANDOM.nextInt(NOUNS.size()));
        return adjective + noun;
    }

    /**
     * Generate a nickname with number suffix
     *
     * Examples: "HappyRabbit123", "ShinyStars456"
     *
     * @param suffix Number suffix to add
     * @return Generated nickname with suffix
     */
    public static String generateWithSuffix(int suffix) {
        return generate() + suffix;
    }

    /**
     * Generate unique nickname with index
     * Guarantees uniqueness within same session by using index
     * Maximum length: 7 (adj) + 7 (noun) + 3 (index) = 17 chars (within 20 char limit)
     *
     * @param index Index for uniqueness (1-based)
     * @return Generated unique nickname (max 17 characters)
     */
    public static String generateUnique(int index) {
        // Always append number directly without underscore to save space
        // Examples: "HappyRabbit1", "ElegantRainbow410"
        return generate() + index;
    }
}
