package Service;

import DBHandling.StiDatabase;
import Models.StiEntry;
import javafx.concurrent.Task;
import net.bytebuddy.implementation.bytecode.Throw;
import utils.General;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StiService {
    private final StiDatabase stiDatabase = new StiDatabase();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void searchByNameAsync(List<StiEntry> data, String keyword, Consumer<ArrayList<StiEntry>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<ArrayList<StiEntry>> task = new Task<ArrayList<StiEntry>>() {
            @Override
            protected ArrayList<StiEntry> call() throws Exception {
                if (keyword == null || keyword.isBlank()) return new ArrayList<>(data);
                return data.stream()
                        .filter(sti -> sti.getName() != null &&
                                sti.getName().toLowerCase().contains(keyword.toLowerCase()))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        };
        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void searchBySymptomsAsync(List<StiEntry> data, String keyword, Consumer<ArrayList<StiEntry>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<ArrayList<StiEntry>> task = new Task<ArrayList<StiEntry>>() {
            @Override
            protected ArrayList<StiEntry> call() throws Exception {
                if (keyword == null || keyword.isBlank()) return new ArrayList<>(data);
                return data.stream()
                        .filter(sti -> sti.getName() != null &&
                                sti.getSymptoms().toLowerCase().contains(keyword.toLowerCase()))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        };
        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void searchByRiskLevelAsync(List<StiEntry> data, String keyword, Consumer<ArrayList<StiEntry>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<ArrayList<StiEntry>> task = new Task<ArrayList<StiEntry>>() {
            @Override
            protected ArrayList<StiEntry> call() throws Exception {
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
        };
        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void getAllAsync(Consumer<ArrayList<StiEntry>> onSucceeded, Consumer<Throwable> onFailed){
        Task<ArrayList<StiEntry>> task = new Task<ArrayList<StiEntry>>() {
            @Override
            protected ArrayList<StiEntry> call() throws Exception {
                return stiDatabase.getAll();
            }
        };
        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void deleteStiAsync(Integer stiId, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return stiDatabase.delete(stiId);
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void insertStiInfo(StiEntry sti, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return stiDatabase.addSti(sti);
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void updateStiInfo(StiEntry stiEntry, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return stiDatabase.update(stiEntry);
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public String validateInput(String name, String symptoms, String treatment, String prevention, String riskLevel) {
        if (name.isEmpty() || symptoms.isEmpty() || treatment.isEmpty() || prevention.isEmpty() || riskLevel.isEmpty()) {
            return "Please fill in all the blanks";
        } else if (stiDatabase.findByName(name) != null) {
            return "The Sti already exists. Please either delete or edit the already existing input";
        } else {
            try {
                Integer risk = Integer.parseInt(riskLevel);
                if (risk > 4 || risk < 0) {
                    return "risk level should be between 0 - 4";
                }
            } catch (NumberFormatException e) {
                return "Please enter a valid number between 0 - 4 in the risk level input box";
            }
        }
        return "";
    }
}
