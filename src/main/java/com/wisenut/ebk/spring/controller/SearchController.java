package com.wisenut.ebk.spring.controller;

import com.wisenut.ebk.spring.MissingArgumentException;
import com.wisenut.ebk.spring.dto.SearchPersonalDTO;
import com.wisenut.ebk.spring.dto.TotalSearchDTO;
import com.wisenut.ebk.spring.dto.TotalSearchDTO.TotalSearchDTOBuilder;
import com.wisenut.ebk.spring.service.SearchService;
import com.wisenut.ebk.spring.vo.DocumentSearch;
import com.wisenut.ebk.spring.vo.FileSearch;
import com.wisenut.ebk.spring.vo.FolderSearch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin( origins = "*" )
@RestController
@Tag( name = "ECM 통합검색", description = "호반건설 문서중앙화 통합검색" )
@Slf4j
public class SearchController {

    @Value( "${key.sensitive}" )
    String sensitiveKey;

    @Value( "${key.personal}" )
    String personalKey;

    @Autowired
    private SearchService service;

    @RequestMapping( value = "/search/personal", method = RequestMethod.POST, produces = "application/json" )
    @Operation( summary = "개인정보 검색", description = "개인정보 추출검색" )
    public ResponseEntity< ? > retrivePersonalDataSearchList( HttpServletRequest request , @RequestBody Map< String, String > params ) {

        String collection = params.containsKey( "searchTargetOID" ) ? params.get( "searchTargetOID" ) : "";
        String query = params.containsKey( "query" ) ? params.get( "query" ) : "";

        SearchPersonalDTO dto = null;
        try {
            if ( collection.contentEquals( "fileinfo" ) ) dto = service.searchPersonalDataTotalListByCategory( params );
        } catch ( MissingArgumentException mae ) {
            return ResponseEntity.badRequest( )
                                 .body( mae.toString( ) );
        }
        
        
        HttpSession session = request.getSession( );
        session.setAttribute( personalKey, dto);
        
        return ResponseEntity.ok( dto );
    }

    @RequestMapping( value = "/search/sensitive", method = RequestMethod.POST, produces = "application/json" )
    @Operation( summary = "민감정보검색", description = "입력받은 검색어로 민감정보 검색" )
    public ResponseEntity< ? > retriveSensitiveDataSearchList( HttpServletRequest request , @RequestBody Map< String, String > params ) {

        String collection = params.containsKey( "searchTargetOID" ) ? params.get( "searchTargetOID" ) : "";
        String query = params.containsKey( "query" ) ? params.get( "query" ) : "";

        FileSearch file = FileSearch.builder( )
                                    .build( );
        /*FolderSearch folder = FolderSearch.builder()
                                          .build();*/

        try {
            if ( collection.contentEquals( "ALL" ) || collection.contentEquals( "fileinfo" ) ) {
                file = service.searchSensitiveFileTotalListByCategory( params );
            }

        } catch ( MissingArgumentException mae ) {
            return ResponseEntity.badRequest( )
                                 .body( mae.toString( ) );
        }

        TotalSearchDTOBuilder dtoBuilder = TotalSearchDTO.builder( );
        List< Object > data = new ArrayList<>( );
        data.add( file );
        //data.add(folder);
        TotalSearchDTO dto = dtoBuilder.data( data )
                                       .query( query )
                                       .build( );

        HttpSession session = request.getSession( );
        session.setAttribute( sensitiveKey, dto);

        return ResponseEntity.ok( dto );
    }

    @RequestMapping( value = "/search", method = RequestMethod.POST, produces = "application/json" )
    @Operation( summary = "통합검색", description = "입력받은 검색어, 보안 정보로 통합검색" )
    public ResponseEntity< ? > retriveSearchList( @RequestBody Map< String, String > params ) {

        String collection = params.containsKey( "searchTargetOID" ) ? params.get( "searchTargetOID" ) : "";
        String query = params.containsKey( "query" ) ? params.get( "query" ) : "";


        FileSearch file = FileSearch.builder( )
                                    .build( );
        DocumentSearch document = DocumentSearch.builder( )
                                                .build( );
        FolderSearch folder = FolderSearch.builder( )
                                          .build( );
        try {
            if ( collection.contentEquals( "ALL" ) || collection.contentEquals( "fileinfo" ) )
                file = service.searchFileTotalListByCategory( params );
            if ( collection.contentEquals( "ALL" ) || collection.contentEquals( "documentinfo" ) )
                document = service.searchDocumentTotalListByCategory( params );
            if ( collection.contentEquals( "ALL" ) || collection.contentEquals( "folderinfo" ) )
                folder = service.searchFolderTotalListByCategory( params );
        } catch ( MissingArgumentException mae ) {
            return ResponseEntity.badRequest( )
                                 .body( mae.toString( ) );
        }

        TotalSearchDTOBuilder dtoBuilder = TotalSearchDTO.builder( );
        List< Object > Data = new ArrayList<>( );
        Data.add( document );
        Data.add( folder );
        Data.add( file );
        TotalSearchDTO dto = dtoBuilder.data( Data )
                                       .query( query )
                                       .build( );

        return ResponseEntity.ok( dto );
    }

    @RequestMapping( value = "/search/file", method = RequestMethod.POST, produces = "application/json" )
    @Operation( summary = "통합검색", description = "입력받은 검색어, 보안 정보로 통합검색" )
    public ResponseEntity< ? > retriveFileSearchList( @RequestBody Map< String, String > params ) {

        String query = params.containsKey( "query" ) ? params.get( "query" ) : "";

        FileSearch file = FileSearch.builder( )
                                    .build( );

        try {
            file = service.searchFileTotalListByCategory( params );
        } catch ( MissingArgumentException mae ) {
            return ResponseEntity.badRequest( )
                                 .body( mae.toString( ) );
        }

        TotalSearchDTOBuilder dtoBuilder = TotalSearchDTO.builder( );
        List< Object > data = new ArrayList<>( );
        data.add( file );
        TotalSearchDTO dto = dtoBuilder.data( data )
                                       .query( query )
                                       .build( );

        return ResponseEntity.ok( dto );
    }

    @RequestMapping( value = "/search/folder", method = RequestMethod.POST, produces = "application/json" )
    @Operation( summary = "통합검색", description = "입력받은 검색어, 보안 정보로 통합검색" )
    public ResponseEntity< ? > retriveFolderSearchList( @RequestBody Map< String, String > params ) {

        String query = params.containsKey( "query" ) ? params.get( "query" ) : "";

        FolderSearch folder = FolderSearch.builder( )
                                          .build( );
        try {
            folder = service.searchFolderTotalListByCategory( params );
        } catch ( MissingArgumentException mae ) {
            return ResponseEntity.badRequest( )
                                 .body( mae.toString( ) );
        }

        TotalSearchDTOBuilder dtoBuilder = TotalSearchDTO.builder( );
        List< Object > data = new ArrayList<>( );
        data.add( folder );
        TotalSearchDTO dto = dtoBuilder.data( data )
                                       .query( query )
                                       .build( );

        return ResponseEntity.ok( dto );
    }
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
