//Name: Nicolas Berube
//Student ID: 300239551

import java.util.ArrayList;
import java.util.List;

class NearestNeighbors{
    List<Point3D> points;
    public NearestNeighbors(List<Point3D> points){
        this.points = points;
    }

    public List<Point3D> rangeQuery(double eps, Point3D P){
        List<Point3D> listN = new ArrayList<>();
        for(int i = 0; i<this.points.size();i++){ //for i < numbers of points in the list
            double distance = P.distance(points.get(i)); //calculate distance 
            if (distance < eps){ //if distance is less then the eps
                listN.add(points.get(i)); //add the point to the new list
            }
        }
        
        return listN;
    }
}