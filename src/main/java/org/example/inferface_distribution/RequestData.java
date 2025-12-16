package org.example.inferface_distribution;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class RequestData {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty userPib;
    private final SimpleStringProperty reason;
    private final SimpleStringProperty date;
    private final SimpleStringProperty requestType;
    private final SimpleStringProperty status;

    public RequestData(int id, String pib, String reason, String date, String type, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.userPib = new SimpleStringProperty(pib);
        this.reason = new SimpleStringProperty(reason);
        this.date = new SimpleStringProperty(date);
        this.requestType = new SimpleStringProperty(type);
        this.status = new SimpleStringProperty(status);
    }

    public int getId() { return id.get(); }
    public String getUserPib() { return userPib.get(); }
    public String getReason() { return reason.get(); }
    public String getDate() { return date.get(); }
    public String getRequestType() { return requestType.get(); }
    public String getStatus() { return status.get(); }

    public void setStatus(String newStatus) {
        this.status.set(newStatus);
    }
}
