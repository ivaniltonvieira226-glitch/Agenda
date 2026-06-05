package core;

public class Historico {
  
  private Relatorio primeiro, ultimo;
  private int count;
  
  public Historico() {
    primeiro = null;
    ultimo = null;
    count = 0;
  }

  public Historico(Relatorio primeiro) {
    this.primeiro = primeiro;
    this.ultimo = primeiro;
    this.count = 1;
  }

  private boolean estaVazio() {
    return primeiro ==  null;
  }

  public void adicionarRelatorio(Relatorio novoRel) {
    if (estaVazio()) {
      primeiro = ultimo = novoRel;
      novoRel.proxRel = null;
      count++;
      return;
    }

    if (primeiro == ultimo) {
      primeiro.proxRel = novoRel;
      novoRel.proxRel = null;
      ultimo = novoRel;
      count++;
      return;
    }

    ultimo.proxRel = novoRel;
    ultimo = novoRel;
    count++;
  }

  public Relatorio[] getRelatorios() {
    if (estaVazio()) return new Relatorio[0];

    Relatorio[] historico = new Relatorio[count];

    Relatorio auxRelatorio = primeiro;

    for (int i = 0; i < count; i++) {
      historico[i] = auxRelatorio;
      auxRelatorio = auxRelatorio.proxRel;
    }

    return historico;
  }

}
