import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

//genera el texto de los reportes de gerencia
//no modifica ningun dato
public class Reportes {
    private final TecniHogar sistema;

    public Reportes(TecniHogar sistema) {
        this.sistema = sistema;
    }

    public String ordenesPorCategoriaYEstado() {
        Map<String, Map<EstadoOrden, Integer>> tabla = new LinkedHashMap<>();
        for (OrdenServicio orden : sistema.getOrdenes()) {
            tabla.computeIfAbsent(orden.getCategoria(), k -> new EnumMap<>(EstadoOrden.class))
                 .merge(orden.getEstado(), 1, Integer::sum);
        }
        StringBuilder sb = new StringBuilder("=== Órdenes por categoría y estado ===\n");
        if (tabla.isEmpty()) {
            return sb.append("No hay órdenes registradas.").toString();
        }
        for (Map.Entry<String, Map<EstadoOrden, Integer>> fila : tabla.entrySet()) {
            int total = fila.getValue().values().stream().mapToInt(Integer::intValue).sum();
            sb.append(fila.getKey()).append(" (total ").append(total).append(")\n");
            for (EstadoOrden estado : EstadoOrden.values()) {
                sb.append(String.format("    %-12s %d%n", estado + ":", fila.getValue().getOrDefault(estado, 0)));
            }
        }
        return sb.toString();
    }

    public String ingresos() {
        RegistroIngresos ingresos = sistema.getIngresos();
        Set<String> categorias = new LinkedHashSet<>();
        for (OrdenServicio orden : sistema.getOrdenes()) {
            categorias.add(orden.getCategoria());
        }
        StringBuilder sb = new StringBuilder("=== Ingresos ===\n");
        sb.append("Por registro de órdenes:       ").append(Formato.moneda(ingresos.getIngresoPorRegistro())).append("\n");
        sb.append("Por visitas adicionales:       ").append(Formato.moneda(ingresos.getIngresoPorVisitasAdicionales())).append("\n");
        sb.append("TOTAL:                         ").append(Formato.moneda(ingresos.getIngresoTotal())).append("\n");
        sb.append("Por categoría:\n");
        for (String categoria : categorias) {
            sb.append(String.format("    %-38s %s%n", categoria, Formato.moneda(ingresos.getIngresoCategoria(categoria))));
        }
        return sb.toString();
    }

    public String jornadasAbiertas() {
        StringBuilder sb = new StringBuilder("=== Jornadas no cerradas ===\n");
        boolean hayAbiertas = false;
        for (Jornada jornada : sistema.getJornadas()) {
            if (jornada.getEstado() != EstadoJornada.CERRADA) {
                sb.append(jornada.resumen()).append("\n");
                hayAbiertas = true;
            }
        }
        if (!hayAbiertas) {
            sb.append("No hay jornadas abiertas.\n");
        }
        return sb.toString();
    }
}