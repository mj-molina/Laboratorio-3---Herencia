//información y comportamiento comunes de toda orden de servicio
//cada categoria decide su costo, tiempo, visitas y compatibilidad con la cuadrilla
public abstract class OrdenServicio {
    private static final double CARGO_VISITA_ADICIONAL = 40.0;

    private final String codigo;
    private final String cliente;
    private final String direccion;
    private final double horasEstimadas;
    private final Zona zona;
    private EstadoOrden estado;
    private int visitasRealizadas;

    protected OrdenServicio(String codigo, String cliente, String direccion,
                            double horasEstimadas, Zona zona) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new OperacionInvalidaException("El código de la orden no puede estar vacío.");
        }
        if (cliente == null || cliente.trim().isEmpty()) {
            throw new OperacionInvalidaException("El nombre del cliente no puede estar vacío.");
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new OperacionInvalidaException("La dirección no puede estar vacía.");
        }
        if (Double.isNaN(horasEstimadas) || Double.isInfinite(horasEstimadas) || horasEstimadas <= 0) {
            throw new OperacionInvalidaException("Las horas estimadas deben ser mayores que cero.");
        }
        if (zona == null) {
            throw new OperacionInvalidaException("La zona es obligatoria.");
        }
        this.codigo = codigo.trim();
        this.cliente = cliente.trim();
        this.direccion = direccion.trim();
        this.horasEstimadas = horasEstimadas;
        this.zona = zona;
        this.estado = EstadoOrden.PENDIENTE;
        this.visitasRealizadas = 0;
    }

    // ---------- Decisiones que dependen de la categoría ----------

    public abstract String getCategoria();

    public abstract double calcularCosto();

    public abstract int diasAtencion();

    public abstract int visitasMaximas();

    //por defecto cualquier cuadrilla sirve
    //las categorias con restricciones lo sobrescriben
    public void validarCompatibilidad(Cuadrilla cuadrilla) {
        // sin restricciones
    }

    //detalle propio de la categoria para mostrar en consultas
    protected String detalleEspecifico() {
        return "";
    }

    public double cargoVisitaAdicional() {
        return CARGO_VISITA_ADICIONAL;
    }

    // ---------- Transiciones de estado ----------

    //PENDIENTE -> ASIGNADA
    public void marcarAsignada() {
        exigirEstado(EstadoOrden.PENDIENTE, "asignar");
        estado = EstadoOrden.ASIGNADA;
    }

    //ASIGNADA -> PENDIENTE
    public void marcarPendiente() {
        exigirEstado(EstadoOrden.ASIGNADA, "retirar de la jornada");
        estado = EstadoOrden.PENDIENTE;
    }

    //ASIGNADA -> EN_ATENCION
    //cuenta la visita y devuelve el cargo adicional
    //0 en la primera visita, cargoVisitaAdicional() en las siguientes
    public double iniciarVisita() {
        exigirEstado(EstadoOrden.ASIGNADA, "iniciar la visita de");
        double cargo = visitasRealizadas > 0 ? cargoVisitaAdicional() : 0.0;
        visitasRealizadas++;
        estado = EstadoOrden.EN_ATENCION;
        return cargo;
    }

    //EN_ATENCION -> COMPLETADA, PENDIENTE (visita fallida) o CANCELADA (visitas agotadas)
    public void registrarResultado(ResultadoVisita resultado) {
        if (resultado == null) {
            throw new OperacionInvalidaException("El resultado de la visita es obligatorio.");
        }
        exigirEstado(EstadoOrden.EN_ATENCION, "registrar el resultado de");
        if (resultado == ResultadoVisita.COMPLETADA) {
            estado = EstadoOrden.COMPLETADA;
        } else if (visitasRealizadas >= visitasMaximas()) {
            estado = EstadoOrden.CANCELADA;
        } else {
            estado = EstadoOrden.PENDIENTE;
        }
    }

    private void exigirEstado(EstadoOrden requerido, String operacion) {
        if (estado != requerido) {
            throw new OperacionInvalidaException("No se puede " + operacion + " la orden " + codigo
                    + ": está en estado '" + estado + "' y se requiere '" + requerido + "'.");
        }
    }

    // ---------- Consultas ----------

    public String getCodigo() { return codigo; }
    public String getCliente() { return cliente; }
    public String getDireccion() { return direccion; }
    public double getHorasEstimadas() { return horasEstimadas; }
    public Zona getZona() { return zona; }
    public EstadoOrden getEstado() { return estado; }
    public int getVisitasRealizadas() { return visitasRealizadas; }

    public String resumen() {
        String detalle = detalleEspecifico();
        return String.format("[%s] %s | Cliente: %s | Dir: %s | Zona: %s | Horas est.: %s%s | Estado: %s | "
                        + "Visitas: %d/%d | Costo: %s | Atención: %d día(s) hábil(es)",
                codigo, getCategoria(), cliente, direccion, zona, Formato.horas(horasEstimadas),
                detalle.isEmpty() ? "" : " | " + detalle, estado, visitasRealizadas, visitasMaximas(),
                Formato.moneda(calcularCosto()), diasAtencion());
    }
}