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

    public boolean estaVazia() {
        return ultimo == null;
    }

    public void atualizarTarefaAtual(StatusTarefa status) {
        this.tarefaAtual.setStatus(status);
        if (status != StatusTarefa.Pendente && tarefaAtual.proxTarefa != null) {
            this.tarefaAtual = this.tarefaAtual.proxTarefa;
        }
    }

    private boolean adicionarALista(Tarefa novaTarefa) {
        // caso de lista vazia
        if (estaVazia()) {
            ultimo = novaTarefa;
            ultimo.proxTarefa = ultimo;
            ultimo.antTarefa = ultimo;
            return true;
        }

        // se a nova tarefa for depois da ultima
        if (novaTarefa.getHorario().isAfter(ultimo.getHorario())) {
            System.out.println(novaTarefa.getNome() + " SERÁ ADICIONADA NO FINAL");
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
                var agora = LocalTime.now();
                if (agora.isAfter(novaTarefa.getHorario())) novaTarefa.setStatus(StatusTarefa.Falhado);

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
        if (estaVazia()) {
            return false;
        }

        // única tarefa
        if (tarefa.proxTarefa == tarefa) {
            ultimo = null;
            return true;
        }

        tarefa.antTarefa.proxTarefa = tarefa.proxTarefa;
        tarefa.proxTarefa.antTarefa = tarefa.antTarefa;
        if (tarefa == ultimo) ultimo = tarefa.antTarefa;
        return true;
    }

    public void removerTarefa(Tarefa tarefaRemovida) {
        if (removerDaLista(tarefaRemovida)) {
            return;
        }
        System.out.println("nenhuma tarefa foi removida");
    }

    public void definirTarefaAtual() {
         LocalTime agora = LocalTime.now();
//        LocalTime agora = LocalTime.of(7, 30);
        
        //caso não haja nenhuma tarefa
        if (estaVazia()) {
            tarefaAtual = null;
            return;
        }

        // caso haja apenas uma tarefa
        if (ultimo.proxTarefa == ultimo) {
            tarefaAtual = ultimo;
            return;
        }

        Tarefa node = ultimo.proxTarefa;

        // Se for o primeiro horário
        if (agora.isBefore(node.getHorario())) {
            tarefaAtual = node;
            return;
        }

        //caso a ultima tarefa seja antes de "agora" - todas as tarefas passaram
        if (ultimo.getHorario().isBefore(agora)) {
            tarefaAtual = ultimo;
            System.out.println(tarefaAtual.antTarefa.getNome());
            tarefaAtual.antTarefa.setStatus(StatusTarefa.Falhado);
            return;
        }

        //caso geral - procurar a primeira tarefa com status Pendente entre agora
        while (true) {
            boolean depoisDoAtual = agora.isAfter(node.getHorario());
            boolean antesDoProximo = agora.isBefore(node.proxTarefa.getHorario());

            if (depoisDoAtual && antesDoProximo) {
                // Encontrou o intervalo de tempo, agora procura a primeira tarefa Pendente
                Tarefa candidata = node;
                while (candidata.getStatus() != StatusTarefa.Pendente) {
                    candidata = candidata.proxTarefa;
                    if (candidata == node) {
                        // Completou um ciclo sem encontrar Pendente, define o primeiro do intervalo
                        break;
                    }
                }
                tarefaAtual = candidata;
                // Marca tarefas anteriores ao intervalo como falhadas
                Tarefa anterior = tarefaAtual.antTarefa;
                while (anterior != tarefaAtual && anterior.getStatus() == StatusTarefa.Pendente) {
                    anterior.setStatus(StatusTarefa.Falhado);
                    anterior = anterior.antTarefa;
                }
                return;
            }

            if (node == ultimo) break;
            node = node.proxTarefa;
        }
        
        // Se chegou aqui, nenhuma tarefa foi definida (edge case)
        tarefaAtual = ultimo.proxTarefa;
    }

    public void adicionarTarefa(Tarefa novaTarefa) {
        if (adicionarALista(novaTarefa)) {
            definirTarefaAtual();
        }
    }


    public void mostrarAgenda() {
        if (estaVazia()) {
            System.err.println("Não foi possivel mostrar as tarefas da agenda pois ela está vazia");
            return;
        }
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
        
        if (estaVazia()) {
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
        if (estaVazia()) {
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

    //Exportar Lista em Texto(feat: UI)
    public String exportarListaEmTexto() {
        if (ultimo == null) return "Nenhuma tarefa cadastrada para hoje.";

        StringBuilder sb = new StringBuilder();
        Tarefa node = ultimo.proxTarefa;
        int i = 1;
        while (true) {
            sb.append(String.format("[%s] %d. %s (%s)\n",
                    node.getHorario(), i++, node.getNome(), node.getStatus()));
            if (node == ultimo) break;
            node = node.proxTarefa;
        }
        return sb.toString();
    }

}
