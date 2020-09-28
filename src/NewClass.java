/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsample;

/**
 *
 * @author agras
 */
public class NewClass {
    public static void main(String args[])
    {  
        java.util.List<Integer> arr=new java.util.ArrayList<Integer>();
        for(int i=0;i<4;i++)
        {   arr.add(new Integer(1));
            
        }
        for(int i=0;i<3;i++)
        {   arr.add(new Integer(2));
            
        }
        for(int i=0;i<2;i++)
        {   arr.add(new Integer(3));
            
        }
        
        java.util.Collections.shuffle(arr);
        
        
        for(int i=0;i<9;i++)
            System.out.println(arr.get(i).intValue());
        
        
        
        
        
    }
            
            
            
            }
