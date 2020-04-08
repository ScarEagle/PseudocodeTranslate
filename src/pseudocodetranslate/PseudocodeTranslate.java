/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pseudocodetranslate;

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
     */
    public static void main(String[] args) {
        String translateCode;
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
                System.out.println(oTrans.error);
            }
            else{
                System.out.println(translateCode);
            }
        }
    }
    
}
