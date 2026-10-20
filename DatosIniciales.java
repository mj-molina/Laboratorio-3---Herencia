//carga los datos de partida: 
//2 ordenes por categoría (pendientes), 3 cuadrillas y cero ingresos
public final class DatosIniciales {
    private DatosIniciales() { }

    public static void cargar(TecniHogar sistema) {
        //diagnosticos
        sistema.registrarOrdenInicial(new Diagnostico("D-001", "Ana Pérez", "6a Avenida 10-20, zona 1",
                1.0, Zona.METROPOLITANA));                                           // Q150.00
        sistema.registrarOrdenInicial(new Diagnostico("D-002", "Luis Gómez", "Km 15 Carretera a El Salvador",
                1.5, Zona.PERIFERICA));                                              // Q225.00

        //instalaciones
        sistema.registrarOrdenInicial(new Instalacion("I-001", "Marta López", "12 Calle 5-30, zona 10",
                2.5, Zona.METROPOLITANA, 5));                                        // horas por puntos 3.75 > 2.5 -> Q460.00
        sistema.registrarOrdenInicial(new Instalacion("I-002", "Carlos Ruiz", "Aldea San José Pinula",
                3.0, Zona.PERIFERICA, 2));                                           // Q175 + 3h*90 = Q445.00

        //instalaciones electricas certificadas
        sistema.registrarOrdenInicial(new InstalacionElectricaCertificada("E-001", "Sofía Ramírez",
                "3a Avenida 8-15, zona 14", 2.5, Zona.METROPOLITANA, 5, 2000.0));    // Q680.00
        sistema.registrarOrdenInicial(new InstalacionElectricaCertificada("E-002", "Jorge Méndez",
                "Km 22 Carretera a Fraijanes", 4.0, Zona.PERIFERICA, 8, 3500.0));    // Q1,010.00

        //mantenimientos de aire acondicionado (solo metropolitana)
        sistema.registrarOrdenInicial(new MantenimientoAire("M-001", "Elena Castillo",
                "7a Avenida 2-40, zona 9", 1.5, Zona.METROPOLITANA, 12000));         // Q345.00
        sistema.registrarOrdenInicial(new MantenimientoAire("M-002", "Pedro Morales",
                "Diagonal 6 12-42, zona 10", 2.0, Zona.METROPOLITANA, 24000));       // Q400.00

        //cuadrillas
        sistema.registrarCuadrilla(new Cuadrilla("C-01", 8.0, true));
        sistema.registrarCuadrilla(new Cuadrilla("C-02", 6.0, false));
        sistema.registrarCuadrilla(new Cuadrilla("C-03", 4.0, false));
    }
}