/*************************************************************************
 *                                                                       *
 *  EJBCA: The OpenSource Certificate Authority                          *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/

package org.ejbca.core.ejb.ca.store;

import java.io.Serializable;
import java.util.Date;

import org.ejbca.core.model.SecConst;
import org.ejbca.core.model.ca.crl.RevokedCertInfo;

/** Simple class encapsulating the certificate status information needed when making revocation checks.
 * 
 * @version $Id: CertificateStatus.java 8402 2009-12-04 14:00:15Z anatom $
 */
public class CertificateStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final static CertificateStatus REVOKED = new CertificateStatus("REVOKED", SecConst.CERTPROFILE_NO_PROFILE);
    public final static CertificateStatus OK = new CertificateStatus("OK", SecConst.CERTPROFILE_NO_PROFILE);
    public final static CertificateStatus NOT_AVAILABLE = new CertificateStatus("NOT_AVAILABLE", SecConst.CERTPROFILE_NO_PROFILE);

    private final String name;
    public final Date revocationDate;
    /** RevokedCertInfo.NOT_REVOKED etc */
    public final int revocationReason;
    public final int certificateProfileId;
    
    protected CertificateStatus(String s, int certProfileId) {
        this.name = s;
        this.revocationDate = new Date(-1L);
        this.revocationReason = RevokedCertInfo.NOT_REVOKED;
        this.certificateProfileId = certProfileId;
    }
    protected CertificateStatus( long date, int reason, int certProfileId ) {
        this.name = CertificateStatus.REVOKED.name;
        this.revocationDate = new Date(date);
        this.revocationReason = reason;
        this.certificateProfileId = certProfileId;
    }
    public String toString() {
        return this.name;
    }
    public boolean equals(Object obj) {
        return obj instanceof CertificateStatus && this.equals((CertificateStatus)obj);
    }
    public boolean equals(CertificateStatus obj) {
        return this.name.equals(obj.toString());
    }
}
