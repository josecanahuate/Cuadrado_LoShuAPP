package com.loshu.cuadrado_loshu;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import com.google.android.gms.ads.AdRequest;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ViewFlipper;

import java.util.Random;
import android.os.Handler;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.Random;
public class MainActivity extends AppCompatActivity {
    private Button generateNumbersButton;
    CheckBox checkBox;
    EditText ent_s1, ent_s2, ent_s3;
    private ViewFlipper viewFlipper;
    private AdView mAdView;
    private com.google.android.gms.ads.AdRequest adRequest;
    private InterstitialAd mInterstitialAd;
    BottomNavigationView bottomNavigationView;
    private int clickCounter = 0; // Contador de clics
    private int[] cmagico = {R.id.textview_00, R.id.textview_01, R.id.textview_02, R.id.textview_03,
            R.id.textview_10, R.id.textview_11, R.id.textview_12, R.id.textview_13,
            R.id.textview_20, R.id.textview_21, R.id.textview_22, R.id.textview_23,
            R.id.textview_30, R.id.textview_31, R.id.textview_32, R.id.textview_33};
    private Handler handler = new Handler();
    //ID ANUNCIO INTERSTICIAL
    private static final String AD_UNIT_ID = "ca-app-pub-4434685305331116/9010953416";
    private boolean isGeneratingNumbers = false;
    private boolean stopGeneration = false;
    private int generationCount = 0;

