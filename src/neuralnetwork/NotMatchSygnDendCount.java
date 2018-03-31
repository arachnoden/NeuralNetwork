/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetwork;

/**
 *
 * @author user
 */
public class NotMatchSygnDendCount extends Error{

    public NotMatchSygnDendCount() {
        super("Не співпадає кількість вхідних сигналів і кількість дендритів");
    }
    
}
