import core.Agenda;
import core.Gerenciador;
import core.StatusTarefa;
import core.Tarefa;
import dao.GerenciadorBanco;

import java.time.LocalDate;
import java.time.LocalTime;

public class Program {

    public static void inserirHistoricoTeste() {
        GerenciadorBanco gerenciadorFudido = new GerenciadorBanco();
        Agenda agendafudida = new Agenda(LocalDate.of(2026, 06, 9));

        Tarefa tarefa1 = new Tarefa("Acordar", "Tem que acordar né pae", LocalTime.of(7, 30), true);
        Tarefa tarefa2 = new Tarefa("Café da manhã", "Tem que comer né pae", LocalTime.of(8, 0), true);
        Tarefa tarefa3 = new Tarefa("Estudar", "Ficar menos burro da silva", LocalTime.of(9, 30), true);
        Tarefa tarefa4 = new Tarefa("Al Mosso", "Tem que comer né pae", LocalTime.of(12, 00), true);
        Tarefa tarefa5 = new Tarefa("Estudar mais", "69 QI", LocalTime.of(13, 40), true);
        Tarefa tarefa6 = new Tarefa("Vagabundar da Silva", "Descançar né, o caba num é de ferro", LocalTime.of(17, 40), false);
        Tarefa tarefa7 = new Tarefa("A mimir", "Descançar né, o caba num é de ferro", LocalTime.of(22, 30), false);

        agendafudida.adicionarTarefa(tarefa1);
        agendafudida.adicionarTarefa(tarefa2);
        agendafudida.adicionarTarefa(tarefa3);
        agendafudida.adicionarTarefa(tarefa4);
        agendafudida.adicionarTarefa(tarefa5);
        agendafudida.adicionarTarefa(tarefa6);
        agendafudida.adicionarTarefa(tarefa7);

        gerenciadorFudido.adicionarHistoricoTeste(agendafudida);
    }
    public static void main(String[] args) {
        //descomente essa primeira função e execute ela uma vez 
        //até pode manter descomentado depois da primeira vez 
        // mas por conta dela vai ficar aparecendo um monte de log chato
        // Program.inserirHistoricoTeste();
        
        //já é instanciado com as tarefas ciclicas da ultima agenda criada
        Gerenciador gerenciador = new Gerenciador();

        System.out.println("\nAgenda inicalmente");
        gerenciador.getAgenda().mostrarAgenda();
        
        //instânciação de uma tarefa para fins de testes
        Tarefa tarefaTeste = new Tarefa("Simatar da Silva", "Descançar né, o caba num é de ferro", LocalTime.of(18, 00), false);
        
        //método que adicionar tarefa tando no banco de dados quanto na agenda do programa
        //ela tbm seta o id nesse proprio objeto Tarefa
        gerenciador.adicionarTarefa(tarefaTeste);
        
        System.out.println("\nAgenda depois de adicionar tarefaTeste");
        gerenciador.getAgenda().mostrarAgenda();
        
        
        //  !!!  preste atenção no status da primeira tarefa !!!
        //esse método tanto atualiza o status no banco quanto no programa
        //e também faz com que a tarefa atual da agenda passe a ser a proxima
        System.out.println("\nTarefa atual antes de atualizar seu status");
        System.out.println(gerenciador.getAgenda().getNomeTarefaAtual());
        gerenciador.atualizarTarefaAtual(StatusTarefa.Concluido);
        System.out.println("\nTarefa atual depois de atualizar o status da tarefa anterior");
        System.out.println(gerenciador.getAgenda().getNomeTarefaAtual());
        //acho que seria interessante fazer um método proprio no gerenciador para mostrar toda a agenda
        //mas isso deixo pro proximo que for mexer nesse negocio
        System.out.println("\nToda a lista de tarefas para vc ver que o status mudou");
        gerenciador.getAgenda().mostrarAgenda();
        //  !!!  preste atenção no status da primeira tarefa !!!
        
        //método que remove uma tarefa tanto do banco de dados quanto da agenda temporaria do programa
        gerenciador.removerTarefa(tarefaTeste);
        System.out.println("\nA lista depois que removeu aquela tarefa teste");
        gerenciador.getAgenda().mostrarAgenda();

        //espero que tudo funcione, se não vou me suicidar hj à meia-noite

        
    }
}
