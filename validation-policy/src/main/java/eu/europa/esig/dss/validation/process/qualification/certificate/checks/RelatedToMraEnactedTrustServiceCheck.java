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
package eu.europa.esig.dss.validation.process.qualification.certificate.checks;

import eu.europa.esig.dss.detailedreport.jaxb.XmlConstraintsConclusion;
import eu.europa.esig.dss.diagnostic.TrustServiceWrapper;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.policy.jaxb.LevelConstraint;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.process.ChainItem;

import java.util.List;

/**
 * This class verifies whether MRA enacted trusted services are present
 *
 * @param <T> implementation of the block's conclusion
 */
public class RelatedToMraEnactedTrustServiceCheck<T extends XmlConstraintsConclusion> extends ChainItem<T> {

    /** List of {@code TrustServiceWrapper}s at control time */
    private final List<TrustServiceWrapper> trustServicesAtTime;

    /**
     * Default constructor
     *
     * @param i18nProvider {@link I18nProvider}
     * @param result {@link XmlConstraintsConclusion}
     * @param trustServicesAtTime list of {@link TrustServiceWrapper}s
     * @param constraint {@link LevelConstraint}
     */
    public RelatedToMraEnactedTrustServiceCheck(I18nProvider i18nProvider, T result,
                                                List<TrustServiceWrapper> trustServicesAtTime, LevelConstraint constraint) {
        super(i18nProvider, result, constraint);
        this.trustServicesAtTime = trustServicesAtTime;
    }

    @Override
    protected boolean process() {
        return Utils.isCollectionNotEmpty(trustServicesAtTime);
    }

    @Override
    protected MessageTag getMessageTag() {
        return MessageTag.QUAL_HAS_METS;
    }

    @Override
    protected MessageTag getErrorMessageTag() {
        return MessageTag.QUAL_HAS_METS_ANS;
    }

    @Override
    protected Indication getFailedIndicationForConclusion() {
        return Indication.FAILED;
    }

    @Override
    protected SubIndication getFailedSubIndicationForConclusion() {
        return null;
    }

}
