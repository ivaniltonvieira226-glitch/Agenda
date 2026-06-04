package core;

public class Agenda {
    public Tarefa ultimo;

    public Agenda(Tarefa ultimo) {
        this.ultimo = ultimo;
    }

    public Agenda() {
        this(null);
    }

    public boolean adicionarTarefa(Tarefa novaTarefa) {
        // Caso de primeira tarefa
        if (ultimo == null) {
            ultimo = novaTarefa;
            ultimo.proxTarefa = ultimo;
            ultimo.antTarefa = ultimo;
            return true;
        }

        if (ultimo.horario.before(novaTarefa.horario)) {
            novaTarefa.proxTarefa = ultimo.proxTarefa;
            novaTarefa.antTarefa = ultimo;
            ultimo.proxTarefa = novaTarefa;
            ultimo = novaTarefa;
            return true;
        }

        Tarefa atual = ultimo.proxTarefa;
        while (true) {
            if (novaTarefa.horario.before(atual.horario)) {
                novaTarefa.proxTarefa = atual;
                novaTarefa.antTarefa = atual.antTarefa;
                atual.antTarefa.proxTarefa = novaTarefa;
                atual.antTarefa = novaTarefa;
                return true;
            }

            if (atual == ultimo) break;
            atual = atual.proxTarefa;
        }

        return false;
    }

    public void mostrarAgenda() {
        System.out.println("Exibindo todas as tarefas da agenda:");

        int i = 0;
        Tarefa node = ultimo.proxTarefa;
        while (true) {
            System.out.println("Tarefa " + (++i) + ": " + node.nome);
            if (node == ultimo) break;
            node = node.proxTarefa;
        }
    }
}
