package core;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Agenda {
    private Tarefa ultimo;
    private Tarefa tarefaAtual;
    private LocalDate data;

    public Agenda(Tarefa ultimo, LocalDate data) {
        this.ultimo = ultimo;
        this.tarefaAtual = null;
        this.data = data;
    }

    public Agenda(LocalDate data) {
        this(null, data);
    }

    public Agenda() {
        this(null, LocalDate.now());
    }


    private boolean adicionarALista(Tarefa novaTarefa) {
        if (novaTarefa == null) return false;

        // Caso A: A lista está totalmente vazia
        if (ultimo == null) {
            ultimo = novaTarefa;
            ultimo.proxTarefa = ultimo;
            ultimo.antTarefa = ultimo;
            return true;
        }

        // Caso B: A nova tarefa é ANTES ou IGUAL à primeira tarefa da lista
        Tarefa primeira = ultimo.proxTarefa;
        // Mudamos de: isBefore() para: !isAfter() -> Significa (Antes ou Igual)
        if (!novaTarefa.getHorario().isAfter(primeira.getHorario())) {
            novaTarefa.proxTarefa = primeira;
            novaTarefa.antTarefa = ultimo;
            primeira.antTarefa = novaTarefa;
            ultimo.proxTarefa = novaTarefa;
            return true;
        }

        // Caso C: A nova tarefa é DEPOIS ou IGUAL à última tarefa da lista
        // Mudamos de: isAfter() para: !isBefore() -> Significa (Depois ou Igual)
        if (!novaTarefa.getHorario().isBefore(ultimo.getHorario())) {
            novaTarefa.proxTarefa = primeira;
            novaTarefa.antTarefa = ultimo;
            ultimo.proxTarefa = novaTarefa;
            primeira.antTarefa = novaTarefa;
            ultimo = novaTarefa; // A nova tarefa vira o novo 'ultimo'
            return true;
        }

        // Caso Geral: Inserção ordenada no meio da lista circular
        Tarefa node = primeira;
        while (node != ultimo) {
            // Se o horário da nova tarefa for menor ou igual ao nó atual, insere logo antes dele
            if (!novaTarefa.getHorario().isAfter(node.getHorario())) {
                Tarefa anterior = node.antTarefa;
                
                novaTarefa.proxTarefa = node;
                novaTarefa.antTarefa = anterior;
                anterior.proxTarefa = novaTarefa;
                node.antTarefa = novaTarefa;
                return true;
            }
            node = node.proxTarefa;
        }

        return false;

    }

    private boolean removerDaLista(Tarefa tarefa) {
        if (ultimo == null) {
            return false;
        }

        // única tarefa(fix: mudança de false para true)
        if (tarefa.proxTarefa == tarefa) {
            ultimo = null;
            return true;
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

    //talvez possa remover esse método 
    //já que o método que gera uma agenda nova não inclue os não ciclicos
    public void removerNaoCiclicos() {
        var remover = new ArrayList<Tarefa>();

        Tarefa nodeAtual = ultimo.proxTarefa;
        while (true) {
            if (!nodeAtual.isCiclico()) remover.add(nodeAtual);
            if (nodeAtual == ultimo) break;
            nodeAtual = nodeAtual.proxTarefa;
        }

        for (Tarefa tarefa : remover) {
            removerDaLista(tarefa);
        }
    }

    public void definirTarefaAtual() {
         LocalTime agora = LocalTime.now();
         //LocalTime agora = LocalTime.of(7, 30);
        
        
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
        if (ultimo.getHorario().isBefore(agora)) {
            tarefaAtual = ultimo;
            // Se o horário dela já passou e está pendente, falhou
            if(agora.isAfter(ultimo.getHorario()) && ultimo.getStatus() == StatusTarefa.Pendente){
                ultimo.setStatus(StatusTarefa.Falhado);
            }
            return;
        }

        

        Tarefa node = ultimo.proxTarefa;

        if (agora.isBefore(node.getHorario())) {
            tarefaAtual = node;
            return;
        }

        //O horário atual é DEPOIS da última tarefa do dia
        if (agora.isAfter((ultimo.getHorario()))) {
            tarefaAtual = ultimo;
        }
        else{
            //caso geral: Encontrar em qual intervalo o "agora" se encaixa
            Tarefa auxNode = ultimo.proxTarefa;
            boolean encontrou = false;
            while (auxNode != ultimo) {
                // agora >= node e agora < proximo
                if (!agora.isBefore(auxNode.getHorario()) && agora.isBefore(auxNode.proxTarefa.getHorario())) {
                    tarefaAtual = auxNode;
                    encontrou = true;
                    break;
                }
                auxNode = auxNode.proxTarefa;
            }

            // Se por algum motivo de decolagem de ponteiros não encaixar no meio,
            // garante que a tarefaAtual não fique flutuando como null
            if (!encontrou && tarefaAtual == null) {
                tarefaAtual = auxNode;
            }
        }

        
    
        // Se a tarefa do momento foi pulada ou concluída, avança para a próxima que estiver pendente
        Tarefa salvaguarda = tarefaAtual;
        while (tarefaAtual.getStatus() != StatusTarefa.Pendente) {
            tarefaAtual = tarefaAtual.proxTarefa;
            // Evita loop infinito se todas as tarefas do dia já mudaram de status
            if (tarefaAtual == salvaguarda) break; 
        }

        // VARREDURA DE FALHAS: Agora que a tarefaAtual está definida, 
        // rodamos a lista para falhar APENAS o que ficou no passado cronológico
        Tarefa aux = ultimo.proxTarefa;
        while (true) {
            if (agora.isAfter(aux.getHorario()) && aux.getStatus() == StatusTarefa.Pendente) {
                // Só falha se não for a própria tarefa atual do momento
                if (aux != tarefaAtual) {
                    aux.setStatus(StatusTarefa.Falhado);
                }
            }


            if (aux == ultimo) break;
            aux = aux.proxTarefa;
        }
    }

    public void adicionarTarefa(Tarefa novaTarefa) {
        if (adicionarALista(novaTarefa)) {
            definirTarefaAtual();
            // Força uma garantia caso seja a primeira tarefa do dia
            if (tarefaAtual == null && ultimo != null) {
                tarefaAtual = ultimo;
            }
        }
    }

    public void concluirTarefa() {
        if (tarefaAtual.getStatus() == StatusTarefa.Pendente) {
            tarefaAtual.setStatus(StatusTarefa.Concluido);
            tarefaAtual = tarefaAtual.proxTarefa;
    } 
        else {
            System.out.println("Ação negada: Esta tarefa já foi processada neste ciclo.");
    }
        
    }

    public void pularTarefa() {
        if (tarefaAtual.getStatus() == StatusTarefa.Pendente){
            tarefaAtual.setStatus(StatusTarefa.Pulado);
            tarefaAtual = tarefaAtual.proxTarefa;
        }
        else{
             System.out.println("Ação negada: Esta tarefa já foi processada neste ciclo.");
        }
        
    }

    public void falharTarefa() {
        if (tarefaAtual.getStatus() == StatusTarefa.Pendente){
            tarefaAtual.setStatus(StatusTarefa.Falhado);
            tarefaAtual = tarefaAtual.proxTarefa;
        }
        else{
            System.out.println("Ação negada: Esta tarefa já foi processada neste ciclo.");
        }
    }

    public LocalDate getData() {
        return data;
    }

    public String getTarefaAtual() {
        // Se a tarefaAtual estiver nula por atraso de inicialização, 
        // mas a lista não estiver vazia, usa o primeiro elemento disponível (ultimo.proxTarefa)
        if (tarefaAtual == null) {
            if (ultimo != null) {
                tarefaAtual = ultimo.proxTarefa; // Força o ponteiro para a primeira do dia
                return tarefaAtual.getNome();
            }
            return null; // Lista realmente vazia
        }
        return tarefaAtual.getNome();
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

    public Agenda novaAgendaCiclica() {
        LocalDate amanha = data.plusDays(1);
        Agenda novaAgenda = new Agenda(amanha);

        if (ultimo == null) {
            return novaAgenda;
        }
        
        Tarefa node = ultimo.proxTarefa;
        do  {
            if (node.isCiclico()) {
                novaAgenda.adicionarALista(node.copiarTarefa());
            }
            node = node.proxTarefa;
        } while (node != ultimo.proxTarefa);

        return novaAgenda;
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
