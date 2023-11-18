package com.sunbasedata.controller;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sunbasedata.entity.sunbasedataEntity;
import com.sunbasedata.service.sunbasedataService;
@Controller
public class sunbasedataController {

	@Autowired
	sunbasedataService Service; 
	
	private String uuid;
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
	
    @RequestMapping("/")
    public String index() {
        return "redirect:/sunbasedata_login.html"; 
    }
	
    @PostMapping(value="/sunbased_Authenciate",consumes="application/x-www-form-urlencoded")
    public String authenticateUser(@RequestParam("login_id") String loginId,@RequestParam("password") String password,RedirectAttributes redirectAttributes) 
    {
    	String isAuthenticated = Service.authenticate(loginId, password);
        if (isAuthenticated != null && isAuthenticated.length() > 0) {
            String token = isAuthenticated;
            redirectAttributes.addAttribute("token", token);
            return "redirect:/sunbased_getlist";
        } else {
            redirectAttributes.addFlashAttribute("error", "Authentication failed");
            return "redirect:/sunbasedata_login.html";
        }
    }
    @GetMapping(value="/sunbased_getlist",produces="application/json")
    public String getList(Model model)
    {
        List<sunbasedataEntity> response = Service.getAllList();
        if(response!=null) {
            model.addAttribute("entityList", response);
            return "getListDetails";
        }
        else {
            return "error";
        }
    }
    
    @GetMapping(value="/deleteRecord",produces="application/json")
    public String handleDelete(@RequestParam(name = "uuid") String uuid)
    {
    	boolean result=Service.deleteRecord(uuid);
    	if(result) {
    		return "redirect:/sunbased_getlist";
    	}
    	else {
    		return "error";
    	}
    }
    
    @GetMapping(value="/updateRecord",produces="application/json")
    public String handleUpdate(@RequestParam(name = "uuid") String uuid)
    {
    	setUuid(uuid);
    	return "redirect:/update.html";
    }
    @PostMapping(value="/update",consumes="application/x-www-form-urlencoded")
    public String UpdateDetails(@RequestParam("first_name") String frist_name,@RequestParam("last_name") String last_name,
    		@RequestParam("street") String street,@RequestParam("address") String address,@RequestParam("city") String city,
    		@RequestParam("state") String state,@RequestParam("email") String email,@RequestParam("phone") String phone)
    {
    	boolean result=Service.updateRecord(frist_name,last_name,street,address,city,state,email,phone,getUuid());
    	if(result)
    	{
    		return "redirect:/sunbased_getlist";
    	}
    	else {
    		return "error";
    	}
    }
    @GetMapping(value="/create",produces="application/json")
    public String create()
    {
    	return "redirect:/create.html";
    }
    @PostMapping(value="/createRecord",consumes="application/x-www-form-urlencoded")
    public String createPerson(@RequestParam("first_name") String frist_name,@RequestParam("last_name") String last_name,
    		@RequestParam("street") String street,@RequestParam("address") String address,@RequestParam("city") String city,
    		@RequestParam("state") String state,@RequestParam("email") String email,@RequestParam("phone") String phone)
    {
    	boolean result=Service.addRecord(frist_name,last_name,street,address,city,state,email,phone);
    	if(result)
    	{
    		return "redirect:/sunbased_getlist";
    	}
    	else {
    		return "error";
    	}
    }
}
