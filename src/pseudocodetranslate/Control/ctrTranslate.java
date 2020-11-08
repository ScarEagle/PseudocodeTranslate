/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pseudocodetranslate.Control;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author sebas
 */
public class ctrTranslate {
    
    public String error;
    private ctrRulesTranslate oRules;
    private ctrCodeToTranslate oCode;
    private List<String> newCode;
    private List<String[]> savedliteralText;
    private List<String[]> savedVariableType;
    private int countTranlated;
    private boolean flagClase;
    
    public ctrTranslate(){
        oRules = new ctrRulesTranslate();
        oCode = new ctrCodeToTranslate();
        newCode = new ArrayList<>();
        savedliteralText = new ArrayList<>();
        savedVariableType = new ArrayList<>();
    }
    
    public ctrTranslate(String pathFileToTranslate){
        oRules = new ctrRulesTranslate();
        oCode = new ctrCodeToTranslate(pathFileToTranslate);
        newCode = new ArrayList<>();
        savedliteralText = new ArrayList<>();
        savedVariableType = new ArrayList<>();
    }
    
    public String[] DoTranslateFromConsole(){
        if(!oCode.GetLinesConsole()){
            error = oCode.error;
            return null;
        }
        return Translate();
    }
    
    public String[] DoTranslateFromFile(){
        oCode.GetLinesFile();
        return Translate();
    }
    
    
    public String[] Translate(){
        oRules.ReadFile();
        newCode = oCode.originalCode;
        SplitLiteralText();
        ReplaceKeyWords();
        SetSemicolon();
        RestoreLiteralText();
        AddImportLibraries();
        if (countTranlated > 5) {
            return GetCodeTranslate();
        }else{
            //error = "Code entered not is pseudocode";
            error = "Código ingresado no es pseudocódigo";
            return null;
        }
    }
    
    private void AddImportLibraries(){
        List<String> temp = new ArrayList<>();
        temp.add("import java.util.Scanner;");
        temp.addAll(newCode);
        newCode = temp;
    }
    
    private void RestoreLiteralText(){
        for (String[] slt : savedliteralText) {
            for (int i = 0; i < newCode.size(); i++) {
                if (newCode.get(i).contains(slt[1])) {
                    newCode.set(i, newCode.get(i).replace(slt[1], slt[0]));
                }
            }
        }
    }
    
    private void SetSemicolon(){
        String code;
        List<String[]> pat;
        for (int i = 0; i < newCode.size(); i++) {
            //replace lines with comments
            newCode.set(i, newCode.get(i).replaceAll("\\/.+\\/", ""));
            newCode.set(i, newCode.get(i).replaceAll("\\/*\\/.+", ""));
            newCode.set(i, newCode.get(i).replaceAll(".+\\*\\/", ""));
            code = newCode.get(i).trim();
            if (!code.isEmpty()) {
                if (!code.endsWith(":") && !code.endsWith("{") && !code.endsWith("}") && !code.endsWith(";")) {
                    newCode.set(i, newCode.get(i).concat(";"));
                }
            }
        }
    }
    
