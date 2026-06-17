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
            if (status != StatusTarefa.Pendente && tarefaAtual.proxTarefa != null) {
                tarefaAtual = tarefaAtual.proxTarefa;
            }
            return;
        }
        System.err.println("Não foi possivel atualizar a tarefa atual");
    }

    public void definirTarefaAtual() {
        LocalTime agora = LocalTime.now();

        // agenda vazia
        if (agenda.estaVazia()) {
            tarefaAtual = null;
            return;
        }

        Tarefa atual = agenda.getUltimo().proxTarefa;

        // se só há uma tarefa e ela é pendente, ela deve ser a atual
        if (atual == atual.proxTarefa) {
            if (atual.getStatus() != StatusTarefa.Pendente) {
                tarefaAtual = null;
                return;
            }
            tarefaAtual = atual;
            return;
        }

        // se agora for antes da primeira tarefa
        if (!agora.isAfter(atual.getHorario()) && atual.getStatus() == StatusTarefa.Pendente) {
            tarefaAtual = atual;
            return;
        }

        while (atual != agenda.getUltimo()) {
            boolean depoisDoAtual = !agora.isBefore(atual.getHorario());
            boolean antesDoProximo = agora.isBefore(atual.proxTarefa.getHorario());

            if (depoisDoAtual && antesDoProximo) {
                Tarefa candidata = atual.proxTarefa;

                while (candidata.getStatus() != StatusTarefa.Pendente) {
                    if (candidata == agenda.getUltimo()) {
                        tarefaAtual = null;
                        return;
                    }
                    candidata = candidata.proxTarefa;
                }

                tarefaAtual = candidata;
                return;
            } else if (atual.getStatus() == StatusTarefa.Pendente) {
                atual.setStatus(StatusTarefa.Falhado);
                dao.atualizarTarefa(atual.getId(), StatusTarefa.Falhado);
            }

            atual = atual.proxTarefa;
        }

        if (atual.getStatus() == StatusTarefa.Pendente) tarefaAtual = atual;
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
