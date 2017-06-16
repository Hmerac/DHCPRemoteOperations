package com.core.Controller;

/**
 *
 * @author Mert Acikportali
 */
import com.core.Model.Reservation;
import com.core.Model.Scope;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.core.Repository.ScopeRepository;
import com.core.Repository.ReservationRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.ui.Model;

@Controller
public class ScopeController {

    int createController = 0;
    int deleteController = 0;
    String deleteMethodString = "";

    @Autowired
    private ScopeRepository scopeData;

    @Autowired
    private ReservationRepository reservationData;

    @RequestMapping(value = "/")
    public String home() {
        return "home";
    }

    @RequestMapping(value = "/home")
    public String home(@RequestParam(value = "name", defaultValue = "No name") String name, Map<String, Object> model) {
        model.put("message", name);
        return "home";
    }

    @RequestMapping(value = "/createScope", method = RequestMethod.GET)
    public String showFormG(Model model) {
        model.addAttribute("scopes", scopeData.findAll());
        return "createScope";
    }

    @RequestMapping(value = "/createScope", method = RequestMethod.POST)
    public String showFormP(@RequestParam String name,
            @RequestParam String srange,
            @RequestParam String erange,
            @RequestParam String submask,
            @RequestParam String description,
            @RequestParam String servername, Model model) throws IOException {

        Scope newScope = new Scope();

        String success = "Scope added successfully.";
        String exception = " ";

        //Powershell command to create a new scope
        String command = "powershell.exe  Add-DhcpServerv4Scope -Name '" + name + "' -StartRange "
                + srange + " -EndRange " + erange + " -SubnetMask " + submask + " -Description '" + description + "' -cn " + servername + " ";

        // Executing the command
        Process powerShellProcess = Runtime.getRuntime().exec(command);

        // Getting the results
        powerShellProcess.getOutputStream().close();

        String line;
        //Successful output from powershell
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            System.out.println(line);
            success += line + "\n";
            createController = 1;
        }

        stdout.close();
        //Exception output from powershell
        BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()));
        while ((line = stderr.readLine()) != null) {
            System.out.println(line);
            exception += line + "\n";
            createController = 2;
        }

        //Print exception output
        model.addAttribute("exception", exception);

        stderr.close();

        //If powershell has returned successful output, add to database else don't
        if (createController != 2) {
            newScope.setName(name);
            newScope.setStartRange(srange);
            newScope.setEndRange(erange);
            newScope.setSubnetMask(submask);
            newScope.setDescription(description);
            newScope.setServerName(servername);

            //Setting scope id from start range
            int thirdDot = newScope.getStartRange().lastIndexOf(".");
            String extractedIp = newScope.getStartRange().substring(0, thirdDot + 1) + "0";
            newScope.setScopeID(extractedIp);

            scopeData.save(newScope);

            //Print successful output
            model.addAttribute("success", success);
            model.addAttribute("scope", newScope);
        }

        createController = 0;
        return "createScope";
    }

    @RequestMapping(value = "/listScopes", method = RequestMethod.GET)
    public String scopeList(Model model) {
        model.addAttribute("scopes", scopeData.findAll());
        if (deleteController == 1) {
            model.addAttribute("deleted", deleteMethodString);
            deleteController = 0;
        } else if (deleteController == 2) {
            model.addAttribute("deleted", deleteMethodString);
            deleteController = 0;
        }
        return "listScopes";
    }

    @RequestMapping(value = "/deleteScope/{id}")
    public String deleteScope(@PathVariable Long id, Model model) throws IOException {

        String success = " ";
        String exception = " ";

        //Find corresponding scope
        Scope removeScope = scopeData.findOne(id);

        //Powershell command to delete a scope
        String command = "powershell.exe Remove-DhcpServerv4Scope -scopeId " + removeScope.getScopeID() + " -cn " + removeScope.getServerName();
        System.out.println(command);

        // Executing the command
        Process powerShellProcess = Runtime.getRuntime().exec(command);

        // Getting the results
        powerShellProcess.getOutputStream().close();

        String line;
        //Successful output from powershell
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            System.out.println(line);
            success += line + "\n";
            deleteMethodString = "Scope has been deleted successfully.";
            deleteController = 1;
        }

        stdout.close();
        //Exception output from powershell
        BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()));
        while ((line = stderr.readLine()) != null) {
            System.out.println(line);
            exception += line + "\n";
            deleteMethodString += exception;
            deleteController = 2;
        }

        stderr.close();

        //If powershell has returned successful output, add to database else don't
        if (deleteController != 2) {
            scopeData.delete(id);
            deleteController = 0;
        }

        return "redirect:/listScopes";
    }

    @RequestMapping(value = "/listReservations/{id}", method = RequestMethod.GET)
    public String scopeReservations(@PathVariable Long id, Model model) {
        model.addAttribute("scope", scopeData.findOne(id));
        model.addAttribute("reservations", reservationData.findAll());
        return "listReservations";
    }

    @RequestMapping(value = "/listReservations/{id}", method = RequestMethod.POST)
    public String addReservation(@PathVariable Long id, @RequestParam String name,
            @RequestParam String clientid,
            @RequestParam String ipaddress,
            @RequestParam String description, Model model) {
        
        Scope initialScope = scopeData.findOne(id);
        Reservation newReservation = new Reservation();

        newReservation.setName(name);
        newReservation.setClientID(clientid);
        newReservation.setScopeID(initialScope.getScopeID());
        newReservation.setIPAddress(ipaddress);
        newReservation.setDescription(description);

        reservationData.save(newReservation);

        model.addAttribute("reservation", newReservation);
        return "redirect:/listReservations";
    }

}
