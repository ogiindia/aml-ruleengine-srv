package com.aml.srv.core.efrm.parqute.entity;

import jakarta.persistence.Column;

public class CustomerDetailsParquteEntity {

	@Column(name = "CUSTOMERID")
	private String customerid;

	@Column(name = "CUSTOMERNAME")
	private String customername;

	@Column(name = "CUSTOMERTYPE")
	private String customertype;

	@Column(name = "CUSTOMERCATEGORY")
	private String customercategory;

	@Column(name = "BRANCHCODE")
	private String branchcode;

	@Column(name = "NATUREOFBUSINESS")
	private String natureofbusiness;

	@Column(name = "CREDITRATING")
	private String creditrating;

	@Column(name = "CREATEDDATETIME")
	private String createddatetime;

	@Column(name = "FIRSTNAME")
	private String firstname;

	@Column(name = "LASTNAME")
	private String lastname;

	@Column(name = "DATEOFBIRTH")
	private String dateofbirth;

	@Column(name = "PLACEOFBIRTH")
	private String placeofbirth;

	@Column(name = "NATIONALITY")
	private String nationality;

	@Column(name = "SEX")
	private String sex;

	@Column(name = "PANNO")
	private String panno;

	@Column(name = "OCCUPATION")
	private String occupation;

	@Column(name = "CITY")
	private String city;

	@Column(name = "COUNTRY")
	private String country;

	@Column(name = "PHONENO")
	private String phoneno;

	@Column(name = "MOBILENO")
	private String mobileno;

	@Column(name = "EMAILID")
	private String emailid;

	@Column(name = "FAXNO")
	private String faxNo;

	@Column(name = "PINCODE")
	private String pincode;

	@Column(name = "STATE")
	private String state;

	@Column(name = "RESIDENTIALSTATUS")
	private Long residentialStatus;

	@Column(name = "IDENTIFICATIONMARKS")
	private String identificationMarks;

	@Column(name = "AGE")
	private Long age;

	@Column(name = "EMPLOYERCODE")
	private String employerCode;

	@Column(name = "EMPLOYERADDRESSLINE1")
	private String employerAddressLine1;

	@Column(name = "EMPLOYERADDRESSLINE2")
	private String employerAddressLine2;

	@Column(name = "EMPLOYERCITY")
	private String employerCity;

	@Column(name = "EMPLOYERSTATE")
	private String employerState;

	@Column(name = "EMPLOYERCOUNTRY")
	private String employerCountry;

	@Column(name = "EMPLOYERPINCODE")
	private String employerPincode;

	@Column(name = "EMPLOYERPHONENO")
	private String employerPhoneNo;

	@Column(name = "EMPLOYERFAXNO")
	private String employerFaxNo;

	@Column(name = "EMPLOYEREMAILID")
	private String employerEmailId;

	@Column(name = "SOURCEOFFUNDS")
	private String sourceOfFunds;

	@Column(name = "DRIVINGLICENSENO")
	private String drivingLicenseNo;

	@Column(name = "PASSPORTNO")
	private String passportNo;

	@Column(name = "VOTERIDENTITYCARDNO")
	private String voterIdentityCardNo;

	@Column(name = "MARITALSTATUS")
	private String maritalStatus;

	@Column(name = "DEPENDENTS")
	private Long dependents;

	@Column(name = "EDUCATION")
	private String education;

	@Column(name = "GRADE")
	private String grade;

	@Column(name = "VEHOWNER")
	private String vehOwner;

	@Column(name = "CARVALUE")
	private String carValue;

	@Column(name = "YROFPUR")
	private String yrOfPur;

	@Column(name = "BANKVISIT")
	private String bankVisit;

	@Column(name = "VISITFOR")
	private String visitFor;

	@Column(name = "INVESTMENTS_VAL")
	private Long investmentsVal;

	@Column(name = "LOANSAVAILED")
	private String loansAvailed;

	@Column(name = "LISTENTO")
	private String listenTo;

	@Column(name = "READING")
	private String reading;

	@Column(name = "MOVIETYPE")
	private String movieType;

	@Column(name = "TRAVELABROAD")
	private String travelAbroad;

	@Column(name = "TRAVELTIMES")
	private String travelTimes;

	@Column(name = "ISSUINGAUTHORITY")
	private String issuingAuthority;

	@Column(name = "PLACEOFISSUE")
	private String placeOfIssue;

