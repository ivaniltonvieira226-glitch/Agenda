package core;

public class Agenda {
    public Tarefa ultimo;
    public Tarefa atual;

    public Agenda(Tarefa ultimo) {
        this.ultimo = ultimo;
    }

    public Agenda() {
        this(null);
    }

    public boolean adicionarTarefa(Tarefa novaTarefa) {
        // caso de primeira tarefa
        if (ultimo == null) {
            ultimo = novaTarefa;
            ultimo.proxTarefa = ultimo;
            ultimo.antTarefa = ultimo;
            return true;
        }

        if (novaTarefa.getHorario().after(ultimo.getHorario())) {
            novaTarefa.proxTarefa = ultimo.proxTarefa;
            novaTarefa.antTarefa = ultimo;
            ultimo.proxTarefa.antTarefa = novaTarefa;
            ultimo.proxTarefa = novaTarefa;
            ultimo = novaTarefa;
            return true;
        }

        Tarefa atual = ultimo.proxTarefa;
        while (true) {
            if (novaTarefa.getHorario().before(atual.getHorario())) {
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

    public void removerTarefa(Tarefa tarefa) {
        if (ultimo == null) {
            return;
        }

        // única tarefa
        if (tarefa.proxTarefa == tarefa) {
            ultimo = null;
            return;
        }

        tarefa.antTarefa.proxTarefa = tarefa.proxTarefa;
        tarefa.proxTarefa.antTarefa = tarefa.antTarefa;
        if (tarefa == ultimo) ultimo = tarefa.antTarefa;

        System.out.println(tarefa.antTarefa.getNome());
    }

    public void mostrarAgenda() {
        System.out.println("Exibindo todas as tarefas da agenda:");

        int i = 0;
        Tarefa node = ultimo.proxTarefa;
        while (true) {
            System.out.println("Tarefa " + (++i) + ": " + node.getNome());
            if (node == ultimo) break;
            node = node.proxTarefa;
        }
    }
}
