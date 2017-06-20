package com.core.Controller;

import java.io.IOException;
import java.util.Map;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface ScopeControllerInterface {

    @RequestMapping(value = "/")
    public String home();

    @RequestMapping(value = "/home")
    public String home(@RequestParam(value = "name", defaultValue = "No name") String name, Map<String, Object> model);

    @RequestMapping(value = "/createScope", method = RequestMethod.GET)
    public String showFormG(Model model);

    @RequestMapping(value = "/createScope", method = RequestMethod.POST)
    public String showFormP(@RequestParam String name,
            @RequestParam String srange,
            @RequestParam String erange,
            @RequestParam String submask,
            @RequestParam String description,
            @RequestParam String servername, Model model) throws IOException;

    @RequestMapping(value = "/listScopes", method = RequestMethod.GET)
    public String scopeList(Model model);

    @RequestMapping(value = "/deleteScope/{id}")
    public String deleteScope(@PathVariable Long id, Model model) throws IOException;

    @RequestMapping(value = "/listReservations/{id}", method = RequestMethod.GET)
    public String reservationList(@PathVariable Long id, Model model);

    @RequestMapping(value = "/listReservations/{id}", method = RequestMethod.POST)
    public String addReservation(@PathVariable Long id, @RequestParam String name,
            @RequestParam String clientid,
            @RequestParam String ipaddress,
            @RequestParam String description, Model model) throws IOException;

    @RequestMapping(value = "/deleteReservation/{id}")
    public String deleteReservation(@PathVariable Long id, Model model) throws IOException ;

}
