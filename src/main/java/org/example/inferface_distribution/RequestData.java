package org.example.inferface_distribution;

import javafx.beans.property.SimpleStringProperty;

public class RequestData {
    private final SimpleStringProperty pib;
    private final SimpleStringProperty date;
    private final SimpleStringProperty type;
    private final SimpleStringProperty status;

    public RequestData(String pib, String date, String type, String status) {
        this.pib = new SimpleStringProperty(pib);
        this.date = new SimpleStringProperty(date);
        this.type = new SimpleStringProperty(type);
        this.status = new SimpleStringProperty(status);
    }

    public String getPib() { return pib.get(); }
    public String getDate() { return date.get(); }
    public String getType() { return type.get(); }
    public String getStatus() { return status.get(); }

    public void setStatus(String newStatus) {
        this.status.set(newStatus);
    }
}
