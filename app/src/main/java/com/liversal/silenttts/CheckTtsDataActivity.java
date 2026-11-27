package com.liversal.silenttts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import java.util.ArrayList;

public final class CheckTtsDataActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent incoming = getIntent();
        String action = incoming != null ? incoming.getAction() : null;

        if (TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA.equals(action)) {
            Intent installed = new Intent(TextToSpeech.Engine.ACTION_TTS_DATA_INSTALLED);
            installed.putExtra(
                    TextToSpeech.Engine.EXTRA_TTS_DATA_INSTALLED,
                    TextToSpeech.Engine.CHECK_VOICE_DATA_PASS
            );
            sendBroadcast(installed);
            setResult(RESULT_OK);
            finish();
            return;
        }

        ArrayList<String> requestedVoices = incoming != null
                ? incoming.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_CHECK_VOICE_DATA_FOR)
                : null;
        ArrayList<String> availableVoices = TtsLocales.getAvailableVoiceTags(requestedVoices);
        ArrayList<String> unavailableVoices = new ArrayList<>();

        Intent result = new Intent();
        result.putStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES, availableVoices);
        result.putStringArrayListExtra(TextToSpeech.Engine.EXTRA_UNAVAILABLE_VOICES, unavailableVoices);
        setResult(TextToSpeech.Engine.CHECK_VOICE_DATA_PASS, result);
        finish();
    }
}
