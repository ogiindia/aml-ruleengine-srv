package com.aml.srv.core.efrm.trans.scoring;

public record TransactionGenuinessDetailsRecord(float[][] pred,double modelProb, double blended, String score,String[] levelDecision ) {

}
