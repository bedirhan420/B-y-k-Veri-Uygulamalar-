package com.xeticaret.bigdata.autocomplete.api.interfaces;

import com.xeticaret.bigdata.autocomplete.api.model.AutocompleteResponse;

public interface IAutocompleteService {
    AutocompleteResponse search(String term);
}
