/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exampleapi;

/**
 *
 * @author jorge
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.table.DefaultTableModel;

import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Util;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.variableElimination.VariableElimination;
import org.openmarkov.io.probmodel.PGMXReader;

/**
 *
 * @author Henry Paz
 */
public class ExampleAPI {

    ProbNode probNode;
    private Object[] dat;
//    public static void main(String[] args) {
//        new ExampleAPI();
//        
//    }
    // Constants
    final private String bayesNetworkName = "examenrb.pgmx";
    final public static List<String> li = new ArrayList<>();
    // Constructor

    public ExampleAPI() {
    }

    public ProbNode getProbNode() {
        return probNode;
    }

    public void setProbNode(ProbNode probNode) {
        this.probNode = probNode;
    }

    public Object[] getDat() {
        return dat;
    }

    public void setDat(Object[] dat) {
        this.dat = dat;
    }

    @SuppressWarnings("empty-statement")
    public List<ProbNode> obtenerdatos() {
        List<ProbNode> listPro = null;

        try {
            // Open the file containing the network
            InputStream file = new FileInputStream(new File("C:\\Users\\usuario\\Documents\\Andrea\\Dropbox"
                    + "\\Decimo Modulo\\Inteligencia Artificial\\TAREAS\\examenRB\\examenRB.pgmx"));

            // Load the Bayesian network
            PGMXReader pgmxReader = new PGMXReader();
            ProbNet probNet = pgmxReader.loadProbNet(file, bayesNetworkName).getProbNet();

            System.out.println("nodos: " + probNet.getNumNodes());
            // Create an evidence case
            // (An evidence case is composed of a set of findings)

            listPro = probNet.getProbNodes();

            for (int i = 0; i < listPro.size(); i++) {
                probNode = listPro.get(i);
                System.out.println("NOMBRE " + probNode.getName());
                System.out.println("PROBNET " + probNode.getProbNet());
                System.out.println("RELEVANCE" + probNode.getRelevance());
                System.out.println("TIPO DE NODO " + probNode.getNodeType().toString());
                System.out.println("UTYLITY " + probNode.getUtilityFunction());
                //listnodo.add(new nodo(probNode.getName(), probNode.getProbNet()+"",probNode.getRelevance()+"", probNode.getNodeType().toString(), probNode.getUtilityFunction()+""));
            }
            
            li.add(String.valueOf(probNode.getProbNet()));
            
            EvidenceCase evidence = new EvidenceCase();
            evidence.addFinding(probNet, "Resultado", "aprueba");
            InferenceAlgorithm variableElimination = new VariableElimination(probNet);
            variableElimination.setPreResolutionEvidence(evidence);
            Variable disease1 = probNet.getVariable("Resultado");
            //Variable disease2 = probNet.getVariable("Disease 2");
            ArrayList<Variable> variablesOfInterest = new ArrayList<Variable>();
            variablesOfInterest.add(disease1);
//          Compute the posterior probabilities
            HashMap<Variable, TablePotential> posteriorProbabilities =
                    variableElimination.getProbsAndUtilities();
//          Print the posterior probabilities on the standard output
            printResults(evidence, variablesOfInterest, posteriorProbabilities);
//          evidence.addFinding(probNet, "Sign", "present");
            posteriorProbabilities = variableElimination.getProbsAndUtilities(variablesOfInterest);
            printResults(evidence, variablesOfInterest, posteriorProbabilities);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return listPro;

    }

    /**
     * Print the posterior probabilities of the variables of interest on the
     * standard output
     *
     * @param evidence. <code>EvidenceCase</code> The set of observed findings
     * @param variablesOfInterest. <code>ArrayList</code> of
     * <code>Variable</code> The variables whoseposterior probability we are
     * interested in
     * @param posteriorProbabilities. <code>HashMap</code>. Each
     * <code>Variable</code> is mapped onto a <code>TablePotential</code>
     * containing its posterior probability
     */
    public void printResults(EvidenceCase evidence, ArrayList<Variable> variablesOfInterest,
            HashMap<Variable, TablePotential> posteriorProbabilities) {
        // Print the findings
        System.out.println("Evidence:");
        for (Finding finding : evidence.getFindings()) {
            li.add(String.valueOf(finding.getVariable()));
            li.add(finding.getState());
            System.out.print("  " + finding.getVariable() + ": ");
            System.out.println(finding.getState());
        }
        // Print the posterior probability of the state "present" of each variable of interest
        System.out.println("Posterior probabilities: ");
        for (Variable variable : variablesOfInterest) {
            double value;
            TablePotential posteriorProbabilitiesPotential = posteriorProbabilities.get(variable);
            System.out.println("PROBABILIDAD" + posteriorProbabilities.values());
            System.out.print("  " + variable + ": ");
            //li.add(String.valueOf(posteriorProbabilities.values()));
            //li.add(String.valueOf(variable));
            int stateIndex = -1;
            try {
                stateIndex = variable.getStateIndex("aprueba");
                value = posteriorProbabilitiesPotential.values[stateIndex];
                System.out.println(Util.roundedString(value, "0.001"));
                li.add(String.valueOf(Util.roundedString(value, "0.001")));
            } catch (InvalidStateException e) {
                System.err.println("State \"present\" not found for variable \""
                        + variable.getName() + "\".");
                e.printStackTrace();
            }
        }
        System.out.println();
    }
}
