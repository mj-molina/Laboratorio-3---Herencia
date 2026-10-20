//se lanza cuando una operación viola una regla del negocio
//nunca deja estado a medias
public class OperacionInvalidaException extends RuntimeException {
    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
