package core;

import java.util.Date;

public class Tarefa {
    public String nome;
    public String descricao;
    public Date horario;
    public StatusTarefa status;
    public boolean ciclico;

    public Tarefa antTarefa;
    public Tarefa proxTarefa;

    public Tarefa(String nome, String descricao, Date horario) {
        this.nome = nome;
        this.descricao = descricao;
        this.horario = horario;
        this.status = StatusTarefa.Pendente;
        this.ciclico = false;
    }

    public Tarefa(String nome, String descricao, Date horario, boolean ciclico) {
        this(nome, descricao, horario);
        this.ciclico = ciclico;
    }
}
