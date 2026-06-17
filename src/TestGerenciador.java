import core.Agenda;
import core.Gerenciador;
import core.StatusTarefa;
import core.Tarefa;
import java.time.LocalDate;
import java.time.LocalTime;
import java.lang.reflect.Field;

public class TestGerenciador {

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void main(String[] args) {
        try {
            testDefinirTarefaAtual();
            System.out.println("\n\u001B[32m✅ All tests passed successfully!\u001B[0m\n");
        } catch (Exception e) {
            System.err.println("\n\u001B[31m❌ Test failed!\u001B[0m\n");
            e.printStackTrace();
        }
    }

    public static void testDefinirTarefaAtual() throws Exception {
        Gerenciador gerenciador = new Gerenciador();

        // Get fields of Gerenciador
        Field agendaField = Gerenciador.class.getDeclaredField("agenda");
        agendaField.setAccessible(true);

        Field tarefaAtualField = Gerenciador.class.getDeclaredField("tarefaAtual");
        tarefaAtualField.setAccessible(true);

        // Create a completely clean and isolated Agenda
        Agenda testAgenda = new Agenda(LocalDate.of(2026, 6, 17));
        agendaField.set(gerenciador, testAgenda);

        // Scenario 1: Empty Agenda
        gerenciador.definirTarefaAtual(LocalTime.of(12, 0));
        assertEquals(null, gerenciador.getTarefaAtual(), "Empty agenda must have null current task");

        // Scenario 2: Single task
        Tarefa t1 = new Tarefa("Acordar", "Acordar cedo", LocalTime.of(7, 30), false);
        testAgenda.adicionarTarefa(t1);

        // At 07:00 (before single task)
        gerenciador.definirTarefaAtual(LocalTime.of(7, 0));
        assertEquals(t1, gerenciador.getTarefaAtual(), "Single pending task should be current before its time");

        // At 08:00 (after single task)
        gerenciador.definirTarefaAtual(LocalTime.of(8, 0));
        assertEquals(t1, gerenciador.getTarefaAtual(), "Single pending task remains current after its time");

        // Set single task to Concluido
        gerenciador.atualizarTarefaAtual(StatusTarefa.Concluido);
        assertEquals(null, gerenciador.getTarefaAtual(), "No current task when single task is finished");

        // Scenario 3: Multiple tasks
        // Reset/clean agenda
        testAgenda = new Agenda(LocalDate.of(2026, 6, 17));
        agendaField.set(gerenciador, testAgenda);
        tarefaAtualField.set(gerenciador, null);

        Tarefa mt1 = new Tarefa("T1", "Task 1", LocalTime.of(10, 0), false);
        Tarefa mt2 = new Tarefa("T2", "Task 2", LocalTime.of(11, 0), false);
        Tarefa mt3 = new Tarefa("T3", "Task 3", LocalTime.of(12, 0), false);

        testAgenda.adicionarTarefa(mt1);
        testAgenda.adicionarTarefa(mt2);
        testAgenda.adicionarTarefa(mt3);

        // Case A: Before first task (agora = 09:30)
        gerenciador.definirTarefaAtual(LocalTime.of(9, 30));
        assertEquals(mt1, gerenciador.getTarefaAtual(), "First pending task should be current before first task's start");
        assertEquals(StatusTarefa.Pendente, mt1.getStatus(), "T1 should be Pendente");
        assertEquals(StatusTarefa.Pendente, mt2.getStatus(), "T2 should be Pendente");

        // Case B: In first task's slot (agora = 10:30)
        gerenciador.definirTarefaAtual(LocalTime.of(10, 30));
        assertEquals(mt1, gerenciador.getTarefaAtual(), "T1 should be current during its slot");
        assertEquals(StatusTarefa.Pendente, mt1.getStatus(), "T1 should be Pendente");
        assertEquals(StatusTarefa.Pendente, mt2.getStatus(), "T2 should be Pendente");

        // Case C: In second task's slot (agora = 11:15)
        // This should fail mt1 because now is past mt2's start time, and make mt2 current.
        gerenciador.definirTarefaAtual(LocalTime.of(11, 15));
        assertEquals(mt2, gerenciador.getTarefaAtual(), "T2 should be current during its slot");
        assertEquals(StatusTarefa.Falhado, mt1.getStatus(), "T1 should have failed");
        assertEquals(StatusTarefa.Pendente, mt2.getStatus(), "T2 should be Pendente");

        // Case D: T2 is completed at 11:30 (inside T2's slot)
        // This should make T3 current.
        gerenciador.atualizarTarefaAtual(StatusTarefa.Concluido);
        assertEquals(mt3, gerenciador.getTarefaAtual(), "T3 should be current when T2 is Concluido");
        assertEquals(StatusTarefa.Concluido, mt2.getStatus(), "T2 status should be Concluido");
    }
}
