import java.util.LinkedHashMap;
import java.util.Map;

//unico lugar donde cambian los ingresos
//solo se invoca tras operaciones exitosas
public class RegistroIngresos {
    private double ingresoPorRegistro;
    private double ingresoPorVisitasAdicionales;
    private final Map<String, Double> porCategoria = new LinkedHashMap<>();

    public void registrarCobroRegistro(String categoria, double monto) {
        validar(monto);
        ingresoPorRegistro += monto;
        acumular(categoria, monto);
    }

    public void registrarCargoVisitaAdicional(String categoria, double monto) {
        validar(monto);
        ingresoPorVisitasAdicionales += monto;
        acumular(categoria, monto);
    }

    public double getIngresoPorRegistro() { return ingresoPorRegistro; }
    public double getIngresoPorVisitasAdicionales() { return ingresoPorVisitasAdicionales; }
    public double getIngresoTotal() { return ingresoPorRegistro + ingresoPorVisitasAdicionales; }

    public double getIngresoCategoria(String categoria) {
        return porCategoria.getOrDefault(categoria, 0.0);
    }

    private void acumular(String categoria, double monto) {
        porCategoria.merge(categoria, monto, Double::sum);
    }

    private void validar(double monto) {
        if (monto < 0 || Double.isNaN(monto)) {
            throw new OperacionInvalidaException("El monto de un ingreso no puede ser negativo.");
        }
    }
}