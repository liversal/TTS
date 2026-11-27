package com.liversal.silenttts;

import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.media.AudioFormat;

/**
 * A deliberately silent Android TTS engine.
 *
 * It accepts utterances and completes them without ever submitting PCM data.
 * It does not change system volume or affect audio from other applications.
 */
public final class SilentTtsService extends TextToSpeechService {
    private static final int SAMPLE_RATE_HZ = 16000;

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
        return onIsLanguageAvailable(language, country, variant);
    }

    @Override
    protected String[] onGetLanguage() {
        return new String[] { "eng", "USA", "" };
    }

    @Override
    protected void onStop() {
        // There is no audio stream to interrupt.
    }

    @Override
    protected void onSynthesizeText(SynthesisRequest request, SynthesisCallback callback) {
        // Start a valid PCM stream, but intentionally provide zero audio frames.
        // This lets the Android TTS framework receive a normal completion event.
        int result = callback.start(
                SAMPLE_RATE_HZ,
                AudioFormat.ENCODING_PCM_16BIT,
                1
        );

        if (result == TextToSpeech.SUCCESS) {
            callback.done();
        } else {
            callback.error();
        }
    }
}
