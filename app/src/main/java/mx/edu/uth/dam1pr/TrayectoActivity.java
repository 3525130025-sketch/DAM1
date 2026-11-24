package mx.edu.uth.dam1pr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import org.jspecify.annotations.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.Locale;

import mx.edu.uth.dam1pr.dto.CoordenadaResponse;
import mx.edu.uth.dam1pr.ws.WebApiAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrayectoActivity extends AppCompatActivity {

    private static final long INTERVALO_MS = 10000;
    private EditText etTrayecto;
    private Button btnCaptura;
    private TextView tvEstado;
    private TextView tvUltimacoodenada;
    private FusedLocationProviderClient clienteUbicacion;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean capturado = false;
    private int puntosEnviados = 0;
    private String usuario;
    private String trayecto;

    private final ActivityResultLauncher<String[]> permisosLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            resultado -> {
                Boolean finaccess = resultado.get(Manifest.permission.ACCESS_FINE_LOCATION);
                if (Boolean.TRUE.equals(finaccess)) {
                    iniciarCaptura();
                } else {
                    Toast.makeText(this, "Se necesitan permisos de ubicación", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trayecto);

        etTrayecto = findViewById(R.id.etTrayecto);
        btnCaptura = findViewById(R.id.button);
        tvEstado = findViewById(R.id.textView4);
        tvUltimacoodenada = findViewById(R.id.textView5);

        usuario = getIntent().getStringExtra("usuario");
        if (usuario == null) {
            usuario = "Invitado";
        }

        clienteUbicacion = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVALO_MS)
                .setMinUpdateIntervalMillis(INTERVALO_MS / 2)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location ubicacion = locationResult.getLastLocation();
                if (ubicacion != null) {
                    enviarCoodenadas(ubicacion.getLatitude(), ubicacion.getLongitude());
                }
            }
        };

        btnCaptura.setOnClickListener(v -> {
            if (capturado) {
                detenerCaptura();
            } else {
                ValidarYComanezar();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (capturado) {
                    Toast.makeText(TrayectoActivity.this, "Detén la captura antes de salir", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void ValidarYComanezar() {
        trayecto = etTrayecto.getText().toString().trim();
        if (trayecto.isEmpty()) {
            etTrayecto.setError("Escribe el nombre del trayecto");
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            iniciarCaptura();
        } else {
            permisosLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void iniciarCaptura() {
        clienteUbicacion.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        capturado = true;
        puntosEnviados = 0;
        etTrayecto.setEnabled(false);
        btnCaptura.setText("Detener captura");
        tvEstado.setText("Capturando... esperando la primera coordenada");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void detenerCaptura() {
        clienteUbicacion.removeLocationUpdates(locationCallback);

        capturado = false;
        etTrayecto.setEnabled(true);
        btnCaptura.setText("Iniciar captura");
        tvEstado.setText("Captura detenida. Puntos enviados:" + puntosEnviados);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void enviarCoodenadas(double latitud, double longitud) {
        tvUltimacoodenada.setText(String.format(Locale.US, "Latitud: %.4f, Longitud: %.4f", latitud, longitud));
        WebApiAdapter.getApiService().guardarCoordenada(usuario, trayecto, String.valueOf(latitud), String.valueOf(longitud)).enqueue(new Callback<CoordenadaResponse>() {
            @Override
            public void onResponse(@NonNull Call<CoordenadaResponse> call, @NonNull Response<CoordenadaResponse> response) {
                CoordenadaResponse body = response.body();
                if (response.isSuccessful() && body != null && body.isGuardado()) {
                    puntosEnviados++;
                    tvEstado.setText("Capturando... Puntos enviados: " + puntosEnviados);
                } else {
                    tvEstado.setText("El servidor respondió con un error (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CoordenadaResponse> call, @NonNull Throwable t) {
                tvEstado.setText("Sin conexión, se reintentará con la siguiente coordenada");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (capturado) {
            clienteUbicacion.removeLocationUpdates(locationCallback);
        }
    }
}
