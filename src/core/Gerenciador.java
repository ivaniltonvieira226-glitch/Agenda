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
      agenda.definirTarefaAtual();
      return;
    }
    System.err.println("Não foi possivel adicionar " + tarefa.getNome() + " a agenda, reinicia o aplicativo e tente novamente");
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
