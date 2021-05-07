package com.lwy.his.controller;



import com.lwy.his.service.PatientChangePasswordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Api("患者更改密码")
@RestController
@RequestMapping("/PatientChangePasswordController")
public class PatientChangePasswordController {

    @Autowired
    private PatientChangePasswordService service;

    @RequestMapping("/changepassword")
    @ApiOperation("根据username 修改密码")
    public void changepassword(@RequestParam("username") String username, @RequestParam("password") String password) {
        ConcurrentMap map =  new ConcurrentHashMap();
        map.put("username",username);
        map.put("password",password);
        service.changepassword(map);
    }
}
