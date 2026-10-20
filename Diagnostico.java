public class Diagnostico extends OrdenServicio {
    private static final double HORAS_MAXIMAS = 1.5;
    private static final double COSTO_METROPOLITANA = 150.0;
    private static final double COSTO_PERIFERICA = 225.0;

    public Diagnostico(String codigo, String cliente, String direccion,
                       double horasEstimadas, Zona zona) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (horasEstimadas > HORAS_MAXIMAS) {
            throw new OperacionInvalidaException("Un diagnóstico no puede superar " + HORAS_MAXIMAS + " horas.");
        }
    }

    @Override
    public String getCategoria() {
        return "Diagnóstico";
    }

    @Override
    public double calcularCosto() {
        return getZona() == Zona.METROPOLITANA ? COSTO_METROPOLITANA : COSTO_PERIFERICA;
    }

    @Override
    public int diasAtencion() {
        return getZona() == Zona.METROPOLITANA ? 1 : 2;
    }

    @Override
    public int visitasMaximas() {
        return 3;
    }
}