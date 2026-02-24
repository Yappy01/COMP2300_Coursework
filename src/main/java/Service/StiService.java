package Service;

import DBHandling.StiDatabase;
import Models.StiEntry;
import java.util.ArrayList;
import java.util.List;

public class StiService {
    public List<StiEntry> searchByName(List<StiEntry> data, String keyword) {
        if (keyword == null || keyword.isBlank()) return data;

        return data.stream()
                .filter(sti -> sti.getName().toLowerCase()
                        .contains(keyword.toLowerCase()))
                .toList();
    }

    public List<StiEntry> searchBySymptoms(List<StiEntry> data, String keyword) {
        if (keyword == null || keyword.isBlank()) return data;

        return data.stream()
                .filter(sti -> sti.getSymptoms().toLowerCase()
                        .contains(keyword.toLowerCase()))
                .toList();
    }

    public List<StiEntry> searchByRiskLevel(List<StiEntry> data, String keyword) {
        if (keyword == null || keyword.isBlank()) return data;

        try {
            int risk = Integer.parseInt(keyword);

            return data.stream()
                    .filter(sti -> sti.getRiskLevel() == risk)
                    .toList();

        } catch (NumberFormatException e) {
            return List.of();
        }
    }
}
