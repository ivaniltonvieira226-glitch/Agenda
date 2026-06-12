package core;

import java.time.LocalDate;
import java.time.LocalTime;

public class Agenda {
    private int id;
    private Tarefa ultimo;
    private Tarefa tarefaAtual;
    private LocalDate data;

    public Agenda(LocalDate data) {
        this.data = data;
    }

    public Agenda() {
        this(LocalDate.now());
    }

    public Agenda(int id, LocalDate data) {
        this.id = id;
        this.data = data;
    }

    public Agenda(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getNomeTarefaAtual() {
        return tarefaAtual.getNome();
    }
    
    public Tarefa getTarefaAtual() {
        return tarefaAtual;
    }

    public void atualizarTarefaAtual(StatusTarefa status) {
        this.tarefaAtual.setStatus(status);
        if (status != StatusTarefa.Pendente && tarefaAtual.proxTarefa != null) {
            this.tarefaAtual = this.tarefaAtual.proxTarefa;
        }
    }

    private boolean adicionarALista(Tarefa novaTarefa) {
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
        System.out.println("Não foi possivel encontrar um intervalo para encaixar " + novaTarefa.getNome());
        return false;
    }

    private boolean removerDaLista(Tarefa tarefa) {
        if (ultimo == null) {
            return false;
        }

        // única tarefa
        if (tarefa.proxTarefa == tarefa) {
            ultimo = null;
            return false;
        }

        tarefa.antTarefa.proxTarefa = tarefa.proxTarefa;
        tarefa.proxTarefa.antTarefa = tarefa.antTarefa;
        if (tarefa == ultimo) ultimo = tarefa.antTarefa;
        return true;
    }

    public void removerTarefa(Tarefa tarefa) {
        if (removerDaLista(tarefa)) {
            if (tarefa == tarefaAtual) {
                definirTarefaAtual();
            }
            return;
        }
        System.out.println("nenhuma tarefa foi removida");
    }

    public void definirTarefaAtual() {
        // LocalTime agora = LocalTime.now();
        LocalTime agora = LocalTime.of(7, 30);
        
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

        //caso a ultima tarefa seja antes de "agora"
        if (!agora.isBefore(ultimo.getHorario())) {
            tarefaAtual = ultimo;
        }

        Tarefa node = ultimo.proxTarefa;

        if (agora.isBefore(node.getHorario())) {
            tarefaAtual = node;
        }

        //caso geral
        while (true) {
            boolean depoisDoAtual = !agora.isBefore(node.getHorario());
            boolean antesDoProximo = agora.isBefore(node.proxTarefa.getHorario());

            if (depoisDoAtual && antesDoProximo) {
                while (node.getStatus() == StatusTarefa.Pulado) {
                    node = node.proxTarefa;
                    if (node == ultimo) break;
                }
                tarefaAtual = node;
                break;
            }

            if (node.getHorario().isBefore(agora) && node.getStatus() == StatusTarefa.Pendente) node.setStatus(StatusTarefa.Falhado);

            if (node == ultimo) break;
            node = node.proxTarefa;
        }
    }

    public void adicionarTarefa(Tarefa novaTarefa) {
        if (adicionarALista(novaTarefa)) {
            definirTarefaAtual();
        }
    }


    public void mostrarAgenda() {
        System.out.println("Exibindo todas as tarefas da agenda:");

        int i = 0;
        Tarefa node = ultimo.proxTarefa;
        while (true) {
            System.out.println("Tarefa " + (++i) + ": " + node.getNome() + " - " + node.getStatus().toString());
            if (node == ultimo) break;
            node = node.proxTarefa;
        }
    }

    public Relatorio gerarRelatorio() {

        int tarefasPuladas = 0;
        int tarefasFalhas = 0;
        int tarefasConcluidas = 0;
        
        if (ultimo == null) {
            return new Relatorio();
        }
        
        Tarefa node = ultimo.proxTarefa;
        do  {
            if (node.getStatus() == StatusTarefa.Pulado) tarefasPuladas++;
            if (node.getStatus() == StatusTarefa.Falhado) tarefasFalhas++;
            if (node.getStatus() == StatusTarefa.Concluido) tarefasConcluidas++;

            node = node.proxTarefa;
        } while (node != ultimo.proxTarefa);

        return new Relatorio(this, tarefasPuladas, tarefasFalhas, tarefasConcluidas);
    }


    public Tarefa[] getTarefas() {
        if (ultimo == null) {
            return new Tarefa[0];
        }

        int i = 0;
        Tarefa node = ultimo.proxTarefa;
        while (true) {
            i++;
            if (node == ultimo) break;
            node = node.proxTarefa;
        }

        Tarefa[] tarefas = new Tarefa[i];

        int j = 0;
        node = ultimo.proxTarefa;
        while (true) {
            tarefas[j++] = node;
            if (node == ultimo) break;
            node = node.proxTarefa;
        }
        return tarefas;
    }

}
