package mx.edu.uth.dam1pr.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetalleTrayectoResponse {
    private boolean encontrado;
    private String mensaje;
    @SerializedName("coordenadas")
    private List<Coordenada> detalleTrayectos;

    public boolean isEncontrado() {
        return encontrado;
    }

    public void setEncontrado(boolean encontrado) {
        this.encontrado = encontrado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<Coordenada> getDetalleTrayectos() {
        return detalleTrayectos;
    }

    public void setDetalleTrayectos(List<Coordenada> detalleTrayectos) {
        this.detalleTrayectos = detalleTrayectos;
    }
}
