package core;

public class Relatorio {

  private String titulo;
  private Agenda agenda;
  private int tarefasPuladas, tarefasFalhas, tarefasConcluidas;

  public Relatorio proxRel;
  
  public Relatorio() {
    this.agenda = null;
    this.tarefasFalhas = 0;
    this.tarefasPuladas = 0;
    this.tarefasConcluidas = 0;
  }

  public Relatorio(Agenda agenda, int tarefasPuladas, int tarefasFalhas, int tarefasConcluidas) {
    this.titulo = "Relatório de " + agenda.getData();
    this.agenda = agenda;
    this.tarefasFalhas = tarefasFalhas;
    this.tarefasPuladas = tarefasPuladas;
    this.tarefasConcluidas = tarefasConcluidas;
  }

  public String getTitulo() {
    return titulo;
  }

  public Agenda getAgenda() {
    return agenda;
  }

  public int getPuladas() {
    return tarefasPuladas;
  }

  public int getFalhas() {
    return tarefasFalhas;
  }

  public int getConcluidas() {
    return tarefasConcluidas;
  }
}
