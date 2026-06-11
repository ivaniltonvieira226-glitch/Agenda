package core;

import javax.net.ssl.SSLEngineResult.Status;

import dao.GerenciadorBanco;

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

  public void adicionarTarefa(Tarefa tarefa) {
    if (dao.registrarTarefa(tarefa, agenda.getId())) {
      agenda.adicionarTarefa(tarefa);
    }
  }

  public void atualizarTarefaAtual(StatusTarefa status) {
    if (dao.atualizarTarefa(agenda.getTarefaAtual().getId() ,status)) {
      agenda.atualizarTarefaAtual(status);
      return;
    }
    System.err.println("Não foi possivel atualizar a tarefa atual");
  }

  private Historico buscarHistorico() {
    return dao.montarHistorico();
  }

  private Agenda buscarAgendaAtual() {
    return dao.montarAgendaAtual();
  }

}
