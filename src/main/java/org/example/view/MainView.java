package org.example.view;

import org.example.controller.CategoriaController;
import org.example.controller.TransacaoController;
import org.example.model.Categoria;
import org.example.model.Transacao;
import org.example.model.Usuario;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainView {
    private final Usuario currentUser;
    private final CategoriaController categoriaCtrl = new CategoriaController();
    private final TransacaoController transacaoCtrl = new TransacaoController();
    private JFrame frame;

    private static final DateTimeFormatter BR_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HR_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final Locale LOCALE_BR = new Locale("pt","BR");
    private static final Categoria ALL_CATEGORY = new Categoria("Todas");

    // Transações
    private JTable transacaoTable;
    private DefaultTableModel transacaoModel;
    private TableRowSorter<TableModel> transacaoSorter;
    private JComboBox<String> filtroTipo;
    private JTextField filtroValor;
    private JComboBox<Categoria> filtroCategoria;
    private JFormattedTextField filtroData;
    private JTextField filtroDesc;
    private JComboBox<Categoria> cbCategoriaTransacao;

    // Resumo
    private JLabel lblReceitas, lblDespesas, lblTotal, lblUsuarioHora;

    // Categorias
    private JTable ctgTable;
    private DefaultTableModel ctgModel;
    private TableRowSorter<TableModel> ctgSorter;
    private JTextField filtroCtgId, filtroCtgNome;

    public MainView(Usuario u) {
        this.currentUser = u;
    }

    public void show() {
        frame = new JFrame("Gestão Financeira - " + currentUser.getUsuario());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900,650);
        frame.setLocationRelativeTo(null);

        // Top
        JPanel top = new JPanel(new BorderLayout());
        lblUsuarioHora = new JLabel(currentUser.getUsuario()+" — "+LocalTime.now().format(HR_FORMAT));
        lblUsuarioHora.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        top.add(lblUsuarioHora, BorderLayout.WEST);
        new Timer(60_000,e->lblUsuarioHora.setText(
                currentUser.getUsuario()+" — "+LocalTime.now().format(HR_FORMAT)
        )).start();
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e->{
            if (JOptionPane.showConfirmDialog(frame,
                    "Deseja mesmo sair do sistema?",
                    "Confirmar Logout",
                    JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                frame.dispose();
                new LoginView().show();
            }
        });
        top.add(btnLogout, BorderLayout.EAST);
        frame.add(top, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Transações", createTransacoesPanel());
        tabs.addTab("Resumo",     createResumoPanel());
        tabs.addTab("Categorias", createCategoriasPanel());
        frame.add(tabs, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel createTransacoesPanel() {
        JPanel p = new JPanel(new BorderLayout());

        // Filtros
        JPanel f = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        filtroTipo = new JComboBox<>(new String[]{"Todas","Receita","Despesa"});
        filtroValor = new JTextField(8);
        filtroValor.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e){
                char c=e.getKeyChar();
                if(!Character.isDigit(c)&&c!='.'&&c!=','&&c!=KeyEvent.VK_BACK_SPACE) e.consume();
            }
        });
        filtroCategoria = new JComboBox<>(); atualizarCategoriasComboFiltro(filtroCategoria);
        try{
            MaskFormatter mf = new MaskFormatter("##/##/####"); mf.setPlaceholderCharacter('_');
            filtroData = new JFormattedTextField(new DefaultFormatterFactory(mf));
        } catch(ParseException ex){
            filtroData = new JFormattedTextField();
        }
        filtroDesc = new JTextField(10);

        f.add(new JLabel("Tipo:"));      f.add(filtroTipo);
        f.add(new JLabel("Valor:"));     f.add(filtroValor);
        f.add(new JLabel("Categoria:")); f.add(filtroCategoria);
        f.add(new JLabel("Data:"));      f.add(filtroData);
        f.add(new JLabel("Desc:"));      f.add(filtroDesc);
        JButton btnClear = new JButton("Limpar Filtros");
        btnClear.addActionListener(e->{
            filtroTipo.setSelectedIndex(0);
            filtroValor.setText("");
            filtroCategoria.setSelectedItem(ALL_CATEGORY);
            filtroData.setText("");
            filtroDesc.setText("");
            transacaoSorter.setRowFilter(null);
        });
        f.add(btnClear);
        p.add(f, BorderLayout.NORTH);

        // Tabela de Transações (com coluna ID)
        String[] cols = {"ID","Tipo","Valor","Categoria","Data","Descrição"};
        transacaoModel = new DefaultTableModel(cols,0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        transacaoTable = new JTable(transacaoModel);
        transacaoSorter = new TableRowSorter<>(transacaoModel);
        transacaoTable.setRowSorter(transacaoSorter);
        p.add(new JScrollPane(transacaoTable), BorderLayout.CENTER);

        // dispara filtro ao selecionar/enter
        ActionListener fil = e->aplicarFiltroTransacoes();
        filtroTipo.addActionListener(fil);
        filtroValor.addActionListener(fil);
        filtroCategoria.addActionListener(fil);
        filtroData.addActionListener(fil);
        filtroDesc.addActionListener(fil);

        // Form de nova transação
        JPanel form = new JPanel(new GridLayout(6,2,5,5));
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Receita","Despesa"});
        JTextField txtValor = new JTextField();
        txtValor.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent e){
                char c=e.getKeyChar();
                if(!Character.isDigit(c)&&c!='.'&&c!=',') e.consume();
            }
        });
        JTextField txtData = new JTextField(LocalDate.now().format(BR_FORMAT));
        JTextField txtDesc = new JTextField();
        cbCategoriaTransacao = new JComboBox<>();
        atualizarCategoriasComboTransacao(cbCategoriaTransacao);

        form.add(new JLabel("Tipo:"));      form.add(cbTipo);
        form.add(new JLabel("Valor:"));     form.add(txtValor);
        form.add(new JLabel("Categoria:")); form.add(cbCategoriaTransacao);
        form.add(new JLabel("Data:"));      form.add(txtData);
        form.add(new JLabel("Descrição:")); form.add(txtDesc);

        JButton btnAdd = new JButton("Adicionar Transação");
        JButton btnRem = new JButton("Remover Selecionada");
        form.add(btnAdd); form.add(btnRem);
        p.add(form, BorderLayout.SOUTH);

        btnAdd.addActionListener(e->{
            if(txtValor.getText().isEmpty()||txtData.getText().isEmpty()||txtDesc.getText().isEmpty()){
                JOptionPane.showMessageDialog(frame,"Preencha todos os campos!","Erro",JOptionPane.ERROR_MESSAGE);
                return;
            }
            double val;
            try{ val=Double.parseDouble(txtValor.getText().replace(',','.')); }
            catch(Exception ex){
                JOptionPane.showMessageDialog(frame,"Valor inválido!","Erro",JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate d;
            try{
                d=LocalDate.parse(txtData.getText(),BR_FORMAT);
                if(d.isBefore(LocalDate.now())){
                    JOptionPane.showMessageDialog(frame,"Data não pode ser passada!","Erro",JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch(Exception ex){
                JOptionPane.showMessageDialog(frame,"Data inválida!","Erro",JOptionPane.ERROR_MESSAGE);
                return;
            }
            Transacao t=new Transacao(val,
                    (Categoria)cbCategoriaTransacao.getSelectedItem(),
                    d, txtDesc.getText(),
                    (String)cbTipo.getSelectedItem(),
                    currentUser);
            transacaoCtrl.salvar(t);
            loadTransacoes(); // recarrega tabela
            txtValor.setText(""); txtDesc.setText("");
        });

        btnRem.addActionListener(e->{
            int sel=transacaoTable.getSelectedRow();
            if(sel<0){
                JOptionPane.showMessageDialog(frame,"Selecione uma transação!","Erro",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(JOptionPane.showConfirmDialog(frame,
                    "Deseja mesmo excluir a transação?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
            int modelIdx = transacaoTable.convertRowIndexToModel(sel);
            Integer id = (Integer)transacaoModel.getValueAt(modelIdx,0);
            transacaoCtrl.remover(id);
            loadTransacoes();
        });

        loadTransacoes();
        return p;
    }

    private void aplicarFiltroTransacoes(){
        List<RowFilter<Object,Object>> filters=new ArrayList<>();
        String ft=(String)filtroTipo.getSelectedItem();
        if(!"Todas".equals(ft)) filters.add(RowFilter.regexFilter("^"+ft+"$",1));
        String fv=filtroValor.getText().trim();
        if(!fv.isEmpty()){
            fv=fv.replace(".", "\\.").replace(",", "\\.");
            filters.add(RowFilter.regexFilter(fv,2));
        }
        Categoria fc=(Categoria)filtroCategoria.getSelectedItem();
        if(fc!=null&&fc!=ALL_CATEGORY) filters.add(RowFilter.regexFilter("^"+fc.getNome()+"$",3));
        String fd=filtroData.getText();
        if(fd!=null&&!fd.contains("_")) filters.add(RowFilter.regexFilter("^"+fd+"$",4));
        String fdesc=filtroDesc.getText().trim();
        if(!fdesc.isEmpty()) filters.add(RowFilter.regexFilter("(?i)"+fdesc,5));
        transacaoSorter.setRowFilter(filters.isEmpty()?null:RowFilter.andFilter(filters));
    }

    private void loadTransacoes(){
        transacaoModel.setRowCount(0);
        for(Transacao t: transacaoCtrl.listarTodos()){
            // compara apenas pelo ID, não pelo objeto
            if (!t.getUsuario().getId().equals(currentUser.getId())) continue;
            transacaoModel.addRow(new Object[]{
                    t.getId(),
                    t.getTipo(),
                    String.format(LOCALE_BR,"%.2f",t.getValor()),
                    t.getCategoria().getNome(),
                    t.getData().format(BR_FORMAT),
                    t.getDescricao()
            });
        }
    }

    private JPanel createResumoPanel(){
        JPanel p=new JPanel(new BorderLayout());
        lblReceitas=new JLabel("Total Receitas: 0.00");
        lblDespesas=new JLabel("Total Despesas: 0.00");
        lblTotal=new JLabel("Saldo Total: 0.00");
        Font f=lblTotal.getFont().deriveFont(Font.BOLD,lblTotal.getFont().getSize()+2f);
        lblReceitas.setFont(f); lblDespesas.setFont(f); lblTotal.setFont(f);

        JPanel info=new JPanel(new GridLayout(3,1,5,5));
        info.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        info.add(lblReceitas); info.add(lblDespesas); info.add(lblTotal);
        p.add(info,BorderLayout.CENTER);

        JButton btn=new JButton("Atualizar Resumo");
        btn.addActionListener(e->atualizarResumo());
        JPanel pnl=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnl.add(btn);
        p.add(pnl,BorderLayout.SOUTH);
        return p;
    }

    private void atualizarResumo(){
        double r=0,d=0;
        for(Transacao t: transacaoCtrl.listarTodos()){
            if(!t.getUsuario().getId().equals(currentUser.getId())) continue;
            if("Receita".equals(t.getTipo())) r+=t.getValor(); else d+=t.getValor();
        }
        lblReceitas.setText("Total Receitas: "+String.format(LOCALE_BR,"R$ %.2f",r));
        lblDespesas.setText("Total Despesas: "+String.format(LOCALE_BR,"R$ %.2f",d));
        lblTotal   .setText("Saldo Total:   "+String.format(LOCALE_BR,"R$ %.2f",r-d));
    }

    private JPanel createCategoriasPanel(){
        JPanel p=new JPanel(new BorderLayout());

        JPanel f=new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        filtroCtgId=new JTextField(5);
        filtroCtgNome=new JTextField(10);
        f.add(new JLabel("ID:")); f.add(filtroCtgId);
        f.add(new JLabel("Nome:")); f.add(filtroCtgNome);
        JButton btnClear=new JButton("Limpar Filtros");
        btnClear.addActionListener(e->ctgSorter.setRowFilter(null));
        f.add(btnClear);
        p.add(f,BorderLayout.NORTH);

        ctgModel=new DefaultTableModel(new Object[]{"ID","Nome"},0);
        ctgTable=new JTable(ctgModel);
        ctgSorter=new TableRowSorter<>(ctgModel);
        ctgTable.setRowSorter(ctgSorter);
        p.add(new JScrollPane(ctgTable),BorderLayout.CENTER);

        ActionListener filCtg=e->{
            List<RowFilter<Object,Object>> fl=new ArrayList<>();
            String idText=filtroCtgId.getText().trim();
            if(!idText.isEmpty()){
                try{fl.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL,Integer.parseInt(idText),0));}
                catch(Exception ignore){}
            }
            String nomeText=filtroCtgNome.getText().trim();
            if(!nomeText.isEmpty()){
                fl.add(RowFilter.regexFilter("(?i)"+nomeText,1));
            }
            ctgSorter.setRowFilter(fl.isEmpty()?null:RowFilter.andFilter(fl));
        };
        filtroCtgId.addActionListener(filCtg);
        filtroCtgNome.addActionListener(filCtg);

        JPanel form=new JPanel(new GridLayout(2,2,5,5));
        JTextField txtCat=new JTextField();
        JButton btnAdd=new JButton("Adicionar"), btnRem=new JButton("Remover Selecionada");
        form.add(new JLabel("Nome da Categoria:")); form.add(txtCat);
        form.add(btnAdd); form.add(btnRem);
        p.add(form,BorderLayout.SOUTH);

        btnAdd.addActionListener(e->{
            String nome=txtCat.getText().trim();
            if(nome.isEmpty()){
                JOptionPane.showMessageDialog(frame,"Preencha o nome da categoria!","Erro",JOptionPane.ERROR_MESSAGE);
                return;
            }
            categoriaCtrl.salvar(new Categoria(nome));
            txtCat.setText("");
            loadCategorias();
        });

        btnRem.addActionListener(e->{
            int sel=ctgTable.getSelectedRow(); if(sel<0) return;
            if(JOptionPane.showConfirmDialog(frame,
                    "Deseja mesmo excluir a categoria?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
            int idx=ctgTable.convertRowIndexToModel(sel);
            Integer id=(Integer)ctgModel.getValueAt(idx,0);
            categoriaCtrl.remover(id);
            loadCategorias();
        });

        loadCategorias();
        return p;
    }

    private void loadCategorias(){
        ctgModel.setRowCount(0);
        for(Categoria c:categoriaCtrl.listarTodos()){
            ctgModel.addRow(new Object[]{c.getId(),c.getNome()});
        }
    }

    private void atualizarCategoriasComboFiltro(JComboBox<Categoria> cb){
        cb.removeAllItems();
        cb.addItem(ALL_CATEGORY);
        for(Categoria c:categoriaCtrl.listarTodos())cb.addItem(c);
    }
    private void atualizarCategoriasComboTransacao(JComboBox<Categoria> cb){
        cb.removeAllItems();
        for(Categoria c:categoriaCtrl.listarTodos())cb.addItem(c);
    }
}
