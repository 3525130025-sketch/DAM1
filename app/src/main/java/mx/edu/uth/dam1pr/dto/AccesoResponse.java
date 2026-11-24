package mx.edu.uth.dam1pr.dto;

public class AccesoResponse {
    private boolean acceso;
    private String mensaje;

    public AccesoResponse() {

    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    public boolean isAcceso() {
        return acceso;
    }

    public void setAcceso(boolean acceso) {
        this.acceso = acceso;
    }
}
