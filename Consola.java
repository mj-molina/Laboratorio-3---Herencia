import java.util.Scanner;

//lectura segura de entradas: 
//un formato incorrecto vuelve a pedir el dato, nunca termina el programa
public class Consola {
    private final Scanner scanner = new Scanner(System.in);

    //lanza NoSuchElementException si la entrada se cierra 
    //la maneja el menu
    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    public int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: ingrese un número entero.");
            }
        }
    }

    public double leerDecimal(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).replace(',', '.');
            try {
                double valor = Double.parseDouble(texto);
                if (Double.isNaN(valor) || Double.isInfinite(valor)) {
                    throw new NumberFormatException();
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: ingrese un número (por ejemplo 2.5).");
            }
        }
    }

    //devuelve -1 si el texto no es un entero
    //para que el menu lo trate como opcion invalida
    public int leerOpcion(String mensaje) {
        try {
            return Integer.parseInt(leerTexto(mensaje));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public boolean confirmar(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje + " (s/n): ").toLowerCase();
            if (texto.equals("s") || texto.equals("si") || texto.equals("sí")) {
                return true;
            }
            if (texto.equals("n") || texto.equals("no")) {
                return false;
            }
            System.out.println("Responda 's' o 'n'.");
        }
    }

    public Zona leerZona() {
        while (true) {
            String texto = leerTexto("Zona (1. Metropolitana, 2. Periférica): ");
            if (texto.equals("1")) {
                return Zona.METROPOLITANA;
            }
            if (texto.equals("2")) {
                return Zona.PERIFERICA;
            }
            System.out.println("Zona inválida: ingrese 1 o 2.");
        }
    }
}