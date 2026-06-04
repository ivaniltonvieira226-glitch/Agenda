package core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class Agenda {
    private Tarefa ultimo;
    private Tarefa tarefaAtual;

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
    }

    public void removerNaoCiclicos() {
        var remover = new ArrayList<Tarefa>();

        Tarefa nodeAtual = ultimo.proxTarefa;
        while (true) {
            if (!nodeAtual.isCiclico()) remover.add(nodeAtual);
            if (nodeAtual == ultimo) break;
            nodeAtual = nodeAtual.proxTarefa;
        }

        for (Tarefa tarefa : remover) {
            removerTarefa(tarefa);
        }
    }

    public void definirTarefaAtual() {
        var agora = Date.from(Instant.now());
        var horario = (new GregorianCalendar(2026, GregorianCalendar.JANUARY, 1, agora.getHours(), agora.getMinutes())).getTime();

        if (ultimo == null) {
            tarefaAtual = null;
            return;
        }

        if (ultimo.proxTarefa == ultimo) {
            tarefaAtual = ultimo;
            return;
        }

        Tarefa node = ultimo.proxTarefa;
        while (true) {
            var depoisDoAtual = horario.after(node.getHorario());
            var antesDoProximo = horario.before(node.proxTarefa.getHorario());

            if (depoisDoAtual && antesDoProximo) {
                while (node.getStatus() == StatusTarefa.Pulado) {
                    node = node.proxTarefa;
                    if (node == ultimo) break;
                }
                tarefaAtual = node;
                break;
            }

            if (node.getStatus() == StatusTarefa.Pendente) node.setStatus(StatusTarefa.Falhado);

            if (node == ultimo) break;
            node = node.proxTarefa;
        }
    }

    public void concluirTarefa() {
        tarefaAtual = tarefaAtual.proxTarefa;
    }

    public void pularTarefa() {
        tarefaAtual.setStatus(StatusTarefa.Pulado);
        tarefaAtual = tarefaAtual.proxTarefa;
    }

    public void falharTarefa() {
        tarefaAtual = tarefaAtual.proxTarefa;
    }

    public void mostrarAgenda() {
        System.out.println("Agenda");
        System.out.println("Tarefa atual: " + tarefaAtual.getNome());
        System.out.println("\nExibindo todas as tarefas da agenda:");

        int i = 0;
        Tarefa node = ultimo.proxTarefa;
        while (true) {
            System.out.println("Tarefa " + (++i) + ": " + node.getNome() + " - " + node.getStatus().toString());
            if (node == ultimo) break;
            node = node.proxTarefa;
        }
    }
}
