//inicializa el programa: 
//crea el sistema, carga los datos iniciales y arranca el menu

public class Main {
    public static void main(String[] args) {
        TecniHogar sistema = new TecniHogar();
        DatosIniciales.cargar(sistema);
        new Menu(sistema, new Consola()).ejecutar();
    }
}