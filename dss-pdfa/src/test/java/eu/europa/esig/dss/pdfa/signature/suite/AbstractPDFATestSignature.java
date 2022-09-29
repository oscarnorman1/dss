package eu.europa.esig.dss.pdfa.signature.suite;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.pades.signature.suite.AbstractPAdESTestSignature;
import eu.europa.esig.dss.pdfa.validation.PDFADocumentValidator;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.SignedDocumentValidator;

import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractPDFATestSignature extends AbstractPAdESTestSignature {

    @Override
    protected SignedDocumentValidator getValidator(DSSDocument signedDocument) {
        PDFADocumentValidator validator = new PDFADocumentValidator(signedDocument);
        validator.setCertificateVerifier(getOfflineCertificateVerifier());
        return validator;
    }

    @Override
    protected void checkPDFAInfo(DiagnosticData diagnosticData) {
        super.checkPDFAInfo(diagnosticData);

        assertTrue(diagnosticData.isPDFAValidationPerformed());
        assertTrue(diagnosticData.isPDFACompliant(), diagnosticData.getPDFAValidationErrors().toString());
        assertTrue(Utils.isCollectionEmpty(diagnosticData.getPDFAValidationErrors()));
    }

}