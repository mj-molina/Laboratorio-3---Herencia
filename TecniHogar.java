import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

//fachada del sistema: 
//conserva ordenes, cuadrillas, jornadas e ingresos, y coordina las operaciones
public class TecniHogar {
    private final Map<String, OrdenServicio> ordenes = new LinkedHashMap<>();
    private final Map<String, Cuadrilla> cuadrillas = new LinkedHashMap<>();
    private final Map<String, Jornada> jornadas = new LinkedHashMap<>();
    private final RegistroIngresos ingresos = new RegistroIngresos();
    private int contadorJornadas = 0;

    private static String clave(String codigo) {
        return codigo == null ? "" : codigo.trim().toUpperCase();
    }

    // ---------- Ordenes ----------

    public void validarCodigoOrdenDisponible(String codigo) {
        if (clave(codigo).isEmpty()) {
            throw new OperacionInvalidaException("El código de la orden no puede estar vacío.");
        }
        if (ordenes.containsKey(clave(codigo))) {
            throw new OperacionInvalidaException("Ya existe una orden con el código " + codigo.trim() + ".");
        }
    }

    //registra la orden y cobra el monto completo
    public void registrarOrden(OrdenServicio orden) {
        guardarOrden(orden);
        ingresos.registrarCobroRegistro(orden.getCategoria(), orden.calcularCosto());
    }

    //carga de datos iniciales: 
    //registra la orden sin generar ingresos
    public void registrarOrdenInicial(OrdenServicio orden) {
        guardarOrden(orden);
    }

    private void guardarOrden(OrdenServicio orden) {
        if (orden == null) {
            throw new OperacionInvalidaException("La orden es obligatoria.");
        }
        validarCodigoOrdenDisponible(orden.getCodigo());
        ordenes.put(clave(orden.getCodigo()), orden);
    }

    public OrdenServicio buscarOrden(String codigo) {
        OrdenServicio orden = ordenes.get(clave(codigo));
        if (orden == null) {
            throw new OperacionInvalidaException("No existe una orden con el código '" + codigo + "'.");
        }
        return orden;
    }

    // ---------- Cuadrillas ----------

    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        if (cuadrilla == null) {
            throw new OperacionInvalidaException("La cuadrilla es obligatoria.");
        }
        if (cuadrillas.containsKey(clave(cuadrilla.getCodigo()))) {
            throw new OperacionInvalidaException("Ya existe una cuadrilla con el código " + cuadrilla.getCodigo() + ".");
        }
        cuadrillas.put(clave(cuadrilla.getCodigo()), cuadrilla);
    }

    public Cuadrilla buscarCuadrilla(String codigo) {
        Cuadrilla cuadrilla = cuadrillas.get(clave(codigo));
        if (cuadrilla == null) {
            throw new OperacionInvalidaException("No existe una cuadrilla con el código '" + codigo + "'.");
        }
        return cuadrilla;
    }

    // ---------- Jornadas ----------

    public Jornada crearJornada(String codigoCuadrilla) {
        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);
        String codigo = String.format("J-%03d", contadorJornadas + 1);
        Jornada jornada = new Jornada(codigo, cuadrilla);
        contadorJornadas++;
        jornadas.put(clave(codigo), jornada);
        return jornada;
    }

    public Jornada buscarJornada(String codigo) {
        Jornada jornada = jornadas.get(clave(codigo));
        if (jornada == null) {
            throw new OperacionInvalidaException("No existe una jornada con el código '" + codigo + "'.");
        }
        return jornada;
    }

    public void asignarOrden(String codigoJornada, String codigoOrden) {
        Jornada jornada = buscarJornada(codigoJornada);
        jornada.asignarOrden(buscarOrden(codigoOrden));
    }

    public void retirarOrden(String codigoJornada, String codigoOrden) {
        Jornada jornada = buscarJornada(codigoJornada);
        jornada.retirarOrden(buscarOrden(codigoOrden));
    }

    //devuelve el total de cargos por visitas adicionales generados al iniciar
    public double iniciarJornada(String codigoJornada) {
        return buscarJornada(codigoJornada).iniciar(ingresos);
    }

    public void registrarResultado(String codigoJornada, String codigoOrden, ResultadoVisita resultado) {
        Jornada jornada = buscarJornada(codigoJornada);
        jornada.registrarResultado(buscarOrden(codigoOrden), resultado);
    }

    // ---------- Consultas ----------

    public Collection<OrdenServicio> getOrdenes() {
        return Collections.unmodifiableCollection(ordenes.values());
    }

    public Collection<Cuadrilla> getCuadrillas() {
        return Collections.unmodifiableCollection(cuadrillas.values());
    }

    public Collection<Jornada> getJornadas() {
        return Collections.unmodifiableCollection(jornadas.values());
    }

    public RegistroIngresos getIngresos() {
        return ingresos;
    }
}