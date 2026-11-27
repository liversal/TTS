package com.liversal.silenttts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

public final class CheckTtsDataActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent result = new Intent();
        result.putExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES, new String[0]);
        result.putExtra(TextToSpeech.Engine.EXTRA_UNAVAILABLE_VOICES, new String[0]);
        setResult(TextToSpeech.Engine.CHECK_VOICE_DATA_PASS, result);
        finish();
    }
}
