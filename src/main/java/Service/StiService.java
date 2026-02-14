package Service;

import DBHandling.StiDatabase;
import Models.StiEntry;
import java.util.ArrayList;

public class StiService {
    public ArrayList<StiEntry> searchSti(String keyword) {
        // Can add business logic here, e.g., normalize input
        keyword = keyword.toLowerCase().trim();
        return StiDatabase.searchBySymptom(keyword);
    }
}