    private void ReplaceKeyWords(){
        countTranlated = 0;
        int iRule = 0;
        for (int i = 0; i < newCode.size(); i++) {
            for (String rule[] : oRules.rule) {
                if (newCode.get(i).contains(rule[1])) {
                    String lineCode = newCode.get(i);
                    if (lineCode.contains("finclase") || lineCode.contains("principal") || lineCode.contains("metodo")) {
                        flagClase = false;
                    }
                    if (lineCode.contains("finmetodo") || lineCode.contains("finprincipal") || lineCode.contains("clase")) {
                        flagClase = true;
                    }
                    switch (rule[0]) {
                        case "0":
                            countTranlated++;
                            newCode.set(i, newCode.get(i).replace(rule[1], rule[2]));
                            break;
                        case "1":
                            countTranlated++;
                            newCode.set(i, newCode.get(i).replace(rule[1], rule[2]));
                            newCode.set(i, ReplaceKeyWordWithPattern(rule[3], rule[4], newCode.get(i)));
                            break;
                        case "2":
                            countTranlated++;
                            newCode.set(i, ReplaceKeyWordWithPattern(rule[2], rule[3], rule[4], rule[5], newCode.get(i)));
                            break;
                        case "3":
                            countTranlated++;
                            newCode.set(i, newCode.get(i).replaceAll(rule[2], rule[3]));
                            break;
                        case "4":
                            countTranlated++;
                            String staticT = "";
                            if (flagClase) {
                                staticT = "static ";
                            }
                            newCode.set(i, newCode.get(i).replace(rule[1], staticT + rule[2]));
                            GetVariables(rule[2], rule[3], rule[4], newCode.get(i));
                            if (NeedNestedRule(oRules.getNestedRule(iRule), newCode.get(i))) {
                                newCode.set(i, ReplaceKeyWordWithPattern(rule[3], rule[5], newCode.get(i)));
                            }
                            break;
                        case "5":
                            countTranlated++;
                            if (!savedVariableType.isEmpty()) {
                                newCode.set(i, ReplaceKeyWordWithPatternAndMultipleRules(rule[1], rule[2], rule[3], oRules.getNestedRule(iRule), newCode.get(i)));
                                break;
                            }
                    }
                }
                iRule++;
            }
            iRule = 0;
        }
    }
    
    private boolean NeedNestedRule(List<String[]> list, String code){
        boolean flag = false;
        for (String[] l : list) {
            if (l[1].contentEquals("<>")) {
                if (code.contains(l[2])) {
                    return false;
                }
                flag = true;
            }
        }
        return flag;
    }
    
    private void GetVariables(String type, String pattern, String token, String lineText){
        String[] nameVariables;
        List<String[]> variables = UsePattern(pattern, lineText);
        String tempVariables;
        String typeVariables = "normal";
        if (lineText.contains("[") && !lineText.contains("LiteralString")) {
            typeVariables = "arreglo";
        }
        if (variables != null && variables.size() > 0) {
            for (int i = 0; i < variables.size(); i++) {
                tempVariables = variables.get(i)[0].replace(type, "").trim();
                nameVariables = tempVariables.split(token);
                if (nameVariables.length > 0) {
                    for (String nameVariable : nameVariables) {
                        savedVariableType.add( new String[]{ type, nameVariable.trim().replaceAll("(\\[\\])", ""), typeVariables, "0"});
                    }
                }
            }
            CleanVariables();
        }
    }
    
    private void CleanVariables(){
        String nameVar;
        for (String[] var : savedVariableType) {
            if (var[1].contains("=")) {
                nameVar = var[1].substring(0, var[1].indexOf("=")).trim();
                var[1] = nameVar;
            }
        }
    }
    
    private String ReplaceKeyWordWithPattern(String pattern, String wordToReplace, String wordSpecified, String finalWordKeyReplace, String lineText) {
        List<String[]> saveText;
        String newWord, newLineText;
        String startTempLine, endTempLine;
        int difLength = 0;
        int lengthLine;
        newLineText = lineText;
        saveText = UsePattern(pattern, newLineText);
        if (saveText != null && saveText.size() > 0) {
            for (int i = 0; i < saveText.size(); i++) {
                newWord = finalWordKeyReplace.replace(".?", saveText.get(i)[0].replaceAll(wordToReplace, wordSpecified));
                
                lengthLine = newLineText.length();
                startTempLine = newLineText.substring(0, Integer.parseInt(saveText.get(i)[1])+difLength );
                endTempLine = newLineText.substring(Integer.parseInt(saveText.get(i)[2])+difLength, lengthLine);
                
                newLineText = startTempLine + newWord + endTempLine;
                difLength += newLineText.length() - lengthLine;
            }
        }
        return newLineText;
    }
    
