/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.Model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Reservation implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;

    private String sid;
    private String clientid;
    private String name;
    private String scopeid;
    private String ipaddress;
    private String description;

    public long getId() {
        return id;
    }
    
    public String getSID() {
        return sid;
    }
    
    public void setSID(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getClientID() {
        return clientid;
    }

    public void setClientID(String clientid) {
        this.clientid = clientid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScopeID() {
        return scopeid;
    }

    public void setScopeID(String scopeid) {
        this.scopeid = scopeid;
    }
    
    public String getIPAddress() {
        return ipaddress;
    }

    public void setIPAddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

}
