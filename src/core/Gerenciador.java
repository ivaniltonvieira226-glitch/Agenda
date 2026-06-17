package core;

import dao.GerenciadorBanco;

import java.time.LocalDate;
import java.time.LocalTime;

public class Gerenciador {

    private Agenda agenda;
    private Historico historico;
    private GerenciadorBanco dao;
    private Tarefa tarefaAtual;

    public Gerenciador() {
        this.dao = new GerenciadorBanco();
        this.agenda = buscarAgendaAtual();
        this.historico = buscarHistorico();
        definirTarefaAtual();
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public Historico getHistorico() {
        return historico;
    }

    public Tarefa getTarefaAtual() {
        return tarefaAtual;
    }

    public String getNomeTarefaAtual() {
        return tarefaAtual != null ? tarefaAtual.getNome() : "";
    }

    public boolean adicionarTarefa(Tarefa tarefa) {
        if (dao.registrarTarefa(tarefa, agenda.getId())) {
            var foiAdicionada = agenda.adicionarTarefa(tarefa);
            if (foiAdicionada) this.definirTarefaAtual();
            return foiAdicionada;
        }

        System.err.println("Não foi possivel adicionar " + tarefa.getNome() + " a agenda, reinicia o aplicativo e tente novamente");
        return false;
    }

    public void removerTarefa(Tarefa tarefa) {
        if (dao.removerTarefa(tarefa.getId())) {
            agenda.removerTarefa(tarefa);
            definirTarefaAtual();
            return;
        }
        System.err.println("Não foi possivel remover " + tarefa.getNome() + " da agenda, reinicie o aplicativo e tente novamente");
    }

    public void atualizarTarefaAtual(StatusTarefa status) {
        if (tarefaAtual == null) return;
        if (dao.atualizarTarefa(tarefaAtual.getId(), status)) {
            tarefaAtual.setStatus(status);
            definirTarefaAtual();
            return;
        }
        System.err.println("Não foi possivel atualizar a tarefa atual");
    }

    public void definirTarefaAtual() {
        definirTarefaAtual(LocalTime.now());
    }

    public void definirTarefaAtual(LocalTime agora) {
        // agenda vazia
        if (agenda.estaVazia()) {
            tarefaAtual = null;
            return;
        }

        Tarefa node = agenda.getUltimo().proxTarefa;

        // se só há uma tarefa e ela é pendente, ela deve ser a atual
        if (node == node.proxTarefa) {
            if (node.getStatus() != StatusTarefa.Pendente) {
                tarefaAtual = null;
                return;
            }
            tarefaAtual = node;
            return;
        }

        // se agora for antes da primeira tarefa
        if (!agora.isAfter(node.getHorario()) && node.getStatus() == StatusTarefa.Pendente) {
            tarefaAtual = node;
            return;
        }

        // marcar as tarefas pendentes que já perderam o prazo como falhadas
        Tarefa temp = agenda.getUltimo().proxTarefa;
        while (temp != agenda.getUltimo()) {
            if (!agora.isBefore(temp.proxTarefa.getHorario())) {
                if (temp.getStatus() == StatusTarefa.Pendente) {
                    temp.setStatus(StatusTarefa.Falhado);
                    dao.atualizarTarefa(temp.getId(), StatusTarefa.Falhado);
                }
            }
            temp = temp.proxTarefa;
        }

        // encontra a primeira tarefa pendente e define-a como tarefa atual
        Tarefa candidato = agenda.getUltimo().proxTarefa;
        do {
            if (candidato.getStatus() == StatusTarefa.Pendente) {
                tarefaAtual = candidato;
                return;
            }
            candidato = candidato.proxTarefa;
        } while (candidato != agenda.getUltimo().proxTarefa);

        // se nenhuma tarefa pendente foi encontrada, define a tarefa atual como nulo
        tarefaAtual = null;
    }

    public void finalizarDia() {
        // gera o relatório atual
        Relatorio relatorio = agenda.gerarRelatorio();
        historico.adicionarRelatorio(relatorio);

        // gerar a agenda de amanhã
        LocalDate amanha = LocalDate.now().plusDays(1);
        Agenda novaAgenda = new Agenda(amanha);
        dao.registrarAgenda(novaAgenda);

        // migrar as tarefas cíclicas para a agenda nova
        Tarefa[] tarefas = agenda.getTarefas();
        for (Tarefa tarefa : tarefas) {
            if (tarefa.isCiclico()) {
                // Criar cópia da tarefa cíclica com status Pendente
                Tarefa tarefaCopia = tarefa.copiarTarefa();
                if (dao.registrarTarefa(tarefaCopia, novaAgenda.getId())) {
                    novaAgenda.adicionarTarefa(tarefaCopia);
                }
            }
        }

        // atualizar a agenda para a agenda nova
        this.agenda = novaAgenda;
        definirTarefaAtual();

        System.out.println("Ciclo do dia finalizado com sucesso!");
    }

    private Historico buscarHistorico() {
        return dao.montarHistorico();
    }

    private Agenda buscarAgendaAtual() {
        return dao.montarAgendaAtual();
    }

}
