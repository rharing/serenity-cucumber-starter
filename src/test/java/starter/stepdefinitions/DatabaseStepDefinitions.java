package starter.stepdefinitions;

import io.cucumber.java.nl.Als;
import io.cucumber.java.nl.En;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import starter.helpers.SnapshotManager;

@ContextConfiguration(classes = SnapshotManager.class)
public class DatabaseStepDefinitions {
    @Autowired
    private SnapshotManager snapshotManager;
    @Als("ik een snapshot maak van de database")
    public void ikEenSnapshotMaakVanDeDatabase() {
        ikEenSnapshotMaakVanDeDatabaseMetNaam(null);
    }
    @Als("ik een snapshot maak van de database met de naam \"([^\"]*)\"$")
    public void ikEenSnapshotMaakVanDeDatabaseMetNaam(String snapshotName) {

        snapshotManager.createSnapShot(snapshotName);
    }

    @Als("ik de database terugzet van de snapshot")
    public void ik_de_database_terugzet_van_de_snapshot() {
        // Write code here that turns the phrase above into concrete actions
    }

}
