package cloudsample;

import javax.swing.JFrame;  
import javax.swing.SwingUtilities;  
import javax.swing.WindowConstants;
  
import org.jfree.chart.ChartFactory;  
import org.jfree.chart.ChartPanel;  
import org.jfree.chart.JFreeChart;  
import org.jfree.chart.plot.PlotOrientation;  
import org.jfree.data.category.CategoryDataset;  
import org.jfree.data.category.DefaultCategoryDataset;  
  
public class BarChartExample extends JFrame {  
  
  private static final long serialVersionUID = 1L;  
  
  public BarChartExample( String appTitle,String title,String y,int a,int b,int c, int d , int e , int f){  
    super(appTitle);  
  
    // Create Dataset  
    CategoryDataset dataset = createDataset(a,b,c,d,e,f);  
      
    //Create chart  
    JFreeChart chart=ChartFactory.createBarChart(  
        title, //Chart Title  
        "No. of Tasks", // Category axis  
        y, // Value axis  
        dataset,  
        PlotOrientation.VERTICAL,  
        true,true,false  
       );  
  
    ChartPanel panel=new ChartPanel(chart);  
    setContentPane(panel);  
  }  
  
  private CategoryDataset createDataset(int a , int b ,int c, int d, int e, int f ) {  
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();  
  
    // Population in 2005  
    dataset.addValue(a, "LOA", "25");  
    dataset.addValue(b, "PSO", "25");  
      
  
    // Population in 2010  
    dataset.addValue(c, "LOA", "50");  
    dataset.addValue(d, "PSO", "50");  
      
  
    // Population in 2015  
    dataset.addValue(e, "LOA", "75");  
    dataset.addValue(f, "PSO", "75");  
     
  
    return dataset;  
  }  
  /*
  public static void main(String[] args) throws Exception {  
    SwingUtilities.invokeAndWait(()->{  
      BarChartExample example=new BarChartExample("Bar Chart Window","Completion Time Vs No. of Tasks","Completion Time",10,20,30,15,16,18);  
      example.setSize(800, 400);  
      example.setLocationRelativeTo(null);  
      example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  
      example.setVisible(true);  
    });  
  } 
*/

}