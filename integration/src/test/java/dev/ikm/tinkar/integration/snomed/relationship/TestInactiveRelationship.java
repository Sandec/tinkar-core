package dev.ikm.tinkar.integration.snomed.relationship;

import dev.ikm.tinkar.entity.StampRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static dev.ikm.tinkar.integration.snomed.core.MockEntity.getNid;
import static dev.ikm.tinkar.integration.snomed.core.SnomedCTConstants.*;
import static dev.ikm.tinkar.integration.snomed.core.SnomedCTConstants.INACTIVE_UUID;
import static dev.ikm.tinkar.integration.snomed.core.SnomedCTHelper.openSession;
import static dev.ikm.tinkar.integration.snomed.core.SnomedCTStampChronology.createSTAMPChronologyForAllRecords;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestInactiveRelationship {

    @Test
    @DisplayName("Test Stamp with Inactive Transform Result for Snomed to Entity Relationship. - One Record")
    public void testStampWithInActiveTransformResultOneRecord(){
       openSession((mockedStaticEntity) -> {
            List<StampRecord> stampRecords =  createSTAMPChronologyForAllRecords(this,"sct2_Relationship_Full_US1000124_20220901_2.txt");
            StampRecord record = stampRecords.get(0);
            assertEquals(getNid(INACTIVE_UUID), record.stateNid(), "State is active");
            assertEquals(getNid(SNOMED_CT_AUTHOR_UUID), record.authorNid(), "Author couldn't be referenced");
            assertEquals(getNid(DEVELOPMENT_PATH_UUID), record.pathNid(), "Path could not be referenced");
            assertEquals(getNid(SNOMED_TEXT_MODULE_ID_UUID), record.moduleNid(), "Module could not be referenced");

        });
    }

    @Test
    @DisplayName("Test Stamp with Inactive Transform Result for Snomed to Entity Relationship. - Many Records")
    public void testStampWithInActiveTransformResultManyRecords(){
       openSession((mockedStaticEntity) -> {
            List<StampRecord> stampRecords =  createSTAMPChronologyForAllRecords(this,"sct2_Relationship_Full_US1000124_20220901_9.txt");//"sct2_Relationship_Full_US1000124_20220901_2.txt");
            for(StampRecord record : stampRecords ){
                assertEquals(getNid(SNOMED_CT_AUTHOR_UUID), record.authorNid(), "Author couldn't be referenced");
                assertEquals(getNid(DEVELOPMENT_PATH_UUID), record.pathNid(), "Path could not be referenced");
                assertEquals(getNid(SNOMED_TEXT_MODULE_ID_UUID), record.moduleNid(), "Module could not be referenced");
            }
            StampRecord record = stampRecords.get(1);
            assertEquals(getNid(INACTIVE_UUID), record.stateNid(), "State is active");
        });
    }
}
