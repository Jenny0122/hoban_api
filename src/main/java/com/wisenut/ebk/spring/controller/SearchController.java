package com.wisenut.ebk.spring.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wisenut.ebk.spring.MissingArgumentException;
import com.wisenut.ebk.spring.dto.SearchPersonalDTO;
import com.wisenut.ebk.spring.dto.SearchPersonalDTO.SearchPersonalDTOBuilder;
import com.wisenut.ebk.spring.dto.TotalSearchDTO;
import com.wisenut.ebk.spring.dto.TotalSearchDTO.TotalSearchDTOBuilder;
import com.wisenut.ebk.spring.service.SearchService;
import com.wisenut.ebk.spring.vo.DocumentSearch;
import com.wisenut.ebk.spring.vo.FileSearch;
import com.wisenut.ebk.spring.vo.FolderSearch;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "ECM 통합검색", description = "호반건설 문서중앙화 통합검색")
@Slf4j
public class SearchController {

	@Autowired
	private SearchService service;
	
	@RequestMapping(value = "/search/personaldata", method = RequestMethod.GET, produces = "application/json")
	@Operation(summary = "통합검색", description = "입력받은 검색어, 보안 정보로 개인정보 추출검색")
	public ResponseEntity<?> retrivePersonalDataSearchList(@RequestParam Map<String, String> params) {
		
		String collection = params.containsKey("searchTargetOID") ? params.get("searchTargetOID") : "";		
		String query = params.containsKey("query") ? params.get("query") : "";		

		
		SearchPersonalDTO dto = null;
		try {
			if(collection.contentEquals("fileinfo")) dto = service.searchPersonalDataTotalListByCategory(params);		
		} catch (MissingArgumentException mae) {
			return ResponseEntity.badRequest().body(mae.toString());
		}
		return ResponseEntity.ok(dto);
	}

//	@RequestMapping(value = "/search/backup", method = RequestMethod.GET, produces = "application/json")
//	@Operation(summary = "통합검색", description = "입력받은 검색어, 보안 정보로 통합검색")
//	public ResponseEntity<SearchDTO> retriveSearchList(
//			@Parameter(description = "컬렉션") @RequestParam(name = "collection") String collection,
//			@Parameter(description = "검색어") @RequestParam(name = "query") String query,
//			@Parameter(description = "시작 위치") @RequestParam(name = "pageStart") int pageStart,
//			@Parameter(description = "힌반에 출력되는 결과 수") @RequestParam(name = "count") int count,
//			//@Parameter(description = "사용자정보") @RequestParam(name = "userInfo") String userinfo,
//			@Parameter(description = "보안정보") @RequestParam(name = "aclfilterInfo") String aclfilterInfo) {
//
//		
//		FileSearch file = FileSearch.builder().build();
//		DocumentSearch document = DocumentSearch.builder().build();	
//		FolderSearch folder = FolderSearch.builder().build();
//		
//		if(collection.contentEquals("ALL") || collection.contentEquals("fileinfo")) file = service.searchFileTotalListByCategory(collection, query, pageStart, count, aclfilterInfo);
//		if(collection.contentEquals("ALL") || collection.contentEquals("documentinfo")) document = service.searchDocumentTotalListByCategory(collection, query, pageStart, count, aclfilterInfo);
//		if(collection.contentEquals("ALL") || collection.contentEquals("folderinfo")) folder = service.searchFolderTotalListByCategory(collection, query, pageStart, count, aclfilterInfo);
//
//		SearchDTOBuilder dtoBuilder = SearchDTO.builder();
//		List<Object> Data = new ArrayList<>();
//		Data.add(document);
//		Data.add(folder);
//		Data.add(file);
//		SearchDTO dto = dtoBuilder.Data(Data).Query(query).build();
//
//		return ResponseEntity.ok(dto);
//	}
	
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
	@Operation(summary = "통합검색", description = "입력받은 검색어, 보안 정보로 통합검색")
	public ResponseEntity<?> retriveSearchList(@RequestParam Map<String, String> params) {
		
		String collection = params.containsKey("searchTargetOID") ? params.get("searchTargetOID") : "";		
		String query = params.containsKey("query") ? params.get("query") : "";		

		
		FileSearch file = FileSearch.builder().build();
		DocumentSearch document = DocumentSearch.builder().build();		
		FolderSearch folder = FolderSearch.builder().build();
		try {
			if(collection.contentEquals("ALL") || collection.contentEquals("fileinfo")) file = service.searchFileTotalListByCategory(params);
			if(collection.contentEquals("ALL") || collection.contentEquals("documentinfo")) document = service.searchDocumentTotalListByCategory(params);
			if(collection.contentEquals("ALL") || collection.contentEquals("folderinfo")) folder = service.searchFolderTotalListByCategory(params);			
		} catch (MissingArgumentException mae) {
			return ResponseEntity.badRequest().body(mae.toString());
		}
		
		TotalSearchDTOBuilder dtoBuilder = TotalSearchDTO.builder();
		List<Object> Data = new ArrayList<>();
		Data.add(document);
		Data.add(folder);
		Data.add(file);
		TotalSearchDTO dto = dtoBuilder.data(Data).query(query).build();

		return ResponseEntity.ok(dto);
	}
	
