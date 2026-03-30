package Service;

import DBHandling.StiDatabase;
import Models.StiEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StiService {
    public ArrayList<StiEntry> searchByName(List<StiEntry> data, String keyword) {
        if (keyword == null || keyword.isBlank()) return new ArrayList<>(data);
        return data.stream()
                .filter(sti -> sti.getName() != null &&
                        sti.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<StiEntry> searchBySymptoms(List<StiEntry> data, String keyword) {
        if (keyword == null || keyword.isBlank()) return new ArrayList<>(data);

        return data.stream()
                .filter(sti -> sti.getSymptoms() != null &&
                        sti.getSymptoms().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<StiEntry> searchByRiskLevel(List<StiEntry> data, String keyword) {
        if (keyword == null || keyword.isBlank()) return new ArrayList<>(data);

        try {
            int risk = Integer.parseInt(keyword);

            return data.stream()
                    .filter(sti -> sti.getRiskLevel() == risk)
                    .collect(Collectors.toCollection(ArrayList::new));

        } catch (NumberFormatException e) {
            return new ArrayList<>(); // return empty ArrayList
        }
    }

    public static ArrayList<StiEntry> getAll(){
        return StiDatabase.getAll();
    }
}
