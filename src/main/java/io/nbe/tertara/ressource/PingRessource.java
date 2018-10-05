package io.nbe.tertara.ressource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingRessource {

    @GetMapping("/ping")
    public String getPong() {
        return "pong";
    }
}
