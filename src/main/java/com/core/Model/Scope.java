package com.core.Model;

/**
 *
 * @author user
 */
import java.io.Serializable;
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
    private String scopeid;

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

    @Override
    public String toString() {
        return "Scope [ ID = " + id + ", Name = " + name + ", Start Range = " + srange + ", End Range = " + erange + ", Subnet Mask = " + submask + ", Description = " + description + ", Server Name" + servername + "]";
    }

}
