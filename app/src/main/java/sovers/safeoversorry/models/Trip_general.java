package sovers.safeoversorry.models;


public class Trip_general {

    private String publisher;
    private String destination;
    private String status;
    private float distance;

    public Trip_general(String publisher,String status, String destination, float distance) {
        this.publisher = publisher;
        this.destination = destination;
        this.distance = distance;
        this.status = status;
    }

    public Trip_general() {

    }




    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status= status;
    }


    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "User{" +
                "publisher='" + publisher + '\'' +
                "status='" + status + '\'' +
                "destination='" + destination + '\'' +
                "distance='" + distance + '\'' +
                '}';
    }
}
