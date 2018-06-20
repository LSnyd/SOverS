package sovers.safeoversorry.models;

/**
 * Created by User on 11/11/2017.
 */

public class Message {

    private String message;
    private String user_id;
    private String timestamp;
    private String status;
    private float speed;
    private float distance;
    private android.location.Location position;

    public Message(String message, String user_id, String timestamp, String status, float speed, float distance) {
        this.message = message;
        this.user_id = user_id;
        this.timestamp = timestamp;
        this.status = status;
        this.speed = speed;
        this.distance = distance;

    }

    public Message() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setPosition(android.location.Location position) {
        this.position = position;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }



    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                "position='" + position + '\'' +
                ", user_id='" + user_id + '\'' +
                "status='" + status + '\'' +
                "speed='" + speed + '\'' +
                ", distance='" + distance + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
