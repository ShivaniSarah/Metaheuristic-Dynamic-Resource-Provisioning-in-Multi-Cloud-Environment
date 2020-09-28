/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsample;

import static java.lang.Math.pow;
import cloudsample.utils.Constants;

/**
 *
 * @author agras
 */

public  class Lion
{
    
        public  boolean isMale;
        public  boolean isNomad;
        public  double bestVisitedPosition;
        public  double x;
        public  double o;
        public  int huntingGroup;
        //public  double bestScoreHistory[]  = new double[Constants.NO_OF_TASKS];      // keep track of best score found so far for tournament

        public double HC(double x)
{
    //double sum = 0.0;
    return (pow(10,(1 / (x - 1))) )* (pow(x, 2));
}
public  double SHC(double x,double o)
{   int c = 10;
    return HC(x-o) + c;
}
        

    // fitness value of best visited position
        
    public  double getBestVisitedPositionScore()
    {return SHC(bestVisitedPosition,o);
    }
    // fitness value of current position
    public  double getCurrentPositionScore()
    {   return SHC(x,o);
    }
}
