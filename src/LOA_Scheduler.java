/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudsample;
import cloudsample.LOA;
import cloudsample.LOADatacenterBroker;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import cloudsample.utils.Constants;
import cloudsample.utils.DatacenterCreator;
import cloudsample.utils.GenerateMatrices;

import java.text.DecimalFormat;
import java.util.*;
import java.io.IOException;
 

public class LOA_Scheduler {

    public static List<Cloudlet> cloudletList;
    public static List<Cloudlet> newList;
    public static List<Vm> vmList;
    public static Datacenter[] datacenter;
    public static LOA LOASchedularInstance;
    public static int mapping[];
    public static double[][] commMatrix;
    public static double[][] execMatrix;
    public static int no_of_vms;
    public static double mxFinishTime,maxActualTime,cost,avg,rut;
    
    public static List<Vm> createVM(int userId, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<Vm> list = new LinkedList<Vm>();

        //VM Parameters
        long size = Constants.VM_SIZE; //image size (MB)
        int ram = Constants.VM_RAM; //vm memory (MB)
        int mips = Constants.VM_MIPS;
        long bw = Constants.VM_BW;
        int pesNumber = Constants.VM_CPUS; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        Vm[] vm = new Vm[vms];

        for (int i = 0; i < vms; i++) {
            vm[i] = new Vm(datacenter[i].getId(), userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }

        return list;
    }

    public static List<Cloudlet> createCloudlet(int userId, int cloudlets, int idShift) {
        LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

        //cloudlet parameters
        long fileSize = Constants.FILE_SIZE;
        long outputSize = Constants.OUTPUT_SIZE;
        int pesNumber = Constants.VM_CPUS;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        System.out.println("No of cloudlets"+cloudlets);
        Cloudlet[] cloudlet = new Cloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            int dcId = (int) (mapping[i]);
            long length = (long) (1e3 * (commMatrix[i][dcId] + execMatrix[i][dcId]));
            cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet[i].setUserId(userId);
            list.add(cloudlet[i]);
        }

        return list;
    }
       public static LOADatacenterBroker createBroker(String name) throws Exception {
        return new LOADatacenterBroker(name);
    }
    
    public static void printCloudletList(List<Cloudlet> list) throws NullPointerException {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" +
                indent + "Data center ID" +
                indent + "VM ID" +
                indent + indent + "Time" +
                indent + "Start Time" +
                indent + "Finish Time");

        mxFinishTime = 0;
        maxActualTime=0;
        DecimalFormat dft = new DecimalFormat("###.##");
        dft.setMinimumIntegerDigits(2);
        cost=0.0;
        double sum=0;
        avg=0;
        
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + dft.format(cloudlet.getCloudletId()) + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");
                Log.printLine(indent + indent + dft.format(cloudlet.getResourceId()) +
                        indent + indent + indent + dft.format(cloudlet.getVmId()) +
                        indent + indent + dft.format(cloudlet.getActualCPUTime()) +
                        indent + indent + dft.format(cloudlet.getExecStartTime()) +
                        indent + indent + indent + dft.format(cloudlet.getFinishTime()));
                if(cloudlet.getActualCPUTime()>maxActualTime)
                    maxActualTime=cloudlet.getActualCPUTime();
            if(cloudlet.getVmId()==2)
                cost+= 0.001*cloudlet.getActualCPUTime();
            else if(cloudlet.getVmId()==3)
                cost+= 0.015*cloudlet.getActualCPUTime();
            else if(cloudlet.getVmId()==4)
                cost+= 0.002*cloudlet.getActualCPUTime();
            else if(cloudlet.getVmId()==5)
                cost+= 0.005*cloudlet.getActualCPUTime();
            else if(cloudlet.getVmId()==6)
                cost+= 0.001*cloudlet.getActualCPUTime();
            sum+=cloudlet.getActualCPUTime();
            }
 
            mxFinishTime = Math.max(mxFinishTime, cloudlet.getFinishTime());
        }
        avg=sum/size;
        rut=sum/(maxActualTime*Constants.NO_OF_DATA_CENTERS);
        Log.printLine("Completion Time: "+mxFinishTime);
        Log.printLine("Makespan:  "+maxActualTime);
        Log.printLine("Cost of VMs (0.001,0.015,0.002,0.005,0.001) : Rs "+cost);
        Log.printLine("Average Response Time(seconds): "+avg);
        Log.printLine("Average Resource Utilization: "+rut);
        
        //LOASchedularInstance.printBestFitness();
    }



    public static void loa_run() throws IOException {
      
        
        Log.printLine("Starting LOA Scheduler...");

        new GenerateMatrices();
        commMatrix = GenerateMatrices.getCommMatrix();
        execMatrix = GenerateMatrices.getExecMatrix();
        LOASchedularInstance = new LOA();
        mapping = LOASchedularInstance.run();
        
        try {
            int num_user = 1;   // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events

            CloudSim.init(num_user, calendar, trace_flag);

            // Second step: Create Datacenters
            datacenter = new Datacenter[Constants.NO_OF_DATA_CENTERS];
            for (int i = 0; i < Constants.NO_OF_DATA_CENTERS; i++) {
                datacenter[i] = DatacenterCreator.createDatacenter("Datacenter_" + i);
            }
            
            //Third step: Create Broker
            LOADatacenterBroker broker = createBroker("Broker_0");
            int brokerId = broker.getId();
       
            //Fourth step: Create VMs and Cloudlets and send them to broker
            vmList = createVM(brokerId,Constants.NO_OF_DATA_CENTERS);
            System.out.println("NO of tasks "+Constants.NO_OF_TASKS);
            cloudletList = createCloudlet(brokerId, Constants.NO_OF_TASKS, 0);

            // mapping our dcIds to cloudsim dcIds
            HashSet<Integer> dcIds = new HashSet<>();
            HashMap<Integer, Integer> hm = new HashMap<>();
            //int i=0;
            for (Datacenter dc : datacenter) {
               
                if (!dcIds.contains(dc.getId()))
                    dcIds.add(dc.getId());
                    //mapping[i]=(int)dc.getId();
                    //i+=1;
            }
            
            Iterator<Integer> it = dcIds.iterator();
            for (int i = 0; i < mapping.length; i++) {
                if (hm.containsKey((int) mapping[i])) continue;
                hm.put((int) mapping[i], it.next());
            }
            for (int i = 0; i < mapping.length; i++)
                mapping[i] = hm.containsKey((int) mapping[i]) ? hm.get((int) mapping[i]) : mapping[i];  
            

            broker.submitVmList(vmList);
            broker.setMapping(mapping);
            broker.submitCloudletList(cloudletList);


            // Fifth step: Starts the simulation
            CloudSim.startSimulation();

            newList = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            printCloudletList(newList);

            Log.printLine(LOA_Scheduler.class.getName() + " finished!");  
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
       
        
    }//main


}
