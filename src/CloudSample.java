package cloudsample;
// Lion Optimisation Algorithm implementation
// Auxiliary script for main LOA engine
// represents a pride
import java.io.*;

/*
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.lang.Math.random;
import static java.lang.Math.round;
import static java.lang.Math.pow;
import static java.util.Collections.shuffle; 
import java.io.*;
import utils.Constants;
import java.lang.Double;
import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static java.lang.Math.PI;
import static java.lang.Math.tan;

*/

public  class CloudSample {  
    /**
public static double HC(double x)
{
    //double sum = 0.0;
    return (pow(10,(1 / (x - 1))) )* (pow(x, 2));
}
public static double SHC(double x,double o)
{   int c = 10;
    return HC(x-o) + c;
}

public  class Lion
{
    
        public  boolean isMale;
        public  boolean isNomad;
        public  double bestVisitedPosition;
        public  double x;
        public  double o;
        public  int huntingGroup;
        public  double bestScoreHistory[]  = new double[Constants.NO_OF_TASKS];      // keep track of best score found so far for tournament
    // fitness value of best visited position       
    public  double getBestVisitedPositionScore()
    {return SHC(bestVisitedPosition,o);
    }
    // fitness value of current position
    public  double getCurrentPositionScore()
    {   return SHC(x,o);
    }
    
}
public class Group {
    public  ArrayList<Lion> lionArrayList = new ArrayList<Lion>();
    public  Lion[] lionArray;
    
}
/* initialises a population of lions based on the parameters specified and partitions them into the pride and nomad structures */
/*
    public static void updateBestScoreList(Group[] prideArray)
    {
        int k=0;
       for(int i=0;i<prideArray.length;i++)
       {  
            for(int j=0;j<prideArray[i].lionArray.length;j++)
            {
                prideArray[i].lionArray[j].bestScoreHistory[k++]=prideArray[i].lionArray[j].getCurrentPositionScore();
            }
        }
       //System.out.println("UPDATE BEST SCORE LIST  "+Arrays.toString(bestScoreHistory);
       //for(int i=0;i<nomadArray.length;i++)
    }

public double getCurrentGlobalBest(Group[] prideArray)
   {
      //double scores [] = new double[Constants.NO_OF_TASKS];
       double minScore=Double.MAX_VALUE;
      for(int i=0;i<prideArray.length;i++)
       {  
            for(int j=0;j<prideArray[i].lionArray.length;j++)
            {
               //scores[i]=prideArray[i].lionArray[j].getCurrentPositionScore();
               if(prideArray[i].lionArray[j].getCurrentPositionScore()<minScore)
                   minScore=prideArray[i].lionArray[j].getCurrentPositionScore();
            }
        }
      return minScore;
   }

public static Group[] moveToSafePlace(Group[] prideArray,int lower_limit,int upper_limit)
{
    
    for(int i=0;i<prideArray.length;i++)
    {
        // the number of lions that have improved in the previous iteration
        int numberImprovedLions =0;        
        int sum=0;
        for(int a=0;a<prideArray.length;a++)
       {  
            for(int b=0;b<prideArray[a].lionArray.length;b++)
            {
                if(prideArray[a].lionArray[b].bestScoreHistory[-1] < prideArray[a].lionArray[b].bestScoreHistory[-2])
                   sum+=1;
         }
       }
            
        // compute tournament size
        int q=(int)(ceil(numberImprovedLions / 2));
        double tournamentsize = max(2, q);

        // best visited positions and their scores in one list
        // bestVisitedPositions = [(lion.bestVisitedPosition, lion.getBestVisitedPositionScore()) for lion in pride.lionArray]
        for(int j=0;j<prideArray[i].lionArray.length;j++)
        {
          // if the female is not hunting
            if(prideArray[i].lionArray[j].huntingGroup == 0 && prideArray[i].lionArray[j].isMale == false)
            {
                // tournament selection
                //tournamentSelection = random.sample(bestVisitedPositions, tournamentsize)
                // winner has lowest fitness value
                double winner=Double.MAX_VALUE;// = min(tournamentSelection,key=lambda item:item[1])[0]
                for(int a=0;a<prideArray.length;a++)
                 {  
                    for(int b=0;b<prideArray[a].lionArray.length;b++)
                      {
               //scores[i]=prideArray[i].lionArray[j].getCurrentPositionScore();
                         if(prideArray[a].lionArray[b].getBestVisitedPositionScore()< winner)
                              winner=prideArray[a].lionArray[b].getBestVisitedPositionScore();
                      }
                  }
                double R1 = winner;
                double startposition = prideArray[i].lionArray[j].x;
                R1 = R1 - startposition;

                // some parameters for moving the female non-hunting lion
                // distance is a percetage of the maximum distance in the search space
                double D = (R1 - prideArray[i].lionArray[j].x)/(upper_limit - lower_limit);

                // create random orthonormal vector to R1
                double R2 = random()*4 +0;
                if(R1 != 0)
                    R2 -= R2/R1;
                else
                    // if R1 is the zero, generate 0 vector with random 1
                    R2 = 1;

                double theta = (random()*PI/3)-PI/6;

              // move the female lion according to the formula provided in the paper
                prideArray[i].lionArray[j].x = prideArray[i].lionArray[j].x +( 2 * D * ((random()*1)+0) * R1) + ((random()*2)-1) * tan(theta) * R2 * D;
            }
  
        }
    }
    return prideArray;
}

public Group[] generateGroups(int pridePop,double sexRate,int prideNo,int lower_limit,int upper_limit,double o)
{
    // setting up gender distribution for lions
    // bit array to determine whether lion is a male
    // eg. [0,1,0,0,0,1] indicates the second and last lion to be males
    // the rest being females

    Integer malePrideIndicies[]=new Integer[pridePop];
    for(int i=0;i<pridePop;i++)                                //very important
        malePrideIndicies[i] = 0; 
    // the number of expected males in the population of prides and nomads
    int noPrideMales = (int)(round(pridePop * (1 - sexRate)));
    // generate bit array with correct no of males
    for(int i=0;i<noPrideMales;i++)
        malePrideIndicies[i] = 1;    
    // mix up the distribution of males a bit
    List<Integer> malePrideIndicieslist = Arrays.asList(malePrideIndicies);
    shuffle(malePrideIndicieslist);
    malePrideIndicieslist.toArray(malePrideIndicies);
    
    // generating lions into the structures
    // init arrays of nomad and pride lions
    Lion prideLionsArray[]=new Lion[pridePop];
    for(int i=0;i<pridePop;i++)
        prideLionsArray[i]=new Lion();
    // init array of prideNo pride groups
    Group prideArray[]=new Group[prideNo];
    for(int i=0;i<prideNo;i++)
        prideArray[i]=new Group();
    // counter to ensure that there is at least one male in each pride (prevents errors)
    int j = 0;
    int prideIndex=0;
    for(int i=0;i<pridePop;i++)
    {
        prideLionsArray[i].isNomad=false;
        prideLionsArray[i].o = o;
        
        // set gender of pride lions
        if(malePrideIndicies[i] == 1)
        {   prideLionsArray[i].isMale = true;
            // ensure each pride has two male lions
            if(j< noPrideMales)
            {  prideIndex = j % 4;
                j += 1;
            }
        }
        else
        {  prideLionsArray[i].isMale = false;
           prideIndex = (int)(random()*prideNo);
        }   
        // initialize lion positions
        prideLionsArray[i].x = (random()*(upper_limit-lower_limit-1))+lower_limit;
        prideLionsArray[i].bestVisitedPosition = prideLionsArray[i].x;
        
        // assigning each pride lion to a pride
        // index of pride to assign lion
        // eg for 4 prides, number is 0,1,2,3
        
        if( j<0 || j>=(noPrideMales) || malePrideIndicies[i] != 1)
        {
          prideIndex = (int)(random()*prideNo);
        }
        
        prideArray[prideIndex].lionArrayList.add(prideLionsArray[i]);
        //System.out.println(prideIndex+" "+prideArray[prideIndex].lionArrayList.size());
    }
    for(int i=0;i<prideNo;i++)
    {
        //System.out.println(prideArray[i].lionArrayList.size());
       prideArray[i].lionArray= new Lion[prideArray[i].lionArrayList.size()]; 
       prideArray[i].lionArray = prideArray[i].lionArrayList.toArray(prideArray[i].lionArray); 
    }
    return prideArray;
}

// females go hunting to explore the search space by attacking hypothetical prey '''
// Opposition-Based-Learning implementation '''
// under step 3

public static int[] hunting(Group[] prideArray,int lower_limit,int upper_limit,double o)
{   int huntingGroup1Fitness;
    int huntingGroup2Fitness;
    int huntingGroup3Fitness;
    int hunterLionNumber;
    int centre,left,right;
    int bestPositions[]=new int[Constants.NO_OF_TASKS];
    int counter=0;
    
    for(int i=0;i<prideArray.length;i++)
    {   // assign lion to a hunting group
         huntingGroup1Fitness=0;
         huntingGroup2Fitness=0;
         huntingGroup3Fitness=0;
         hunterLionNumber = 0; 
        for(int j=0;j<prideArray[i].lionArray.length;j++)
         {  // put lion back into search area
              if( prideArray[i].lionArray[j].x >= upper_limit)
                    prideArray[i].lionArray[j].x= upper_limit-1;
                if(prideArray[i].lionArray[j].x < lower_limit)
                    prideArray[i].lionArray[j].x= lower_limit; 
            // 0 is not in group, 1, 2, 3 correspond to respective hunting groups
            if(prideArray[i].lionArray[j].isMale == true)        // male lions do not hunt
                prideArray[i].lionArray[j].huntingGroup = 0;
            else
                prideArray[i].lionArray[j].huntingGroup =(int)(random()*3)+1;
          }      
        for(int j=0;j<prideArray[i].lionArray.length;j++)
         { if(prideArray[i].lionArray[j].huntingGroup == 1)
               huntingGroup1Fitness += prideArray[i].lionArray[j].getCurrentPositionScore();
         }
        for(int j=0;j<prideArray[i].lionArray.length;j++)
         { if(prideArray[i].lionArray[j].huntingGroup == 2)
               huntingGroup2Fitness += prideArray[i].lionArray[j].getCurrentPositionScore();
         }
        for(int j=0;j<prideArray[i].lionArray.length;j++)
         { if(prideArray[i].lionArray[j].huntingGroup == 3)
               huntingGroup3Fitness += prideArray[i].lionArray[j].getCurrentPositionScore();
         }
         
        // set position of prey to average of hunter positions
        double preyPosition=0.0;     // initialize prey position
        // count the number of hunter lions in the pride
        for(int j=0;j<prideArray[i].lionArray.length;j++)
        {if(prideArray[i].lionArray[j].huntingGroup != 0)
          { preyPosition += prideArray[i].lionArray[j].x;
            hunterLionNumber += 1;
          }
        }
        // get the average position of the hunter lions
        preyPosition /= hunterLionNumber;
        // hunting females all attack the prey
        // first set center group to be the group with max fitness, left and right groups are two lower fitness groups
        if(huntingGroup1Fitness>=huntingGroup2Fitness && huntingGroup1Fitness>=huntingGroup3Fitness)
        {
            centre=1;
            left=2;
            right=3;
        }
        else if(huntingGroup2Fitness>=huntingGroup1Fitness && huntingGroup2Fitness>=huntingGroup3Fitness)
        {
            centre=2;
            left=1;
            right=3;
        }
        else
        {
            centre=3;
            left=1;
            right=2;
        }
        
        for(int j=0;j<prideArray[i].lionArray.length;j++)
         {  
            // Change of lion position depends if they are in left or right group
            if(prideArray[i].lionArray[j].huntingGroup == centre)
            {  // move the lion according to strategy provided by the paper
                if(SHC(preyPosition,o) > prideArray[i].lionArray[j].getCurrentPositionScore())
                    prideArray[i].lionArray[j].x = (random()*(preyPosition-prideArray[i].lionArray[j].x))+prideArray[i].lionArray[j].x;
                if(SHC(preyPosition,o) < prideArray[i].lionArray[j].getCurrentPositionScore())
                    prideArray[i].lionArray[j].x = (random()*(prideArray[i].lionArray[j].x-preyPosition))+preyPosition;
            }
            if((prideArray[i].lionArray[j].huntingGroup == right)||(prideArray[i].lionArray[j].huntingGroup == left) )
            {   // move lion according to strategy provided by the paper
                if(SHC(2 * preyPosition -prideArray[i].lionArray[j].x,o) < SHC(preyPosition,o))
                    prideArray[i].lionArray[j].x = (random()*preyPosition)+(2 * preyPosition -prideArray[i].lionArray[j].x);

                if(SHC(2 * preyPosition - prideArray[i].lionArray[j].x,o) > SHC(preyPosition,o))
                    prideArray[i].lionArray[j].x =(random()*(2 * preyPosition -prideArray[i].lionArray[j].x)) + preyPosition;
            }   
            // If lion's position is improved, update it's best visited position and score
            if(prideArray[i].lionArray[j].getBestVisitedPositionScore() > prideArray[i].lionArray[j].getCurrentPositionScore())
            {   // get the improvement percentage
                double improvement_percentage = prideArray[i].lionArray[j].getBestVisitedPositionScore() / prideArray[i].lionArray[j].getCurrentPositionScore();
                prideArray[i].lionArray[j].bestVisitedPosition = prideArray[i].lionArray[j].x;
                // change the position of the prey according to Opposition Based Learning
                preyPosition = preyPosition +( random()*improvement_percentage * (preyPosition - prideArray[i].lionArray[j].x) );
                
            }
            
            
                bestPositions[counter++]=(int)(prideArray[i].lionArray[j].x);
            
         }
        for(int j=0;j<bestPositions.length;j++)
        {  if(bestPositions[j]>=upper_limit)
            bestPositions[j]=upper_limit-1;
            if(bestPositions[j]<lower_limit)
            bestPositions[j]=lower_limit;
           
         }
    
    }
    return bestPositions;
   }

*/

//MAIN FUNCTION
//MAIN FUNCTION
//MAIN FUNCTION



public static void main(String[] args) throws IOException
{
/* initialises a population of lions based on the parameters specified and partitions them into the pride and nomad structures */
   
/*
    int prideNo = 4   ;
    //double percentNomad = 0.2;
    double sexRate = 0.8;
    //int nPop = POPULATION_SIZE = NO_OF_TASKS /( 1-percentNomad ) ;
    int pridePop=Constants.NO_OF_TASKS;  
    int lower_limit = 0;
    int upper_limit = Constants.NO_OF_DATA_CENTERS;
    double o = 0.0;
          
    // initialise the populations into structures of prides and nomads
    
    CloudSample loa=new CloudSample();
    Group[] prideArray= loa.generateGroups(pridePop, sexRate, prideNo,lower_limit,upper_limit, o);
    
    for(int i=0;i<prideArray.length;i++)
    {   System.out.println("Group No. : "+i);
        for(int j=0;j<prideArray[i].lionArray.length;j++)
    {
        System.out.println("Lion: ");
        System.out.println(prideArray[i].lionArray[j].isMale);
        System.out.println(prideArray[i].lionArray[j].isNomad);
        System.out.println(prideArray[i].lionArray[j].bestVisitedPosition);
        System.out.println(prideArray[i].lionArray[j].x);
        System.out.println(prideArray[i].lionArray[j].o);
    }
        }
    
    int[] bestPositions=hunting(prideArray,lower_limit,upper_limit,o);
    for(int i=0;i<Constants.NO_OF_TASKS;i++)
       System.out.println(bestPositions[i]);
    for(int i=0;i<prideArray.length;i++)
    {   System.out.println("Group No. : "+i);
        for(int j=0;j<prideArray[i].lionArray.length;j++)
    {   System.out.println(prideArray[i].lionArray[j].x);
    
    }
    }
   */
        
String s=null;
//Process p1 = Runtime.getRuntime().exec("python C:\\Users\\agras\\Desktop\\cloudProj\\CloudSample\\src\\cloudsample\\pp.py");
ProcessBuilder pb = new ProcessBuilder("python","C:\\Users\\agras\\Desktop\\cloudProj\\CloudSample\\src\\cloudsample\\newpythonproject1.py");
Process p1=pb.start();
BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(p1.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(p1.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            
            //read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            
            p1.destroy();
           
           
    }//main
    
}//class cloudsample
