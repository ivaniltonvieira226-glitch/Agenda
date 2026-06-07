package core;


public class Gerenciador {
  
  private Agenda agenda;
  private Historico historico;

  public Gerenciador() {
    this.agenda = new Agenda();
    this.historico = new Historico();
  }

  public Gerenciador(Agenda agenda) {
    this.agenda = agenda;
    this.historico = new Historico();
  }

  public Gerenciador(Agenda agenda, Historico historico) {
    this.agenda = agenda;
    this.historico = historico;
  }

  public Agenda getAgenda() {
    return agenda;
  }

  public void finalizarDia() {
    Relatorio relatorio = agenda.gerarRelatorio();
    historico.adicionarRelatorio(relatorio);

    agenda = agenda.novaAgendaCiclica();
  }

  public Historico getHistorico() {
    return historico;
  }
}
