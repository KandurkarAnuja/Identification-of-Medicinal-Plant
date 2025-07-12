package com.example.demo_project;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Recommendation extends AppCompatActivity {

    EditText editTextText;
    Button button2;
    TextView textViewResult, textView6;
    Switch langSwitch;

    boolean isMarathi = false;

    Translator marathiToEnglishTranslator;
    Translator englishToMarathiTranslator;

    String lastEnglishResult = ""; // Store last English recommendation for toggling

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        editTextText = findViewById(R.id.editTextText);
        button2 = findViewById(R.id.button2);
        textViewResult = findViewById(R.id.textViewResult);
        textView6 = findViewById(R.id.textView6);
        langSwitch = findViewById(R.id.langSwitch);

        initTranslators();

        langSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isMarathi = isChecked;
            updateStaticTexts();

            // 🔄 Translate recommendation result based on toggle
            if (!lastEnglishResult.isEmpty()) {
                if (isMarathi) {
                    englishToMarathiTranslator.translate(lastEnglishResult)
                            .addOnSuccessListener(textViewResult::setText)
                            .addOnFailureListener(e -> textViewResult.setText("⚠️ Error: " + e.getMessage()));
                } else {
                    textViewResult.setText(lastEnglishResult);
                }
            }
        });

        button2.setOnClickListener(v -> {
            String userInput = editTextText.getText().toString().trim();

            if (userInput.isEmpty()) {
                textViewResult.setText(isMarathi ? "❗ कृपया आजाराचे नाव प्रविष्ट करा." : "❗ Please enter a disease.");
                return;
            }

            if (isMarathi) {
                // Translate Marathi input to English
                marathiToEnglishTranslator.translate(userInput)
                        .addOnSuccessListener(translatedInput -> recommendPlant(translatedInput, userInput))
                        .addOnFailureListener(e -> textViewResult.setText("⚠️ Translation error: " + e.getMessage()));
            } else {
                recommendPlant(userInput, userInput);
            }
        });
    }

    private void recommendPlant(String query, String originalQuery) {
        try {
            String jsonStr = loadJSONFromAsset(this, "recommendation_data.json");
            JSONArray jsonArray = new JSONArray(jsonStr);

            double bestScore = 0.0;
            JSONObject bestMatch = null;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject plant = jsonArray.getJSONObject(i);
                String disease = plant.getString("Disease Cured");
                String benefits = plant.getString("Medicinal Benefits");

                String content = disease + " " + benefits;
                double score = getSimilarityScore(query.toLowerCase(), content.toLowerCase());

                if (score > bestScore) {
                    bestScore = score;
                    bestMatch = plant;
                }
            }

            if (bestMatch != null) {
                lastEnglishResult = "🌿 Plant Name: " + bestMatch.getString("Plant Name") + "\n"
                        + "💊 Disease Cured: " + bestMatch.getString("Disease Cured") + "\n"
                        + "✨ Benefits: " + bestMatch.getString("Medicinal Benefits");

                if (isMarathi) {
                    englishToMarathiTranslator.translate(lastEnglishResult)
                            .addOnSuccessListener(textViewResult::setText)
                            .addOnFailureListener(e -> textViewResult.setText("⚠️ Error: " + e.getMessage()));
                } else {
                    textViewResult.setText(lastEnglishResult);
                }

            } else {
                textViewResult.setText(isMarathi ? "❌ जुळणारी वनस्पती सापडली नाही." : "❌ No matching plant found.");
                lastEnglishResult = "";
            }

        } catch (Exception e) {
            textViewResult.setText("⚠️ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private double getSimilarityScore(String query, String content) {
        String[] queryWords = query.split("\\s+");
        int matchCount = 0;
        for (String word : queryWords) {
            if (content.contains(word)) {
                matchCount++;
            }
        }
        return (double) matchCount / queryWords.length;
    }

    private String loadJSONFromAsset(Context context, String filename) {
        String json;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void initTranslators() {
        TranslatorOptions marathiToEnglishOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.MARATHI)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build();
        marathiToEnglishTranslator = Translation.getClient(marathiToEnglishOptions);

        TranslatorOptions englishToMarathiOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.MARATHI)
                .build();
        englishToMarathiTranslator = Translation.getClient(englishToMarathiOptions);

        DownloadConditions conditions = new DownloadConditions.Builder().build();

        marathiToEnglishTranslator.downloadModelIfNeeded(conditions);
        englishToMarathiTranslator.downloadModelIfNeeded(conditions);
    }

    private void updateStaticTexts() {
        if (isMarathi) {
            textView6.setText("❖ आजाराचे नाव प्रविष्ट करा:");
            button2.setText("शिफारस मिळवा");
            langSwitch.setText("इंग्रजीकडे स्विच करा");
        } else {
            textView6.setText("❖ Enter Disease Name:");
            button2.setText("Get Recommendation");
            langSwitch.setText("Switch to Marathi");
        }
    }
}
