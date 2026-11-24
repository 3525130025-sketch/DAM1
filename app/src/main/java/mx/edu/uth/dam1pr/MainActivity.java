package mx.edu.uth.dam1pr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jspecify.annotations.NonNull;

import mx.edu.uth.dam1pr.dto.AccesoResponse;
import mx.edu.uth.dam1pr.ws.WebApiAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Callback<AccesoResponse> {

    private EditText userNameVar;
    private EditText userPassVar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userNameVar = findViewById(R.id.userName);
        userPassVar = findViewById(R.id.userPassword);
    }

    public void iniciarSesion(View v) {
        String usuario = userNameVar.getText().toString().trim();
        String password = userPassVar.getText().toString().trim();

        Call<AccesoResponse> call = WebApiAdapter.getApiService().validarAcceso(usuario, password);
        call.enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<AccesoResponse> call, @NonNull Response<AccesoResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            AccesoResponse dto = response.body();
            if (dto.isAcceso()) {
                String usuario = userNameVar.getText().toString().trim();
                Intent i = new Intent(this, MenuIndex.class);
                i.putExtra("usuario", usuario);
                startActivity(i);
            } else {
                Toast.makeText(this, "Error de credenciales", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error de credenciales", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(@NonNull Call<AccesoResponse> call, @NonNull Throwable t) {
        Toast.makeText(this, "Error de datos: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        System.out.println("Error datos: " + t.getMessage());
    }
}
