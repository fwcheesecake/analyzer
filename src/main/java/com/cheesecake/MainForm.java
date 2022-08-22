package com.cheesecake;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Vector;

import com.cheesecake.components.*;
import com.cheesecake.helpers.Token;
import com.github.javaparser.JavaToken;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class MainForm {
    private JTextPane codeTextArea;
    private JTable tokensTable;
    private JButton analyzeButton;
    private JButton eraseButton;
    private JButton loadFileButton;
    private JTextArea outputTextArea;
    private JPanel mainPanel;
    private JScrollPane codeScrollPane;

    private JFrame mainFrame;

    private File file;

    public MainForm() {
        // MEMBERS
        mainFrame = new JFrame("Syntax Analyzer");
        mainFrame.setContentPane(mainPanel);
        TextLineNumber tln = new TextLineNumber(codeTextArea);
        codeScrollPane.setRowHeaderView(tln);

        // LISTENERS
        loadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Pick a file");
                String userHome = System.getProperty("user.home") + "/IdeaProjects/Analizador-Sintactico/src/tests";
                fileChooser.setCurrentDirectory(new File(userHome));
                int result = fileChooser.showOpenDialog(mainFrame);
                if (result != JFileChooser.CANCEL_OPTION) {
                    file = fileChooser.getSelectedFile();
                    if (!((file == null) || (file.getName().equals("")))) {
                        codeTextArea.setText("");
                        try {
                            Files.lines(file.toPath(), StandardCharsets.UTF_8)
                                .forEach(line -> codeTextArea.setText(codeTextArea.getText() + line.concat("\n")));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
        eraseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                codeTextArea.setText("");
                outputTextArea.setText("");
                // Compiler.getInstance().clearTokens();
                // Compiler.getInstance().clearParserOutput();
                tokensTable.setModel(new DefaultTableModel());
            }
        });
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                outputTextArea.setText("");
                if(file != null) {
                    try {
                        // Read
                        String code = codeTextArea.getText();

                        // Writing the file
                        FileWriter fw = new FileWriter(file);
                        for (int i = 0; i < code.length(); i++)
                            fw.write(code.charAt(i));
                        fw.close();

                        // Creating a String reader
                        StringReader sr = new StringReader(code);

                        // Set up a minimal type solver that only looks at the classes used to run this
                        // sample.
                        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
                        combinedTypeSolver.add(new ReflectionTypeSolver());

                        // Configure JavaParser to use type resolution
                        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
                        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

                        // Parse some code
                        CompilationUnit cu;

                        ArrayList<Token> tokens = new ArrayList<>();

                        try {
                            cu = StaticJavaParser.parse(file);

                            cu.getTokenRange().get().forEach(t -> {
                                // System.out.println(t.toString());
                                // System.out.print(t.getText() + " - ");
                                // System.out.println(JavaToken.Kind.valueOf(t.getKind()));

                                if (t.getKind() > 3) {
                                    tokens.add(makeToken(t));
                                }
                            });
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (ParseProblemException e) {
                            outputTextArea.setText(e.getMessage().toString());
                        }

                        // Creating the Scanner
                        // Lexer lexer = new Lexer(sr);

                        // Craeting the parser with the Scanner
                        // Parser parser = new Parser(lexer);

                        // Parsing
                        // Compiler.getInstance().clearParserOutput();
                        // parser.parse();

                        // Filling the table with the Scanner output
                        showData(tokens);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // DRAW FRAME
        try {
            UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
            for(UIManager.LookAndFeelInfo i : info) {
                System.out.println(i.getName() + ": " + i.getClassName());
            }
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch(Exception e){
            e.printStackTrace();
        }

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(1440, 850);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    public static void main(String[] args) {
        MainForm form = new MainForm();
    }

    public void showData(ArrayList<Token> tokens) {
        DefaultTableModel model = (DefaultTableModel) tokensTable.getModel();
        Vector<String> columns = new Vector<>();
        columns.add("Start");
        columns.add("End");
        columns.add("Token");
        columns.add("Value");

        model.setColumnIdentifiers(columns);

        for (Token s:
            tokens) {
            model.addRow(new String[]{
                String.valueOf(s.start),
                String.valueOf(s.end),
                String.valueOf(s.token),
                String.valueOf(s.value)
            });
        }

        // String output = Compiler.getInstance().getParserOutput();
        // outputTextArea.setText(output.length() == 0 ? "Compilacion terminada exitosamente" : output + "\nHubo errores en la compilacion");
    }

    public static Token makeToken(JavaToken t) {
        String[] s = t.toString().split("\\s{2,}");
        int kind = Integer.parseInt(s[1].substring(1, s[1].length() - 1));
        String[] se = s[2].split("-");
        return new Token(s[0], JavaToken.Kind.valueOf(kind).toString(), se[0].substring(1, se[0].length() - 1), se[1].substring(1, se[1].length() - 1));
    }

    private void createUIComponents() {
    }
}
