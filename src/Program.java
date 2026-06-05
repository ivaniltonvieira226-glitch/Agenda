import core.Agenda;
import core.Tarefa;

import java.time.LocalDate;
import java.time.LocalTime;

public class Program {
    public static void main(String[] args) {

        Agenda agenda = new Agenda(LocalDate.now());


        LocalTime horario1 = LocalTime.of(14, 30);
        Tarefa tarefa1 = new Tarefa("Estudar", "", horario1);


        LocalTime horario2 = LocalTime.of(13, 0);
        Tarefa tarefa2 = new Tarefa("Comer", "", horario2, true);


        LocalTime horario3 = LocalTime.of(15, 0);
        Tarefa tarefa3 = new Tarefa("Descansar", "", horario3);


        LocalTime horario4 = LocalTime.of(15,30);
        Tarefa tarefa4 = new Tarefa("Acordar", "", horario4);

        agenda.adicionarTarefa(tarefa1);
        agenda.adicionarTarefa(tarefa2);
        agenda.adicionarTarefa(tarefa3);
        agenda.adicionarTarefa(tarefa4);


        agenda.definirTarefaAtual();

        agenda.mostrarAgenda();
    }
}
