package org.example.inferface_distribution;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class RequestData {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty pib;
    private final SimpleStringProperty reason;
    private final SimpleStringProperty date;
    private final SimpleStringProperty type;
    private final SimpleStringProperty status;

    public RequestData(int id, String pib, String reason, String date, String type, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.pib = new SimpleStringProperty(pib);
        this.reason = new SimpleStringProperty(reason);
        this.date = new SimpleStringProperty(date);
        this.type = new SimpleStringProperty(type);
        this.status = new SimpleStringProperty(status);
    }

    public int getId() { return id.get(); }
    public String getPib() { return pib.get(); }
    public String getReason() { return reason.get(); }
    public String getDate() { return date.get(); }
    public String getType() { return type.get(); }
    public String getStatus() { return status.get(); }

    public void setStatus(String newStatus) {
        this.status.set(newStatus);
    }
}
