package com.cheesecake;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.javaparser.JavaToken;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.CodeGenerationUtils;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * 
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        File file = CodeGenerationUtils.mavenModuleRoot(App.class).resolve("src/main/tests/PruebaVector11.java").toFile();

        // Set up a minimal type solver that only looks at the classes used to run this
        // sample.
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        // Parse some code
        CompilationUnit cu;
        try {
            cu = StaticJavaParser.parse(file);

            cu.getTokenRange().get().forEach(t -> {
                // System.out.println(t.toString());
                // System.out.print(t.getText() + " - ");
                // System.out.println(JavaToken.Kind.valueOf(t.getKind()));

                if (t.getKind() > 3) {
                    System.out.println(printToken(t));
                }
            });
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String printToken(JavaToken t) {
        String[] s = t.toString().split("\\s{2,}");
        String ret = s[0];
        int kind = Integer.parseInt(s[1].substring(1, s[1].length() - 1));
        ret += "\t" + JavaToken.Kind.valueOf(kind).toString();
        ret += "\t" + s[2];
        return ret;
    }
}
