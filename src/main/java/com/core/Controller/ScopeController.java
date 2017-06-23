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
public class ScopeController implements ScopeControllerInterface {

    int ScopeCreateController = 0;
    int ScopeDeleteController = 0;
    int ReservationCreateController = 0;
    int ReservationDeleteController = 0;
    String deleteMethodString = "Scope has been deleted successfully";

    @Autowired
    private ScopeRepository scopeData;

    @Autowired
    private ReservationRepository reservationData;

    public String home() {
        return "home";
    }

    public String home(@RequestParam(value = "name", defaultValue = "No name") String name, Map<String, Object> model) {
        model.put("message", name);
        return "home";
    }

    public String showFormG(Model model) {
        model.addAttribute("scopes", scopeData.findAll());
        return "createScope";
    }

    public String showFormP(@RequestParam String name,
            @RequestParam String srange,
            @RequestParam String erange,
            @RequestParam String submask,
            @RequestParam String description,
            @RequestParam String servername, Model model) throws IOException {

        Scope newScope = new Scope();

        String success = "Scope has been added successfully.";
        String exception = " ";

        //Powershell command to create a new scope
        String command = "powershell.exe  Add-DhcpServerv4Scope -Name '" + name + "' -StartRange "
                + srange + " -EndRange " + erange + " -SubnetMask " + submask + " -Description '" + description + "' -cn " + servername + " | Format-List";

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
            ScopeCreateController = 1;
        }

