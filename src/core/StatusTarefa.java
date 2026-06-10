package core;

public enum StatusTarefa {
    Pendente,
    Concluido,
    Falhado,
    Pulado;

    public static StatusTarefa deString(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return Pendente;
        }

        for (StatusTarefa status : StatusTarefa.values()) {
            if (status.name().equals(texto)) {
                return status;
            }
        }

        System.err.println("Aviso: Status desconhecido ('" + texto + "'). Definindo como Pendente.");
        return Pendente; 
    }
}
