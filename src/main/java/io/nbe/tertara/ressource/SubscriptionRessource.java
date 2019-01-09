package io.nbe.tertara.ressource;

import io.nbe.tertara.exception.BadRequestBody;
import io.nbe.tertara.exception.ConflitException;
import io.nbe.tertara.model.Subscription;
import io.nbe.tertara.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.util.List;

@RestController("subscription")
public class SubscriptionRessource {

    private SubscriptionService subscriptionService;

    public SubscriptionRessource(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }


    @GetMapping
    public List<Subscription> getAll() {
        return this.subscriptionService.findAll();
    }

    @PostMapping
    public ResponseEntity<Subscription> saveOne(@Valid @RequestBody Subscription subscription) {
        if (subscription.getId() != null) {
            throw new BadRequestBody("A new subscription cannot already have an ID");
        }

        if (subscriptionService.isEmailInDatabase(subscription.getEmail())) {
            throw new ConflitException("Email already in use");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriptionService.save(subscription));
    }
}
