package core;

import dao.GerenciadorBanco;

import java.time.LocalDate;
import java.time.LocalTime;

public class Gerenciador {

    private Agenda agenda;
    private Historico historico;
    private GerenciadorBanco dao;

    public Gerenciador() {
        this.dao = new GerenciadorBanco();
        this.agenda = buscarAgendaAtual();
        this.historico = buscarHistorico();
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public Historico getHistorico() {
        return historico;
    }

    public boolean adicionarTarefa(Tarefa tarefa) {
        if (tarefa.getHorario().isBefore(LocalTime.now()) && tarefa.getStatus() == StatusTarefa.Pendente) {
            tarefa.setStatus(StatusTarefa.Falhado);
        }

        if (dao.registrarTarefa(tarefa, agenda.getId())) {
            var foiAdicionada = agenda.adicionarTarefa(tarefa);
            agenda.definirTarefaAtual();
            return foiAdicionada;
        }

        System.err.println("Não foi possivel adicionar " + tarefa.getNome() + " a agenda, reinicia o aplicativo e tente novamente");
        return false;
    }

    public void removerTarefa(Tarefa tarefa) {
        if (dao.removerTarefa(tarefa.getId())) {
            agenda.removerTarefa(tarefa);
            agenda.definirTarefaAtual();
            return;
        }
        System.err.println("Não foi possivel remover " + tarefa.getNome() + " da agenda, reinicie o aplicativo e tente novamente");
    }

    public void atualizarTarefaAtual(StatusTarefa status) {
        if (dao.atualizarTarefa(agenda.getTarefaAtual().getId(), status)) {
            agenda.atualizarTarefaAtual(status);
            return;
        }
        System.err.println("Não foi possivel atualizar a tarefa atual");
    }

    public void finalizarDia() {
        // 1. Gerar relatório da agenda atual (já salvo na base por status atualizado)
        Relatorio relatorio = agenda.gerarRelatorio();
        historico.adicionarRelatorio(relatorio);

        // 2. Criar nova agenda para amanhã
        LocalDate amanha = LocalDate.now().plusDays(1);
        Agenda novaAgenda = new Agenda(amanha);
        dao.registrarAgenda(novaAgenda);

        // 3. Migrar tarefas cíclicas da agenda atual para a nova
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

        novaAgenda.definirTarefaAtual();

        // 4. Atualizar a agenda atual para a nova
        this.agenda = novaAgenda;

        System.out.println("Ciclo do dia finalizado com sucesso!");
    }

    private Historico buscarHistorico() {
        return dao.montarHistorico();
    }

    private Agenda buscarAgendaAtual() {
        return dao.montarAgendaAtual();
    }

}
