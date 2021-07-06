package Model;

public class Schedule_data {
    String Date;
    String Time;
    String AlertTime;
    String Task;
    String Description;
    String id;

    public Schedule_data(){

    }

    public Schedule_data(String date, String time, String alertTime, String task, String description, String id) {
        Date = date;
        Time = time;
        AlertTime = alertTime;
        Task = task;
        Description = description;
        this.id = id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getAlertTime() {
        return AlertTime;
    }

    public void setAlertTime(String alertTime) {
        AlertTime = alertTime;
    }

    public String getTask() {
        return Task;
    }

    public void setTask(String task) {
        Task = task;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
