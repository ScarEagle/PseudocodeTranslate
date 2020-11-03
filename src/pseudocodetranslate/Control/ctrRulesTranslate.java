/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pseudocodetranslate.Control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author sebas
 */
public class ctrRulesTranslate {
    public String pathFile;
    public List<String[]> rule;
    private List<String[]> nestedRule;

    public List<String[]> getNestedRule(int i) {
        List<String[]> list = new ArrayList<>();
        for (int j = 0; j < nestedRule.size(); j++) {
            if (nestedRule.get(j)[0].contentEquals(Integer.toString(i))) {
                list.add(nestedRule.get(j));
            }
        }
        return list;
    }
    
    public String getPathFile() {
        return pathFile;
    }
    
    public ctrRulesTranslate(){
        this.pathFile = "tabla/TablaConversion.txt";
        rule = new ArrayList<>();
        nestedRule = new ArrayList<>();
    }
    
    public void ReadFile(){
        File oFile = new File(pathFile);
        if (!oFile.exists()) {
            JOptionPane.showMessageDialog(null, "Error tabla de conversión no existe en la ruta específicada", "Alerta", JOptionPane.ERROR_MESSAGE);
        }
        else{
            try {
                InputStreamReader file = new InputStreamReader(new FileInputStream(pathFile),"UTF-8");
                Scanner read = new Scanner(file);
                String linea;
                //String telefono1= request.getParameter("telefono").toString().toUpperCase();
                //String telefono = new String(telefono1.getBytes("ISO-8859-1"), "UTF-8");
                while (read.hasNextLine()) {
                    linea = read.nextLine();
                    rule.add(linea.split("\";\""));
                }
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                JOptionPane.showMessageDialog(null, "Error en el proceso de lectura de la tabla de conversión", "Alerta", JOptionPane.ERROR_MESSAGE);
            }
            ClearText();
            GetMultiRules();
        }
    }
    
    private void GetMultiRules(){
        String temp;
        String[] temp2, temp3;
        for (int i = 0; i < rule.size(); i++) {
            if (rule.get(i)[0].contentEquals("5")) {
                if (rule.get(i)[4].startsWith("{") && rule.get(i)[4].endsWith("}")) {
                    temp = rule.get(i)[4].replace("{", "");
                    temp = temp.replace("}", "");
                    temp2 = temp.split(",");
                    for (int j = 0; j < temp2.length; j++) {
                        temp3 = temp2[j].split(":");
                        nestedRule.add(new String[]{ Integer.toString(i), temp3[0], temp3[1]});
                    }
                }
            }
            if (rule.get(i)[0].contentEquals("4")) {
                if (rule.get(i)[6].startsWith("{") && rule.get(i)[6].endsWith("}")) {
                    temp = rule.get(i)[6].replace("{", "");
                    temp = temp.replace("}", "");
                    temp2 = temp.split(",");
                    for (int j = 0; j < temp2.length; j++) {
                        temp3 = temp2[j].split(":");
                        nestedRule.add(new String[]{ Integer.toString(i), temp3[0], temp3[1]});
                    }
                }
            }
        }
    }
    
    private void ClearText(){
        String[] r;
        for (int i = 0; i < rule.size(); i++) {
            r = rule.get(i);
            for (int j = 0; j < r.length; j++) {
                rule.get(i)[j] = r[j].replace("\"", "");
            }
        }
    }
}
