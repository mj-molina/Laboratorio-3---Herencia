public enum Zona {
    METROPOLITANA("Metropolitana", 100.0),
    PERIFERICA("Periférica", 175.0);

    private final String etiqueta;
    private final double tarifaBase;

    Zona(String etiqueta, double tarifaBase) {
        this.etiqueta = etiqueta;
        this.tarifaBase = tarifaBase;
    }

    //tarifa base compartida por instalaciones y mantenimientos
    public double getTarifaBase() {
        return tarifaBase;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}