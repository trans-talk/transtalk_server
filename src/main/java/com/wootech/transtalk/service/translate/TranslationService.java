package com.wootech.transtalk.service.translate;

import org.springframework.stereotype.Service;

@Service
public class TranslationService {
    private final Translator translator;

    public TranslationService(Translator translator) {
        this.translator = translator;
    }

    public String translate(String text, String targetLanguage) {
        return translator.translate(text, targetLanguage);
    }
}
