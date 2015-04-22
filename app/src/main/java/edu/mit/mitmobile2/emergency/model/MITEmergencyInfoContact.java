package edu.mit.mitmobile2.emergency.model;

/**
 * Created by grmartin on 4/16/15.
 */
public class MITEmergencyInfoContact {
    private String description;
    private String name;
    private String phone;

    public MITEmergencyInfoContact() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "MITEmergencyInfoContact{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }


}
