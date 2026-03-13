package com.aml.srv.core.efrmsrv.repo;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.aml.srv.core.efrmsrv.entity.MapperEntity;
import com.efrm.rt.srv.core.recordDTO.MapperSummarizationFiledDTO;


@Component
public class MapperImpl {

	public static final Logger LOGGER = LoggerFactory.getLogger(MapperImpl.class);
	
	@Autowired
	MapperRepository<?> mapperRepository;

	public List<MapperSummarizationFiledDTO> getMapperByIdentifier(String srchIdentifierVal) {
		List<MapperSummarizationFiledDTO> retunMapp = null;
		MapperEntity mapper = null;
		Example<MapperEntity> exampleMapperNNtty = null;
		try {
			mapper = new MapperEntity();
			mapper.setIdentifier(srchIdentifierVal);
			exampleMapperNNtty = Example.of(mapper);
			//retunMapp = MapperRepository.findBy(exampleMapperNNtty, q -> q.sortBy(Sort.by("id")).as(MapperEntity.class).all());
			//retunMapp = mapperRepository.findBy(exampleMapperNNtty, q -> q.sortBy(Sort.by("id")).project(MapperSummarizationFiledDTO.class).all());
			retunMapp =  mapperRepository.findBy( exampleMapperNNtty,  q -> q.sortBy(Sort.by("id"))
			              .as(MapperSummarizationFiledDTO.class)
			              .all()
			    );
		} catch (Exception e) {
			retunMapp = null; 
			LOGGER.error("Exception found in MapperImpl@getMapperByIdentifier : {}",e);
		} finally {
			mapper=null; //exampleMapperNNtty = null;
		}
		return retunMapp;
	}
	
	public Optional<MapperSummarizationFiledDTO> getMapperBySourceName(String srchSrchVal, String mapperTblefeildName) {
		Optional<MapperSummarizationFiledDTO> retunMapp = null;
		MapperEntity mapper = null;
		Example<MapperEntity> exampleMapperNNtty = null;
		try {
			mapper = new MapperEntity();
			mapper.setSourceFileName(srchSrchVal);
			mapper.setFieldName(mapperTblefeildName);
			exampleMapperNNtty = Example.of(mapper);
			//retunMapp = MapperRepository.findBy(exampleMapperNNtty, q -> q.sortBy(Sort.by("id")).as(MapperEntity.class).all());
			//retunMapp = mapperRepository.findBy(exampleMapperNNtty, q -> q.sortBy(Sort.by("id")).project(MapperSummarizationFiledDTO.class).all());
			retunMapp =  mapperRepository.findBy( exampleMapperNNtty,  q -> q.sortBy(Sort.by("id"))
			              .as(MapperSummarizationFiledDTO.class)
			              .one()
			    );
		} catch (Exception e) {
			retunMapp = null; 
			LOGGER.error("Exception found in MapperImpl@getMapperByIdentifier : {}",e);
		} finally {
			mapper=null; //exampleMapperNNtty = null;
		}
		return retunMapp;
	}
}