    private String ReplaceKeyWordWithPattern(String pattern, String wordKeyReplace, String lineText){
        List<String[]> saveText;
        String newWord, newLineText;
        String startTempLine, endTempLine;
        int difLength = 0;
        int lengthLine;
        newLineText = lineText;
        saveText = UsePattern(pattern, newLineText);
        if (saveText != null && saveText.size() > 0) {
            for (int i = 0; i < saveText.size(); i++) {
                newWord = wordKeyReplace.replace(".?", saveText.get(i)[0]);
                
                lengthLine = newLineText.length();
                startTempLine = newLineText.substring(0, Integer.parseInt(saveText.get(i)[1])+difLength );
                endTempLine = newLineText.substring(Integer.parseInt(saveText.get(i)[2])+difLength, lengthLine);
                
                newLineText = startTempLine + newWord + endTempLine;
                difLength += newLineText.length() - lengthLine;
            }
        }
        return newLineText;
    }
    
    private String ReplaceKeyWordWithPatternAndMultipleRules(String wordToReplace, String wordSpecified, String pattern, List<String[]> tokens, String lineText){
        List<String[]> saveText;
        String[] variable;
        String newWord, newText, newLineText;
        String startTempLine, endTempLine;
        int difLength = 0;
        int lengthLine;
        newLineText = lineText;
        saveText = UsePattern(pattern, newLineText);
        if (saveText != null && saveText.size() > 0) {
            for (int i = 0; i < saveText.size(); i++) {
                newWord = saveText.get(i)[0].replace(wordToReplace, wordSpecified);
                
                newText = saveText.get(i)[0];
                variable = GetVariable(newWord);
                if (variable.length > 0) {
                    for (String[] token : tokens) {
                        if (token[1].contentEquals(variable[0])) {
                            newText = variable[1] + token[2];
                            break;
                        }
                        if (token[1].contentEquals(variable[2])) {
                            newText = variable[1] + token[2];
                            break;
                        }
                    }
                }
                
                lengthLine = newLineText.length();
                startTempLine = newLineText.substring(0, Integer.parseInt(saveText.get(i)[1])+difLength );
                endTempLine = newLineText.substring(Integer.parseInt(saveText.get(i)[2])+difLength, lengthLine);
                
                newLineText = startTempLine + newText + endTempLine;
                difLength += newLineText.length() - lengthLine;
            }
        }
        return newLineText;
    }
    
    private String[] GetVariable(String name){
        String name2 = "";
        for (String[] var : savedVariableType) {
            if (var[1].contentEquals(name)) {
                var[3] = "1";
                return var;
            }
            if (var[2].equals("arreglo")) {
                name2 = name.substring(0, name.indexOf("["));
                if (var[1].contentEquals(name2)) {
                    var[3] = "1";
                    var[1] = name;
                    return var;
                }
            }
        }
        return new String[0];
    }
    
    private void SplitLiteralText(){
        List<String[]> saveText;
        String line, key, newLine;
        //String pattern = "(['\"].*?['\"])";
        String pattern = "([\"].*?[\"])";
        for (int i=0; i < newCode.size(); i++) {
            line = newCode.get(i);
            saveText = UsePattern(pattern, line);
            if (saveText != null && saveText.size() > 0) {
                for (int j = 0; j < saveText.size(); j++) {
                    key = "¿LiteralString[" + i + "-" + j + "]?";
                    savedliteralText.add( new String[]{ saveText.get(j)[0], key});
                    //newLine = line.replaceFirst(saveText.get(j)[0], key);
                    newLine = Pattern.compile(pattern).matcher(line).replaceFirst(key);
                    newCode.set(i, newLine);
                    line = newLine;
                }
            }
        }
    }
        
    private List<String[]> UsePattern(String pattern, String text){
        List<String[]> listMatch = new ArrayList<>();
        Pattern patron = Pattern.compile(pattern);
        Matcher matcher = patron.matcher(text);
        while (matcher.find()) {
            listMatch.add(
                new String[]{
                    matcher.group(), 
                    Integer.toString(matcher.start()), 
                    Integer.toString(matcher.end())
                }
            );
        }
        if(listMatch.size()>0){
            return listMatch;
        }
        else{
            return null;
        }
    }
    
    private String[] GetCodeTranslate(){
        String[] resp = {"",""};
        for (String code : newCode) {
           resp[0]+=code + "\n";
        }
        for (String code : oCode.originalCode) {
           resp[1]+=code + "\n";
        }
        
        return resp;
    }
}
