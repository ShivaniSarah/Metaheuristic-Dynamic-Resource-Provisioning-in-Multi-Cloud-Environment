/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsample;

import cloudsample.utils.Constants;
import cloudsample.utils.GenerateMatrices;

public class SchedulerFitnessFunction{
    private static double[][] execMatrix, commMatrix;

    SchedulerFitnessFunction() {
        
        commMatrix = GenerateMatrices.getCommMatrix();
        execMatrix = GenerateMatrices.getExecMatrix();
    }
    

    

   /* @Override
    
    public double evaluate(double[] Bestposition) {
        double alpha = 0.3;
        return alpha * calcTotalTime(Bestposition) + (1 - alpha) * calcMakespan(Bestposition);
//        return calcMakespan(position);
    }*/

    public double calcTotalTime(int[] position) {
        double totalCost = 0;
        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int dcId =  position[i];
            totalCost += execMatrix[i][dcId] + commMatrix[i][dcId];
        }
        return totalCost;
    }
    double makespan = 0;
    
    double calcMakespan(int[] position) {
        double makespan = 0;
        double[] dcWorkingTime = new double[Constants.NO_OF_DATA_CENTERS];

        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int dcId = (int) position[i];
            if(dcWorkingTime[dcId] != 0) --dcWorkingTime[dcId];
            dcWorkingTime[dcId] =dcWorkingTime[dcId]+ (execMatrix[i][dcId] + commMatrix[i][dcId]);
            makespan = Math.max(makespan, dcWorkingTime[dcId]);
        }
        return makespan;
    }
    public double average_resource_utilization(int[] position,double mspan) {
    	double totalexectime=0;
    	double avg_utilization=0;
    	for(int i=0;i<Constants.NO_OF_TASKS;i++)
    	{
                //System.out.println("Position"+position[i]);
    		
    		int dcId = (int) position[i];
    		totalexectime +=execMatrix[i][dcId];
                //System.out.println("Total  -"+totalexectime);
    	}
    	//System.out.println("Calccccc "+makespan*Constants.NO_OF_TASKS);
    	avg_utilization = totalexectime/mspan;
	return avg_utilization;
    	
    }
/*
    @Override
    
    public double evaluate(double[] position) {
        double alpha = 0.3;
        return alpha * calcTotalTime(position) + (1 - alpha) * calcMakespan(position);
//        return calcMakespan(position);
    }
*/
    
  
}
