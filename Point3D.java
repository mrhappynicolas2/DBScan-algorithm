//Name: Nicolas Berube
//Student ID: 300239551

import java.lang.Math;

class Point3D {

        private double x;
        private double y;
        private double z;
        private int label;
        public int count = 0;
        public Point3D(double x, double y, double z){
            this.x = x;
            this.y = y;
            this.z = z;
            this.label = 0;
            
        }

        public double getX() {
            return x;
        }
    
        public double getY() {
            return y;
        }
       
        public double getZ() {
            return z;
        }

        public int getLabel(){
            return label;
        }
        public void setLabel(int lab) {
            this.label = lab;
            
        }

        //?
        
    public Double distance(Point3D pt){
        double x = (this.x - pt.getX());
        double y = (this.y - pt.getY());
        double z = (this.z - pt.getZ());
        return Math.sqrt(x * x + y * y + z * z);
    }
}