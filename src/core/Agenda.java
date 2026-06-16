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
        var agora = LocalTime.now();
        if (agora.isAfter(novaTarefa.getHorario()) && novaTarefa.getStatus() == StatusTarefa.Pendente) {
            novaTarefa.setStatus(StatusTarefa.Falhado);
        }

        // caso de lista vazia
        if (estaVazia()) {
            ultimo = novaTarefa;
            ultimo.proxTarefa = ultimo;
            ultimo.antTarefa = ultimo;
            return true;
        }

        // se a nova tarefa for depois da ultima
        if (!novaTarefa.getHorario().isBefore(ultimo.getHorario())) {
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
        
        //caso não haja nenhuma tarefa
        if (estaVazia()) {
            tarefaAtual = null;
            return;
        }

        // caso haja apenas uma tarefa
        if (ultimo.proxTarefa == ultimo) {
            tarefaAtual = ultimo;
            if (agora.isAfter(tarefaAtual.getHorario()) && tarefaAtual.getStatus() == StatusTarefa.Pendente) {
                tarefaAtual.setStatus(StatusTarefa.Falhado);
            }
            return;
        }

        Tarefa node = ultimo.proxTarefa;

        // Se for antes do primeiro horário
        if (agora.isBefore(node.getHorario())) {
            tarefaAtual = node;
            return;
        }

        //caso a ultima tarefa seja antes ou igual a "agora" - todas as tarefas passaram
        if (!agora.isBefore(ultimo.getHorario())) {
            tarefaAtual = ultimo;
            
            // Marca todas as tarefas pendentes como falhadas
            Tarefa t = ultimo;
            do {
                if (t.getStatus() == StatusTarefa.Pendente) {
                    t.setStatus(StatusTarefa.Falhado);
                }
                t = t.antTarefa;
            } while (t != ultimo);
            
            return;
        }

        //caso geral - procurar a primeira tarefa com status Pendente entre agora
        while (true) {
            boolean depoisDoAtual = !agora.isBefore(node.getHorario());
            boolean antesDoProximo = agora.isBefore(node.proxTarefa.getHorario());

            if (depoisDoAtual && antesDoProximo) {
                // Encontrou o intervalo de tempo
                // A próxima tarefa a fazer é node.proxTarefa (não node, que já passou)
                Tarefa candidata = node.proxTarefa;
                
                // Procura a primeira tarefa Pendente a partir da próxima
                while (candidata.getStatus() != StatusTarefa.Pendente && candidata != node) {
                    candidata = candidata.proxTarefa;
                }
                
                tarefaAtual = candidata;
                
                // Marca tarefas anteriores (que já passaram) como falhadas
                Tarefa anterior = node;
                while (anterior.getStatus() == StatusTarefa.Pendente && anterior != tarefaAtual) {
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

    public boolean adicionarTarefa(Tarefa novaTarefa) {
        var foiAdicionada = adicionarALista(novaTarefa);

        if (foiAdicionada) {
            definirTarefaAtual();
        }

        return foiAdicionada;
    }

    // Método para adicionar tarefa sem definir a tarefa atual (usado ao carregar do banco)
    public void adicionarTarefaSemDefinir(Tarefa novaTarefa) {
        adicionarALista(novaTarefa);
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

    public Tarefa getUltimo() {
        return ultimo;
    }
}
