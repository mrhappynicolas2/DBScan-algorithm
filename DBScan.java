//Name: Nicolas Berube
//Student ID: 300239551


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.io.*;
import com.opencsv.CSVWriter;
import java.util.Random; 

class DBScan {
    private List<Point3D> pointList;
    private double minPts;
    private double eps;

    public DBScan(List<Point3D> pointList){
        this.pointList = pointList;
    }

    public static List<Point3D> read(String filename){
        File file= new File(filename);

        // this gives you a list of a list of strings
        List<List<String>> lines = new ArrayList<>();
        Scanner input;

        try{ //try to find the file and then add the lines to a array
            input = new Scanner(file);

            while(input.hasNext()){
                String line = input.next();
                String[] values = line.split(",");
                lines.add(Arrays.asList(values)); //add the line to the array
            }

            input.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("The file was not found");
        }

        List<Point3D> pointArray = new ArrayList<>();
        for (int i = 1; i<lines.size(); i++){
            Double x = Double.parseDouble(lines.get(i).get(0));
            Double y = Double.parseDouble(lines.get(i).get(1));
            Double z = Double.parseDouble(lines.get(i).get(2));
            ///create a new Point with the values that were given            
            Point3D point = new Point3D(x, y, z);
            pointArray.add(point); //add those new points to the array
            
        }
        return pointArray;
    }

    public int getNumberOfClusters(){ //return the number of clusters
        int max = 0;
        for (int i = 0; i<this.pointList.size()-2;i++){
            if(this.pointList.get(i).getLabel() > this.pointList.get(i+1).getLabel()){
                max = this.pointList.get(i).getLabel();
            }
        }
        return max;
    }

    public void findClusters(){
        int C = 0;
        for (int i = 1; i<this.pointList.size(); i++){

            Point3D newPoint = this.getPoints().get(i); 
            if (newPoint.getLabel() != 0 ){continue;} //if the Point already has a label, skip the point
            NearestNeighbors N = new NearestNeighbors(pointList);
            List<Point3D> values =  N.rangeQuery(this.eps,newPoint); //calculate the rangeQuery of the list of points
            
            if(values.size() < minPts){ //if the size is smaller then the minPts
                newPoint.setLabel(-1); //set the point as noise
                continue;
            } 

            C++;
            newPoint.setLabel(C);
            Stack<Point3D> stackPoint = new Stack<>(); //create a stack
            for(int v = 0; v < values.size(); v++){
                stackPoint.push(values.get(v)); //push the values into the stack
            }
          
            while(!stackPoint.empty()){
                Point3D Q = stackPoint.pop();
                if (Q.getLabel() == -1) {Q.setLabel(C);}  //if the point is noise, change it from noise to a point
                if(Q.getLabel() > 0){continue;} //if the point already has a label, continue
                Q.setLabel(C);
                NearestNeighbors N2 = new NearestNeighbors(pointList);
                List<Point3D> values2 =  N2.rangeQuery(this.eps,Q);

                if(values2.size() >= minPts){
                    for(int v = 0; v < values2.size(); v++){
                        stackPoint.push(values2.get(v));
                    }
                }
            }    
        }
    }


//going to save a list, that has a list of everyone with the same label
    public void save(String filename){
        List<Point3D> value = this.pointList;
        int numberCluster = this.getNumberOfClusters();
        List<List<Point3D>> Array = new ArrayList<>(numberCluster);
        int noise = 0; //this will help save the ammount of points are considerd noise
        for(int i = 0; i<value.size()-1;i++){
            int label = value.get(i).getLabel();
            if(label>=0){
                if(Array.size() > label){
                    Array.get(label).add(value.get(i));
                }
                else{
                    List<Point3D> smallArray = new ArrayList<>();
                    smallArray.add(value.get(i));
                    Array.add(smallArray);
                }
            }
            else{noise++;}
        }

        List<Integer> orderArray = new ArrayList<>(numberCluster);
        //create a array to save the ammount of points in a cluster
        for (int j = 0; j<Array.size(); j++){
            orderArray.add(Array.get(j).size());
        }
        //sort the array 
        Collections.sort(orderArray, Collections.reverseOrder());
        for (int j = 0; j<Array.size()-1; j++){
            System.out.println(orderArray.get(j)+" points in a cluster");
            
        }
        System.out.println(noise+" points were counted as noise");
        File file = new File(filename);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);
    
            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);
    
            // adding header to csv
            String[] header = { "x", "y", "z", "C", "R", "G", "B" };
            writer.writeNext(header);
    
            // add data to csv
            for (int i = 0; i<Array.size(); i++){
                //create a color for each cluster using the random import
                Random rand = new Random();
                String color = String.valueOf(Double.valueOf(rand.nextInt(10000))/10000);
                String color2 = String.valueOf(Double.valueOf(rand.nextInt(10000))/10000);
                String color3 = String.valueOf(Double.valueOf(rand.nextInt(10000))/10000);
                
                for(int l = 0; l<Array.get(i).size(); l++){
                    Point3D newVal = Array.get(i).get(l);
                    //create a new line with the specific values x,y,z and  colors
                    String[] next = { String.valueOf(newVal.getX()), String.valueOf(newVal.getY()), String.valueOf(newVal.getZ()), color, color2, color3 };
                    writer.writeNext(next);
                }
            }   
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double setEps(double eps){
        this.eps = eps;
        return eps;
    }

    public double setMinPts(double minPts){
        this.minPts = minPts;
        return minPts;
    }

    public List<Point3D> getPoints(){
        return  pointList;
    }



    public static void main(String[] args) {
        
        try{
            String filename = args[0]; //save the filepath as filename
            List<Point3D> test = (read(filename)); //create a new list of points with the read method
            DBScan dbScan = new DBScan(test); //create a new DBScan
            
            dbScan.setEps(Double.parseDouble(args[1]));
            dbScan.setMinPts(Double.parseDouble(args[2]));
 
            dbScan.findClusters();
            String[] newFilename = filename.split(".csv");
            
            filename = newFilename[0]+"_"+args[1]+"_"+args[2]+".csv";
            System.out.println("saved the file as "+filename);
            dbScan.save(filename);

            System.out.println(dbScan.getNumberOfClusters() + " Clusters in total");
        }
        catch (ArrayIndexOutOfBoundsException e){System.out.println("You dont have the three required values");} 
        //if the 3 arguments are not given
    }    
}