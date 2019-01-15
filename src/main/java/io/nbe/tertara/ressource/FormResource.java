package io.nbe.tertara.ressource;

import io.nbe.tertara.exception.ResourceNotFoundException;
import io.nbe.tertara.model.Form;
import io.nbe.tertara.service.FormService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FormResource {

    private FormService formService;

    public FormResource(FormService formService) {
        this.formService = formService;
    }

    @GetMapping("/forms/latest")
    public Form getLatestForm() {
        return this.formService.getLatestForm().orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    @PostMapping("/forms/reset")
    public Form resetForm() {
        return this.formService.resetView();
    }

    @PostMapping("/forms/{formId}/start")
    public Form startForm(@PathVariable Long formId) {
        return this.formService.startForm(formId).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    @PostMapping("/forms/{formId}/terminate")
    public Form terminate(@PathVariable Long formId) {
        return this.formService.setFormFinished(formId).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }
}
