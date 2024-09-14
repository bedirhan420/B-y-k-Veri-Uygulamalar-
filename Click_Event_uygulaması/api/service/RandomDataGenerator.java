package com.xcommerce.bigdata.clickevents.api.service;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class RandomDataGenerator {

    private static final String[] SESSIONS = {
            "Ana Sayfa", "Kıyafetler", "Elektronik", "Ev ve Yaşam", "Kampanyalar",
            "Çocuk", "Spor", "Kozmetik", "Ayakkabılar", "Aksesuarlar", "İndirimler"
    };

    private static final String[] BUTTONS = {
            "Satın Al", "Sepete Ekle", "Ürünü İncele", "Yorumları Oku",
            "Favorilere Ekle", "Karşılaştır", "İndirimi Gör", "Hediye Paketi Seç",
            "Kampanyaları Gör", "Mağaza Bilgisi", "Detayları Gör"
    };

    private static final String[] REGIONS = {
            "North America", "Europe", "Asia", "South America", "Australia",
            "Africa", "Middle East", "Eastern Europe"
    };

    private final Random random = new Random();

    public String generateDeviceId() {
        return UUID.randomUUID().toString();
    }

    public String generateClickButton() {
        int index = random.nextInt(BUTTONS.length);
        return BUTTONS[index];
    }

    public String generateSession() {
        int index = random.nextInt(SESSIONS.length);
        return SESSIONS[index];
    }

    public String generateRegion() {
        int index = random.nextInt(REGIONS.length);
        return REGIONS[index];
    }

    public String generateTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }
}
