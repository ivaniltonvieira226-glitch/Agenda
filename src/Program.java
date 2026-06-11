import core.Agenda;
import core.Gerenciador;
import core.Relatorio;
import core.Tarefa;
import dao.GerenciadorBanco;

import java.time.LocalDate;
import java.time.LocalTime;

public class Program {
    public static void main(String[] args) {
        // GerenciadorBanco gerenciadorFudido = new GerenciadorBanco();
        // Agenda agendafudida = new Agenda(LocalDate.of(2026, 06, 8));

        // Tarefa tarefa1 = new Tarefa("Acordar", "Tem que acordar né pae", LocalTime.of(7, 30), true);
        // Tarefa tarefa2 = new Tarefa("Café da manhã", "Tem que comer né pae", LocalTime.of(8, 0), true);
        // Tarefa tarefa3 = new Tarefa("Estudar", "Ficar menos burro da silva", LocalTime.of(9, 30), true);
        // Tarefa tarefa4 = new Tarefa("Al Mosso", "Tem que comer né pae", LocalTime.of(12, 00), true);
        // Tarefa tarefa5 = new Tarefa("Estudar mais", "69 QI", LocalTime.of(13, 40), true);
        // Tarefa tarefa6 = new Tarefa("Vagabundar da Silva", "Descançar né, o caba num é de ferro", LocalTime.of(17, 40), false);
        // Tarefa tarefa7 = new Tarefa("A mimir", "Descançar né, o caba num é de ferro", LocalTime.of(22, 30), false);

        // agendafudida.adicionarTarefa(tarefa1);
        // agendafudida.adicionarTarefa(tarefa2);
        // agendafudida.adicionarTarefa(tarefa3);
        // agendafudida.adicionarTarefa(tarefa4);
        // agendafudida.adicionarTarefa(tarefa5);
        // agendafudida.adicionarTarefa(tarefa6);
        // agendafudida.adicionarTarefa(tarefa7);

        // gerenciadorFudido.adicionarHistoricoTeste(agendafudida);

        Gerenciador gerenciador = new Gerenciador();

        Relatorio[] relatorios = gerenciador.getHistorico().getRelatorios();
        
        for (Relatorio relatorio: relatorios) {
            System.out.println(relatorio.getTitulo());
            relatorio.getAgenda().mostrarAgenda();
            System.out.println("Tarefas Concluidas: " + relatorio.getConcluidas());
            System.out.println("Tarefas Puladas: " + relatorio.getPuladas());
            System.out.println("Tarefas Falhas: " + relatorio.getFalhas());
        }
    }
}
