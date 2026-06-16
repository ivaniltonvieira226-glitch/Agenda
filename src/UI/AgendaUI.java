package UI;

import core.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AgendaUI extends JFrame {

    private Gerenciador gerenciador;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    // Componentes da Tela
    private JLabel lblTarefaAtual;
    private JPanel painelTarefasContainer;
    private JButton btnConcluir, btnPular;
    private JTextField txtNomeTarefa, txtDescricaoTarefa, txtHorarioTarefa;
    private JCheckBox chkCiclico;
    private JTextArea txtHistorico;

    // Paleta de Cores (Estilo Dark/Moderno)
    private final Color COR_FUNDO = new Color(245, 246, 248);
    private final Color COR_CARD = Color.WHITE;
    private final Color COR_TEXTO_PADRAO = new Color(33, 37, 41);
    private final Color COR_AZUL_PRINCIPAL = new Color(43, 108, 176);
    private final Color COR_VERDE_SUCESSO = new Color(39, 110, 144);
    private final Color COR_BORDAS = new Color(218, 224, 233);

    public AgendaUI() {
        this.gerenciador = new Gerenciador();

        // Configurações da Janela
        setTitle("Dashboard - Minha Agenda Inteligente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(COR_FUNDO);

        // FAZ ABRIR EM TELA CHEIA
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Layout principal com margens ao redor da tela (padding)
        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
        painelPrincipal.setBackground(COR_FUNDO);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Construção das seções
        painelPrincipal.add(criarPainelTarefaAtual(), BorderLayout.NORTH);
        painelPrincipal.add(criarPainelCentral(), BorderLayout.CENTER);
        painelPrincipal.add(criarPainelInferior(), BorderLayout.SOUTH);

        add(painelPrincipal);

        // Atualiza a tela com os dados iniciais
        atualizarInterface();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AgendaUI().setVisible(true);
        });
    }

    // 1. PAINEL SUPERIOR: Banner elegante para a tarefa em destaque
    private JPanel criarPainelTarefaAtual() {
        JPanel painelTopo = new JPanel(new BorderLayout(10, 10));
        painelTopo.setBackground(COR_CARD);
        painelTopo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDAS, 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        lblTarefaAtual = new JLabel("Nenhuma tarefa para agora.", JLabel.LEFT);
        lblTarefaAtual.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTarefaAtual.setForeground(COR_AZUL_PRINCIPAL);
        painelTopo.add(lblTarefaAtual, BorderLayout.CENTER);

        // Botões de ação com cores e fontes limpas
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBotoes.setBackground(COR_CARD);

        btnConcluir = criarBotaoEstilizado("Concluir ✅", new Color(40, 167, 69));
        btnPular = criarBotaoEstilizado("Pular ↩️", new Color(255, 193, 7));

        // Ajuste fino na cor do texto do botão amarelo para leitura
        btnPular.setForeground(Color.BLACK);

        painelBotoes.add(btnConcluir);
        painelBotoes.add(btnPular);
        painelTopo.add(painelBotoes, BorderLayout.EAST);

        // Ações
        btnConcluir.addActionListener(e -> {
            gerenciador.atualizarTarefaAtual(StatusTarefa.Concluido);
            atualizarInterface();
        });
        btnPular.addActionListener(e -> {
            gerenciador.atualizarTarefaAtual(StatusTarefa.Pulado);
            atualizarInterface();
        });

        return painelTopo;
    }

    // 2. PAINEL CENTRAL: Dividido entre Cadastro (Mediano) e Lista de Tarefas
    private JPanel criarPainelCentral() {
        JPanel painelCentro = new JPanel(new BorderLayout(15, 0));
        painelCentro.setBackground(COR_FUNDO);

        // --- ESQUERDA: Form de Cadastro Customizado para ficar MEDIANO ---
        JPanel painelFormWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        painelFormWrapper.setBackground(COR_FUNDO);

        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBackground(COR_CARD);
        painelForm.setBorder(criarBordaCustomizada(" Nova Tarefa "));
        // Define uma largura mediana fixa para o formulário não esticar na tela cheia
        painelForm.setPreferredSize(new Dimension(380, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fonteLabels = new Font("Segoe UI", Font.BOLD, 13);

        // Linha 0: Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setFont(fonteLabels);
        painelForm.add(lblNome, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtNomeTarefa = new JTextField();
        EstilarCampoTexto(txtNomeTarefa);
        painelForm.add(txtNomeTarefa, gbc);

        // Linha 1: Descrição
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblDesc = new JLabel("Descrição:");
        lblDesc.setFont(fonteLabels);
        painelForm.add(lblDesc, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtDescricaoTarefa = new JTextField();
        EstilarCampoTexto(txtDescricaoTarefa);
        painelForm.add(txtDescricaoTarefa, gbc);

        // Linha 2: Horário
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel lblHora = new JLabel("Horário:");
        lblHora.setFont(fonteLabels);
        painelForm.add(lblHora, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtHorarioTarefa = new JTextField("12:00");
        EstilarCampoTexto(txtHorarioTarefa);
        painelForm.add(txtHorarioTarefa, gbc);

        // Linha 3: Cíclico
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel lblCiclo = new JLabel("Cíclica?");
        lblCiclo.setFont(fonteLabels);
        painelForm.add(lblCiclo, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        chkCiclico = new JCheckBox();
        chkCiclico.setBackground(COR_CARD);
        painelForm.add(chkCiclico, gbc);

        // Linha 4: Botão Adicionar (Ocupando as duas colunas)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 12, 10, 12);
        JButton btnAdicionar = criarBotaoEstilizado("Adicionar a Lista", COR_AZUL_PRINCIPAL);
        painelForm.add(btnAdicionar, gbc);

        painelFormWrapper.add(painelForm);

        // --- DIREITA: Lista de Tarefas (Ocupa o resto do espaço dinamicamente) ---
        JPanel painelLista = new JPanel(new BorderLayout());
        painelLista.setBackground(COR_CARD);
        painelLista.setBorder(criarBordaCustomizada(" Lista de Hoje "));

        // Painel contêiner para as tarefas com BoxLayout para layout vertical
        JPanel painelTarefasContainer = new JPanel();
        painelTarefasContainer.setLayout(new BoxLayout(painelTarefasContainer, BoxLayout.Y_AXIS));
        painelTarefasContainer.setBackground(COR_CARD);
        painelTarefasContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ScrollPane para as tarefas
        JScrollPane scrollLista = new JScrollPane(painelTarefasContainer);
        scrollLista.setBorder(BorderFactory.createEmptyBorder());
        scrollLista.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        painelLista.add(scrollLista, BorderLayout.CENTER);
        
        // Guardar referência para atualizar depois
        this.painelTarefasContainer = painelTarefasContainer;

        painelCentro.add(painelFormWrapper, BorderLayout.WEST);
        painelCentro.add(painelLista, BorderLayout.CENTER);

        // Ação de Cadastrar
        btnAdicionar.addActionListener(e -> {
            try {
                String nome = txtNomeTarefa.getText();

                // todo: vamos ver se melhora isso aqui
                if (nome == null || nome.isEmpty()) return;

                String desc = txtDescricaoTarefa.getText();
                LocalTime horario = LocalTime.parse(txtHorarioTarefa.getText(), timeFormatter);
                boolean ciclico = chkCiclico.isSelected();

                Tarefa nova = new Tarefa(nome, desc, horario, ciclico);
                gerenciador.adicionarTarefa(nova);

                txtNomeTarefa.setText("");
                txtDescricaoTarefa.setText("");
                chkCiclico.setSelected(false);

                atualizarInterface();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Formato de horário inválido! Use o padrão HH:mm (Ex: 14:30).", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        return painelCentro;
    }

    // 3. PAINEL INFERIOR: Histórico e Fechamento
    private JPanel criarPainelInferior() {
        JPanel painelBaixo = new JPanel(new BorderLayout(0, 10));
        painelBaixo.setBackground(COR_CARD);
        painelBaixo.setBorder(criarBordaCustomizada(" Histórico de Produtividade "));

        JButton btnFinalizarDia = criarBotaoEstilizado("🏁 Finalizar Ciclo do Dia e Migrar Cíclicas", COR_VERDE_SUCESSO);
        btnFinalizarDia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        painelBaixo.add(btnFinalizarDia, BorderLayout.NORTH);

        txtHistorico = new JTextArea(6, 40);
        txtHistorico.setEditable(false);
        txtHistorico.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtHistorico.setForeground(new Color(100, 110, 120));
        txtHistorico.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JScrollPane scrollHist = new JScrollPane(txtHistorico);
        scrollHist.setBorder(BorderFactory.createLineBorder(COR_BORDAS));
        painelBaixo.add(scrollHist, BorderLayout.CENTER);

        btnFinalizarDia.addActionListener(e -> {
//            gerenciador.finalizarDia();
            JOptionPane.showMessageDialog(this, "Relatório arquivado! Nova agenda gerada com sucesso.");
            atualizarInterface();
        });

        return painelBaixo;
    }

    // --- MÉTODOS AUXILIARES DE ESTILIZAÇÃO ---

    private JButton criarBotaoEstilizado(String texto, Color corFundo) {
        JButton botao = new JButton(texto);

        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));

        botao.setOpaque(true);
        botao.setBorderPainted(false);

        botao.setBackground(corFundo);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);

        botao.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return botao;
    }

    private void EstilarCampoTexto(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDAS, 1, true),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)
        ));
    }

    private TitledBorder criarBordaCustomizada(String titulo) {
        TitledBorder borda = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COR_BORDAS, 1, true), titulo
        );
        borda.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        borda.setTitleColor(COR_AZUL_PRINCIPAL);
        return borda;
    }

    private void atualizarInterface() {
        Agenda agendaAtual = gerenciador.getAgenda();

        // 1. Atualiza a label do topo e verifica se há tarefa pendente para ativar botões
        String textoBanner = "🎉 Parabéns! Nenhuma tarefa pendente no momento.";
        boolean temTarefaPendente = false;
        try {
            if (gerenciador.getAgenda() != null) {
                // Verifica se há alguma tarefa pendente na agenda
                if (temTarefasPendentes(agendaAtual)) {
                    Tarefa tarefaAtual = gerenciador.getAgenda().getTarefaAtual();
                    if (tarefaAtual != null) {
                        String nomeTarefa = tarefaAtual.getNome();
                        if (nomeTarefa != null && !nomeTarefa.trim().isEmpty()) {
                            textoBanner = "📌 Próxima Atividade: " + nomeTarefa;
                            // Só ativar botões se a tarefa estiver com status Pendente
                            temTarefaPendente = (tarefaAtual.getStatus() == StatusTarefa.Pendente);
                        }
                    }
                } else {
                    // Nenhuma tarefa pendente - mostrar mensagem de parabéns
                    textoBanner = "🎉 Parabéns! Você completou todas as tarefas do dia!";
                    temTarefaPendente = false;
                }
            }
        } catch (Exception e) {
            // Caso ocorra qualquer erro de ponteiro na leitura, mantém o padrão seguro
            textoBanner = "🎉 Parabéns! Nenhuma tarefa pendente no momento.";
            temTarefaPendente = false;
        }
        lblTarefaAtual.setText(textoBanner);
        
        // Ativar/desativar botões conforme houver tarefa pendente
        btnConcluir.setEnabled(temTarefaPendente);
        btnPular.setEnabled(temTarefaPendente);

        // 2. Atualiza a lista renderizando os componentes das tarefas
        atualizarListaTarefas(agendaAtual);

        // 3. Atualiza o Histórico
        StringBuilder sbHist = new StringBuilder();
        Relatorio[] relatorios = gerenciador.getHistorico().getRelatorios();
        if (relatorios.length == 0) {
            sbHist.append("O histórico está limpo. Complete um dia para gerar dados.");
        } else {
            for (Relatorio r : relatorios) {
                if (r.getTitulo() != null) {
                    sbHist.append("📊 ").append(r.getTitulo())
                            .append("  |  ✅ Concluídas: ").append(r.getConcluidas())
                            .append("  |  ↩️ Puladas: ").append(r.getPuladas())
                            .append("  |  ❌ Falhadas: ").append(r.getFalhas())
                            .append("\n_________________________________________________________________________________\n");
                }
            }
        }
        txtHistorico.setText(sbHist.toString());
    }

    // Método auxiliar para verificar se há tarefas pendentes na agenda
    private boolean temTarefasPendentes(Agenda agenda) {
        if (agenda == null || agenda.estaVazia()) {
            return false;
        }

        try {
            // Usar reflexão para acessar o campo privado 'ultimo'
            java.lang.reflect.Field fieldUltimo = agenda.getClass().getDeclaredField("ultimo");
            fieldUltimo.setAccessible(true);
            Tarefa ultimo = (Tarefa) fieldUltimo.get(agenda);
            
            if (ultimo != null) {
                Tarefa atual = ultimo.proxTarefa;
                do {
                    if (atual.getStatus() == StatusTarefa.Pendente) {
                        return true;
                    }
                    atual = atual.proxTarefa;
                } while (atual != ultimo.proxTarefa);
            }
        } catch (Exception e) {
            // Fallback
            return false;
        }

        return false;
    }

    // Método para atualizar a lista de tarefas renderizando painéis individuais
    private void atualizarListaTarefas(Agenda agenda) {
        painelTarefasContainer.removeAll();

        if (agenda == null || agenda.estaVazia()) {
            JLabel lblVazio = new JLabel("Nenhuma tarefa adicionada ainda");
            lblVazio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblVazio.setForeground(new Color(150, 150, 150));
            painelTarefasContainer.add(lblVazio);
        } else {
            // Percorrer a lista ligada circular de tarefas sempre começando PELA PRIMEIRA (ordenada por horário)
            // Em uma lista circular: ultimo.proxTarefa = primeiro elemento
            Tarefa primeira = null;
            try {
                // Usar reflexão para acessar o campo privado 'ultimo'
                java.lang.reflect.Field fieldUltimo = agenda.getClass().getDeclaredField("ultimo");
                fieldUltimo.setAccessible(true);
                Tarefa ultimo = (Tarefa) fieldUltimo.get(agenda);
                if (ultimo != null) {
                    primeira = ultimo.proxTarefa; // Primeira tarefa em ordem de horário
                }
            } catch (Exception e) {
                // Fallback: usar tarefa atual
                primeira = agenda.getTarefaAtual();
            }

            if (primeira != null) {
                Tarefa atual = primeira;
                do {
                    painelTarefasContainer.add(criarPainelTarefa(atual));
                    painelTarefasContainer.add(Box.createVerticalStrut(8));
                    atual = atual.proxTarefa;
                } while (atual != primeira);
            }
        }

        // Adicionar espaço vazio no final para melhor visualização
        painelTarefasContainer.add(Box.createVerticalGlue());

        painelTarefasContainer.revalidate();
        painelTarefasContainer.repaint();
    }

    // Método para criar um painel visual para cada tarefa
    private JPanel criarPainelTarefa(Tarefa tarefa) {
        JPanel painelTarefa = new JPanel(new GridBagLayout());
        painelTarefa.setBackground(COR_CARD);
        painelTarefa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDAS, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        painelTarefa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 8);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;

        // Painel esquerdo: Info principal (nome, descrição, horário) - OCUPA MAIS ESPAÇO
        JPanel painelInfoEsquerda = new JPanel();
        painelInfoEsquerda.setLayout(new BoxLayout(painelInfoEsquerda, BoxLayout.Y_AXIS));
        painelInfoEsquerda.setBackground(COR_CARD);

        // Nome da tarefa
        JLabel lblNome = new JLabel(tarefa.getNome());
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNome.setForeground(COR_TEXTO_PADRAO);
        painelInfoEsquerda.add(lblNome);

        // Descrição com limite de linhas para não crescer demais
        String descricao = tarefa.getDescricao();
        if (descricao.length() > 60) {
            descricao = descricao.substring(0, 60) + "...";
        }
        JLabel lblDesc = new JLabel(descricao);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(100, 110, 120));
        painelInfoEsquerda.add(lblDesc);

        // Horário
        JLabel lblHora = new JLabel("⏰ " + tarefa.getHorario().format(timeFormatter));
        lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHora.setForeground(new Color(120, 130, 140));
        painelInfoEsquerda.add(lblHora);

        // Adicionar ao painel principal na esquerda com peso 1 para crescer
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 8);
        painelTarefa.add(painelInfoEsquerda, gbc);

        // Painel direito: Status, tags e botão de remover - NÃO CRESCE
        JPanel painelDireita = new JPanel();
        painelDireita.setLayout(new BoxLayout(painelDireita, BoxLayout.Y_AXIS));
        painelDireita.setBackground(COR_CARD);

        // Tag de status
        JLabel lblStatus = criarLabelStatus(tarefa.getStatus());
        painelDireita.add(lblStatus);
        painelDireita.add(Box.createVerticalStrut(3));

        // Tag cíclica se aplicável
        if (tarefa.isCiclico()) {
            JLabel lblCiclica = new JLabel("🔄 Cíclica");
            lblCiclica.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblCiclica.setForeground(new Color(255, 193, 7));
            painelDireita.add(lblCiclica);
            painelDireita.add(Box.createVerticalStrut(3));
        }

        // Botão de remover tarefa
        JButton btnRemover = criarBotaoEstilizado("🗑️ Remover", new Color(220, 53, 69));
        btnRemover.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRemover.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btnRemover.addActionListener(e -> {
            gerenciador.removerTarefa(tarefa);
            atualizarInterface();
        });
        painelDireita.add(btnRemover);
        painelDireita.add(Box.createVerticalGlue());

        // Adicionar painel direito com peso 0 (não cresce)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHEAST;
        painelTarefa.add(painelDireita, gbc);

        return painelTarefa;
    }

    // Método auxiliar para criar label de status com cores
    private JLabel criarLabelStatus(StatusTarefa status) {
        String texto = "";
        Color cor = Color.GRAY;

        switch (status) {
            case Pendente:
                texto = "⏳ Pendente";
                cor = new Color(255, 193, 7);
                break;
            case Concluido:
                texto = "✅ Concluído";
                cor = new Color(39, 174, 96);
                break;
            case Pulado:
                texto = "↩️ Pulado";
                cor = new Color(52, 152, 219);
                break;
            case Falhado:
                texto = "❌ Falhado";
                cor = new Color(231, 76, 60);
                break;
        }

        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(cor);
        return lbl;
    }
}