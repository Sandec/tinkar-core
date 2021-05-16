package org.hl7.tinkar.integration.coordinate;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.set.ImmutableSet;
import org.hl7.tinkar.common.id.IntIdList;
import org.hl7.tinkar.common.service.PrimitiveData;
import org.hl7.tinkar.common.service.ServiceKeys;
import org.hl7.tinkar.common.service.ServiceProperties;
import org.hl7.tinkar.coordinate.Calculators;
import org.hl7.tinkar.coordinate.Coordinates;
import org.hl7.tinkar.coordinate.language.LanguageCoordinateRecord;
import org.hl7.tinkar.coordinate.language.calculator.LanguageCalculatorWithCache;
import org.hl7.tinkar.coordinate.stamp.StampPositionRecord;
import org.hl7.tinkar.coordinate.stamp.calculator.PathProvider;
import org.hl7.tinkar.coordinate.stamp.calculator.StampCalculatorWithCache;
import org.hl7.tinkar.coordinate.stamp.StampFilterRecord;
import org.hl7.tinkar.coordinate.view.calculator.ViewCalculator;
import org.hl7.tinkar.entity.*;
import org.hl7.tinkar.coordinate.stamp.calculator.Latest;
import org.hl7.tinkar.entity.internal.Get;
import org.hl7.tinkar.integration.TestConstants;
import org.hl7.tinkar.terms.TinkarTerm;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.Optional;
import java.util.logging.Logger;

import static org.hl7.tinkar.terms.TinkarTerm.PATH_ORIGINS_PATTERN;

class CoordinatesTest {
    private static Logger LOG = Logger.getLogger(CoordinatesTest.class.getName());

    @BeforeSuite
    public void setupSuite() {
        LOG.info("setupSuite: " + this.getClass().getSimpleName());
        LOG.info(ServiceProperties.jvmUuid());
        ServiceProperties.set(ServiceKeys.DATA_STORE_ROOT, TestConstants.MVSTORE_ROOT);
        PrimitiveData.selectControllerByName(TestConstants.MV_STORE_OPEN_NAME);
        PrimitiveData.start();
    }

    @Test
    void countPathOrigins() {
        Assert.assertEquals(PrimitiveData.get().entityNidsOfPattern(PATH_ORIGINS_PATTERN.nid()).length, 3);
    }

    @Test
    void pathOrigins() {
        for (int pathNid : PrimitiveData.get().entityNidsOfPattern(PATH_ORIGINS_PATTERN.nid())) {
            SemanticEntity originSemantic = EntityService.get().getEntityFast(pathNid);
            Entity pathEntity = EntityService.get().getEntityFast(originSemantic.referencedComponentNid());
            ImmutableSet<StampPositionRecord> origin = PathProvider.getPathOrigins(originSemantic.referencedComponentNid());
            LOG.info("Path '" + PrimitiveData.text(pathEntity.nid()) + "' has an origin of: " + origin);
        }
    }

    @Test
    void computeLatest() {
        StampFilterRecord developmentLatestFilter = Coordinates.Stamp.DevelopmentLatest();
        LOG.info("development latest filter '" + developmentLatestFilter);
        ConceptEntity englishLanguage = Entity.getFast(TinkarTerm.ENGLISH_LANGUAGE);
        StampCalculatorWithCache calculator = StampCalculatorWithCache.getCalculator(developmentLatestFilter);
        Latest<ConceptEntityVersion> latest = calculator.latest(englishLanguage);
        LOG.info("Latest computed: '" + latest);

        Entity.provider().forEachSemanticForComponent(TinkarTerm.ENGLISH_LANGUAGE.nid(), semanticEntity -> {
            LOG.info(semanticEntity.toString() + "\n");
            for (int acceptibilityNid : Get.entityService().semanticNidsForComponentOfPattern(semanticEntity.nid(), TinkarTerm.US_DIALECT_PATTERN.nid())) {
                LOG.info("  Acceptability US: \n    " + Get.entityService().getEntityFast(acceptibilityNid));
            }
        });
        Entity.provider().forEachSemanticForComponent(TinkarTerm.NECESSARY_SET.nid(), semanticEntity -> {
            LOG.info(semanticEntity.toString() + "\n");
            for (int acceptibilityNid : Get.entityService().semanticNidsForComponentOfPattern(semanticEntity.nid(), TinkarTerm.US_DIALECT_PATTERN.nid())) {
                LOG.info("  Acceptability US: \n    " + Get.entityService().getEntityFast(acceptibilityNid));
            }
        });
    }

