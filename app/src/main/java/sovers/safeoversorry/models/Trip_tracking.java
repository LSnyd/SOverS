package sovers.safeoversorry.models;

public class Trip_tracking {

    private String status;
    private double distance;
   // private float speed;

    public Trip_tracking(String status, double distance) {

        this.status = status;
       this.distance = distance;
        //this.speed = speed;
    }

    public Trip_tracking() {

    }

    public String getStatus() {  return status; }

    public void setStatus(String status) {this.status = status; }


    public double getDistance() { return distance; }

  public void setDistance(double distance) { this.distance = distance;}

  // public double getSpeed() { return speed; }

    //public void setSpeed(double speed) {this.speed = speed; }


    @Override
    public String toString() {
        return "User{" +
                "status='" + status+ '\'' +
               "distance='" + distance + '\'' +
               //"speed='" + speed + '\'' +
                '}';
    }
}


