package eu.europa.esig.dss.validation.process.qualification.trust.consistency;

import eu.europa.esig.dss.diagnostic.TrustServiceWrapper;
import eu.europa.esig.dss.enumerations.ServiceQualification;

import java.util.Collections;
import java.util.List;

/**
 * Verifies whether the applicable qualifiers are known and can be processed by the application
 *
 */
public class TrustServiceQualifiersKnownConsistency implements TrustServiceCondition {

    /**
     * Default constructor
     */
    public TrustServiceQualifiersKnownConsistency() {
        // empty
    }

    @Override
    public boolean isConsistent(TrustServiceWrapper trustService) {
        List<String> capturedQualifiers = trustService.getCapturedQualifierUris();
        for (String qualifier : capturedQualifiers) {
            if (!isQualifierKnown(qualifier)) {
                return false;
            }
        }
        return true;
    }

    public boolean isQualifierKnown(String qualifierUri) {
        List<String> singletonList = Collections.singletonList(qualifierUri);
        return ServiceQualification.isQcWithSSCD(singletonList) || ServiceQualification.isQcNoSSCD(singletonList) ||
                ServiceQualification.isQcSSCDStatusAsInCert(singletonList) || ServiceQualification.isQcWithQSCD(singletonList) ||
                ServiceQualification.isQcNoQSCD(singletonList) || ServiceQualification.isQcQSCDStatusAsInCert(singletonList) ||
                ServiceQualification.isQcQSCDManagedOnBehalf(singletonList) || ServiceQualification.isQcForLegalPerson(singletonList) ||
                ServiceQualification.isQcForEsig(singletonList) || ServiceQualification.isQcForEseal(singletonList) ||
                ServiceQualification.isQcForWSA(singletonList) || ServiceQualification.isNotQualified(singletonList) ||
                ServiceQualification.isQcStatement(singletonList);
    }

}