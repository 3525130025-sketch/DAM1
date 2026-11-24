package mx.edu.uth.dam1pr.ws;

import mx.edu.uth.dam1pr.dto.AccesoResponse;
import mx.edu.uth.dam1pr.dto.CoordenadaResponse;
import mx.edu.uth.dam1pr.dto.DetalleTrayectoResponse;
import mx.edu.uth.dam1pr.dto.EliminarTrayectoResponse;
import mx.edu.uth.dam1pr.dto.TrayectoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WebApiService {
    @GET("login.php")
    Call<AccesoResponse> validarAcceso(
        @Query("user") String user,
        @Query("pass") String pass
    );

    @GET("guardar_coordenadas.php")
    Call<CoordenadaResponse> guardarCoordenada(
            @Query("usuario") String usuario,
            @Query("trayecto") String trayecto,
            @Query("latitud") String latitud,
            @Query("longitud") String longitud
    );

    @GET("listar_trayectos.php")
    Call<TrayectoResponse> obtenerTrayectos(
            @Query("usuario") String user
   );

    @GET("obtener_coordenadas.php")
    Call<DetalleTrayectoResponse> ConsultarDetalleTrayectos(
            @Query("usuario") String user,
            @Query("trayecto") String trayecto
    );
    @GET("eliminarTrayecto.php")
    Call<EliminarTrayectoResponse> EliminarTrayecto(
            @Query("usuario") String user,
            @Query("trayecto") String trayecto
    );

}

