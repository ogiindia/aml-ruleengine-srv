package com.aml.srv.core.efrmsrv.ruleengine.serive;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrm.cust.scoring.RedisService;
import com.aml.srv.core.efrmsrv.entity.SanctionListConfigEntity;
import com.aml.srv.core.efrmsrv.repo.SanctionListConfigImpl;
import com.efrm.rt.srv.core.common.repo.Dynamic_VO;

@Component
public class WhiteListAccountCheck {

	public static final Logger LOGGER = LoggerFactory.getLogger(WhiteListAccountCheck.class);

	@Autowired
	RedisService redisService;

	@Autowired
	SanctionListConfigImpl sanctionListConfigImpl;
	
	public boolean checkIsWhiteLitedAccount(String accountNo) {
		LOGGER.info("WhiteListAccountCheck@checkIsWhiteLitedAccount Method Called.....");
		boolean isWhiteListed = false;
		List<Dynamic_VO> dynamicVoLstObj = null;
		List<SanctionListConfigEntity> sancLstCOnfigENtyLst =null;
		try {
			sancLstCOnfigENtyLst = sanctionListConfigImpl.getSanctionListConfigBySanctionName("WL");
			boolean accountExists = false;
			if(sancLstCOnfigENtyLst!=null && sancLstCOnfigENtyLst.size()>0) {
				for (SanctionListConfigEntity sanctionListConfigEntity : sancLstCOnfigENtyLst) {
					String sanctionCOde = sanctionListConfigEntity.getSanctionCode();
					dynamicVoLstObj = redisService.toPullListIntoRedis(sanctionCOde);
					if (dynamicVoLstObj != null && dynamicVoLstObj.size() > 0) {
						accountExists = dynamicVoLstObj.stream().anyMatch(pojo -> pojo.getProperties().values().stream()
								.anyMatch(val -> val != null && val.toString().equalsIgnoreCase(accountNo)));
						if (accountExists) {
							isWhiteListed = true;
							break;
						} else {
							isWhiteListed = false;
						}
					} else {
						LOGGER.info("WHITELIST Data is Null : {}",dynamicVoLstObj);
					}
				}
			}
			
		
		} catch (Exception e) {
			LOGGER.error("Exception found in WhiteListAccountCheck@checkIsWhiteLitedAccount Method : {}",e);
		} finally {
			LOGGER.info("Account No. is available in Whitelist List : {}");
			LOGGER.info("WhiteListAccountCheck@checkIsWhiteLitedAccount Method End.....");
		}
		return isWhiteListed;
	}
}
