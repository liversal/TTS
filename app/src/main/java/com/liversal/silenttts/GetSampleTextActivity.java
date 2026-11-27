package com.liversal.silenttts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

public final class GetSampleTextActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent result = new Intent();
        result.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.sample_text));
        setResult(TextToSpeech.LANG_AVAILABLE, result);
        finish();
    }
}
