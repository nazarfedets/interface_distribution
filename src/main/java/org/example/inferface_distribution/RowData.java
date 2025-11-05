package org.example.inferface_distribution;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class RowData {
    private String pib;
    private Map<Integer, String> duties = new HashMap<>();

    public RowData(String pib, int daysInMonth) {
        this.pib = pib;
        for (int i = 1; i <= daysInMonth; i++) {
            duties.put(i, "");
        }
    }

    public String getPib() {
        return pib;
    }
    public String getValuesForDays(int day){
        return duties.getOrDefault(day, "");
    }
    public void setValueForDay(int day, String value) {
        duties.put(day, value);
    }
    public String getToday() {
        int today = LocalDate.now().getDayOfMonth();
        return duties.getOrDefault(today, "");
    }
}
