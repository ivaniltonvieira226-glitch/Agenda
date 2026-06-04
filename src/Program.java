import core.Agenda;
import core.Tarefa;

import java.util.GregorianCalendar;

public class Program {
    public static void main(String[] args) {
        Agenda agenda = new Agenda();

        var data1 = new GregorianCalendar(2026, GregorianCalendar.JANUARY, 1, 10, 30);
        var tarefa1 = new Tarefa("Estudar", "", data1.getTime());

        var data2 = new GregorianCalendar(2026, GregorianCalendar.JANUARY, 1, 9, 30);
        var tarefa2 = new Tarefa("Comer", "", data2.getTime(), true);

        var data3 = new GregorianCalendar(2026, GregorianCalendar.JANUARY, 1, 11, 30);
        var tarefa3 = new Tarefa("Descansar", "", data3.getTime());

        var data4 = new GregorianCalendar(2026, GregorianCalendar.JANUARY, 1, 8, 30);
        var tarefa4 = new Tarefa("Acordar", "", data4.getTime());

        agenda.adicionarTarefa(tarefa1);
        agenda.adicionarTarefa(tarefa2);
        agenda.adicionarTarefa(tarefa3);
        agenda.adicionarTarefa(tarefa4);

//        agenda.removerNaoCiclicos();
        agenda.definirTarefaAtual();

        agenda.mostrarAgenda();
    }
}