    @Test
    void names() {
        LanguageCoordinateRecord usFqn = Coordinates.Language.UsEnglishFullyQualifiedName();
        LanguageCalculatorWithCache usFqnCalc = LanguageCalculatorWithCache.getCalculator(Coordinates.Stamp.DevelopmentLatest(), Lists.immutable.of(usFqn));
        LOG.info("fqn: " + usFqnCalc.getDescriptionText(TinkarTerm.NECESSARY_SET) + "\n");
        LOG.info("reg: " + usFqnCalc.getRegularDescriptionText(TinkarTerm.NECESSARY_SET) + "\n");
        LOG.info("def: " + usFqnCalc.getDefinitionDescriptionText(TinkarTerm.NECESSARY_SET) + "\n");
    }

    @Test
    void navigate() {
        ViewCalculator viewCalculator = Calculators.View.Default();
        IntIdList children = viewCalculator.children(TinkarTerm.DESCRIPTION_ACCEPTABILITY);
        StringBuilder sb = new StringBuilder("Focus: [");
        Optional<String> optionalName = viewCalculator.getRegularDescriptionText(TinkarTerm.DESCRIPTION_ACCEPTABILITY);
        optionalName.ifPresentOrElse(name -> sb.append(name), () -> sb.append(TinkarTerm.DESCRIPTION_ACCEPTABILITY.nid()));
        sb.append("]\nchildren: [");
        for (int childNid: children.toArray()) {
            optionalName = viewCalculator.getRegularDescriptionText(childNid);
            optionalName.ifPresentOrElse(name -> sb.append(name), () -> sb.append(childNid));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("]\nparents: [");
        IntIdList parents = viewCalculator.parents(TinkarTerm.DESCRIPTION_ACCEPTABILITY);
        for (int parentNid: parents.toArray()) {
            optionalName = viewCalculator.getRegularDescriptionText(parentNid);
            optionalName.ifPresentOrElse(name -> sb.append(name), () -> sb.append(parentNid));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("]\n");
        LOG.info(sb.toString());
    }

    @Test
    void sortedNavigate() {
        ViewCalculator viewCalculator = Calculators.View.Default();
        IntIdList children = viewCalculator.sortedChildren(TinkarTerm.DESCRIPTION_ACCEPTABILITY);
        StringBuilder sb = new StringBuilder("Focus: [");
        Optional<String> optionalName = viewCalculator.getRegularDescriptionText(TinkarTerm.DESCRIPTION_ACCEPTABILITY);
        optionalName.ifPresentOrElse(name -> sb.append(name), () -> sb.append(TinkarTerm.DESCRIPTION_ACCEPTABILITY.nid()));
        sb.append("]\nsorted children: [");
        for (int childNid: children.toArray()) {
            optionalName = viewCalculator.getRegularDescriptionText(childNid);
            optionalName.ifPresentOrElse(name -> sb.append(name), () -> sb.append(childNid));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("]\nsorted parents: [");
        IntIdList parents = viewCalculator.sortedParents(TinkarTerm.DESCRIPTION_ACCEPTABILITY);
        for (int parentNid: parents.toArray()) {
            optionalName = viewCalculator.getRegularDescriptionText(parentNid);
            optionalName.ifPresentOrElse(name -> sb.append(name), () -> sb.append(parentNid));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("]\n");
        LOG.info(sb.toString());
    }

}