public class Cuadrilla {
    private final String codigo;
    private final double capacidadHoras;
    private final boolean certificadaRefrigeracion;
    private boolean jornadaAbierta;

    public Cuadrilla(String codigo, double capacidadHoras, boolean certificadaRefrigeracion) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new OperacionInvalidaException("El código de la cuadrilla no puede estar vacío.");
        }
        if (Double.isNaN(capacidadHoras) || Double.isInfinite(capacidadHoras) || capacidadHoras <= 0) {
            throw new OperacionInvalidaException("La capacidad de la cuadrilla debe ser mayor que cero.");
        }
        this.codigo = codigo.trim();
        this.capacidadHoras = capacidadHoras;
        this.certificadaRefrigeracion = certificadaRefrigeracion;
        this.jornadaAbierta = false;
    }

    public String getCodigo() { return codigo; }
    public double getCapacidadHoras() { return capacidadHoras; }
    public boolean isCertificadaRefrigeracion() { return certificadaRefrigeracion; }
    public boolean estaLibre() { return !jornadaAbierta; }

    //marca la cuadrilla como ocupada por una jornada sin cerrar
    public void ocupar() {
        if (jornadaAbierta) {
            throw new OperacionInvalidaException("La cuadrilla " + codigo + " ya tiene una jornada sin cerrar.");
        }
        jornadaAbierta = true;
    }

    public void liberar() {
        jornadaAbierta = false;
    }

    public String resumen() {
        return String.format("[%s] Capacidad: %s h | Certificada en refrigeración: %s | %s",
                codigo, Formato.horas(capacidadHoras), certificadaRefrigeracion ? "Sí" : "No",
                jornadaAbierta ? "Ocupada" : "Libre");
    }
}