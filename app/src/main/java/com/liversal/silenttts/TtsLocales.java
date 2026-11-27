package com.liversal.silenttts;

import android.speech.tts.Voice;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

/**
 * Shared locale and voice metadata utilities for Silent TTS.
 * Keeps reported voices consistent across CheckTtsDataActivity and SilentTtsService
 * without triggering AOSP's default 800+ locale scan over Binder IPC.
 */
final class TtsLocales {
    private static final Locale[] DEFAULT_LOCALES = new Locale[] {
            Locale.US,
            Locale.UK,
            Locale.FRANCE,
            Locale.CANADA_FRENCH,
            new Locale("fr", "MA"),
            new Locale("fr", "BE"),
            new Locale("fr", "CH"),
            new Locale("ar", "SA"),
            new Locale("ar", "EG"),
            new Locale("ar", "MA"),
            new Locale("es", "ES"),
            new Locale("es", "US"),
            Locale.GERMANY,
            Locale.ITALY,
            new Locale("pt", "BR"),
            new Locale("pt", "PT"),
            new Locale("nl", "NL"),
            new Locale("tr", "TR"),
            new Locale("ru", "RU"),
            Locale.SIMPLIFIED_CHINESE,
            Locale.JAPAN,
            Locale.KOREA,
            new Locale("hi", "IN")
    };

    private TtsLocales() {
    }

    static String[] getDefaultLanguageTriplet() {
        String[] triplet = toIso3Triplet(Locale.getDefault());
        if (triplet != null) {
            return triplet;
        }
        return new String[] { "eng", "USA", "" };
    }

    static String[] toIso3Triplet(Locale locale) {
        if (locale == null) {
            return null;
        }
        try {
            String lang = safeTrim(locale.getISO3Language());
            if (lang.isEmpty()) {
                return null;
            }
            String country = "";
            if (!safeTrim(locale.getCountry()).isEmpty()) {
                try {
                    country = safeTrim(locale.getISO3Country());
                } catch (MissingResourceException ignored) {
                    country = "";
                }
            }
            String variant = safeTrim(locale.getVariant());
            return new String[] { lang, country, variant };
        } catch (MissingResourceException ignored) {
            return null;
        }
    }

    static String formatVoiceName(String language, String country, String variant) {
        String lang = safeTrim(language);
        if (lang.isEmpty()) {
            return "eng-USA";
        }
        String ctry = safeTrim(country);
        String var = safeTrim(variant);
        StringBuilder sb = new StringBuilder(lang);
        if (!ctry.isEmpty()) {
            sb.append('-').append(ctry);
        }
        if (!var.isEmpty()) {
            sb.append('-').append(var);
        }
        return sb.toString();
    }

    static String[] parseVoiceName(String voiceName) {
        String trimmed = safeTrim(voiceName);
        if (trimmed.isEmpty()) {
            return null;
        }
        String[] parts = trimmed.split("[-_]", 3);
        String lang = parts.length > 0 ? safeTrim(parts[0]) : "";
        if (lang.isEmpty()) {
            return null;
        }
        String country = parts.length > 1 ? safeTrim(parts[1]) : "";
        String variant = parts.length > 2 ? safeTrim(parts[2]) : "";
        return new String[] { lang, country, variant };
    }

    static ArrayList<String> getAvailableVoiceTags(List<String> requestedVoices) {
        Set<String> tags = new LinkedHashSet<>();

        // Always include the device's active locale first.
        String[] defaultTriplet = toIso3Triplet(Locale.getDefault());
        if (defaultTriplet != null) {
            tags.add(formatVoiceName(defaultTriplet[0], defaultTriplet[1], defaultTriplet[2]));
        }

        // Include any voices explicitly requested by CHECK_TTS_DATA callers.
        if (requestedVoices != null) {
            for (String requested : requestedVoices) {
                String clean = safeTrim(requested);
                if (!clean.isEmpty()) {
                    tags.add(clean);
                }
            }
        }

        // Include common built-in locales.
        for (Locale locale : DEFAULT_LOCALES) {
            String[] triplet = toIso3Triplet(locale);
            if (triplet != null) {
                tags.add(formatVoiceName(triplet[0], triplet[1], triplet[2]));
            }
        }

        if (tags.isEmpty()) {
            tags.add("eng-USA");
        }
        return new ArrayList<>(tags);
    }

    static List<Voice> getSupportedVoices() {
        Map<String, Voice> voicesByName = new LinkedHashMap<>();

        addVoiceForLocale(voicesByName, Locale.getDefault());
        for (Locale locale : DEFAULT_LOCALES) {
            addVoiceForLocale(voicesByName, locale);
        }

        return new ArrayList<>(voicesByName.values());
    }

    private static void addVoiceForLocale(Map<String, Voice> voicesByName, Locale locale) {
        String[] triplet = toIso3Triplet(locale);
        if (triplet == null) {
            return;
        }
        String name = formatVoiceName(triplet[0], triplet[1], triplet[2]);
        if (!voicesByName.containsKey(name)) {
            voicesByName.put(name, new Voice(
                    name,
                    locale,
                    Voice.QUALITY_NORMAL,
                    Voice.LATENCY_VERY_LOW,
                    false,
                    Collections.emptySet()
            ));
        }
    }

    static String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
