package Models;

public class StiEntry {

    // Attributes
    private int stiId;
    private String name;
    private String symptoms;
    private String prevention;
    private String treatment;
    private int riskLevel;

    // Default Constructor
    public StiEntry() {
    }

    // Parameterized Constructor
    public StiEntry(int stiId, String name, String symptoms,
                    String prevention, String treatment, int riskLevel) {
        this.stiId = stiId;
        this.name = name;
        this.symptoms = symptoms;
        this.prevention = prevention;
        this.treatment = treatment;
        this.riskLevel = riskLevel;
    }

    // Methods

    public void getStiDetails() {
        System.out.println("STI ID: " + stiId);
        System.out.println("Name: " + name);
        System.out.println("Symptoms: " + symptoms);
        System.out.println("Prevention: " + prevention);
        System.out.println("Treatment: " + treatment);
        System.out.println("Risk Level: " + riskLevel);
    }

    public int getStiId() {
        return stiId;
    }

    public void setStiId(int id) {
        this.stiId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getPrevention() {
        return prevention;
    }

    public void setPrevention(String prevention) {
        this.prevention = prevention;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public int getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(int level) {
        this.riskLevel = level;
    }
}
