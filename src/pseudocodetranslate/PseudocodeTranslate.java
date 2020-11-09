/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pseudocodetranslate;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Scanner;
import pseudocodetranslate.Control.ctrTranslate;
import pseudocodetranslate.View.viewTranslate;

/**
 *
 * @author sebas
 */
public class PseudocodeTranslate {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        String[] translateCode;
        Scanner reader = new Scanner(System.in);
   
        String opc = reader.nextLine();
        
        //Mostrar Panel del Traductor
        if (opc.contains("help")) {
            System.out.println("Modo de Ejecución:");
            System.out.println("1.Traducir mediante interface Java");
            System.out.println("2.Traducir mediante ventana de comandos CMD");
            System.out.print("Ingrese opción:... ->");  
            opc = reader.nextLine();
        }
        
        //Mostrar Panel del Traductor
        if (opc.contains("1")) {
            viewTranslate v = new viewTranslate();
            v.setVisible(true);
            v.setLocationRelativeTo(null);
        }
        
        //Traducción por ventana de comandos
        if(opc.contains("2")){
            ctrTranslate oTrans = new ctrTranslate();
            translateCode = oTrans.DoTranslateFromConsole();
            if(translateCode == null){
                throw new Exception(oTrans.error);
                //System.out.println(oTrans.error);
            }
            else{
                //linea = new String(read.nextLine().getBytes("ISO-8859-1"), "UTF-8");
                System.out.println(translateCode[0]);
                //System.out.println(translateCode[1]);
                //System.out.println(GetDefaultCharset());
                //System.out.println(translateCode);
                //System.out.println(Charset.defaultCharset());
            }
        }
        // Tradicción sin GUI desde archivo
        if(opc.contains("4")){
            //ctrTranslate oTrans = new ctrTranslate();
            //set path file
            //translateCode = oTrans.DoTranslateFromFile();
            //if(translateCode == null){
                //throw new Exception(oTrans.error);
                //System.out.println(oTrans.error);
            //}
            //else{
                //linea = new String(read.nextLine().getBytes("ISO-8859-1"), "UTF-8");
                //System.out.println(translateCode[0]);
                //System.out.println(translateCode[1]);
                //System.out.println(GetDefaultCharset());
                //System.out.println(translateCode);
                //System.out.println(Charset.defaultCharset());
            //}
            System.out.println("modo no disponible");
        }
        // Get charset
        if(opc.contains("3")){
            System.out.println(GetDefaultCharset());
        }
    }
    
    private static String GetDefaultCharset(){
        String charset = "";
        charset += "Default locale:   " + Locale.getDefault() + "\n";
        charset += "Default Charset:  " + Charset.defaultCharset() + "\n";
        charset += "file.encoding:    " + System.getProperty("file.encoding") + "\n";
        charset += "sun.jnu.encoding: " + System.getProperty("sun.jnu.encoding") + "\n";
        return charset;
    }
}