	@Column(name = "IDCARD")
	private String idCard;

	@Column(name = "ADDRESSLINE1")
	private String addressLine1;

	@Column(name = "ADDRESSLINE2")
	private String addressLine2;

	@Column(name = "ADDRESSLINE3")
	private String addressLine3;

	@Column(name = "WEBSITE")
	private Double website;

	@Column(name = "COMM_ADDRESSLINE1")
	private String commAddressLine1;

	@Column(name = "COMM_ADDRESSLINE2")
	private String commAddressLine2;

	@Column(name = "COMM_CITY")
	private Long commCity;

	@Column(name = "COMM_STATE")
	private String commState;

	@Column(name = "COMM_COUNTRY")
	private String commCountry;

	@Column(name = "COMM_PINCODE")
	private Long commPincode;

	@Column(name = "COMM_PHONENO")
	private String commPhoneNo;

	@Column(name = "COMM_FAXNO")
	private String commFaxNo;

	@Column(name = "COMM_EMAILID")
	private String commEmailId;

	@Column(name = "REMARKS")
	private String remarks;

	@Column(name = "UPDATETIMESTAMP")
	private String updateTimestamp;

	@Column(name = "SECURITYID")
	private String securityId;

	@Column(name = "CUSTOMERTYPECODE")
	private String customerTypeCode;

	@Column(name = "CUSTOMERSTATCODE")
	private String customerStatCode;

	@Column(name = "PARTYID")
	private String partyId;

	@Column(name = "JTH1_NAME")
	private String jth1Name;

	@Column(name = "JTH1_RELATION")
	private String jth1Relation;

	@Column(name = "JTH2_NAME")
	private String jth2Name;

	@Column(name = "JTH2_RELATION")
	private String jth2Relation;

	@Column(name = "JTH3_NAME")
	private String jth3Name;

	@Column(name = "JTH3_RELATION")
	private String jth3Relation;

	@Column(name = "JTH4_NAME")
	private String jth4Name;

	@Column(name = "JTH4_RELATION")
	private String jth4Relation;

	@Column(name = "SOURCETYPE")
	private String sourceType;

	@Column(name = "LASTUPDATEDDATE")
	private String lastUpdatedDate;

	@Column(name = "PASSPORTEXPIRYDATE")
	private String passportExpiryDate;

	@Column(name = "FATHERNAME")
	private String fatherName;

	@Column(name = "MOTHERNAME")
	private String motherName;

	@Column(name = "MIDDLENAME")
	private String middleName;

	@Column(name = "INTRODUCERCUSTOMERID")
	private Long introducerCustomerId;

	@Column(name = "INTRODUCERNAME")
	private String introducerName;

	@Column(name = "INTRODUCERCATEGORY")
	private String introducerCategory;

	@Column(name = "CUSTOMEREMPCODE")
	private String customerEmpCode;

	@Column(name = "ESTIMATEDINCOMEFROMBUSINESS")
	private Long estimatedIncomeFromBusiness;

	@Column(name = "OTHERINCOME")
	private Long otherIncome;

	@Column(name = "ANNUALINCOME")
	private Long annualIncome;

	@Column(name = "NET_WORTH")
	private Long netWorth;

	@Column(name = "CHECKED")
	private String checked;

	@Column(name = "INTERCOUNTRY")
	private String interCountry;

	@Column(name = "FATFNCCT")
	private String fatfNcct;

	@Column(name = "DRUGTRAFFICKING")
	private String drugTrafficking;

	@Column(name = "OFACTERRORISM")
	private String ofacTerrorism;

	@Column(name = "CURRUPTIONSCORE")
	private String corruptionScore;

	@Column(name = "POLITICALSTABILITY")
	private String politicalStability;

	@Column(name = "AMLEXISTENCE")
	private String amlExistence;

	@Column(name = "NEIGHBOURLYRELATION")
	private String neighbourlyRelation;

	@Column(name = "SCORE")
	private String score;