	@RequestMapping(value = "/search/file", method = RequestMethod.GET, produces = "application/json")
	@Operation(summary = "통합검색", description = "입력받은 검색어, 보안 정보로 통합검색")
	public ResponseEntity<?> retriveFileSearchList(@RequestParam Map<String, String> params) {
		
		String query = params.containsKey("query") ? params.get("query") : "";
		
		FileSearch file = FileSearch.builder().build();
		
		try {
			file = service.searchFileTotalListByCategory(params);			
		} catch (MissingArgumentException mae) {
			return ResponseEntity.badRequest().body(mae.toString());
		}
		
		TotalSearchDTOBuilder dtoBuilder = TotalSearchDTO.builder();
		List<Object> data = new ArrayList<>();
		data.add(file);
		TotalSearchDTO dto = dtoBuilder.data(data).query(query).build();		

		return ResponseEntity.ok(dto);
	}
	
	@RequestMapping(value = "/search/folder", method = RequestMethod.GET, produces = "application/json")
	@Operation(summary = "통합검색", description = "입력받은 검색어, 보안 정보로 통합검색")
	public ResponseEntity<?> retriveFolderSearchList(@RequestParam Map<String, String> params) {
		
		String query = params.containsKey("query") ? params.get("query") : "";
		
		FolderSearch folder = FolderSearch.builder().build();
		try {
			folder = service.searchFolderTotalListByCategory(params);
		} catch (MissingArgumentException mae) {
			return ResponseEntity.badRequest().body(mae.toString());
		}

		TotalSearchDTOBuilder dtoBuilder = TotalSearchDTO.builder();
		List<Object> data = new ArrayList<>();
		data.add(folder);
		TotalSearchDTO dto = dtoBuilder.data(data).query(query).build();

		return ResponseEntity.ok(dto);
	}
	
	/*
	 * final String WNRun_API = "/WNRun.do";
	 * 
	 * @RequestMapping(value = WNRun_API, method = RequestMethod.GET, produces = * "application/json")
	 * 
	 * @Operation(summary = "API 중개") public ResponseEntity<Object>
	 * retriveARK(@RequestParam MultiValueMap<String, String> params) {
	 * 
	 * RestTemplate restTemplate = new RestTemplate(); final String protocol =
	 * "http"; final String host = "localhost"; final int port = 7803; final String
	 * targetKey = "target"; if (!params.containsKey(targetKey)) {
	 * log.info(targetKey + "이 queryparameter에 없습니다."); return null; }
	 * 
	 * URI uri = null; 
	 * uri = UriComponentsBuilder .newInstance() .scheme(protocol)
	 * .host(host) .port(port) .path(WNRun_API) .queryParams(params) .build()
	 * .toUri();
	 * 
	 *log.debug("uri : " + uri);
	 * 
	 * ResponseEntity<Map> response = null; try { response =
	 * restTemplate.getForEntity(uri, null); } catch (Exception e) {
	 * log.info(e.getMessage()); return ResponseEntity .badRequest() .build(); }
	 * 
	 * return ResponseEntity.ok(response.getBody()); }
	 */
}
