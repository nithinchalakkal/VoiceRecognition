package com.example.voicerecognition;

import java.util.ArrayList;
import java.util.Locale;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class VoiceRecognitionActivity extends Activity implements RecognitionListener {

	private TextView returnedText, textViewhead;
	private ToggleButton toggleButton;
	private static ProgressBar progressBar;
	private static SpeechRecognizer Recog_speech = null;
	private static Intent intent;
	private String LOG_TAG = "VoiceRecognitionActivity";

	static TextToSpeech t1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		returnedText = (TextView) findViewById(R.id.textView1);
		textViewhead = (TextView) findViewById(R.id.textViewhead);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);

		t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if (status != TextToSpeech.ERROR) {
					t1.setLanguage(Locale.UK);
					t1.speak(textViewhead.getText().toString().trim(), TextToSpeech.QUEUE_FLUSH, null);

					final Handler h = new Handler();
					h.postDelayed(new Runnable() {

						@Override
						public void run() {

							progressBar.setVisibility(View.VISIBLE);
							progressBar.setIndeterminate(true);
							Recog_speech.startListening(intent);

						}
					}, 5000);

				}
			}
		});

		progressBar.setVisibility(View.INVISIBLE);

		Recog_speech = SpeechRecognizer.createSpeechRecognizer(this);
		Recog_speech.setRecognitionListener(this);

		intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {

					progressBar.setVisibility(View.VISIBLE);
					progressBar.setIndeterminate(true);
					Recog_speech.startListening(intent);
				} else {
					progressBar.setIndeterminate(false);
					progressBar.setVisibility(View.INVISIBLE);
					Recog_speech.stopListening();
				}
			}
		});

	}

	@Override
	protected void onPause() {
		super.onPause();

		if (t1 != null) {
			t1.stop();
			t1.shutdown();
		}

		if (Recog_speech != null) {
			Recog_speech.destroy();
			Log.i(LOG_TAG, "destroy");
		}
		super.onPause();
	}

	@Override
	public void onBeginningOfSpeech() {
		Log.i(LOG_TAG, "onBeginningOfSpeech");
		progressBar.setIndeterminate(false);
		progressBar.setMax(10);
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		Log.i(LOG_TAG, "onBufferReceived: " + buffer);
	}

	@Override
	public void onEndOfSpeech() {
		Log.i(LOG_TAG, "onEndOfSpeech");
		progressBar.setIndeterminate(true);
		toggleButton.setChecked(false);

		progressBar.setIndeterminate(false);
		progressBar.setVisibility(View.INVISIBLE);
		Recog_speech.stopListening();

	}

	@Override
	public void onError(int errorCode) {
		String errorMessage = getErrorText(errorCode);
		Log.d(LOG_TAG, "FAILED " + errorMessage);
		returnedText.setText(errorMessage);
		toggleButton.setChecked(false);
	}

	@Override
	public void onEvent(int arg0, Bundle arg1) {
		Log.i(LOG_TAG, "onEvent");
	}

	@Override
	public void onPartialResults(Bundle arg0) {
		Log.i(LOG_TAG, "onPartialResults");
	}

	@Override
	public void onReadyForSpeech(Bundle arg0) {
		Log.i(LOG_TAG, "onReadyForSpeech");
	}

	@Override
	public void onResults(Bundle results) {
		Log.i(LOG_TAG, "onResults");
		ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		String text = "";
		for (String result : matches)
			text += result + "\n";

		returnedText.setText(text);
		
		if((returnedText.getText().toString() .equalsIgnoreCase("good")) && (returnedText.getText().toString() .equalsIgnoreCase("Average")) && (returnedText.getText().toString() .equalsIgnoreCase("poor"))){
			
			
		}
		else{
			
		
			progressBar.setVisibility(View.INVISIBLE);
			t1.speak("Sorry , You have said" + text + " , Which is not in the option list , please answer again", TextToSpeech.QUEUE_FLUSH, null);

			
			final Handler h = new Handler();
			h.postDelayed(new Runnable() {

				@Override
				public void run() {

					progressBar.setVisibility(View.VISIBLE);
					progressBar.setIndeterminate(true);
					Recog_speech.startListening(intent);

				}
			}, 8000);
			
			
		}
		
		
	}

	@Override
	public void onRmsChanged(float rmsdB) {
		Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
		progressBar.setProgress((int) rmsdB);
	}

	public static String getErrorText(int errorCode) {
		String message;
		switch (errorCode) {
		case SpeechRecognizer.ERROR_AUDIO:
			message = "Audio recording error";
			break;
		case SpeechRecognizer.ERROR_CLIENT:
			message = "Client side error";
			break;
		case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
			message = "Insufficient permissions";
			break;
		case SpeechRecognizer.ERROR_NETWORK:
			message = "Network error";
			break;
		case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
			message = "Network timeout";
			break;
		case SpeechRecognizer.ERROR_NO_MATCH:
			message = "No match";
			break;
		case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
			message = "RecognitionService busy";
			break;
		case SpeechRecognizer.ERROR_SERVER:
			message = "error from server";
			break;
		case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
			message = "No speech input";
			break;
		default:
			message = "Didn't understand, please try again.";
			break;
		}
		progressBar.setVisibility(View.INVISIBLE);
		t1.speak("Sorry " + message + "please answer again", TextToSpeech.QUEUE_FLUSH, null);

		
		final Handler h = new Handler();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {

				progressBar.setVisibility(View.VISIBLE);
				progressBar.setIndeterminate(true);
				Recog_speech.startListening(intent);

			}
		}, 5000);
		
		
		
		
		return message;
	}

}