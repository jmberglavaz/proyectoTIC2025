package um.edu.uy.jdftech.validators;

import org.springframework.stereotype.Component;
import um.edu.uy.jdftech.entitites.MedioDePago;

import java.util.Date;

@Component
public class ValidacionDeMetodoDePago {
    public ValidationResult validarTarjeta(MedioDePago medioDePago) {
        ValidationResult result = new ValidationResult();

        if (!validarNroDeTarjeta(medioDePago.getCardNumber())) {
            result.addError("Número de tarjeta inválido");
        }

        if (!validarCVV(medioDePago.getCvv(), medioDePago.getCardNumber())) {
            result.addError("CVV inválido");
        }

        if (!validarFechaDeVencimiento(medioDePago.getExpirationDate())) {
            result.addError("Tarjeta vencida");
        }

        return result;
    }

    private boolean validarFechaDeVencimiento(Date fechaDeVencimiento) {
        if (fechaDeVencimiento == null) {
            return false;
        }

        if (fechaDeVencimiento.after(new Date())) {
            return true;
        }
        return false;
        }

    private boolean validarCVV(int cvv, Long nroDeTarjeta) {
        if (String.valueOf(cvv).length() == 3 && !isAmex(nroDeTarjeta)){
            return true;
        } else if (String.valueOf(cvv).length() == 4 && isAmex(nroDeTarjeta)) {
            return true;
        }
        return false;
    }

    private boolean isAmex(Long nroDeTarjeta) {
        String stringNroDeTarjeta = String.valueOf(nroDeTarjeta);
        if (stringNroDeTarjeta.length() == 15 && stringNroDeTarjeta.charAt(0) == '3' && (stringNroDeTarjeta.charAt(1) == '4' || stringNroDeTarjeta.charAt(1) == '7')) {
            return true;
        }
        return false;
    }

    // Según lo que googleé, hay un algoritmo xa validar los nros de las tarjetas que se llama Algoritmo de Luhn. Saqué info de este link: https://stripe.com/es/resources/more/how-to-use-the-luhn-algorithm-a-guide-in-applications-for-businesses
    private boolean validarNroDeTarjeta(Long nroDeTarjeta) {
        char[] arrayNroDeTarjeta = nroDeTarjeta.toString().toCharArray();
        int[] arrayNrosACheckear = new int[arrayNroDeTarjeta.length];

        // Copiar todos los dígitos primero
        for (int i = 0; i < arrayNroDeTarjeta.length; i++) {
            arrayNrosACheckear[i] = Character.getNumericValue(arrayNroDeTarjeta[i]);
        }

        // Aplicar Luhn: duplicar cada segundo dígito empezando desde la derecha
        for (int i = arrayNroDeTarjeta.length - 2; i >= 0; i -= 2) {
            int intValue = arrayNrosACheckear[i] * 2;
            if (intValue > 9) {
                intValue = intValue - 9; // Equivalente a sumar los dígitos
            }
            arrayNrosACheckear[i] = intValue;
        }

        int verifySum = 0;
        for (int count = 0; count < arrayNrosACheckear.length; count++) {
            verifySum += arrayNrosACheckear[count];
        }

        return verifySum % 10 == 0;
    }


}
