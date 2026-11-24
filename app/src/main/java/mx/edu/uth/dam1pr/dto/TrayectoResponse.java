package mx.edu.uth.dam1pr.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrayectoResponse {

    private boolean encontrado;
    private String mensaje;
    private int total;

    @SerializedName("trayectos")
    private List<Trayecto> trayectos;

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Trayecto> getTrayectos() {
        return trayectos;
    }

    public void setTrayectos(List<Trayecto> trayectos) {
        this.trayectos = trayectos;
    }
}
