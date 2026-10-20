public class Instalacion extends OrdenServicio {
    private static final double HORAS_POR_PUNTO = 0.75;
    private static final double TARIFA_POR_HORA = 90.0;

    private final int puntos;

    public Instalacion(String codigo, String cliente, String direccion,
                       double horasEstimadas, Zona zona, int puntos) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (puntos <= 0) {
            throw new OperacionInvalidaException("La cantidad de puntos debe ser un entero positivo.");
        }
        this.puntos = puntos;
    }

    public int getPuntos() {
        return puntos;
    }

    public double horasPorPuntos() {
        return puntos * HORAS_POR_PUNTO;
    }

    public double horasFacturables() {
        return Math.max(getHorasEstimadas(), horasPorPuntos());
    }

    @Override
    public String getCategoria() {
        return "Instalación";
    }

    @Override
    public double calcularCosto() {
        return getZona().getTarifaBase() + TARIFA_POR_HORA * Math.ceil(horasFacturables());
    }

    @Override
    public int diasAtencion() {
        return getZona() == Zona.METROPOLITANA ? 3 : 5;
    }

    @Override
    public int visitasMaximas() {
        return 3;
    }

    @Override
    protected String detalleEspecifico() {
        return "Puntos: " + puntos;
    }
}