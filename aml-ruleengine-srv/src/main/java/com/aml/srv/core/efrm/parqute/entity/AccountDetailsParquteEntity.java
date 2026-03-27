package com.aml.srv.core.efrm.parqute.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;


public class AccountDetailsParquteEntity implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "ACCOUNTNO")
    private String accountno;

    @Column(name = "CUSTOMERID")
    private String customerid;

    @Column(name = "ACCOUNTTYPE")
    private String accounttype;
    
    @Column(name = "BRANCHCODE")
    private String branchcode;
    
    @Column(name = "ACCOUNTOPENEDDATE")
    private String accountOpenedDate;

    @Column(name = "ACCOUNTSTATUS")
    private String status;
    
    
    @Column(name = "BANKCODE")
    private String bankCode;


    @Column(name = "PRODUCTCODE")
    private Long productCode;
    
    @Column(name = "MODEOFOPERATION")
    private String modeOfOperation;

    @Column(name = "ODLIMIT")
    private Long odLimit;

    @Column(name = "ACCOUNTCURRENCY")
    private String accountCurrency;

    @Column(name = "CURRENTBALANCE")
    private Long currentBalance;

    @Column(name = "NOOFDAYSNOTOPERATED")
    private String noOfDaysNotOperated;

  

    @Column(name = "ACCOUNTCLOSEDDATE")
    private String accountClosedDate;

    @Column(name = "MATURITYDATE")
    private String maturityDate;

    @Column(name = "LOANASSETSTATUS")
    private String loanAssetStatus;

    @Column(name = "ATMCARDNO")
    private String atmCardNo;

    @Column(name = "CREDITCARDNO")
    private String creditCardNo;

    @Column(name = "PURPOSEOFOPENINGACCOUNT")
    private String purposeOfOpeningAccount;

    @Column(name = "EXPECTEDTURNOVER")
    private Long expectedTurnover;

    @Column(name = "ADDRESSPROOF")
    private String addressProof;

    @Column(name = "CERTIFICATIONOFINCORPORATION")
    private String certificationOfIncorporation;

    @Column(name = "MOA")
    private String moa;

    @Column(name = "AGREEMENTFORFUNDSTRANSFER")
    private String agreementForFundsTransfer;

    @Column(name = "FOREIGNCUSTOMERRBIAPPROVAL")
    private String foreignCustomerRbiApproval;

    @Column(name = "PARTNERSHIPAGREEMENT")
    private String partnershipAgreement;

    @Column(name = "AUTHORISEDTORECEIVEFOREX")
    private String authorisedToReceiveForex;

    @Column(name = "RISKRATING")
    private String riskRating;

    @Column(name = "ISCATEGORIZED")
    private String isCategorized;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "UPDATETIMESTAMP")
    private String updateTimestamp;

    @Column(name = "ROI")
    private String roi;

    @Column(name = "SOURCEOFFUNDS")
    private String sourceOfFunds;

    @Column(name = "SCORE")
    private String score;

    @Column(name = "RESIDENTIALSTATUS")
    private String residentialStatus;

    @Column(name = "INTRODUCERPROOF")
    private String introducerProof;

    @Column(name = "FIRSTACCOUNTOPENEDDATE")
    private String firstAccountOpenedDate;

    @Column(name = "SOURCETYPE")
    private String sourceType;

    @Column(name = "INDUSTRYCODE")
    private String industryCode;

    @Column(name = "IDENTITYPROOF")
    private String identityProof;

    @Column(name = "EXPECTEDFOREXTXNVALUE")
    private Long expectedForexTxnValue;

    @Column(name = "ACCOUNTOPENLOGINID")
    private String accountOpenLoginId;

    @Column(name = "ACCOUNTLASTUPDATELOGINID")
    private String accountLastUpdateLoginId;

    @Column(name = "ACCOUNTLASTUPDATEDDATE")
    private String accountLastUpdatedDate;

    @Column(name = "LASTTRANSACTIONDATE")
    private String lastTransactionDate;

    @Column(name = "DEPOSITAMOUNT")
    private BigDecimal depositAmount;

    @Column(name = "AVAILABLEBALANCE")
    private Long availableBalance;

    @Column(name = "TOTALLOANSANCTIONAMOUNT")
    private Long totalLoanSanctionAmount;

	public String getAccountno() {
		return accountno;
	}

	public void setAccountno(String accountno) {
		this.accountno = accountno;
	}

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}

	public String getAccounttype() {
		return accounttype;
	}

	public void setAccounttype(String accounttype) {
		this.accounttype = accounttype;
	}

	public String getBranchcode() {
		return branchcode;
	}

	public void setBranchcode(String branchcode) {
		this.branchcode = branchcode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getProductCode() {
		return productCode;
	}

	public void setProductCode(Long productCode) {
		this.productCode = productCode;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	

	public String getAccountOpenedDate() {
		return accountOpenedDate;
	}

	public void setAccountOpenedDate(String accountOpenedDate) {
		this.accountOpenedDate = accountOpenedDate;
	}

	public String getModeOfOperation() {
		return modeOfOperation;
	}

	public void setModeOfOperation(String modeOfOperation) {
		this.modeOfOperation = modeOfOperation;
	}

	public Long getOdLimit() {
		return odLimit;
	}

	public void setOdLimit(Long odLimit) {
		this.odLimit = odLimit;
	}

	public String getAccountCurrency() {
		return accountCurrency;
	}

	public void setAccountCurrency(String accountCurrency) {
		this.accountCurrency = accountCurrency;
	}

	public Long getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(Long currentBalance) {
		this.currentBalance = currentBalance;
	}

	public String getNoOfDaysNotOperated() {
		return noOfDaysNotOperated;
	}

	public void setNoOfDaysNotOperated(String noOfDaysNotOperated) {
		this.noOfDaysNotOperated = noOfDaysNotOperated;
	}

	public String getAccountClosedDate() {
		return accountClosedDate;
	}

	public void setAccountClosedDate(String accountClosedDate) {
		this.accountClosedDate = accountClosedDate;
	}

	public String getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getLoanAssetStatus() {
		return loanAssetStatus;
	}

	public void setLoanAssetStatus(String loanAssetStatus) {
		this.loanAssetStatus = loanAssetStatus;
	}

	public String getAtmCardNo() {
		return atmCardNo;
	}

	public void setAtmCardNo(String atmCardNo) {
		this.atmCardNo = atmCardNo;
	}

	public String getCreditCardNo() {
		return creditCardNo;
	}

	public void setCreditCardNo(String creditCardNo) {
		this.creditCardNo = creditCardNo;
	}

	public String getPurposeOfOpeningAccount() {
		return purposeOfOpeningAccount;
	}

	public void setPurposeOfOpeningAccount(String purposeOfOpeningAccount) {
		this.purposeOfOpeningAccount = purposeOfOpeningAccount;
	}

	public Long getExpectedTurnover() {
		return expectedTurnover;
	}

	public void setExpectedTurnover(Long expectedTurnover) {
		this.expectedTurnover = expectedTurnover;
	}

	public String getAddressProof() {
		return addressProof;
	}

	public void setAddressProof(String addressProof) {
		this.addressProof = addressProof;
	}

	public String getCertificationOfIncorporation() {
		return certificationOfIncorporation;
	}

	public void setCertificationOfIncorporation(String certificationOfIncorporation) {
		this.certificationOfIncorporation = certificationOfIncorporation;
	}

	public String getMoa() {
		return moa;
	}

	public void setMoa(String moa) {
		this.moa = moa;
	}

	public String getAgreementForFundsTransfer() {
		return agreementForFundsTransfer;
	}

	public void setAgreementForFundsTransfer(String agreementForFundsTransfer) {
		this.agreementForFundsTransfer = agreementForFundsTransfer;
	}

	public String getForeignCustomerRbiApproval() {
		return foreignCustomerRbiApproval;
	}

	public void setForeignCustomerRbiApproval(String foreignCustomerRbiApproval) {
		this.foreignCustomerRbiApproval = foreignCustomerRbiApproval;
	}

	public String getPartnershipAgreement() {
		return partnershipAgreement;
	}

	public void setPartnershipAgreement(String partnershipAgreement) {
		this.partnershipAgreement = partnershipAgreement;
	}

	public String getAuthorisedToReceiveForex() {
		return authorisedToReceiveForex;
	}

	public void setAuthorisedToReceiveForex(String authorisedToReceiveForex) {
		this.authorisedToReceiveForex = authorisedToReceiveForex;
	}

	public String getRiskRating() {
		return riskRating;
	}

	public void setRiskRating(String riskRating) {
		this.riskRating = riskRating;
	}

	public String getIsCategorized() {
		return isCategorized;
	}

	public void setIsCategorized(String isCategorized) {
		this.isCategorized = isCategorized;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(String updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public String getRoi() {
		return roi;
	}

	public void setRoi(String roi) {
		this.roi = roi;
	}

	public String getSourceOfFunds() {
		return sourceOfFunds;
	}

	public void setSourceOfFunds(String sourceOfFunds) {
		this.sourceOfFunds = sourceOfFunds;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getResidentialStatus() {
		return residentialStatus;
	}

	public void setResidentialStatus(String residentialStatus) {
		this.residentialStatus = residentialStatus;
	}

	public String getIntroducerProof() {
		return introducerProof;
	}

	public void setIntroducerProof(String introducerProof) {
		this.introducerProof = introducerProof;
	}

	public String getFirstAccountOpenedDate() {
		return firstAccountOpenedDate;
	}

	public void setFirstAccountOpenedDate(String firstAccountOpenedDate) {
		this.firstAccountOpenedDate = firstAccountOpenedDate;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getIndustryCode() {
		return industryCode;
	}

	public void setIndustryCode(String industryCode) {
		this.industryCode = industryCode;
	}

	public String getIdentityProof() {
		return identityProof;
	}

	public void setIdentityProof(String identityProof) {
		this.identityProof = identityProof;
	}

	public Long getExpectedForexTxnValue() {
		return expectedForexTxnValue;
	}

	public void setExpectedForexTxnValue(Long expectedForexTxnValue) {
		this.expectedForexTxnValue = expectedForexTxnValue;
	}

	public String getAccountOpenLoginId() {
		return accountOpenLoginId;
	}

	public void setAccountOpenLoginId(String accountOpenLoginId) {
		this.accountOpenLoginId = accountOpenLoginId;
	}

	public String getAccountLastUpdateLoginId() {
		return accountLastUpdateLoginId;
	}

	public void setAccountLastUpdateLoginId(String accountLastUpdateLoginId) {
		this.accountLastUpdateLoginId = accountLastUpdateLoginId;
	}

	public String getAccountLastUpdatedDate() {
		return accountLastUpdatedDate;
	}

	public void setAccountLastUpdatedDate(String accountLastUpdatedDate) {
		this.accountLastUpdatedDate = accountLastUpdatedDate;
	}

	public String getLastTransactionDate() {
		return lastTransactionDate;
	}

	public void setLastTransactionDate(String lastTransactionDate) {
		this.lastTransactionDate = lastTransactionDate;
	}

	public BigDecimal getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(BigDecimal depositAmount) {
		this.depositAmount = depositAmount;
	}

	public Long getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(Long availableBalance) {
		this.availableBalance = availableBalance;
	}

	public Long getTotalLoanSanctionAmount() {
		return totalLoanSanctionAmount;
	}

	public void setTotalLoanSanctionAmount(Long totalLoanSanctionAmount) {
		this.totalLoanSanctionAmount = totalLoanSanctionAmount;
	}

    
}
