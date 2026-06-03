package ui.formatter;

import model.valueobject.Cpf;

public final class CpfFormatter {

    private CpfFormatter() {
    }

    public static String format(Cpf cpf) {
        return cpf.toString().replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}