        stdout.close();
        //Exception output from powershell
        BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()));
        while ((line = stderr.readLine()) != null) {
            System.out.println(line);
            exception += line + "\n";
            ScopeCreateController = 2;
        }

        //Print exception output
        model.addAttribute("exception", exception);

        stderr.close();

        //If powershell has returned successful output, add to database else don't
        if (ScopeCreateController != 2) {
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

        ScopeCreateController = 0;
        return "createScope";
    }

    public String scopeList(Model model) {
        model.addAttribute("scopes", scopeData.findAll());
        if (ScopeDeleteController == 1) {
            model.addAttribute("deleted", deleteMethodString);
            ScopeDeleteController = 0;
        } else if (ScopeDeleteController == 2) {
            model.addAttribute("deleted", deleteMethodString);
            ScopeDeleteController = 0;
        }
        return "listScopes";
    }

    public String deleteScope(@PathVariable Long id, Model model) throws IOException {

        String success = " ";
        String exception = " ";

        //Find the corresponding scope
        Scope removeScope = scopeData.findOne(id);
        
        //Powershell command to delete reservations under the scope
        String command1 = "powershell.exe Remove-DhcpServerv4Lease -ComputerName " + removeScope.getServerName() + " -scopeId " + removeScope.getScopeID() + " | Format-List";

        //Powershell command to delete leases under the scope
        String command2 = "powershell.exe Remove-DhcpServerv4Reservation -ComputerName " + removeScope.getServerName() + " -scopeId " + removeScope.getScopeID() + " | Format-List";
        
        //Powershell command to delete a scope
        String command3 = "powershell.exe Remove-DhcpServerv4Scope -scopeId " + removeScope.getScopeID() + " -cn " + removeScope.getServerName() + " | Format-List";

        // Executing the delete all reservations under the scope command
        Process powerShellProcess1 = Runtime.getRuntime().exec(command1);
        
        // Executing the delete all leases under the scope command
        Process powerShellProcess2 = Runtime.getRuntime().exec(command2);
        
        // Executing the delete scope command
        Process powerShellProcess3 = Runtime.getRuntime().exec(command3);

        // Getting the results
        powerShellProcess3.getOutputStream().close();

        String line;
        //Successful output from powershell
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess3.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            System.out.println(line);
            success += line + "\n";
            deleteMethodString += success;
            ScopeDeleteController = 1;
        }

        stdout.close();
        //Exception output from powershell
        BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess3.getErrorStream()));
        while ((line = stderr.readLine()) != null) {
            System.out.println(line);
            exception += line + "\n";
            deleteMethodString += exception;
            ScopeDeleteController = 2;
        }

        stderr.close();

        //If powershell has returned successful output, add to database else don't
        if (ScopeDeleteController != 2) {
            scopeData.delete(id);
            ScopeDeleteController = 0;
        }

        return "redirect:/listScopes";
    }

    public String reservationList(@PathVariable Long id, Model model) {

        model.addAttribute("scope", scopeData.findOne(id));
        model.addAttribute("reservations", reservationData.findAll());

        return "listReservations";
    }

    public String addReservation(@PathVariable Long id, @RequestParam String name,
            @RequestParam String clientid,
            @RequestParam String ipaddress,
            @RequestParam String description, Model model) throws IOException {

        model.addAttribute("scope", scopeData.findOne(id));
        model.addAttribute("reservations", reservationData.findAll());

        String success = "Reservation has been added successfully";
        String exception = " ";

        Scope initialScope = scopeData.findOne(id);
        Reservation newReservation = new Reservation();

        //Powershell command to create a new reservation
        String command = "powershell.exe  Add-DhcpServerv4Reservation -Name '" + name + "' -ScopeId "
                + initialScope.getScopeID() + " -ClientId " + clientid + " -IPAddress " + ipaddress + " -Description '" + description + "' | Format-List";

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
            ReservationCreateController = 1;
        }

        stdout.close();
        //Exception output from powershell
        BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()));
        while ((line = stderr.readLine()) != null) {
            System.out.println(line);
            exception += line + "\n";
            ReservationCreateController = 2;
        }

        //Print exception output
        model.addAttribute("exception", exception);

        stderr.close();

        if (ReservationCreateController != 2) {
            newReservation.setName(name);
            newReservation.setSID(initialScope.getId() + "");
            newReservation.setClientID(clientid);
            newReservation.setScopeID(initialScope.getScopeID());
            newReservation.setIPAddress(ipaddress);
            newReservation.setDescription(description);

            initialScope.getReservations().add(newReservation);
            reservationData.save(newReservation);

            model.addAttribute("success", success);
            model.addAttribute("reservation", newReservation);
        }

        ReservationCreateController = 0;

        return "listReservations";
    }

    public String deleteReservation(@PathVariable Long id, Model model) throws IOException {

        String success = "Reservation has been deleted successfully";
        String exception = " ";

        Reservation initialReservation = reservationData.findOne(id);
        Scope initialScope = scopeData.findOne(Long.parseLong(initialReservation.getSID()));

        //Powershell command to create a new reservation
        String command = "powershell.exe Remove-DhcpServerv4Reservation -ComputerName '"
                + initialScope.getServerName() + "' -IPAddress " + initialReservation.getIPAddress() + " | Format-List";

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
            ReservationDeleteController = 1;
        }

        stdout.close();
        //Exception output from powershell
        BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()));
        while ((line = stderr.readLine()) != null) {
            System.out.println(line);
            exception += line + "\n";
            ReservationDeleteController = 2;
        }

        //Print exception output
        model.addAttribute("exception", exception);

        stderr.close();

        if (ReservationCreateController != 2) {
            initialScope.getReservations().remove(initialReservation);
            reservationData.delete(id);
            model.addAttribute("success", success);
        }

        ReservationDeleteController = 0;

        model.addAttribute("scope", scopeData.findOne(Long.parseLong(initialReservation.getSID())));
        model.addAttribute("reservations", reservationData.findAll());

        return "listReservations";
    }

    public String leaseList(@PathVariable Long id, Model model) throws IOException {

        String success = "";
        String exception = "";

        Scope initialScope = scopeData.findOne(id);

        model.addAttribute("scope", initialScope);

        //Powershell command to get leases
        String command = "powershell.exe  Get-DHCPServerv4Scope -ScopeId "
                + initialScope.getScopeID() + " | " + "Get-DHCPServerv4Lease";

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
        }

        stdout.close();
        //Exception output from powershell
        BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()));
        while ((line = stderr.readLine()) != null) {
            System.out.println(line);
            exception += line + "\n";
        }

        //Print success output
        model.addAttribute("success", success);

        //Print exception output
        model.addAttribute("exception", exception);

        stderr.close();

        return "listLeases";
    }

}
