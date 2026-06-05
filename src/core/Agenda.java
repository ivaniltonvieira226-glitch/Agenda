package core;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Agenda {
    private Tarefa ultimo;
    private Tarefa tarefaAtual;
    LocalDate data;
    
    public Agenda(Tarefa ultimo, LocalDate data) {
        this.ultimo = ultimo;
        this.data = data;
    }

    public Agenda(LocalDate data) {
        this.ultimo = null;
        this.tarefaAtual = null;
        this.data = data;
    }

    public boolean adicionarTarefa(Tarefa novaTarefa) {
        // caso de primeira tarefa
        if (ultimo == null) {
            ultimo = novaTarefa;
            ultimo.proxTarefa = ultimo;
            ultimo.antTarefa = ultimo;
            return true;
        }

        // se a nova tarefa for depois da ultima
        if (novaTarefa.getHorario().isAfter(ultimo.getHorario())) {
            novaTarefa.proxTarefa = ultimo.proxTarefa;
            novaTarefa.antTarefa = ultimo;
            ultimo.proxTarefa.antTarefa = novaTarefa;
            ultimo.proxTarefa = novaTarefa;
            ultimo = novaTarefa;
            return true;
        }

        // caso geral
        Tarefa atual = ultimo.proxTarefa;
        while (true) {
            if (novaTarefa.getHorario().isBefore(atual.getHorario())) {
                novaTarefa.proxTarefa = atual;
                novaTarefa.antTarefa = atual.antTarefa;
                atual.antTarefa.proxTarefa = novaTarefa;
                atual.antTarefa = novaTarefa;
                return true;
            }

            if (atual == ultimo) break;
            atual = atual.proxTarefa;
        }
        // retorna falso se não encontrar intervalo para colocar a nova tarefa
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
        LocalTime agora = LocalTime.now();
        
        //caso não haja nenhuma tarefa
        if (ultimo == null) {
            tarefaAtual = null;
            return;
        }

        //caso haja haja apenas uma tarefa
        if (ultimo.proxTarefa == ultimo) {
            tarefaAtual = ultimo;
            return;
        }

        Tarefa node = ultimo.proxTarefa;

        //caso a primeira tarefa for depois de "agora"
        if (agora.isBefore(node.getHorario())) {
            tarefaAtual = node;
        }

        //caso geral
        while (true) {
            boolean depoisDoAtual = agora.isAfter(node.getHorario());
            boolean antesDoProximo = agora.isBefore(node.proxTarefa.getHorario());

            if (depoisDoAtual && antesDoProximo) {
                while (node.getStatus() == StatusTarefa.Pulado) {
                    node = node.proxTarefa;
                    if (node == ultimo) break;
                }
                tarefaAtual = node;
                System.out.println("definida tarefa atual");
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
