package io.nbe.tertara.service;

import io.nbe.tertara.model.Subscription;
import io.nbe.tertara.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    private SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }


    public Subscription save(Subscription sub) {
        return this.subscriptionRepository.save(sub);
    }

    public List<Subscription> findAll() {
        return this.subscriptionRepository.findAll();
    }
}
