package mx.edu.uth.dam1pr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jspecify.annotations.NonNull;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import mx.edu.uth.dam1pr.dto.Coordenada;
import mx.edu.uth.dam1pr.dto.DetalleTrayectoResponse;
import mx.edu.uth.dam1pr.dto.EliminarTrayectoResponse;
import mx.edu.uth.dam1pr.ws.WebApiAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapaTrayecto extends AppCompatActivity {

    private MapView mapa;
    private TextView tvResumen;
    private String usuario;
    private String trayecto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().setOsmdroidBasePath(getCacheDir());

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapa_trayecto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getStringExtra("usuario");
        if (usuario == null) {
            usuario = "Invitado";
        }

        trayecto = getIntent().getStringExtra("trayecto");
        if (trayecto == null) {
            trayecto = "prueba";
        }

        TextView tvTitulo = findViewById(R.id.tvTitulo);
        tvTitulo.setText(trayecto);
        tvResumen = findViewById(R.id.tvResumen);

        mapa = findViewById(R.id.mapa);
        mapa.setTileSource(TileSourceFactory.MAPNIK);
        mapa.setMultiTouchControls(true);
        mapa.getController().setZoom(15.0);

        cargarCoordenadas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapa != null) {
            mapa.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapa != null) {
            mapa.onPause();
        }
    }

    private void cargarCoordenadas() {
        WebApiAdapter.getApiService().ConsultarDetalleTrayectos(usuario, trayecto).enqueue(new Callback<DetalleTrayectoResponse>() {
            @Override
            public void onResponse(@NonNull Call<DetalleTrayectoResponse> call, @NonNull Response<DetalleTrayectoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isEncontrado()) {
                    DetalleTrayectoResponse body = response.body();
                    dibujarTrayecto(body.getDetalleTrayectos());
                } else {
                    tvResumen.setText("Este trayecto no está disponible");
                }
            }

            @Override
            public void onFailure(@NonNull Call<DetalleTrayectoResponse> call, @NonNull Throwable t) {
                tvResumen.setText("Error: " + t.getMessage());
            }
        });
    }

    private void dibujarTrayecto(List<Coordenada> coord) {
        if (coord == null || coord.isEmpty()) {
            tvResumen.setText("Sin coordenadas registradas");
            return;
        }

        List<GeoPoint> puntos = new ArrayList<>();
        for (Coordenada c : coord) {
            puntos.add(new GeoPoint(c.getLatitud(), c.getLongitud()));
        }

        // Limpiar overlays anteriores si los hubiere
        mapa.getOverlays().clear();

        // Dibujar la línea del trayecto
        Polyline linea = new Polyline();
        linea.setPoints(puntos);
        linea.getOutlinePaint().setColor(Color.parseColor("#1565c0"));
        linea.getOutlinePaint().setStrokeWidth(10f);
        mapa.getOverlays().add(linea);

        // Marcador en la primera coordenada recibida (Inicio)
        GeoPoint puntoInicial = puntos.get(0);
        Marker marcadorInicio = new Marker(mapa);
        marcadorInicio.setPosition(puntoInicial);
        marcadorInicio.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marcadorInicio.setTitle("Inicio");
        marcadorInicio.setSnippet("Primera coordenada del trayecto");
        mapa.getOverlays().add(marcadorInicio);

        // Marcador en la última coordenada recibida (Fin)
        if (puntos.size() > 1) {
            GeoPoint puntoFinal = puntos.get(puntos.size() - 1);
            Marker marcadorFin = new Marker(mapa);
            marcadorFin.setPosition(puntoFinal);
            marcadorFin.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marcadorFin.setTitle("Fin");
            marcadorFin.setSnippet("Última coordenada del trayecto");
            mapa.getOverlays().add(marcadorFin);
        }

        // Centrar la cámara del mapa en la primera coordenada recibida
        mapa.getController().setZoom(16.0);
        mapa.getController().setCenter(puntoInicial);

        // Redibujar el mapa
        mapa.invalidate();

        tvResumen.setText("Trayecto cargado (" + puntos.size() + " puntos)");
    }

    public void EliminarTrayecto(View v){
        WebApiAdapter.getApiService().EliminarTrayecto(usuario, trayecto).enqueue(new Callback<EliminarTrayectoResponse>() {
            @Override
            public void onResponse(@NonNull Call<EliminarTrayectoResponse> call, @NonNull Response<EliminarTrayectoResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isEncontrado()) {
                        EliminarTrayectoResponse body = response.body();
                        Toast.makeText(MapaTrayecto.this,    "Trayecto eliminado correctamente", Toast.LENGTH_SHORT).show();

                        Intent nuevaActividad = new Intent(MapaTrayecto.this, ListaTrayectos.class);
                        nuevaActividad.putExtra("usuario", usuario);
                        startActivity(nuevaActividad);
                    } else {
                        Toast.makeText(MapaTrayecto.this, "Error al eliminar el trayecto", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<EliminarTrayectoResponse> call, @NonNull Throwable t) {
                    Toast.makeText(MapaTrayecto.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }


}
