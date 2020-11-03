
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pseudocodetranslate.Control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author sebas
 */
public class ctrCodeToTranslate {
    
    public String pathFile;
    public List<String> originalCode;
    public String[] separator;
    public String error;

    public String getPathFile() {
        return pathFile;
    }
    
    public ctrCodeToTranslate(){
        originalCode = new ArrayList<>();
    }
    
    public ctrCodeToTranslate(String file){
        this.pathFile = file;
        originalCode = new ArrayList<>();
    }
    
    public boolean GetLinesConsole(){       
        if (originalCode.isEmpty()) {
            return ReadFromConsole();
        }
        return false;
    }
    
    public String GetLinesFile(){       
        if (originalCode.isEmpty()) {
            if(ReadFromFile()){
                return GetOriginalCode();
            }
        }
        return null;
    }
    
    private boolean ReadFromConsole(){
        //https://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html
        Scanner read = new Scanner(System.in);
        //Scanner read = new Scanner(System.in, "IBM850");        
        //Scanner read = new Scanner(System.in, "Cp1252");
        //Scanner read = new Scanner(System.in, "US-ASCII");
        String linea;
        boolean flag = true;

        while (flag) {
            //try {
                //linea = System.console().readLine();
                linea = read.nextLine();
                //linea = new String(read.nextLine().getBytes(), "UTF-8");
                if (linea.endsWith("**logicodefinlectura**")) {
                flag = false;
                } else {
                    originalCode.add(linea);
                }
            /*} catch (UnsupportedEncodingException ex) {
                //Logger.getLogger(ctrCodeToTranslate.class.getName()).log(Level.SEVERE, null, ex);
                error = ex.getMessage();
                return false;
            }*/
        }
        return SetParameters();
    }
    
    private boolean ReadFromFile(){
        File oFile = new File(pathFile);
        if (!oFile.exists()) {
            error = "Error archivo a traducir no existe";
            return false;
        }
        else{
            try {
                FileReader file = new FileReader(pathFile);
                Scanner read = new Scanner(file);
                String linea;
                while (read.hasNextLine()) {
                    linea = read.nextLine();
                    //linea = new String(read.nextLine().getBytes("ISO-8859-1"), "UTF-8");

                    originalCode.add(linea);
                }
                
            } catch (FileNotFoundException e) {
                error = "Error en el proceso de lectura del archivo a traducir";
                return false;
            }/* catch (UnsupportedEncodingException ex) {
                //Logger.getLogger(ctrCodeToTranslate.class.getName()).log(Level.SEVERE, null, ex);
                error = ex.getMessage();
                return false;
            }*/
            return SetParameters();
        }
    }
    
    private boolean SetParameters(){
        if (!CheckSeparator()) {
            return false;
        }
        BreakLines();
        CleanEmptyIndex();
        return true;
    }
    
    private String GetOriginalCode(){
        String info = "";
        for (String line : originalCode) {
            info += line + "\n";
        }
        return info;
    }
    
    private boolean CheckSeparator(){
        separator = new String[]{"", ""};
        if (originalCode.size() > 0) {
            if (originalCode.size() == 1) {
                if (originalCode.get(0).contains("\t")) {
                    separator[1] = "\\t";
                }
                else{
                    error = "El código no posee separadores de línea válidos.\nSolo se admiten los siguientes:\n1. Salto de línea (\\n)\n2. Tabulador (\\t)";
                    return false;
                }
            }
            else{
                separator[0] = "\\n";
            }
            return true;
        }else{
            error = "No se logró extrer información";
            return false;
        }
    }
    
    private void BreakLines(){
        String nestedLines[];
        List<String> splitCode = new ArrayList<>();
        
        if (!separator[1].isEmpty()) {
            for (String lineCode : originalCode) {
                nestedLines = lineCode.split(separator[1]);
                for (String nestedLine : nestedLines) {
                    splitCode.add(nestedLine + separator[0]);
                }
            }
        }
        if (splitCode.size() > 0) {
            originalCode = splitCode;
        }
    }
    
    private void CleanEmptyIndex(){
        String[] tempSplit;
        List<String> temp = new ArrayList<>();
        for (String code : originalCode) {
            /*if (!code.isEmpty() || code.contentEquals("\\t")) {
                tempSplit = code.split("\\t");
                if (tempSplit.length > 0) {
                    temp.add(code);
                }
            }*/
            if (code.contentEquals("\\t")) {
                tempSplit = code.split("\\t");
                if (tempSplit.length > 0) {
                    temp.add(code);
                }
            }else{
                //quitar espacios al final
                temp.add(code.replaceAll("\\s*$",""));
            }
        }
        originalCode = temp;
    }
}
