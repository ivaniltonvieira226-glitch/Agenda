import core.Gerenciador;
import core.Relatorio;
import core.Tarefa;

import java.time.LocalTime;

public class Program {
    public static void main(String[] args) {

        Tarefa tarefa1 = new Tarefa("Acordar", "Tem que acordar né pae", LocalTime.of(7, 30), true);
        Tarefa tarefa2 = new Tarefa("Café da manhã", "Tem que comer né pae", LocalTime.of(8, 0), true);
        Tarefa tarefa3 = new Tarefa("Estudar", "Ficar menos burro da silva", LocalTime.of(9, 30), true);
        Tarefa tarefa4 = new Tarefa("Al Mosso", "Tem que comer né pae", LocalTime.of(12, 00), true);
        Tarefa tarefa5 = new Tarefa("Estudar mais", "69 QI", LocalTime.of(13, 40), true);
        Tarefa tarefa6 = new Tarefa("Vagabundar da Silva", "Descançar né, o caba num é de ferro", LocalTime.of(17, 40));
        Tarefa tarefa7 = new Tarefa("A mimir", "Descançar né, o caba num é de ferro", LocalTime.of(22, 30));

        Gerenciador gerenciador = new Gerenciador();

        //adicionando tarefas a agenda
        gerenciador.getAgenda().adicionarTarefa(tarefa1);
        gerenciador.getAgenda().adicionarTarefa(tarefa2);
        gerenciador.getAgenda().adicionarTarefa(tarefa3);
        gerenciador.getAgenda().adicionarTarefa(tarefa4);
        gerenciador.getAgenda().adicionarTarefa(tarefa5);
        gerenciador.getAgenda().adicionarTarefa(tarefa6);
        gerenciador.getAgenda().adicionarTarefa(tarefa7);
        

        //se quiser que sincronize com o horario atual mude a declaração da variavel agora em Agenda.definirTarefaAtual
        //mudança de status de tarefas
        gerenciador.getAgenda().concluirTarefa();
        gerenciador.getAgenda().concluirTarefa();
        gerenciador.getAgenda().pularTarefa();   
        
        System.out.println("\nA tarefa atual é:");
        System.out.println(gerenciador.getAgenda().getTarefaAtual());

        gerenciador.getAgenda().concluirTarefa();
        gerenciador.getAgenda().concluirTarefa();
        gerenciador.getAgenda().pularTarefa();
        gerenciador.getAgenda().falharTarefa();

        gerenciador.finalizarDia();

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
