public class MantenimientoAire extends OrdenServicio {
    private static final int BTU_MINIMO = 9000;
    private static final int BTU_MAXIMO = 60000;
    private static final double TARIFA_POR_HORA = 110.0;
    private static final double CARGO_GAS_REFRIGERANTE = 80.0;

    private final int capacidadBtu;

    public MantenimientoAire(String codigo, String cliente, String direccion,
                             double horasEstimadas, Zona zona, int capacidadBtu) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (zona != Zona.METROPOLITANA) {
            throw new OperacionInvalidaException(
                    "El mantenimiento de aire acondicionado solo se acepta en la zona metropolitana.");
        }
        if (capacidadBtu < BTU_MINIMO || capacidadBtu > BTU_MAXIMO) {
            throw new OperacionInvalidaException("La capacidad debe estar entre " + BTU_MINIMO
                    + " y " + BTU_MAXIMO + " BTU.");
        }
        this.capacidadBtu = capacidadBtu;
    }

    public int getCapacidadBtu() {
        return capacidadBtu;
    }

    @Override
    public String getCategoria() {
        return "Mantenimiento de aire acondicionado";
    }

    @Override
    public double calcularCosto() {
        return getZona().getTarifaBase() + TARIFA_POR_HORA * getHorasEstimadas() + CARGO_GAS_REFRIGERANTE;
    }

    @Override
    public int diasAtencion() {
        return 2;
    }

    @Override
    public int visitasMaximas() {
        return 1;
    }

    @Override
    public void validarCompatibilidad(Cuadrilla cuadrilla) {
        if (!cuadrilla.isCertificadaRefrigeracion()) {
            throw new OperacionInvalidaException("La orden " + getCodigo()
                    + " requiere una cuadrilla certificada en refrigeración; la cuadrilla "
                    + cuadrilla.getCodigo() + " no lo está.");
        }
    }

    @Override
    protected String detalleEspecifico() {
        return "Capacidad: " + capacidadBtu + " BTU";
    }
}