public class InstalacionElectricaCertificada extends Instalacion {
    private static final double COSTO_CERTIFICADO = 120.0;
    private static final double PORCENTAJE_GARANTIA = 0.05;
    private static final int DIAS_ADICIONALES = 2;

    private final double valorMateriales;

    public InstalacionElectricaCertificada(String codigo, String cliente, String direccion,
                                           double horasEstimadas, Zona zona, int puntos,
                                           double valorMateriales) {
        super(codigo, cliente, direccion, horasEstimadas, zona, puntos);
        if (Double.isNaN(valorMateriales) || Double.isInfinite(valorMateriales) || valorMateriales <= 0) {
            throw new OperacionInvalidaException("El valor declarado de los materiales debe ser mayor que cero.");
        }
        this.valorMateriales = valorMateriales;
    }

    public double getValorMateriales() {
        return valorMateriales;
    }

    public double garantia() {
        return valorMateriales * PORCENTAJE_GARANTIA;
    }

    @Override
    public String getCategoria() {
        return "Instalación eléctrica certificada";
    }

    @Override
    public double calcularCosto() {
        return super.calcularCosto() + COSTO_CERTIFICADO + garantia();
    }

    @Override
    public int diasAtencion() {
        return super.diasAtencion() + DIAS_ADICIONALES;
    }

    @Override
    public int visitasMaximas() {
        return 2;
    }

    @Override
    protected String detalleEspecifico() {
        return super.detalleEspecifico() + ", Materiales: " + Formato.moneda(valorMateriales);
    }
}