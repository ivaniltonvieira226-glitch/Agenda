package core;

import java.util.Date;

public class Tarefa {
    private String nome;
    private String descricao;
    private Date horario;
    private StatusTarefa status;
    private boolean ciclico;

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

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public Date getHorario() {
        return horario;
    }

    public StatusTarefa getStatus() {
        return status;
    }

    public void setStatus(StatusTarefa status) {
        this.status = status;
    }

    public boolean isCiclico() {
        return ciclico;
    }
}
