package mx.edu.uth.dam1pr.dto;

import com.google.gson.annotations.SerializedName;

public class CoordenadaResponse {
    private boolean guardado;
    private String mensaje;

    @SerializedName("trayecto_id")
    private String trayectoId;

    public boolean isGuardado() {
        return guardado;
    }

    public void setGuardado(boolean guardado) {
        this.guardado = guardado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTrayectoId() {
        return trayectoId;
    }

    public void setTrayectoId(String trayectoId) {
        this.trayectoId = trayectoId;
    }
}
