package com.liversal.silenttts;

import android.media.AudioFormat;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.speech.tts.Voice;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A deliberately silent Android TTS engine.
 *
 * It accepts utterances and completes them without ever submitting PCM data.
 * It does not change system volume or affect audio from other applications.
 */
public final class SilentTtsService extends TextToSpeechService {
    private static final int SAMPLE_RATE_HZ = 16000;

    private volatile String[] currentLanguage = TtsLocales.getDefaultLanguageTriplet();

    @Override
    protected int onIsLanguageAvailable(String language, String country, String variant) {
        if (language == null || language.trim().isEmpty()) {
            return TextToSpeech.LANG_NOT_SUPPORTED;
        }
        if (variant != null && !variant.trim().isEmpty()) {
            return TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE;
        }
        if (country != null && !country.trim().isEmpty()) {
            return TextToSpeech.LANG_COUNTRY_AVAILABLE;
        }
        return TextToSpeech.LANG_AVAILABLE;
    }

    @Override
    protected int onLoadLanguage(String language, String country, String variant) {
        int status = onIsLanguageAvailable(language, country, variant);
        if (status >= TextToSpeech.LANG_AVAILABLE) {
            currentLanguage = new String[] {
                    TtsLocales.safeTrim(language),
                    TtsLocales.safeTrim(country),
                    TtsLocales.safeTrim(variant)
            };
        }
        return status;
    }

    @Override
    protected String[] onGetLanguage() {
        String[] lang = currentLanguage;
        return new String[] { lang[0], lang[1], lang[2] };
    }

    @Override
    protected Set<String> onGetFeaturesForLanguage(String lang, String country, String variant) {
        return Collections.emptySet();
    }

    @Override
    public List<Voice> onGetVoices() {
        return TtsLocales.getSupportedVoices();
    }

    @Override
    public String onGetDefaultVoiceNameFor(String lang, String country, String variant) {
        return TtsLocales.formatVoiceName(lang, country, variant);
    }

    @Override
    public int onIsValidVoiceName(String voiceName) {
        return TtsLocales.parseVoiceName(voiceName) != null
                ? TextToSpeech.SUCCESS
                : TextToSpeech.ERROR;
    }

    @Override
    public int onLoadVoice(String voiceName) {
        String[] parsed = TtsLocales.parseVoiceName(voiceName);
        if (parsed == null) {
            return TextToSpeech.ERROR;
        }
        currentLanguage = parsed;
        return TextToSpeech.SUCCESS;
    }

    @Override
    protected void onStop() {
        // There is no audio stream to interrupt.
    }

    @Override
    protected void onSynthesizeText(SynthesisRequest request, SynthesisCallback callback) {
        if (callback == null) {
            return;
        }

        if (request != null) {
            String reqLang = TtsLocales.safeTrim(request.getLanguage());
            if (!reqLang.isEmpty()) {
                currentLanguage = new String[] {
                        reqLang,
                        TtsLocales.safeTrim(request.getCountry()),
                        TtsLocales.safeTrim(request.getVariant())
                };
            }
        }

        // Start a valid PCM stream, but intentionally provide zero audio frames.
        // This lets the Android TTS framework receive a normal completion event
        // without opening audio playback or ducking other apps.
        int result = callback.start(
                SAMPLE_RATE_HZ,
                AudioFormat.ENCODING_PCM_16BIT,
                1
        );

        if (result == TextToSpeech.SUCCESS) {
            callback.done();
        } else if (result == TextToSpeech.STOPPED) {
            // Request was flushed/interrupted (common with TalkBack QUEUE_FLUSH).
            // Do not report an error so TalkBack does not mark the engine as failing.
            return;
        } else {
            callback.error();
        }
    }
}
