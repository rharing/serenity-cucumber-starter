package starter.stepdefinitions;

import io.cucumber.java.nl.En;
import starter.helpers.SnapshotManager;

public class DatabaseStepDefinitions {
    @Autowired
    private SnapshotManager snapshotManager;
    @En("ik een snapshot maak van de database")
    public void ikEenSnapshotMaakVanDeDatabase() {
    }
}
