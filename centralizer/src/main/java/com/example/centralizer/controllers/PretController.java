package com.example.centralizer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.example.centralizer.services.PretProxyService;


@Controller
public class PretController {
    @Autowired
    private PretProxyService pretProxyService;

    @GetMapping("/prets")
    public String getPrets(Model model) {
        model.addAttribute("prets", pretProxyService.getAllPrets());
        return "prets";
    }

    @GetMapping("/prets/{id}")
    @ResponseBody
    public Object getPretById(@PathVariable int id) {
        return pretProxyService.getPretById(id);
    }

    @PostMapping("/prets")
    @ResponseBody
    public Object createPret(@RequestBody Map<String, Object> pret) {
        return pretProxyService.createPret(pret);
    }

    @PutMapping("/prets/{id}")
    @ResponseBody
    public Object updatePret(@PathVariable int id, @RequestBody Map<String, Object> pret) {
        return pretProxyService.updatePret(id, pret);
    }

    @DeleteMapping("/prets/{id}")
    @ResponseBody
    public void deletePret(@PathVariable int id) {
        pretProxyService.deletePret(id);
    }
}
