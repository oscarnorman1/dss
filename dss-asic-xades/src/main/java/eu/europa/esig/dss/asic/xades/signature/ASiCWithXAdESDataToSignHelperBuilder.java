/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.asic.xades.signature;

import eu.europa.esig.dss.DomUtils;
import eu.europa.esig.dss.asic.common.ASiCContent;
import eu.europa.esig.dss.asic.common.ASiCUtils;
import eu.europa.esig.dss.asic.common.signature.AbstractASiCDataToSignHelperBuilder;
import eu.europa.esig.dss.asic.xades.ASiCWithXAdESContainerExtractor;
import eu.europa.esig.dss.asic.xades.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.asic.xades.OpenDocumentSupportUtils;
import eu.europa.esig.dss.asic.xades.signature.asice.ASiCEWithXAdESManifestBuilder;
import eu.europa.esig.dss.asic.xades.signature.asice.DataToSignASiCEWithXAdESHelper;
import eu.europa.esig.dss.asic.xades.signature.asice.DataToSignOpenDocumentHelper;
import eu.europa.esig.dss.asic.xades.signature.asics.DataToSignASiCSWithXAdESHelper;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Builds a relevant {@code GetDataToSignASiCWithXAdESHelper} for ASiC with XAdES dataToSign creation
 */
public class ASiCWithXAdESDataToSignHelperBuilder extends AbstractASiCDataToSignHelperBuilder {

	/** The default manifest filename */
	private static final String ZIP_ENTRY_ASICE_METAINF_MANIFEST = ASiCUtils.META_INF_FOLDER + "manifest.xml";

	/**
	 * Builds a {@code GetDataToSignASiCWithXAdESHelper} from the given list of
	 * documents and defined parameters
	 * 
	 * @param documents  a list of {@link DSSDocument}s to get a helper from
	 * @param parameters {@link ASiCWithXAdESSignatureParameters}
	 * @return {@link GetDataToSignASiCWithXAdESHelper}
	 */
	public GetDataToSignASiCWithXAdESHelper build(List<DSSDocument> documents, ASiCWithXAdESSignatureParameters parameters) {
		if (Utils.isCollectionNotEmpty(documents) && documents.size() == 1) {
			DSSDocument archiveDocument = documents.get(0);
			if (ASiCUtils.isZip(archiveDocument)) {
				return fromZipArchive(archiveDocument, parameters);
			}
		}
		return fromFiles(documents, parameters);
	}
	
	private GetDataToSignASiCWithXAdESHelper fromZipArchive(DSSDocument archiveDocument, ASiCWithXAdESSignatureParameters parameters) {

		boolean asice = ASiCUtils.isASiCE(parameters.aSiC());
		
		ASiCWithXAdESContainerExtractor extractor = new ASiCWithXAdESContainerExtractor(archiveDocument);
		ASiCContent asicContent = extractor.extract();
		assertContainerTypeValid(asicContent);
		
		if (ASiCUtils.isOpenDocument(asicContent.getMimeTypeDocument())) {
			asicContent = moveExternalDataContent(asicContent);
			return new DataToSignOpenDocumentHelper(asicContent, parameters.aSiC());
		}

		// if ASiC with XAdES (no detached timestamps are allowed)
		if (Utils.isCollectionNotEmpty(asicContent.getSignatureDocuments())) {

			ASiCContainerType currentContainerType = ASiCUtils.getContainerType(archiveDocument,
					asicContent.getMimeTypeDocument(), asicContent.getZipComment(), asicContent.getSignedDocuments());

			if (asice && ASiCContainerType.ASiC_E.equals(currentContainerType)) {
				return new DataToSignASiCEWithXAdESHelper(asicContent, parameters.aSiC());
			} else if (!asice && ASiCContainerType.ASiC_S.equals(currentContainerType)) {
				return new DataToSignASiCSWithXAdESHelper(asicContent, parameters.aSiC());
			} else {
				throw new UnsupportedOperationException(
						String.format("Original container type '%s' vs parameter : '%s'", currentContainerType,
								parameters.aSiC().getContainerType()));
			}

		}

		return fromFiles(Arrays.asList(archiveDocument), parameters);
	}

	private GetDataToSignASiCWithXAdESHelper fromFiles(List<DSSDocument> documents,
			ASiCWithXAdESSignatureParameters parameters) {
		assertDocumentNamesDefined(documents);
		ASiCContent asicContent = new ASiCContent();
		if (ASiCUtils.isASiCE(parameters.aSiC())) {
			asicContent.setSignedDocuments(documents);
			DSSDocument asicManifest = createASiCManifest(documents);
			asicContent.getManifestDocuments().add(asicManifest);
			return new DataToSignASiCEWithXAdESHelper(asicContent, parameters.aSiC());
		} else {
			DSSDocument asicsSignedDocument = getASiCSSignedDocument(documents, parameters.bLevel().getSigningDate(), parameters.aSiC());
			asicContent.setSignedDocuments(Collections.singletonList(asicsSignedDocument));
			return new DataToSignASiCSWithXAdESHelper(asicContent, parameters.aSiC());
		}
	}

	private static void assertContainerTypeValid(ASiCContent result) {
		if (ASiCUtils.filesContainSignatures(DSSUtils.getDocumentNames(result.getAllDocuments()))
				&& Utils.isCollectionEmpty(result.getSignatureDocuments())) {
			throw new UnsupportedOperationException("Container type doesn't match");
		}
	}

	/**
	 * Returns the ASiC Manifest
	 *
	 * @param documents a list of {@link DSSDocument}s to cover by the manifest
	 * @return {@link DSSDocument} manifest
	 */
	private DSSDocument createASiCManifest(List<DSSDocument> documents) {
		ASiCEWithXAdESManifestBuilder manifestBuilder = new ASiCEWithXAdESManifestBuilder(documents);
		return DomUtils.createDssDocumentFromDomDocument(manifestBuilder.build(), ZIP_ENTRY_ASICE_METAINF_MANIFEST);
	}

	/**
	 * Special method for OpenDocument processing, moving "/external-data" documents to unsigned documents
	 *
	 * @param asicContent {@link ASiCContent} extracted content
	 * @return {@link ASiCContent} processed content
	 */
	private ASiCContent moveExternalDataContent(ASiCContent asicContent) {
		List<DSSDocument> signedDocuments = asicContent.getSignedDocuments();
		List<DSSDocument> unsupportedDocuments = asicContent.getUnsupportedDocuments();
		Iterator<DSSDocument> iterator = signedDocuments.iterator();
		while (iterator.hasNext()) {
			DSSDocument document = iterator.next();
			if (OpenDocumentSupportUtils.isExternalDataDocument(document)) {
				unsupportedDocuments.add(document);
				iterator.remove();
			}
		}
		return asicContent;
	}

}
