package com.example.demo_project;



import android.content.Context;
import java.util.*;

public class TfidfVectorizer {
    private List<String> vocabulary;
    private Map<String, Integer> vocabIndex;
    private List<String> plantLabels;

    public TfidfVectorizer(Context context) {
        // Example vocabulary (MUST match Python training)
        vocabulary = Arrays.asList("Joint Pain", "Anemia", "Respiratory disorders", "Malnutrition", "Skin infections", "Stress", "Anemia", "Skin disorders", "Sore throat", "Liver disorders", "Diarrhea", "Oral infections", "Arthritis", "Inflammation", "Skin disorders, Burns", "Inflammation", "Nausea", "High blood pressure, Cholesterol", "Stress", "Headaches", "Dental issues", "High cholesterol", "Digestive issues", "Bloating", "Hair loss", "Respiratory disorders", "Memory loss", "Anemia, Digestive issues", "Bone health issues", "Diabetes", "Anemia, Digestive issues", "Skin infections", "Cough","cold");
                ;
        vocabIndex = new HashMap<>();
        for (int i = 0; i < vocabulary.size(); i++) {
            vocabIndex.put(vocabulary.get(i), i);
        }

        // Example label list (MUST match label encoder used in training)
        plantLabels = Arrays.asList("Indian Mustard", "Karanda", "Lemon Mint", "Drumstick (Moringa)", "Oleander", "Jasmine", "Basale (Malabar Spinach)", "Sandalwood", "Jamun (Indian Blackberry)", "Rose Apple", "Guava", "Betel", "Parjatka (Night-Flowering Jasmine)", "Fenugreek", "Aloe Vera", "Turmeric", "Ginger", "Garlic", "Lavender", "Peppermint", "Neem", "Coriander", "Chamomile", "Fennel Seeds", "Rosemary", "Basil", "Sage", "Mango", "Arive Dantu (Amaranth)", "Roxburgh Fig", "Jackfruit", "Mexican Mint", "Tulsi"
        );
    }

    public float[] transform(String text) {
        float[] vector = new float[vocabulary.size()];
        String[] tokens = text.toLowerCase().split("\\s+");
        for (String token : tokens) {
            if (vocabIndex.containsKey(token)) {
                vector[vocabIndex.get(token)] += 1.0f;
            }
        }
        return vector;
    }

    public String getPlantLabel(int index) {
        if (index >= 0 && index < plantLabels.size()) {
            return plantLabels.get(index);
        }
        return "Unknown Plant";
    }
}
