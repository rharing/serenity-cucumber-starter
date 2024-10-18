package starter.stepdefinitions;

import io.cucumber.java.nl.Als;
import io.cucumber.java.nl.Gegeven;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;

@Slf4j
public class DatabaseStepDefinitions {


    @Als("ik de database terugzet van de snapshot")
    public void ik_de_database_terugzet_van_de_snapshot() {
        log.info("ik_de_database_terugzet_van_de_snapshot");
    }

    @Gegeven("ik een snapshot maak van de database")
    public void ik_een_snapshot_maak_van_de_database() {
        ik_een_snapshot_maak_van_de_database_metNaam(null);
    }

        @Gegeven("ik een snapshot maak van de database voor \"([^\"]*)\"$")
    public void ik_een_snapshot_maak_van_de_database_metNaam(String snapshotName) {
        if (StringUtils.isBlank(snapshotName)) {
            snapshotName = "default";
        }
        log.info("ik_een_snapshot_maak_van_de_database voor " + snapshotName);
    }

}
