package com.core.Model;

/**
 *
 * @author user
 */
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@Entity
public class Scope implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;

    private String name;
    private String srange;
    private String erange;
    private String submask;
    private String description;
    private String servername;
    // Scope ID is initialized during form submit phase
    private String scopeid;

    @OneToMany
    private List<Reservation> reservations;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartRange() {
        return srange;
    }

    public void setStartRange(String srange) {
        this.srange = srange;
    }

    public String getEndRange() {
        return erange;
    }

    public void setEndRange(String erange) {
        this.erange = erange;
    }

    public String getSubnetMask() {
        return submask;
    }

    public void setSubnetMask(String submask) {
        this.submask = submask;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServerName() {
        return servername;
    }

    public void setServerName(String servername) {
        this.servername = servername;
    }

    public String getScopeID() {
        return scopeid;
    }

    public void setScopeID(String scopeid) {
        this.scopeid = scopeid;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public String toString() {
        return "Scope [ ID = " + id + ", Name = " + name + ", Start Range = " + srange + ", End Range = " + erange + ", Subnet Mask = " + submask + ", Description = " + description + ", Server Name" + servername + "]";
    }

}
