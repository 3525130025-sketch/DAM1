package mx.edu.uth.dam1pr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import mx.edu.uth.dam1pr.adapters.TrayectoAdapter;
import mx.edu.uth.dam1pr.dto.Trayecto;
import mx.edu.uth.dam1pr.dto.TrayectoResponse;
import mx.edu.uth.dam1pr.ws.WebApiAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaTrayectos extends AppCompatActivity {

    private RecyclerView rvTrayecto;
    private ProgressBar progress;
    private TextView tvMensaje;
    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_trayectos);

        rvTrayecto = findViewById(R.id.rvTrayectos);
        progress = findViewById(R.id.progress);
        tvMensaje = findViewById(R.id.tvMensaje);

        rvTrayecto.setLayoutManager(new LinearLayoutManager(this));
        rvTrayecto.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        usuario = getIntent().getStringExtra("usuario");
        if (usuario == null) {
            usuario = "Invitado";
        }

        cargarTrayecto();
    }

    private void cargarTrayecto() {
        WebApiAdapter.getApiService()
                .obtenerTrayectos(usuario)
                .enqueue(new Callback<TrayectoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TrayectoResponse> call,
                                           @NonNull Response<TrayectoResponse> response) {
                        progress.setVisibility(View.GONE);
                        TrayectoResponse body = response.body();

                        if (response.isSuccessful() && body != null && body.isEncontrado() && body.getTrayectos() != null && !body.getTrayectos().isEmpty()) {
                            rvTrayecto.setAdapter(new TrayectoAdapter(body.getTrayectos(),
                                    trayecto -> abrirTrayecto(trayecto)));
                        } else {
                            String msg = (body != null && body.getMensaje() != null) ? body.getMensaje() : "Aún no hay trayectos registrados para " + usuario;
                            mostrarMensaje(msg);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TrayectoResponse> call, @NonNull Throwable t) {
                        progress.setVisibility(View.GONE);
                        mostrarMensaje("Sin conexión con el servidor: " + t.getMessage());
                    }
                });
    }

    private void mostrarMensaje(String texto) {
        tvMensaje.setText(texto);
        tvMensaje.setVisibility(View.VISIBLE);
    }

    private void abrirTrayecto(Trayecto trayecto) {
        Toast.makeText(this, "Cargando mapa para: " + trayecto.getNombre(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ListaTrayectos.this, MapaTrayecto.class);
        intent.putExtra("usuario", usuario);
        intent.putExtra("trayecto", trayecto.getNombre());
        startActivity(intent);
    }
}
