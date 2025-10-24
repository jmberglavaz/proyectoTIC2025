package um.edu.uy.jdftech.components;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ValidacionDeMetodoDePago {
    public boolean esTarjetaValida(Long nroDeTarjeta, int cvv, Date fechaDeVencimiento) {
        if (validarNroDeTarjeta(nroDeTarjeta) && validarCVV(cvv, nroDeTarjeta) && validarFechaDeVencimiento(fechaDeVencimiento)) {
            return true;
        }
        return false;
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
        
        for (int i = arrayNroDeTarjeta.length - 2; i >= 0; i-=2) {
            int intValue = Character.getNumericValue(arrayNroDeTarjeta[i]);
            intValue = intValue * 2;
            if (String.valueOf(intValue).length() > 1) {
                char[] arrayValue = String.valueOf(intValue).toCharArray();
                int sumValue = 0;
                for (char c : arrayValue) {
                    sumValue += Character.getNumericValue(c);
                }
                intValue = sumValue;
            }
            arrayNrosACheckear[i] = intValue;
        }

        int verifySum = 0;
        for (int count = 0; count <= arrayNrosACheckear.length; count++) {
            verifySum += arrayNrosACheckear[count];
        }

        if (verifySum % 10 == 0) {
            return true;
        }
        return false;
    }


}
