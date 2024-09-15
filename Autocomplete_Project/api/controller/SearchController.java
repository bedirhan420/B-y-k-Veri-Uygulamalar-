package com.xeticaret.bigdata.autocomplete.api.controller;

import com.xeticaret.bigdata.autocomplete.api.model.AutocompleteResponse;
import com.xeticaret.bigdata.autocomplete.api.interfaces.IAutocompleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    private final IAutocompleteService autocompleteService;

    @Autowired
    public SearchController(IAutocompleteService autocompleteService) {
        this.autocompleteService = autocompleteService;
    }

    @GetMapping("/autocomplete")
    public AutocompleteResponse autocomplete(@RequestParam String term) {
        return autocompleteService.search(term);
    }
}
