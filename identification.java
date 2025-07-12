package com.example.demo_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demo_project.ml.Medicine;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

public class identification extends AppCompatActivity {

    ImageView camera;
    Button predict, select, more;
    TextView txt1, info;
    Switch langSwitch;
    Bitmap img;

    String nameEn = "", infoEn = "";
    Translator translator;
    boolean isMarathi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_identification);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        camera = findViewById(R.id.camera);
        select = findViewById(R.id.select);
        predict = findViewById(R.id.predict);
        txt1 = findViewById(R.id.txt1);
        info = findViewById(R.id.info);
        more = findViewById(R.id.more);
        langSwitch = findViewById(R.id.langSwitch);

        // Translator setup
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.MARATHI)
                .build();
        translator = Translation.getClient(options);

        translator.downloadModelIfNeeded()
                .addOnFailureListener(e -> {
                    txt1.setText("Translation model download failed");
                    info.setText("Check your internet connection");
                });

        select.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        predict.setOnClickListener(view -> {
            if (img == null) {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap scaledImg = Bitmap.createScaledBitmap(img, 256, 256, true);

            try {
                Medicine model = Medicine.newInstance(getApplicationContext());

                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
                TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                tensorImage.load(scaledImg);
                ByteBuffer byteBuffer = tensorImage.getBuffer();
                inputFeature0.loadBuffer(byteBuffer);

                Medicine.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                ArrayList<Float> flt = new ArrayList<>();
                for (float i : outputFeature0.getFloatArray()) {
                    flt.add(i);
                }
                int maxIndex = flt.indexOf(Collections.max(flt));

                String[] medicinal = {
                        "Alpinia Galanga (Rasna)", "Amaranthus Viridis (Arive-Dantu)", "Artocarpus Heterophyllus (Jackfruit)",
                        "Azadirachta Indica (Neem)", "Basella Alba (Basale)", "Brassica Juncea (Indian Mustard)",
                        "Carissa Carandas (Karanda)", "Citrus Limon (Lemon)", "Ficus Auriculata (Roxburgh fig)",
                        "Ficus Religiosa (Peepal Tree)", "Hibiscus Rosa-sinensis", "Jasminum (Jasmine)",
                        "Mangifera Indica (Mango)", "Mentha (Mint)", "Moringa Oleifera (Drumstick)",
                        "Muntingia Calabura (Jamaica Cherry)", "Murraya Koenigii (Curry)", "Nerium Oleander (Oleander)",
                        "Nyctanthes Arbor-tristis (Parijata)", "Ocimum Tenuiflorum (Tulsi)", "Piper Betle (Betel)",
                        "Plectranthus Amboinicus (Mexican Mint)", "Pongamia Pinnata (Indian Beech)", "Psidium Guajava (Guava)",
                        "Punica Granatum (Pomegranate)", "Santalum Album (Sandalwood)", "Syzygium Cumini (Jamun)",
                        "Syzygium Jambos (Rose Apple)", "Tabernaemontana Divaricata (Crape Jasmine)", "Trigonella Foenum-graecum (Fenugreek)"
                };

                String[] medicinal_info = {
                        "Alpinia galanga, commonly known as Rasna, is a medicinal herb belonging to the ginger family, used in traditional medicine for its anti-inflammatory and digestive properties.",
                        "Amaranthus viridis, also known as Arive-Dantu, is a nutritious leafy green vegetable rich in vitamins and minerals.",
                        "Artocarpus heterophyllus, commonly known as jackfruit, is a tropical fruit prized for its large size, sweet flavor, and versatility in dishes.",
                        "Neem, a versatile plant, exhibits potent antimicrobial properties, making it invaluable in traditional medicine.",
                        "Basella alba, commonly known as Basale, is a leafy green vegetable rich in nutrients such as vitamins A and C.",
                        "Indian Mustard is a medicinal plant known for its anti-inflammatory and antioxidant properties.",
                        "Carissa carandas is a medicinal plant rich in antioxidants, used for its anti-inflammatory and digestive benefits.",
                        "Citrus limon, or lemon, is rich in vitamin C and used in culinary and medicinal contexts.",
                        "Ficus auriculata is a medicinal plant with anti-inflammatory and antimicrobial properties.",
                        "Ficus religiosa (Peepal Tree) is used traditionally for respiratory, digestive, and skin conditions.",
                        "Hibiscus rosa-sinensis is used in traditional medicine for hair growth and managing high blood pressure.",
                        "Jasminum is used in aromatherapy for its calming, anti-inflammatory, and aphrodisiac effects.",
                        "Mangifera indica (mango) is used in herbal medicine for its antioxidant and digestive benefits.",
                        "Mentha (mint) is used for soothing digestion and respiratory health.",
                        "Moringa oleifera is a nutritious plant that supports immunity and blood sugar regulation.",
                        "Muntingia calabura is rich in antioxidants and helps in digestion and wellness.",
                        "Murraya koenigii (Curry Leaf) is used in Ayurveda for digestive and hair/skin benefits.",
                        "Nerium oleander is traditionally used for heart conditions but is toxic and requires caution.",
                        "Nyctanthes arbor-tristis (Parijata) is used for arthritis, fever, and respiratory ailments.",
                        "Ocimum tenuiflorum (Tulsi) helps manage stress and boost immunity.",
                        "Piper betle (Betel Leaf) has antioxidant, antimicrobial, and digestive properties.",
                        "Plectranthus amboinicus (Mexican Mint) is used for respiratory and digestive relief.",
                        "Pongamia pinnata (Indian Beech) is used for skin disorders and wound healing.",
                        "Psidium guajava (Guava) helps in stress management and digestion.",
                        "Punica granatum (Pomegranate) promotes heart health and reduces inflammation.",
                        "Santalum album (Sandalwood) aids in mental clarity and skin treatment.",
                        "Syzygium cumini (Jamun) aids in diabetes management and overall health.",
                        "Syzygium jambos (Rose Apple) helps with digestion and heart health.",
                        "Tabernaemontana divaricata (Crape Jasmine) is used for pain and skin issues.",
                        "Trigonella foenum-graecum (Fenugreek) is known for anti-inflammatory and lactation benefits."

                };

                nameEn = medicinal[maxIndex];
                infoEn = medicinal_info[maxIndex];

                if (isMarathi) {
                    translateToMarathi();
                } else {
                    txt1.setText(nameEn);
                    info.setText(infoEn);
                }

                model.close();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Model error", Toast.LENGTH_SHORT).show();
            }
        });

        langSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isMarathi = isChecked;
            if (isMarathi && !nameEn.isEmpty()) {
                translateToMarathi();
            } else {
                txt1.setText(nameEn);
                info.setText(infoEn);
            }
            updateButtonLanguage(isMarathi);
        });

        more.setOnClickListener(view -> {
            Intent intent = new Intent(identification.this, Benefits.class);
            startActivity(intent);
        });
    }

    void translateToMarathi() {
        translator.translate(nameEn)
                .addOnSuccessListener(txt1::setText)
                .addOnFailureListener(e -> txt1.setText("भाषांतर अयशस्वी"));

        translator.translate(infoEn)
                .addOnSuccessListener(info::setText)
                .addOnFailureListener(e -> info.setText("भाषांतर अयशस्वी"));
    }

    void updateButtonLanguage(boolean marathi) {
        if (marathi) {
            select.setText("प्रतिमा निवडा");
            predict.setText("ओळखा");
            more.setText("अधिक माहिती");
            langSwitch.setText("मराठी");
        } else {
            select.setText("Select Image");
            predict.setText("Predict");
            more.setText("More Info");
            langSwitch.setText("English");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null) {
            Uri uri = data.getData();
            try {
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                camera.setImageBitmap(img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
