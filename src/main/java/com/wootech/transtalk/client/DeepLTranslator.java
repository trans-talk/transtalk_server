package com.wootech.transtalk.client;

import com.deepl.api.DeepLClient;
import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.wootech.transtalk.service.translate.Translator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DeepLTranslator implements Translator {
    @Value("${deepl.key}")
    private String authKey;
    @Override
    public String translate(String textToTranslate, String targetLanguage) {
        try {
            DeepLClient client = new com.deepl.api.DeepLClient(authKey);
            TextResult translatedText = client.translateText(textToTranslate, null, targetLanguage);
            return translatedText.getText();
        } catch (DeepLException | InterruptedException e) {
            throw new RuntimeException("");
        }
    }
}
