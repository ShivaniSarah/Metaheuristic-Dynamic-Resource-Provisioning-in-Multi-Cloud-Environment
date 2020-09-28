/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsample;
import cloudsample.utils.Constants;
import cloudsample.SchedulerFitnessFunction;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class LOA {
private final int[] best=new int[Constants.NO_OF_TASKS]; 

private static SchedulerFitnessFunction ff = new SchedulerFitnessFunction();
    public LOA() {
            

    }
   public int[] run() throws IOException
   {   //for(int i=0;i<30;i++)
        // best[i]=
       
       
       
       String s=null;

       ProcessBuilder pb = new ProcessBuilder("python","C:\\Users\\agras\\Desktop\\cloudProj\\CloudSample\\src\\cloudsample\\newpythonproject1.py",""+(Constants.NO_OF_TASKS),""+(Constants.NO_OF_DATA_CENTERS));
       Process p1=pb.start();
       BufferedReader stdInput = new BufferedReader(new InputStreamReader(p1.getInputStream()));

       BufferedReader stdError = new BufferedReader(new InputStreamReader(p1.getErrorStream()));

       // read the output from the command
       System.out.println("Here is the standard output of the command:\n");
       //System.out.println("tasks= "+Constants.NO_OF_TASKS);
       int k=0;
       while ((s = stdInput.readLine()) != null) 
       {        best[k++]=Integer.parseInt(s);
                System.out.println(s);
       }
       
           
       //read any errors from the attempted command
       System.out.println("Here is the standard error of the command (if any):\n");
       
       while ((s = stdError.readLine()) != null)
       {   
           System.out.println(s);
      }
            
       //p1.destroy();
       
   /*    
       System.out.println("\nBest makespan: " + ff.calcMakespan(best));
         double mspan = ff.calcMakespan(best);
        System.out.println("\nBest Avg Resource Utilization: " + ff.average_resource_utilization(best,mspan));

*/
    return best;

   }
  
   // public void printBestFitness() {
   //     System.out.println("\nBest makespan: " + ff.calcMakespan(bestPositions));
    //}


}

