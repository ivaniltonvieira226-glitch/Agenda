package core;

import java.time.LocalTime;

public class Tarefa {
    private int id;
    private String nome;
    private String descricao;
    private LocalTime horario;
    private StatusTarefa status;
    private boolean ciclico;

    public Tarefa antTarefa;
    public Tarefa proxTarefa;

    public Tarefa(String nome, String descricao, LocalTime horario, boolean ciclico) {
        this.nome = nome;
        this.descricao = descricao;
        this.horario = horario;
        this.status = StatusTarefa.Pendente;
        this.ciclico = ciclico;
    }
    
    public Tarefa(int id, String nome, String descricao, LocalTime horario, StatusTarefa status, boolean ciclico) {
        this.id = id;
        this(nome, descricao, horario, ciclico);
        this.status = status;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalTime getHorario() {
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

    public Tarefa copiarTarefa() {
        return new Tarefa(this.nome, this.descricao, this.horario, this.ciclico);
    }
}
