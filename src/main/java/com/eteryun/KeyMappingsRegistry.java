package com.eteryun;

import com.eteryun.mixin.client.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KeyMappingsRegistry {
    private static final List<KeyMapping> keyMappings = new ArrayList<>();

    private static Map<String, Integer> getCategories() {
        return KeyMappingAccessor.getCategories();
    }

    private static boolean hasCategory(String categoryTranslationKey) {
        return getCategories().containsKey(categoryTranslationKey);
    }

    public static boolean addCategory(String categoryTranslationKey) {
        Map<String, Integer> map = getCategories();
        if (map.containsKey(categoryTranslationKey))
            return false;
        Optional<Integer> largest = map.values().stream().max(Integer::compareTo);
        int largestInt = largest.orElse(0);
        map.put(categoryTranslationKey, largestInt + 1);
        return true;
    }

    public static KeyMapping registerKeyMapping(KeyMapping keyMapping) {
        if (!hasCategory(keyMapping.getCategory()))
            addCategory(keyMapping.getCategory());
        return keyMappings.add(keyMapping) ? keyMapping : null;
    }

    public static KeyMapping[] process(KeyMapping[] allKeyMappings) {
        List<KeyMapping> newKeyMappings = new ArrayList<>(List.of(allKeyMappings));
        newKeyMappings.removeAll(keyMappings);
        newKeyMappings.addAll(keyMappings);
        return newKeyMappings.toArray(new KeyMapping[0]);
    }
}
