package ui.formatter;

import service.dto.ClientData;

public class ClientFormatter {
    private ClientFormatter() {
    }

    public static String format(ClientData clientData) {
        return """
                Nome: %s
                CPF: %s
                Email: %s
                """.formatted(
                clientData.name(),
                CpfFormatter.format(clientData.cpf()),
                clientData.email()
        );
    }
}
