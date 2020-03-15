package de.bright_side.brightprojectchart.dao;

import de.bright_side.brightprojectchart.model.ProjectPlan;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProjectPlanExcelDAOTest {
    private static final Logger LOGGER = LogManager.getLogger(ProjectPlanExcelDAOTest.class);

    @Test
    public void readColor_normalCase(){
        ProjectPlan plan = new ProjectPlan();
        new ProjectPlanExcelDAO().readColor("128,128,128", plan);

        log("Errors: ", plan.getErrors());
        assertEquals(null, plan.getErrors());
    }

    private void log(String message, List<String> errorsList) {
        Configurator.setRootLevel(Level.ALL);

        LOGGER.info(message);
        if (errorsList != null){
            for (String i: errorsList){
                LOGGER.info(" - "  + i);
            }
        }
    }
}
