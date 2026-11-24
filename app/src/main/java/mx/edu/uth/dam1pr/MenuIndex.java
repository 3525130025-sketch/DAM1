package mx.edu.uth.dam1pr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuIndex extends AppCompatActivity {
    public ListView listaDeTrayectos;
    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_index);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getStringExtra("usuario");
        if (usuario == null) {
            usuario = "Invitado";
        }

        listaDeTrayectos = findViewById(R.id.ListaMenu);
        String[] elementos = new String[]{"Capturar Trayectos", "Lista de Trayectos"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, elementos);
        listaDeTrayectos.setAdapter(adapter);

        listaDeTrayectos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int i, long l) {
                Intent nuevaActividad = null;

                switch (i) {
                    case 0:
                        nuevaActividad = new Intent(MenuIndex.this, TrayectoActivity.class);
                        nuevaActividad.putExtra("usuario", usuario);
                        break;
                    case 1:
                        nuevaActividad = new Intent(MenuIndex.this, ListaTrayectos.class);
                        nuevaActividad.putExtra("usuario", usuario);
                        break;
                }
                if (nuevaActividad != null) {
                    startActivity(nuevaActividad);
                }
            }
        });
    }
}
