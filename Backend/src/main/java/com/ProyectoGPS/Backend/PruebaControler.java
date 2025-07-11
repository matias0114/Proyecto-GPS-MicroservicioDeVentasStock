package com.ProyectoGPS.Backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PruebaControler {

    @GetMapping("/hola")
    public String hola() {

        return "¡Hola Mundo!";
    }
}
