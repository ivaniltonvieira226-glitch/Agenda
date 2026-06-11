package core;

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

  private Historico buscarHistorico() {
    return dao.montarHistorico();
  }

  private Agenda buscarAgendaAtual() {
    return dao.montarAgendaAtual();
  }

}
