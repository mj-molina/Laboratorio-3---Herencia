import java.util.Locale;

public final class Formato {
    private Formato() { }

    public static String moneda(double monto) {
        return String.format(Locale.US, "Q%,.2f", monto);
    }

    public static String horas(double horas) {
        return String.format(Locale.US, "%.2f", horas);
    }
}