    private boolean isAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "ad_clicked");
        mFirebaseAnalytics.logEvent("ad_clicked", bundle);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setupLayoutResource();
        // Inicializar adRequest
        adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();

        checkBox = findViewById(R.id.checkBox);
        ent_s1 = findViewById(R.id.ent_s1);
        ent_s2 = findViewById(R.id.ent_s2);
        ent_s3 = findViewById(R.id.ent_s3);

        // Deshabilitar los campos de texto por defecto
        ent_s1.setEnabled(false);
        ent_s2.setEnabled(false);
        ent_s3.setEnabled(false);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ent_s1.setEnabled(isChecked);
                ent_s2.setEnabled(isChecked);
                ent_s3.setEnabled(isChecked);

                // Si deseas también limpiar los campos cuando el CheckBox se desmarca
                if (!isChecked) {
                    ent_s1.setText("");
                    ent_s2.setText("");
                    ent_s3.setText("");
                }
            }
        });

        viewFlipper = findViewById(R.id.viewFlipper);
        generateNumbersButton = findViewById(R.id.button_generate_numbers);

        generateNumbersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener los valores ingresados en los EditText
                String valorS1 = ent_s1.getText().toString().trim();
                String valorS2 = ent_s2.getText().toString().trim();
                String valorS3 = ent_s3.getText().toString().trim();

                // Validar si los campos están vacíos
                if (checkBox.isChecked() && (valorS1.isEmpty() || valorS2.isEmpty() || valorS3.isEmpty())) {
                    // Mostrar un error dialog indicando que los campos son requeridos
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("Por favor, complete todos los campos.");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } else {
                    if (!isGeneratingNumbers) {
                        isGeneratingNumbers = true;
                        startNumberGeneration();

                        // Generar número aleatorio entre 2 y 4
                        int randomNum = new Random().nextInt(4) + 3;

                        clickCounter++;
                        if (clickCounter >= randomNum) {
                            if (mInterstitialAd != null) {
                                mInterstitialAd.show(MainActivity.this);
                                mInterstitialAd = null;
                                InterstitialAd.load(getApplicationContext(), AD_UNIT_ID, adRequest, new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                        super.onAdLoaded(interstitialAd);
                                        mInterstitialAd = interstitialAd;
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        super.onAdFailedToLoad(loadAdError);
                                        mInterstitialAd = null;
                                    }
                                });
                                clickCounter = 0;
                            }
                        }

                    }
                }
            }
        });

    // Dentro del método onCreate
        checkBox = findViewById(R.id.checkBox);
        ent_s1 = findViewById(R.id.ent_s1);
        ent_s2 = findViewById(R.id.ent_s2);
        ent_s3 = findViewById(R.id.ent_s3);

        // Deshabilitar los campos de texto por defecto
        ent_s1.setEnabled(false);
        ent_s2.setEnabled(false);
        ent_s3.setEnabled(false);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ent_s1.setEnabled(isChecked);
                ent_s2.setEnabled(isChecked);
                ent_s3.setEnabled(isChecked);

                // Si deseas también limpiar los campos cuando el CheckBox se desmarca
                if (!isChecked) {
                    ent_s1.setText("");
                    ent_s2.setText("");
                    ent_s3.setText("");
                }
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });
        //carga Banner
        mAdView = findViewById(R.id.adView);
        com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                FirebaseAnalytics.getInstance(MainActivity.this).logEvent("ad_clicked", null);
            }

            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                FirebaseAnalytics.getInstance(MainActivity.this).logEvent("ad_load_failed", null);
            }

            @Override
            public void onAdImpression() {
                FirebaseAnalytics.getInstance(MainActivity.this).logEvent("ad_impression", null);
            }

            @Override
            public void onAdLoaded() {
                FirebaseAnalytics.getInstance(MainActivity.this).logEvent("ad_loaded", null);
            }

            @Override
            public void onAdOpened() {
                FirebaseAnalytics.getInstance(MainActivity.this).logEvent("ad_opened", null);
            }

        });

        // Cargar el anuncio intersticial
        InterstitialAd.load(this,AD_UNIT_ID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // El anuncio se cargó correctamente
                        mInterstitialAd = interstitialAd;
                        Log.d("MainActivity", "Anuncio intersticial cargado");
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Error al cargar el anuncio
                        //Log.d("MainActivity", loadAdError.toString());
                        Log.d("MainActivity", "Error al cargar anuncio intersticial");
                        mInterstitialAd = null;
                    }
                });
    }

    // Método para mostrar el anuncio intersticial
    public void showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    FirebaseAnalytics.getInstance(MainActivity.this).logEvent("ad_clicked", null);
                    Log.d(TAG, "El anuncio se clickeo.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Se llama cuando se cierra el anuncio
                    Log.d("MainActivity", "El anuncio se cerró.");
                    mInterstitialAd = null;
                    // Recargamos el anuncio para que esté listo para el siguiente botón
                    loadInterstitialAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Se llama si hay un error al mostrar el anuncio
                    Log.d("MainActivity", "No se pudo mostrar el anuncio.");
                    mInterstitialAd = null;
                }

                @Override
                public void onAdImpression() {
                    FirebaseAnalytics.getInstance(MainActivity.this).logEvent("ad_impression", null);
                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Se llama cuando el anuncio se muestra correctamente
                    Log.d("MainActivity", "El anuncio se mostró.");
                }
            });
            mInterstitialAd.show(this);
        } else {
            Log.d("MainActivity", "El anuncio no está listo todavía.");
        }
    }

    // Método para cargar un nuevo anuncio intersticial
    private void loadInterstitialAd() {
        InterstitialAd.load(this, AD_UNIT_ID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // El anuncio se cargó correctamente
                        mInterstitialAd = interstitialAd;
                        Log.d("MainActivity", "Anuncio intersticial cargado");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Error al cargar el anuncio
                        Log.d("MainActivity", "Error al cargar anuncio intersticial");
                        mInterstitialAd = null;
                    }
                });
    }

    private void startNumberGeneration() {
        if (isAnimating) {
            return; // Evitar que se inicie una nueva generación mientras se está animando
        }

        isAnimating = true; // Comenzar la animación
        generationCount = 0; // Reiniciar el contador de generación
        stopGeneration = false; // Reiniciar la bandera de detener generación

        // Reiniciar el texto de los TickerView
        for (int i = 0; i < cmagico.length; i++) {
            TickerView tickerView = findViewById(cmagico[i]);
            tickerView.setCharacterLists(TickerUtils.provideNumberList());
            tickerView.setText("0");
        }

        // Mostrar la animación
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.flip_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.flip_out));
        viewFlipper.showNext();

        // Iniciar el proceso de generación
        handler.postDelayed(numberGenerationRunnable, 1000);
    }
    private void setupLayoutResource() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float density = displayMetrics.densityDpi;
        int layoutResourceId = R.layout.activity_main; // Default layout resource
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        if (density <= DisplayMetrics.DENSITY_LOW) {
            layoutResourceId = R.layout.activity_main;

        } else if (density <= DisplayMetrics.DENSITY_MEDIUM) {
            layoutResourceId = R.layout.activity_main;

        } else if (density <= DisplayMetrics.DENSITY_HIGH) {
            layoutResourceId = R.layout.layout_480x800;

        } else if (density <= DisplayMetrics.DENSITY_XHIGH) {
            layoutResourceId = R.layout.layout_720x1280;

        } else if (width == 1080 && height <= 1920) {
            layoutResourceId = R.layout.layout_1080x1920;

        } else if (width == 1080 && height <= 2400) {
            // 1080x2400 --> RESOLUCION ORIGINAL DEL A52
            layoutResourceId = R.layout.layout_1080x2400;

        } else if (width == 1440 && height <= 2560) {
            // 1440x2560
            layoutResourceId = R.layout.layout_1440x2560;

        } else if (density <= DisplayMetrics.DENSITY_XXXHIGH) {
            // 1440x2960 --> RESOLUCION ORIGINAL DEL S8, S9, EDGE
            layoutResourceId = R.layout.layout_1440x2960;

        } else {
                layoutResourceId = R.layout.activity_main;
            }

        setContentView(layoutResourceId);
    }

    private Runnable numberGenerationRunnable = new Runnable() {
        @Override
        public void run() {
            if (stopGeneration) {
                handler.removeCallbacks(this);
                isAnimating = false;
                isGeneratingNumbers = false; // Permitir generar nuevamente
                return;
            }

            int[] results = new int[16];
            Random random = new Random();

            for (int i = 0; i < 16; i++) {
                results[i] = random.nextInt(10);
                TickerView tickerView = findViewById(cmagico[i]);
                tickerView.setText(String.valueOf(results[i]));
            }

            generationCount++;
            if (generationCount >= 10) {
                stopGeneration = true;
                handler.postDelayed(this, 1000);
            } else {
                handler.postDelayed(this, 100);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGeneration = true;
        handler.removeCallbacks(numberGenerationRunnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.comofunciona) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.comofunciona); // Título del AlertDialog
            builder.setMessage(R.string.metodo); // Mensaje del AlertDialog
            //builder.setMessage("En el método Lo Shu, juegas con grupos de cuatro números en líneas diagonales, horizontales y verticales. También puedes sumar estos números para obtener resultados de uno o dos dígitos. Puedes Personalizar tu propio método Lo Shu insertando tu fecha de nacimiento.");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();  // Cerrar el AlertDialog
                }
            });
            builder.show();
            return true;

        } else if  (itemId == R.id.mas) {
            // Abrir la URL correspondiente a "Mas apps"
            openURL("https://play.google.com/store/apps/developer?id=Tristar+Dev&hl=es&gl=US");
            return true;

        } else if (itemId == R.id.calificanos) {
            // Abrir la URL correspondiente a "Calificanos"
            openURL("https://play.google.com/store/apps/details?id=com.loshu.cuadrado_loshu");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openURL(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}


