import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Jornada {
    private static final double TOLERANCIA = 1e-9;

    private final String codigo;
    private final Cuadrilla cuadrilla;
    private final List<OrdenServicio> ordenes = new ArrayList<>();
    private final Set<OrdenServicio> conResultado = new HashSet<>();
    private EstadoJornada estado;

    //al crearse ocupa la cuadrilla
    //si ya esta ocupada, lanza excepcion sin crear nada
    public Jornada(String codigo, Cuadrilla cuadrilla) {
        if (cuadrilla == null) {
            throw new OperacionInvalidaException("La jornada requiere una cuadrilla.");
        }
        cuadrilla.ocupar();
        this.codigo = codigo;
        this.cuadrilla = cuadrilla;
        this.estado = EstadoJornada.EN_PREPARACION;
    }

    //valida las tres condiciones de asignacion
    //la compatibilidad la decide la propia orden
    public void asignarOrden(OrdenServicio orden) {
        exigirEstado(EstadoJornada.EN_PREPARACION, "asignar órdenes a");
        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new OperacionInvalidaException("La orden " + orden.getCodigo() + " no se puede asignar: está en estado '"
                    + orden.getEstado() + "' (debe estar pendiente y no asignada a otra jornada).");
        }
        orden.validarCompatibilidad(cuadrilla);
        if (horasAsignadas() + orden.getHorasEstimadas() > cuadrilla.getCapacidadHoras() + TOLERANCIA) {
            throw new OperacionInvalidaException(String.format(
                    "La orden %s (%s h) excede la capacidad disponible de la cuadrilla %s (%s h).",
                    orden.getCodigo(), Formato.horas(orden.getHorasEstimadas()), cuadrilla.getCodigo(),
                    Formato.horas(capacidadDisponible())));
        }
        orden.marcarAsignada();
        ordenes.add(orden);
    }

    public void retirarOrden(OrdenServicio orden) {
        exigirEstado(EstadoJornada.EN_PREPARACION, "retirar órdenes de");
        if (!ordenes.contains(orden)) {
            throw new OperacionInvalidaException("La orden " + orden.getCodigo()
                    + " no está asignada a la jornada " + codigo + ".");
        }
        orden.marcarPendiente();
        ordenes.remove(orden);
    }

    //EN_PREPARACION -> EN_CURSO
    //registra el cargo de cada visita adicional
    //devuelve el total de cargos adicionales generados
    public double iniciar(RegistroIngresos ingresos) {
        exigirEstado(EstadoJornada.EN_PREPARACION, "iniciar");
        if (ordenes.isEmpty()) {
            throw new OperacionInvalidaException("No se puede iniciar una jornada vacía.");
        }
        estado = EstadoJornada.EN_CURSO;
        double totalCargos = 0.0;
        for (OrdenServicio orden : ordenes) {
            double cargo = orden.iniciarVisita();
            if (cargo > 0) {
                ingresos.registrarCargoVisitaAdicional(orden.getCategoria(), cargo);
                totalCargos += cargo;
            }
        }
        return totalCargos;
    }

    //registra el resultado de una orden
    //cierra la jornada y libera la cuadrilla al terminar todas
    public void registrarResultado(OrdenServicio orden, ResultadoVisita resultado) {
        exigirEstado(EstadoJornada.EN_CURSO, "registrar resultados en");
        if (!ordenes.contains(orden)) {
            throw new OperacionInvalidaException("La orden " + orden.getCodigo()
                    + " no pertenece a la jornada " + codigo + ".");
        }
        if (conResultado.contains(orden)) {
            throw new OperacionInvalidaException("La orden " + orden.getCodigo()
                    + " ya tiene resultado registrado en la jornada " + codigo + ".");
        }
        orden.registrarResultado(resultado);
        conResultado.add(orden);
        if (conResultado.size() == ordenes.size()) {
            estado = EstadoJornada.CERRADA;
            cuadrilla.liberar();
        }
    }

    public double horasAsignadas() {
        double total = 0.0;
        for (OrdenServicio orden : ordenes) {
            total += orden.getHorasEstimadas();
        }
        return total;
    }

    public double capacidadDisponible() {
        return cuadrilla.getCapacidadHoras() - horasAsignadas();
    }

    public List<OrdenServicio> getOrdenes() {
        return Collections.unmodifiableList(ordenes);
    }

    public List<OrdenServicio> ordenesSinResultado() {
        List<OrdenServicio> pendientes = new ArrayList<>();
        for (OrdenServicio orden : ordenes) {
            if (!conResultado.contains(orden)) {
                pendientes.add(orden);
            }
        }
        return pendientes;
    }

    public String getCodigo() { return codigo; }
    public Cuadrilla getCuadrilla() { return cuadrilla; }
    public EstadoJornada getEstado() { return estado; }

    private void exigirEstado(EstadoJornada requerido, String operacion) {
        if (estado != requerido) {
            throw new OperacionInvalidaException("No se puede " + operacion + " la jornada " + codigo
                    + ": está en estado '" + estado + "' y se requiere '" + requerido + "'.");
        }
    }

    public String resumen() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s] Cuadrilla: %s | Estado: %s | Horas asignadas: %s | Capacidad: %s | Disponible: %s",
                codigo, cuadrilla.getCodigo(), estado, Formato.horas(horasAsignadas()),
                Formato.horas(cuadrilla.getCapacidadHoras()), Formato.horas(capacidadDisponible())));
        for (OrdenServicio orden : ordenes) {
            sb.append("\n      - ").append(orden.getCodigo()).append(" (").append(orden.getCategoria())
              .append(", ").append(Formato.horas(orden.getHorasEstimadas())).append(" h, ")
              .append(orden.getEstado()).append(")");
        }
        return sb.toString();
    }
}