import java.util.List;
import java.util.NoSuchElementException;

//interfaz de consola: 
//solo lee datos, invoca al sistema y muestra resultados
public class Menu {
    private final TecniHogar sistema;
    private final Consola consola;
    private final Reportes reportes;

    public Menu(TecniHogar sistema, Consola consola) {
        this.sistema = sistema;
        this.consola = consola;
        this.reportes = new Reportes(sistema);
    }

    public void ejecutar() {
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            try {
                salir = procesar(consola.leerOpcion("Seleccione una opción: "));
            } catch (OperacionInvalidaException e) {
                System.out.println("[RECHAZADO] " + e.getMessage() + " No se modificó el estado.");
            } catch (NoSuchElementException e) {
                System.out.println("\nSe cerró la entrada de datos.");
                salir = true;
            }
        }
        System.out.println("Hasta luego.");
    }

    private void mostrarMenu() {
        System.out.println("\n===== TECNIHOGAR =====");
        System.out.println(" 1. Cotizar y registrar orden");
        System.out.println(" 2. Registrar cuadrilla");
        System.out.println(" 3. Crear jornada");
        System.out.println(" 4. Asignar orden a jornada");
        System.out.println(" 5. Retirar orden de jornada");
        System.out.println(" 6. Iniciar jornada");
        System.out.println(" 7. Registrar resultados de visitas");
        System.out.println(" 8. Consultar órdenes");
        System.out.println(" 9. Consultar cuadrillas");
        System.out.println("10. Consultar jornadas");
        System.out.println("11. Reporte: órdenes por categoría y estado");
        System.out.println("12. Reporte: ingresos");
        System.out.println("13. Reporte: jornadas no cerradas");
        System.out.println(" 0. Salir");
    }

    private boolean procesar(int opcion) {
        switch (opcion) {
            case 1: cotizarYRegistrar(); break;
            case 2: registrarCuadrilla(); break;
            case 3: crearJornada(); break;
            case 4: asignarOrden(); break;
            case 5: retirarOrden(); break;
            case 6: iniciarJornada(); break;
            case 7: registrarResultados(); break;
            case 8: consultarOrdenes(); break;
            case 9: consultarCuadrillas(); break;
            case 10: consultarJornadas(); break;
            case 11: System.out.println(reportes.ordenesPorCategoriaYEstado()); break;
            case 12: System.out.println(reportes.ingresos()); break;
            case 13: System.out.println(reportes.jornadasAbiertas()); break;
            case 0: return true;
            default: System.out.println("Opción inválida. Elija un número del menú.");
        }
        return false;
    }

    // ---------- Ordenes ----------

    private void cotizarYRegistrar() {
        System.out.println("Categorías: 1. Diagnóstico  2. Instalación  3. Instalación eléctrica certificada  "
                + "4. Mantenimiento de aire acondicionado");
        int categoria = consola.leerEntero("Categoría: ");
        if (categoria < 1 || categoria > 4) {
            throw new OperacionInvalidaException("La categoría " + categoria + " no existe.");
        }
        String codigo = consola.leerTexto("Código de la orden: ");
        sistema.validarCodigoOrdenDisponible(codigo);
        String cliente = consola.leerTexto("Nombre del cliente: ");
        String direccion = consola.leerTexto("Dirección: ");
        Zona zona = consola.leerZona();
        double horas = consola.leerDecimal("Horas estimadas: ");

        OrdenServicio orden = construirOrden(categoria, codigo, cliente, direccion, horas, zona);

        System.out.println("\n--- COTIZACIÓN ---");
        System.out.println("Categoría: " + orden.getCategoria());
        System.out.println("Costo: " + Formato.moneda(orden.calcularCosto()));
        System.out.println("Tiempo estimado de atención: " + orden.diasAtencion() + " día(s) hábil(es)");
        System.out.println("Visitas permitidas: " + orden.visitasMaximas());

        if (consola.confirmar("¿El cliente acepta la cotización?")) {
            sistema.registrarOrden(orden);
            System.out.println("Orden registrada y pendiente. Monto cobrado: " + Formato.moneda(orden.calcularCosto()));
        } else {
            System.out.println("Cotización descartada. No se registró nada ni se cobró.");
        }
    }

    private OrdenServicio construirOrden(int categoria, String codigo, String cliente, String direccion,
                                         double horas, Zona zona) {
        switch (categoria) {
            case 1:
                return new Diagnostico(codigo, cliente, direccion, horas, zona);
            case 2:
                return new Instalacion(codigo, cliente, direccion, horas, zona,
                        consola.leerEntero("Cantidad de puntos a instalar: "));
            case 3:
                int puntos = consola.leerEntero("Cantidad de puntos a instalar: ");
                double materiales = consola.leerDecimal("Valor declarado de los materiales (Q): ");
                return new InstalacionElectricaCertificada(codigo, cliente, direccion, horas, zona, puntos, materiales);
            default:
                return new MantenimientoAire(codigo, cliente, direccion, horas, zona,
                        consola.leerEntero("Capacidad del equipo (BTU): "));
        }
    }

    private void consultarOrdenes() {
        String codigo = consola.leerTexto("Código de la orden (Enter para ver todas): ");
        if (codigo.isEmpty()) {
            if (sistema.getOrdenes().isEmpty()) {
                System.out.println("No hay órdenes registradas.");
            }
            for (OrdenServicio orden : sistema.getOrdenes()) {
                System.out.println(orden.resumen());
            }
        } else {
            System.out.println(sistema.buscarOrden(codigo).resumen());
        }
    }

    // ---------- Cuadrillas ----------

    private void registrarCuadrilla() {
        String codigo = consola.leerTexto("Código de la cuadrilla: ");
        double capacidad = consola.leerDecimal("Capacidad máxima de horas por jornada: ");
        boolean certificada = consola.confirmar("¿Está certificada en refrigeración?");
        sistema.registrarCuadrilla(new Cuadrilla(codigo, capacidad, certificada));
        System.out.println("Cuadrilla registrada.");
    }

    private void consultarCuadrillas() {
        if (sistema.getCuadrillas().isEmpty()) {
            System.out.println("No hay cuadrillas registradas.");
        }
        for (Cuadrilla cuadrilla : sistema.getCuadrillas()) {
            System.out.println(cuadrilla.resumen());
        }
    }

    // ---------- Jornadas ----------

    private void crearJornada() {
        consultarCuadrillas();
        String cuadrilla = consola.leerTexto("Código de la cuadrilla: ");
        Jornada jornada = sistema.crearJornada(cuadrilla);
        System.out.println("Jornada " + jornada.getCodigo() + " creada para la cuadrilla "
                + jornada.getCuadrilla().getCodigo() + " (en preparación).");
    }

    private void asignarOrden() {
        System.out.println("Órdenes pendientes:");
        boolean hay = false;
        for (OrdenServicio orden : sistema.getOrdenes()) {
            if (orden.getEstado() == EstadoOrden.PENDIENTE) {
                System.out.println("  " + orden.getCodigo() + " - " + orden.getCategoria() + " ("
                        + Formato.horas(orden.getHorasEstimadas()) + " h)");
                hay = true;
            }
        }
        if (!hay) {
            System.out.println("  (ninguna)");
        }
        String jornada = consola.leerTexto("Código de la jornada: ");
        String orden = consola.leerTexto("Código de la orden: ");
        sistema.asignarOrden(jornada, orden);
        Jornada j = sistema.buscarJornada(jornada);
        System.out.println("Orden asignada. Horas asignadas: " + Formato.horas(j.horasAsignadas())
                + " | Capacidad disponible: " + Formato.horas(j.capacidadDisponible()));
    }

    private void retirarOrden() {
        String jornada = consola.leerTexto("Código de la jornada: ");
        System.out.println(sistema.buscarJornada(jornada).resumen());
        String orden = consola.leerTexto("Código de la orden a retirar: ");
        sistema.retirarOrden(jornada, orden);
        System.out.println("Orden retirada; vuelve a estar pendiente.");
    }

    private void iniciarJornada() {
        String jornada = consola.leerTexto("Código de la jornada: ");
        double cargos = sistema.iniciarJornada(jornada);
        System.out.println("Jornada iniciada: todas sus órdenes pasan a 'En atención'.");
        if (cargos > 0) {
            System.out.println("Cargos por visitas adicionales registrados: " + Formato.moneda(cargos));
        }
    }

    private void registrarResultados() {
        String codigoJornada = consola.leerTexto("Código de la jornada: ");
        Jornada jornada = sistema.buscarJornada(codigoJornada);
        List<OrdenServicio> sinResultado = jornada.ordenesSinResultado();
        if (jornada.getEstado() != EstadoJornada.EN_CURSO) {
            throw new OperacionInvalidaException("La jornada " + jornada.getCodigo()
                    + " no está en curso (estado: " + jornada.getEstado() + ").");
        }
        for (OrdenServicio orden : sinResultado) {
            ResultadoVisita resultado = leerResultado(orden);
            if (resultado == null) {
                System.out.println("Se detiene el registro; quedan órdenes sin resultado.");
                return;
            }
            sistema.registrarResultado(jornada.getCodigo(), orden.getCodigo(), resultado);
            System.out.println("  " + orden.getCodigo() + " -> " + orden.getEstado());
        }
        if (jornada.getEstado() == EstadoJornada.CERRADA) {
            System.out.println("Jornada cerrada. La cuadrilla " + jornada.getCuadrilla().getCodigo() + " quedó libre.");
        }
    }

    //devuelve null si el usuario decide detenerse
    private ResultadoVisita leerResultado(OrdenServicio orden) {
        while (true) {
            String texto = consola.leerTexto("Orden " + orden.getCodigo() + " (" + orden.getCategoria()
                    + ") - [C]ompletada, [F]allida o [S]alir: ").toLowerCase();
            if (texto.equals("c")) return ResultadoVisita.COMPLETADA;
            if (texto.equals("f")) return ResultadoVisita.FALLIDA;
            if (texto.equals("s")) return null;
            System.out.println("Opción inválida. Escriba C, F o S.");
        }
    }

    private void consultarJornadas() {
        if (sistema.getJornadas().isEmpty()) {
            System.out.println("No hay jornadas creadas.");
        }
        for (Jornada jornada : sistema.getJornadas()) {
            System.out.println(jornada.resumen());
        }
    }
}