	@Column(name = "SALUTATION")
	private String salutation;

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}

	public String getCustomername() {
		return customername;
	}

	public void setCustomername(String customername) {
		this.customername = customername;
	}

	public String getCustomertype() {
		return customertype;
	}

	public void setCustomertype(String customertype) {
		this.customertype = customertype;
	}

	public String getCustomercategory() {
		return customercategory;
	}

	public void setCustomercategory(String customercategory) {
		this.customercategory = customercategory;
	}

	public String getBranchcode() {
		return branchcode;
	}

	public void setBranchcode(String branchcode) {
		this.branchcode = branchcode;
	}

	public String getNatureofbusiness() {
		return natureofbusiness;
	}

	public void setNatureofbusiness(String natureofbusiness) {
		this.natureofbusiness = natureofbusiness;
	}

	public String getCreditrating() {
		return creditrating;
	}

	public void setCreditrating(String creditrating) {
		this.creditrating = creditrating;
	}

	public String getCreateddatetime() {
		return createddatetime;
	}

	public void setCreateddatetime(String createddatetime) {
		this.createddatetime = createddatetime;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getDateofbirth() {
		return dateofbirth;
	}

	public void setDateofbirth(String dateofbirth) {
		this.dateofbirth = dateofbirth;
	}

	public String getPlaceofbirth() {
		return placeofbirth;
	}

	public void setPlaceofbirth(String placeofbirth) {
		this.placeofbirth = placeofbirth;
	}

	public String getPanno() {
		return panno;
	}

	public void setPanno(String panno) {
		this.panno = panno;
	}

	public String getPhoneno() {
		return phoneno;
	}

	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}

	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public String getEmailid() {
		return emailid;
	}

	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}

	public String getUpdateTimestamp() {
		return updateTimestamp;
	}

	public Long getIntroducerCustomerId() {
		return introducerCustomerId;
	}

	public void setIntroducerCustomerId(Long introducerCustomerId) {
		this.introducerCustomerId = introducerCustomerId;
	}

	public String getIntroducerName() {
		return introducerName;
	}

	public void setIntroducerName(String introducerName) {
		this.introducerName = introducerName;
	}

	public String getIntroducerCategory() {
		return introducerCategory;
	}

	public void setIntroducerCategory(String introducerCategory) {
		this.introducerCategory = introducerCategory;
	}

	public String getCustomerEmpCode() {
		return customerEmpCode;
	}

	public void setCustomerEmpCode(String customerEmpCode) {
		this.customerEmpCode = customerEmpCode;
	}

	public Long getEstimatedIncomeFromBusiness() {
		return estimatedIncomeFromBusiness;
	}

	public void setEstimatedIncomeFromBusiness(Long estimatedIncomeFromBusiness) {
		this.estimatedIncomeFromBusiness = estimatedIncomeFromBusiness;
	}

	public Long getOtherIncome() {
		return otherIncome;
	}

	public void setOtherIncome(Long otherIncome) {
		this.otherIncome = otherIncome;
	}

	public Long getAnnualIncome() {
		return annualIncome;
	}

	public void setAnnualIncome(Long annualIncome) {
		this.annualIncome = annualIncome;
	}

	public Long getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(Long netWorth) {
		this.netWorth = netWorth;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getInterCountry() {
		return interCountry;
	}

	public void setInterCountry(String interCountry) {
		this.interCountry = interCountry;
	}

	public String getFatfNcct() {
		return fatfNcct;
	}

	public void setFatfNcct(String fatfNcct) {
		this.fatfNcct = fatfNcct;
	}

	public String getDrugTrafficking() {
		return drugTrafficking;
	}

	public void setDrugTrafficking(String drugTrafficking) {
		this.drugTrafficking = drugTrafficking;
	}

	public String getOfacTerrorism() {
		return ofacTerrorism;
	}

	public void setOfacTerrorism(String ofacTerrorism) {
		this.ofacTerrorism = ofacTerrorism;
	}

	public String getCorruptionScore() {
		return corruptionScore;
	}

	public void setCorruptionScore(String corruptionScore) {
		this.corruptionScore = corruptionScore;
	}

	public String getPoliticalStability() {
		return politicalStability;
	}

	public void setPoliticalStability(String politicalStability) {
		this.politicalStability = politicalStability;
	}

	public String getAmlExistence() {
		return amlExistence;
	}

	public void setAmlExistence(String amlExistence) {
		this.amlExistence = amlExistence;
	}

	public String getNeighbourlyRelation() {
		return neighbourlyRelation;
	}

	public void setNeighbourlyRelation(String neighbourlyRelation) {
		this.neighbourlyRelation = neighbourlyRelation;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public Long getResidentialStatus() {
		return residentialStatus;
	}

	public void setResidentialStatus(Long residentialStatus) {
		this.residentialStatus = residentialStatus;
	}

	public String getIdentificationMarks() {
		return identificationMarks;
	}

	public void setIdentificationMarks(String identificationMarks) {
		this.identificationMarks = identificationMarks;
	}

	public Long getAge() {
		return age;
	}

	public void setAge(Long age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getEmployerCode() {
		return employerCode;
	}

	public void setEmployerCode(String employerCode) {
		this.employerCode = employerCode;
	}

	public String getEmployerAddressLine1() {
		return employerAddressLine1;
	}

	public void setEmployerAddressLine1(String employerAddressLine1) {
		this.employerAddressLine1 = employerAddressLine1;
	}

	public String getEmployerAddressLine2() {
		return employerAddressLine2;
	}

	public void setEmployerAddressLine2(String employerAddressLine2) {
		this.employerAddressLine2 = employerAddressLine2;
	}

	public String getEmployerCity() {
		return employerCity;
	}

	public void setEmployerCity(String employerCity) {
		this.employerCity = employerCity;
	}

	public String getEmployerState() {
		return employerState;
	}

	public void setEmployerState(String employerState) {
		this.employerState = employerState;
	}

	public String getEmployerCountry() {
		return employerCountry;
	}

	public void setEmployerCountry(String employerCountry) {
		this.employerCountry = employerCountry;
	}

	public String getEmployerPincode() {
		return employerPincode;
	}

	public void setEmployerPincode(String employerPincode) {
		this.employerPincode = employerPincode;
	}

	public String getEmployerPhoneNo() {
		return employerPhoneNo;
	}

	public void setEmployerPhoneNo(String employerPhoneNo) {
		this.employerPhoneNo = employerPhoneNo;
	}

	public String getEmployerFaxNo() {
		return employerFaxNo;
	}

	public void setEmployerFaxNo(String employerFaxNo) {
		this.employerFaxNo = employerFaxNo;
	}

	public String getEmployerEmailId() {
		return employerEmailId;
	}

	public void setEmployerEmailId(String employerEmailId) {
		this.employerEmailId = employerEmailId;
	}

	public String getSourceOfFunds() {
		return sourceOfFunds;
	}

	public void setSourceOfFunds(String sourceOfFunds) {
		this.sourceOfFunds = sourceOfFunds;
	}

	public String getDrivingLicenseNo() {
		return drivingLicenseNo;
	}

	public void setDrivingLicenseNo(String drivingLicenseNo) {
		this.drivingLicenseNo = drivingLicenseNo;
	}

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public String getVoterIdentityCardNo() {
		return voterIdentityCardNo;
	}

	public void setVoterIdentityCardNo(String voterIdentityCardNo) {
		this.voterIdentityCardNo = voterIdentityCardNo;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public Long getDependents() {
		return dependents;
	}

	public void setDependents(Long dependents) {
		this.dependents = dependents;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getVehOwner() {
		return vehOwner;
	}

	public void setVehOwner(String vehOwner) {
		this.vehOwner = vehOwner;
	}

	public String getCarValue() {
		return carValue;
	}

	public void setCarValue(String carValue) {
		this.carValue = carValue;
	}

	public String getYrOfPur() {
		return yrOfPur;
	}

	public void setYrOfPur(String yrOfPur) {
		this.yrOfPur = yrOfPur;
	}

	public String getBankVisit() {
		return bankVisit;
	}

	public void setBankVisit(String bankVisit) {
		this.bankVisit = bankVisit;
	}

	public String getVisitFor() {
		return visitFor;
	}

	public void setVisitFor(String visitFor) {
		this.visitFor = visitFor;
	}

	public Long getInvestmentsVal() {
		return investmentsVal;
	}

	public void setInvestmentsVal(Long investmentsVal) {
		this.investmentsVal = investmentsVal;
	}

	public String getLoansAvailed() {
		return loansAvailed;
	}

	public void setLoansAvailed(String loansAvailed) {
		this.loansAvailed = loansAvailed;
	}

	public String getListenTo() {
		return listenTo;
	}

	public void setListenTo(String listenTo) {
		this.listenTo = listenTo;
	}

	public String getReading() {
		return reading;
	}

	public void setReading(String reading) {
		this.reading = reading;
	}

	public String getMovieType() {
		return movieType;
	}

	public void setMovieType(String movieType) {
		this.movieType = movieType;
	}

	public String getTravelAbroad() {
		return travelAbroad;
	}

	public void setTravelAbroad(String travelAbroad) {
		this.travelAbroad = travelAbroad;
	}

	public String getTravelTimes() {
		return travelTimes;
	}

	public void setTravelTimes(String travelTimes) {
		this.travelTimes = travelTimes;
	}

	public String getIssuingAuthority() {
		return issuingAuthority;
	}

	public void setIssuingAuthority(String issuingAuthority) {
		this.issuingAuthority = issuingAuthority;
	}

	public String getPlaceOfIssue() {
		return placeOfIssue;
	}

	public void setPlaceOfIssue(String placeOfIssue) {
		this.placeOfIssue = placeOfIssue;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	public Double getWebsite() {
		return website;
	}

	public void setWebsite(Double website) {
		this.website = website;
	}

	public String getCommAddressLine1() {
		return commAddressLine1;
	}

	public void setCommAddressLine1(String commAddressLine1) {
		this.commAddressLine1 = commAddressLine1;
	}

	public String getCommAddressLine2() {
		return commAddressLine2;
	}

	public void setCommAddressLine2(String commAddressLine2) {
		this.commAddressLine2 = commAddressLine2;
	}

	public Long getCommCity() {
		return commCity;
	}

	public void setCommCity(Long commCity) {
		this.commCity = commCity;
	}

	public String getCommState() {
		return commState;
	}

	public void setCommState(String commState) {
		this.commState = commState;
	}

	public String getCommCountry() {
		return commCountry;
	}

	public void setCommCountry(String commCountry) {
		this.commCountry = commCountry;
	}

	public Long getCommPincode() {
		return commPincode;
	}

	public void setCommPincode(Long commPincode) {
		this.commPincode = commPincode;
	}

	public String getCommPhoneNo() {
		return commPhoneNo;
	}

	public void setCommPhoneNo(String commPhoneNo) {
		this.commPhoneNo = commPhoneNo;
	}

	public String getCommFaxNo() {
		return commFaxNo;
	}

	public void setCommFaxNo(String commFaxNo) {
		this.commFaxNo = commFaxNo;
	}

	public String getCommEmailId() {
		return commEmailId;
	}

	public void setCommEmailId(String commEmailId) {
		this.commEmailId = commEmailId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getUpdateString() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(String updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public String getSecurityId() {
		return securityId;
	}

	public void setSecurityId(String securityId) {
		this.securityId = securityId;
	}

	public String getCustomerTypeCode() {
		return customerTypeCode;
	}

	public void setCustomerTypeCode(String customerTypeCode) {
		this.customerTypeCode = customerTypeCode;
	}

	public String getCustomerStatCode() {
		return customerStatCode;
	}

	public void setCustomerStatCode(String customerStatCode) {
		this.customerStatCode = customerStatCode;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getJth1Name() {
		return jth1Name;
	}

	public void setJth1Name(String jth1Name) {
		this.jth1Name = jth1Name;
	}

	public String getJth1Relation() {
		return jth1Relation;
	}

	public void setJth1Relation(String jth1Relation) {
		this.jth1Relation = jth1Relation;
	}

	public String getJth2Name() {
		return jth2Name;
	}

	public void setJth2Name(String jth2Name) {
		this.jth2Name = jth2Name;
	}

	public String getJth2Relation() {
		return jth2Relation;
	}

	public void setJth2Relation(String jth2Relation) {
		this.jth2Relation = jth2Relation;
	}

	public String getJth3Name() {
		return jth3Name;
	}

	public void setJth3Name(String jth3Name) {
		this.jth3Name = jth3Name;
	}

	public String getJth3Relation() {
		return jth3Relation;
	}

	public void setJth3Relation(String jth3Relation) {
		this.jth3Relation = jth3Relation;
	}

	public String getJth4Name() {
		return jth4Name;
	}

	public void setJth4Name(String jth4Name) {
		this.jth4Name = jth4Name;
	}

	public String getJth4Relation() {
		return jth4Relation;
	}

	public void setJth4Relation(String jth4Relation) {
		this.jth4Relation = jth4Relation;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(String lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public String getPassportExpiryDate() {
		return passportExpiryDate;
	}

	public void setPassportExpiryDate(String passportExpiryDate) {
		this.passportExpiryDate = passportExpiryDate;
	}

}