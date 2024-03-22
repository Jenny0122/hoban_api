package com.wisenut.ebk.spring.service;

import com.wisenut.ebk.spring.MissingArgumentException;
import com.wisenut.ebk.spring.dto.SearchPersonalDTO;
import com.wisenut.ebk.spring.dto.SearchPersonalDTO.SearchPersonalDTOBuilder;
import com.wisenut.ebk.spring.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {

    // 검색기 server 설정
    final String server_ip = "172.17.208.36";
    final int server_port = 7000;
    final int server_timeout = 10 * 1000;
    // final String server_ip = "127.0.0.1";
    private final GroupNameService groupNameService;
    RestTemplate restTemplate = new RestTemplate( );

    /**
     * 파일 통합 검색
     *
     * @param query           검색어
     * @param searchTargetOID 검색 대상 컬렉션
     * @param aclfilterInfo   보안 정보
     * @throws MissingArgumentException
     * @author 안선정
     */
    public FileSearch searchFileTotalListByCategory( Map< String, String > params ) throws MissingArgumentException {

        log.info( "**********************************" );
        params.keySet( )
              .stream( )
              .forEach( key -> log.info( " - " + key + ": {}" , params.get( key ) ) );
        log.info( "**********************************" );

        List< FileSearchVo > list = new ArrayList<>( );

        String COLLECTION = "";
        if ( params.containsKey( "searchTargetOID" ) ) {
            COLLECTION = params.get( "searchTargetOID" );
            if ( COLLECTION.contentEquals( "ALL" ) )
                COLLECTION = "fileinfo";
        } else {
            throw new MissingArgumentException( "searchTargetOID는 '필수'값 입니다." );
        }

        // collection, 검색필드, 출력필드 정의
        int QUERY_LOG = 1;
        int EXTEND_OR = 0;
        int RESULT_COUNT = params.containsKey( "count" ) ? Integer.parseInt( params.get( "count" ) ) : 10;// 한번에 출력되는 검색 건수
        int PAGE_START = params.containsKey( "pageStart" ) ? Integer.parseInt( params.get( "pageStart" ) ) * RESULT_COUNT : 0; // 검색결과를 받아오는 시작 위치
        String SEARCH_FIELD = "FILENAME,DOCUMENTNAME,TAGLIST,CREATOROID,CREATORNAME,FILETYPE,FILESIZE,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPFULLPATHOID,DOCTYPEOID,CONTENT"; // 검색필드
        String DOCUMENT_FIELD = "DOCID,DATE,TARGETOID,OID,STORAGEFILEID,FILENAME,DOCUMENTNAME,TAGLIST,CREATOROID,CREATORNAME,CREATORGROUPNAME,LASTMODIFIEROID,LASTMODIFIEDAT,LASTMODIFIEDATN,FILETYPE,FILESIZE,FILESIZEM,FOLDEROID,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPOID,MANAGERGROUPFULLPATHOID,DOCTYPEOID,CHECKOUT,ACLKEYCODE,NO_ACLKEYCODE,CONTENT,CUSTOM_CATEGORY,ALIAS"; // 출력필드

        String SORT_FIELD = ""; // 정렬필드
        if ( params.containsKey( "sortColumnIndex" ) && params.containsKey( "sortDirection" ) )
            SORT_FIELD = params.get( "sortColumnIndex" ) + "/" + params.get( "sortDirection" );
        else
            SORT_FIELD = "RANK/DESC";

        // create object
        QueryAPI530.Search search = new QueryAPI530.Search( );
        int ret = 0;

        // common query 설정
        ret = search.w3SetCodePage( "UTF-8" );
        ret = search.w3SetQueryLog( QUERY_LOG );

        String query = "";
        if ( params.containsKey( "query" ) ) {
            query = params.get( "query" );
            ret = search.w3SetCommonQuery( query , EXTEND_OR );
        }

        // collection, 검색 필드, 출력 필드 설정
        ret = search.w3AddCollection( COLLECTION );
        ret = search.w3SetPageInfo( COLLECTION , PAGE_START , RESULT_COUNT );
        ret = search.w3SetSortField( COLLECTION , SORT_FIELD );
        ret = search.w3SetSearchField( COLLECTION , SEARCH_FIELD );
        ret = search.w3SetDocumentField( COLLECTION , DOCUMENT_FIELD );

        ret = search.w3SetRanking( COLLECTION, "basic", "prkmfo",1000);

        StringBuilder filterQueryBuilder = new StringBuilder( );
        StringBuilder collectionQueryBuilder = new StringBuilder( );

        String name = "";
        if ( params.containsKey( "name" ) ) {
            name = params.get( "name" );
//			ret = search.w3SetFilterQuery(COLLECTION, "<FILENAME:substring:" + name + ">"); 
            filterQueryBuilder.append( "<FILENAME:substring:" + name + ">" );
            filterQueryBuilder.append( " " );
        }

        String contents = "";
        if ( params.containsKey( "contents" ) ) {
            contents = params.get( "contents" );
//			ret = search.w3SetCollectionQuery(COLLECTION, "<CONTENT:contains:" + contents + ">");
            collectionQueryBuilder.append( "<CONTENT:contains:" + contents + ">" );
            collectionQueryBuilder.append( " " );
        }

        String creatorOID = "";
        if ( params.containsKey( "creatorOID" ) ) {
            creatorOID = params.get( "creatorOID" );
//			ret = search.w3SetCollectionQuery(COLLECTION, "<CREATOROID:contains:" + creatorOID + ">");
            collectionQueryBuilder.append( "<CREATOROID:contains:" + creatorOID + ">" );
            collectionQueryBuilder.append( " " );
        }

        String managerGroupOID = "";
        String includeFolderChildren = "";
        if ( params.containsKey( "managerGroupOID" ) ) {
            managerGroupOID = params.get( "managerGroupOID" );

            if ( params.containsKey( "includeFolderChildren" ) ) {
                includeFolderChildren = params.get( "includeFolderChildren" );

                if ( includeFolderChildren.contentEquals( "true" ) ) {
//					ret = search.w3SetFilterQuery(COLLECTION, "<MANAGERGROUPFULLPATHOID:substring:" + managerGroupOID + ">");
                    filterQueryBuilder.append( "<MANAGERGROUPFULLPATHOID:substring:" + managerGroupOID + ">" );
                    filterQueryBuilder.append( " " );
                } else {
//					ret = search.w3SetCollectionQuery(COLLECTION, "<MANAGERGROUPOID:contains:" + managerGroupOID + ">");
                    collectionQueryBuilder.append( "<MANAGERGROUPOID:contains:" + managerGroupOID + ">" );
                    collectionQueryBuilder.append( " " );
                }
            }
        }

        String fileType = "";
        if ( params.containsKey( "fileType" ) ) {
            fileType = params.get( "fileType" );
//			ret = search.w3SetCollectionQuery(COLLECTION, "<FILETYPE:contains:"+ fileType + ">");
            collectionQueryBuilder.append( "<FILETYPE:contains:" + fileType + ">" );
            collectionQueryBuilder.append( " " );
        }

        String aclFilterInfos = "";
        String aclfilterInfoOidType = "";
        // String aclfilterInfoAccessGrade = "";

        if ( params.containsKey( "aclFilterInfos" ) ) {
            aclFilterInfos = params.get( "aclFilterInfos" );

            StringBuilder sb = new StringBuilder( );
            for ( String item : aclFilterInfos.split( "," ) ) {

                String[] aclfilterInfoDetails = item.trim( )
                                                    .split( "\\|" );
                aclfilterInfoOidType = aclfilterInfoDetails[ 0 ];
                // aclfilterInfoAccessGrade = aclfilterInfoDetails[1];
                sb.append( "(" );
                sb.append( "<ACLKEYCODE:substring:" )
                  .append( aclfilterInfoOidType )
                  .append( ">" );
                sb.append( "|" );

                /*
                 * for(char c = aclfilterInfoAccessGrade.charAt(0); c <= 'z'; c++) {
                 * sb.append("<ACLKEYCODE:substring:").append(aclfilterInfoOidType).append("|").
                 * append(c).append(">"); sb.append("|"); }
                 */
            }
//			ret = search.w3SetFilterQuery(COLLECTION, sb.toString());
            filterQueryBuilder.append( sb.toString( )
                                         .subSequence( 0 , sb.toString( )
                                                             .length( ) - 1 ) );
            filterQueryBuilder.append( ")" );
            filterQueryBuilder.append( " " );
        } else {
            throw new MissingArgumentException( "aclFilterInfos는 '필수'값 입니다." );
        }

        if ( params.containsKey( "modifyFrom" ) && params.containsKey( "modifyTo" ) ) {
//			ret = search.w3SetFilterQuery(COLLECTION, "<DATE:gte:" + params.get("modifyFrom") +"> <DATE:lte:"+ params.get("modifyTo") + ">");
            filterQueryBuilder
                    .append( "<DATE:gte:" + params.get( "modifyFrom" ) + "> <DATE:lte:" + params.get( "modifyTo" ) + ">" );
            filterQueryBuilder.append( " " );
        }

        if ( params.containsKey( "fileSizeFrom" ) && params.containsKey( "fileSizeTo" ) ) {
//			ret = search.w3SetFilterQuery(COLLECTION, "<FILESIZEM:gt:" + params.get("fileSizeFrom") +"> <FILESIZEM:lt:"+ params.get("fileSizeTo") + ">");
            filterQueryBuilder.append( "<FILESIZEM:gt:" + params.get( "fileSizeFrom" ) + "> <FILESIZEM:lt:"
                    + params.get( "fileSizeTo" ) + ">" );
            filterQueryBuilder.append( " " );
        }

        String folderOid = "";
        if ( params.containsKey( "folderOid" ) ) {
            folderOid = params.get( "folderOid" );
//			ret = search.w3SetFilterQuery(COLLECTION, "<FOLDERFULLPATHOID:substring:"+ folderOid + ">");
            filterQueryBuilder.append( "<FOLDERFULLPATHOID:substring:" + folderOid + ">" );
            filterQueryBuilder.append( " " );
        }

        log.info( "filterQuery: {}" , filterQueryBuilder.toString( )
                                                        .trim( ) );
        log.info( "collectionQuery: {}" , collectionQueryBuilder.toString( )
                                                                .trim( ) );
        ret = search.w3SetFilterQuery( COLLECTION , filterQueryBuilder.toString( )
                                                                      .trim( ) );
        ret = search.w3SetCollectionQuery( COLLECTION , collectionQueryBuilder.toString( )
                                                                              .trim( ) );

        // request
        ret = search.w3ConnectServer( server_ip , server_port , server_timeout );
        ret = search.w3ReceiveSearchQueryResult( 3 );

        // check error
        if ( search.w3GetError( ) != 0 ) {
            log.info( "검색 오류 로그 : {}" , search.w3GetErrorInfo( ) );
            return null;
        }

        // 전체건수, 결과건수 출력
        int totalCount = search.w3GetResultTotalCount( COLLECTION );
        int resultCount = search.w3GetResultCount( COLLECTION );

        log.info( "검색 결과 : " + resultCount + "건 / 전체 건수 : " + totalCount + "건" );

        for ( int i = 0 ; i < resultCount ; i++ ) {

            // 기본 검색결과 객체 생성
            String oid = search.w3GetField( COLLECTION , "OID" , i );
            String targetoid = search.w3GetField( COLLECTION , "TARGETOID" , i );
            String storagefileid = search.w3GetField( COLLECTION , "STORAGEFILEID" , i );
            String filename = search.w3GetField( COLLECTION , "FILENAME" , i );
            String documentname = search.w3GetField( COLLECTION , "DOCUMENTNAME" , i );
            String taglist = search.w3GetField( COLLECTION , "TAGLIST" , i );
            String creatoroid = search.w3GetField( COLLECTION , "CREATOROID" , i );
            String creatorname = search.w3GetField( COLLECTION , "CREATORNAME" , i );
            String creatorgroupname = search.w3GetField( COLLECTION , "CREATORGROUPNAME" , i );
            String lastmodifieroid = search.w3GetField( COLLECTION , "LASTMODIFIEROID" , i );
            String lastmodifiedat = search.w3GetField( COLLECTION , "LASTMODIFIEDAT" , i );
            String filetype = search.w3GetField( COLLECTION , "FILETYPE" , i );
            String filesize = search.w3GetField( COLLECTION , "FILESIZE" , i );
            String filesizem = search.w3GetField( COLLECTION , "FILESIZEM" , i );
            String folderoid = search.w3GetField( COLLECTION , "FOLDEROID" , i );
            String folderfullpathname = search.w3GetField( COLLECTION , "FOLDERFULLPATHNAME" , i );
            String folderfullpathoid = search.w3GetField( COLLECTION , "FOLDERFULLPATHOID" , i );
            String managergroupoid = search.w3GetField( COLLECTION , "MANAGERGROUPOID" , i );
            String managergroupfullpathoid = search.w3GetField( COLLECTION , "MANAGERGROUPFULLPATHOID" , i );
            String doctypeoid = search.w3GetField( COLLECTION , "DOCTYPEOID" , i );
            String checkout = search.w3GetField( COLLECTION , "CHECKOUT" , i );
            String content = search.w3GetField( COLLECTION , "CONTENT" , i );
            String aclKeyCode = search.w3GetField( COLLECTION , "ACLKEYCODE" , i );

            // String managergroupfullpathoid = search.w3GetField(COLLECTION,
            // "MANAGERGROUPFULLPATHOID", i);
            // String doctypeoid = search.w3GetField(COLLECTION, "DOCTYPEOID", i);

            FileSearchVo vo = FileSearchVo.builder( )
                                          .oid( oid )
                                          .targetoid( targetoid )
                                          .storagefileid( storagefileid )
                                          .filename( filename )
                                          .documentname( documentname )
                                          .taglist( taglist )
                                          .creatoroid( creatoroid )
                                          .creatorname( creatorname )
                                          .creatorgroupname( creatorgroupname )
                                          .lastmodifieroid( lastmodifieroid )
                                          .lastmodifiedat( lastmodifiedat )
                                          .filetype( filetype )
                                          .filesize( filesize )
                                          .filesizem( filesizem )
                                          .folderoid( folderoid )
                                          .folderfullpathname( folderfullpathname )
                                          .folderfullpathoid( folderfullpathoid )
                                          .managergroupoid( managergroupoid )
                                          .managergroupfullpathoid( managergroupfullpathoid )
                                          .doctypeoid( doctypeoid )
                                          .checkout( checkout )
                                          .content( content )
                                          .aclkeycode( aclKeyCode )
                                          .build( );

            list.add( vo );

        }

        log.info( "list  : " + list );

        return FileSearch.builder( )
                         .Collection( COLLECTION )
                         .TotalCount( totalCount )
                         .Count( resultCount )
                         .Result( list )
                         .build( );

    }

    /**
     * 문서 통합 검색
     *
     * @throws MissingArgumentException
     * @author 안선정
     */

    // public List<DocumentSearchVo> searchDocumentTotalListByCategory(String
    // collection, String query, String userinfo, String aclfilterInfo) {
    public DocumentSearch searchDocumentTotalListByCategory( Map< String, String > params ) throws MissingArgumentException {

        log.info( "**********************************" );
        params.keySet( )
              .stream( )
              .forEach( key -> log.info( " - " + key + ": {}" , params.get( key ) ) );
        log.info( "**********************************" );

        String query = "";
        if ( params.containsKey( "query" ) ) {
            query = params.get( "query" );
        } else {
            throw new MissingArgumentException( "query는 '필수'값 입니다." );
        }

        List< DocumentSearchVo > list = new ArrayList<>( );

        // collection, 검색필드, 출력필드 정의
        String COLLECTION = "";
        if ( params.containsKey( "searchTargetOID" ) ) {
            COLLECTION = params.get( "searchTargetOID" );
            if ( COLLECTION.contentEquals( "ALL" ) )
                COLLECTION = "documentinfo";
        } else {
            throw new MissingArgumentException( "searchTargetOID는 '필수'값 입니다." );
        }

        int QUERY_LOG = 1;
        int EXTEND_OR = 0;
        int RESULT_COUNT = params.containsKey( "count" ) ? Integer.parseInt( params.get( "count" ) ) : 10; // 한번에 출력되는 검색 건수
        int PAGE_START = params.containsKey( "pageStart" ) ? Integer.parseInt( params.get( "pageStart" ) ) * RESULT_COUNT : 0; // 검색 결과를 받아오는 시작 위치
        String SORT_FIELD = "RANK/DESC"; // 정렬필드
        String SEARCH_FIELD = "NAME,DESCRIPTION,TAGLIST,CREATOROID,CREATORNAME,DIRECTOR,AUTHOR,COAUTHOR,REVIEWER,APPROVER,RECEIVER,CREATEDATN,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPFULLPATHOID,DOCTYPEOID"; // 검색필드
        String DOCUMENT_FIELD = "DOCID,DATE,OID,NAME,DESCRIPTION,TAGLIST,CREATOROID,CREATORNAME,CREATORGROUPNAME,DIRECTOR,AUTHOR,COAUTHOR,REVIEWER,APPROVER,RECEIVER,CREATEDAT,CREATEDATN,LASTMODIFIEDAT,LASTMODIFIEDATN,REVIEWEDAT,REVIEWEDATN,APPROVEDAT,APPROVEDATN,FOLDEROID,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPOID,MANAGERGROUPFULLPATHOID,DOCTYPEOID,CHECKOUT,ACLKEYCODE,NO_ACLKEYCODE"; // 출력필드

        // create object
        QueryAPI530.Search search = new QueryAPI530.Search( );
        int ret = 0;

        // common query 설정
        ret = search.w3SetCodePage( "UTF-8" );
        ret = search.w3SetQueryLog( QUERY_LOG );
        ret = search.w3SetCommonQuery( query , EXTEND_OR );
        log.info( "query : " + query );

        // collection, 검색 필드, 출력 필드 설정
        ret = search.w3AddCollection( COLLECTION );
        ret = search.w3SetPageInfo( COLLECTION , PAGE_START , RESULT_COUNT );
        ret = search.w3SetSortField( COLLECTION , SORT_FIELD );
        ret = search.w3SetSearchField( COLLECTION , SEARCH_FIELD );
        ret = search.w3SetDocumentField( COLLECTION , DOCUMENT_FIELD );

        ret = search.w3SetRanking( COLLECTION, "basic", "prkmfo",1000);

        String aclFilterInfos = "";
        String aclfilterInfoOidType = "";
        // String aclfilterInfoAccessGrade = "";

        if ( params.containsKey( "aclFilterInfos" ) ) {
            aclFilterInfos = params.get( "aclFilterInfos" );
            StringBuilder sb = new StringBuilder( );

            for ( String item : aclFilterInfos.split( "," ) ) {

                String[] aclfilterInfoDetails = item.trim( )
                                                    .split( "\\|" );
                aclfilterInfoOidType = aclfilterInfoDetails[ 0 ];
                // aclfilterInfoAccessGrade = aclfilterInfoDetails[1];

                sb.append( "<ACLKEYCODE:substring:" )
                  .append( aclfilterInfoOidType )
                  .append( ">" );
                sb.append( "|" );

                /*
                 * for(char c = aclfilterInfoAccessGrade.charAt(0); c <= 'z'; c++) {
                 * sb.append("<ACLKEYCODE:substring:").append(aclfilterInfoOidType).append("|").
                 * append(c).append(">"); sb.append("|"); }
                 */
            }
            ret = search.w3SetFilterQuery( COLLECTION , sb
                    .substring( 0 , sb.toString( )
                                      .length( ) - 1 ) );
        } else {
            throw new MissingArgumentException( "aclFilterInfos는 '필수'값 입니다." );
        }


        // request
        ret = search.w3ConnectServer( server_ip , server_port , server_timeout );
        ret = search.w3ReceiveSearchQueryResult( 3 );

        // check error
        if ( search.w3GetError( ) != 0 ) {
            log.info( "검색 오류 로그 : {}" , search.w3GetErrorInfo( ) );
            return null;
        }

        // 전체건수, 결과건수 출력
        int totalCount = search.w3GetResultTotalCount( COLLECTION );
        int resultCount = search.w3GetResultCount( COLLECTION );

        log.info( "검색 결과 : " + resultCount + "건 / 전체 건수 : " + totalCount + "건" );

        for ( int i = 0 ; i < resultCount ; i++ ) {

            // 기본 검색결과 객체 생성
            String name = search.w3GetField( COLLECTION , "NAME" , i );
            String description = search.w3GetField( COLLECTION , "DESCRIPTION" , i );
            String taglist = search.w3GetField( COLLECTION , "TAGLIST" , i );
            String creatoroid = search.w3GetField( COLLECTION , "CREATOROID" , i );
            String creatorname = search.w3GetField( COLLECTION , "CREATORNAME" , i );
            String creatorgroupname = search.w3GetField( COLLECTION , "CREATORGROUPNAME" , i );
            String createdat = search.w3GetField( COLLECTION , "CREATEDAT" , i );
            String lastmodifiedat = search.w3GetField( COLLECTION , "LASTMODIFIEDAT" , i );
            String folderfullpathname = search.w3GetField( COLLECTION , "FOLDERFULLPATHNAME" , i );
            String doctypeoid = search.w3GetField( COLLECTION , "DOCTYPEOID" , i );
            String aclKeyCode = search.w3GetField( COLLECTION , "ACLKEYCODE" , i );

            DocumentSearchVo vo = DocumentSearchVo.builder( )
                                                  .name( name )
                                                  .description( description )
                                                  .taglist( taglist )
                                                  .creatoroid( creatoroid )
                                                  .creatorname( creatorname )
                                                  .creatorgroupname( creatorgroupname )
                                                  .createdat( createdat )
                                                  .lastmodifiedat( lastmodifiedat )
                                                  .folderfullpathname( folderfullpathname )
                                                  .doctypeoid( doctypeoid )
                                                  .aclkeycode( aclKeyCode )
                                                  .build( );

            list.add( vo );

            log.info( "list  : " + list );

        }

        return DocumentSearch.builder( )
                             .Collection( COLLECTION )
                             .TotalCount( totalCount )
                             .Count( resultCount )
                             .Result( list )
                             .build( );
    }

    /**
     * 폴더 통합 검색
     *
     * @throws MissingArgumentException
     * @author 안선정
     */
    public FolderSearch searchFolderTotalListByCategory( Map< String, String > params ) throws MissingArgumentException {

        log.info( "**********************************" );
        params.keySet( )
              .stream( )
              .forEach( key -> log.info( " - " + key + ": {}" , params.get( key ) ) );
        log.info( "**********************************" );

        List< FolderSearchVo > list = new ArrayList<>( );

        // collection, 검색필드, 출력필드 정의
        String COLLECTION = "";
        if ( params.containsKey( "searchTargetOID" ) ) {
            COLLECTION = params.get( "searchTargetOID" );
            if ( COLLECTION.contentEquals( "ALL" ) )
                COLLECTION = "folderinfo";
        } else {
            throw new MissingArgumentException( "searchTargetOID는 '필수'값 입니다." );
        }

        int QUERY_LOG = 1;
        int EXTEND_OR = 0;
        int RESULT_COUNT = params.containsKey( "count" ) ? Integer.parseInt( params.get( "count" ) ) : 10; // 한번에 출력되는 검색 건수
        int PAGE_START = params.containsKey( "pageStart" ) ? Integer.parseInt( params.get( "pageStart" ) ) * RESULT_COUNT : 0; // 검색	결과 받아오는 시작위치
        String SORT_FIELD = ""; // 정렬필드
        if ( params.containsKey( "sortColumnIndex" ) && params.containsKey( "sortDirection" ) )
            SORT_FIELD = params.get( "sortColumnIndex" ) + "/" + params.get( "sortDirection" );
        else
            SORT_FIELD = "RANK/DESC";
        String SEARCH_FIELD = "NAME,DESCRIPTION,CREATOROID,CREATEDATE,CREATEDATN,LASTMODIFIEDATN,MANAGERGROUPFULLPATHOID,FOLDERFULLPATHOID,KNOWLEDGEFOLDERLIST,DOCTYPEFOLDER"; // 검색필드
        String DOCUMENT_FIELD = "DOCID,DATE,OID,NAME,DESCRIPTION,CREATOROID,CREATORGROUPNAME,CREATEDAT,CREATEDATE,CREATEDATN,LASTMODIFIEDAT,LASTMODIFIEDATN,MANAGERGROUPOID,FULLPATHINDEX,MANAGERGROUPFULLPATHOID,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,KNOWLEDGEFOLDERLIST,DOCTYPEFOLDER,ACLKEYCODE,NO_ACLKEYCODE"; // 출력필드

        // create object
        QueryAPI530.Search search = new QueryAPI530.Search( );
        int ret = 0;

        String query = "";
        if ( params.containsKey( "query" ) ) {
            query = params.get( "query" );
            ret = search.w3SetCommonQuery( query , EXTEND_OR );
        }

        // common query 설정
        ret = search.w3SetCodePage( "UTF-8" );
        ret = search.w3SetQueryLog( QUERY_LOG );

        // collection, 검색 필드, 출력 필드 설정
        ret = search.w3AddCollection( COLLECTION );
        ret = search.w3SetPageInfo( COLLECTION , PAGE_START , RESULT_COUNT );
        ret = search.w3SetSortField( COLLECTION , SORT_FIELD );
        ret = search.w3SetSearchField( COLLECTION , SEARCH_FIELD );
        ret = search.w3SetDocumentField( COLLECTION , DOCUMENT_FIELD );

        ret = search.w3SetRanking( COLLECTION, "basic", "prkmfo",1000);

        StringBuilder filterQueryBuilder = new StringBuilder( );
        StringBuilder collectionQueryBuilder = new StringBuilder( );

        String name = "";
        if ( params.containsKey( "name" ) ) {
            name = params.get( "name" );
//			ret = search.w3SetFilterQuery(COLLECTION, "<NAME:substring:" + name + ">"); 
            filterQueryBuilder.append( "<NAME:substring:" + name + ">" );
            filterQueryBuilder.append( " " );
        }

        String creatorOID = "";
        if ( params.containsKey( "creatorOID" ) ) {
            creatorOID = params.get( "creatorOID" );
//			ret = search.w3SetCollectionQuery(COLLECTION, "<CREATOROID:contains:" + creatorOID + ">");
            collectionQueryBuilder.append( "<CREATOROID:contains:" + creatorOID + ">" );
            collectionQueryBuilder.append( " " );
        }

        String managerGroupOID = "";
        String includeFolderChildren = "";
        if ( params.containsKey( "managerGroupOID" ) ) {
            if ( params.containsKey( "includeFolderChildren" ) ) {
                managerGroupOID = params.get( "managerGroupOID" );
                includeFolderChildren = params.get( "includeFolderChildren" );
                if ( includeFolderChildren == "true" ) {
//					ret = search.w3SetFilterQuery(COLLECTION, "<MANAGERGROUPFULLPATHOID:substring:" + managerGroupOID + ">");
                    filterQueryBuilder.append( "<MANAGERGROUPFULLPATHOID:substring:" + managerGroupOID + ">" );
                    filterQueryBuilder.append( " " );
                } else {
//					ret = search.w3SetCollectionQuery(COLLECTION, "<MANAGERGROUPOID:contains:" + managerGroupOID + ">");
                    collectionQueryBuilder.append( "<MANAGERGROUPOID:contains:" + managerGroupOID + ">" );
                    collectionQueryBuilder.append( " " );
                }
            }
        }

        String aclFilterInfos = "";
        String aclfilterInfoOidType = "";
        // String aclfilterInfoAccessGrade = "";

        if ( params.containsKey( "aclFilterInfos" ) ) {
            aclFilterInfos = params.get( "aclFilterInfos" );
            StringBuilder sb = new StringBuilder( );
            for ( String item : aclFilterInfos.split( "," ) ) {

                String[] aclfilterInfoDetails = item.trim( )
                                                    .split( "\\|" );
                aclfilterInfoOidType = aclfilterInfoDetails[ 0 ];
                // aclfilterInfoAccessGrade = aclfilterInfoDetails[1];
                sb.append( "(" );
                sb.append( "<ACLKEYCODE:substring:" )
                  .append( aclfilterInfoOidType )
                  .append( ">" );
                sb.append( "|" );

                /*
                 * for(char c = aclfilterInfoAccessGrade.charAt(0); c <= 'z'; c++) {
                 * sb.append("<ACLKEYCODE:substring:").append(aclfilterInfoOidType).append("|").
                 * append(c).append(">"); sb.append("|"); }
                 */
            }
//			ret = search.w3SetFilterQuery(COLLECTION, sb.toString());
            filterQueryBuilder.append( sb , 0 , sb.toString( )
                                                  .length( ) - 1 );
            System.out.println( sb
                    .substring( 0 , sb.toString( )
                                      .length( ) - 1 ) );
            filterQueryBuilder.append( ")" );
            filterQueryBuilder.append( " " );
        } else {
            throw new MissingArgumentException( "aclFilterInfos는 '필수'값 입니다." );
        }

        if ( params.containsKey( "createFrom" ) && params.containsKey( "createTo" ) ) {
//			ret = search.w3SetFilterQuery(COLLECTION, "<CREATEDATE:gte:" + params.get("createFrom") +"> <CREATEDATE:lte:"+ params.get("createTo") + ">");
            filterQueryBuilder.append( "<CREATEDATE:gte:" + params.get( "createFrom" ) + "> <CREATEDATE:lte:"
                    + params.get( "createTo" ) + ">" );
            filterQueryBuilder.append( " " );
        }

        if ( params.containsKey( "modifyFrom" ) && params.containsKey( "modifyTo" ) ) {
//			ret = search.w3SetFilterQuery(COLLECTION, "<DATE:gte:" + params.get("modifyFrom") +"> <DATE:lte:"+ params.get("modifyTo") + ">");
            filterQueryBuilder
                    .append( "<DATE:gte:" + params.get( "modifyFrom" ) + "> <DATE:lte:" + params.get( "modifyTo" ) + ">" );
            filterQueryBuilder.append( " " );
        }

        String folderOid = "";
        if ( params.containsKey( "folderOid" ) ) {
            folderOid = params.get( "folderOid" );
//			ret = search.w3SetCollectionQuery(COLLECTION, "<FOLDERFULLPATHOID:contains:"+ folderOid + ">");
            filterQueryBuilder.append( "<FOLDERFULLPATHOID:substring:" + folderOid + ">" );
            filterQueryBuilder.append( " " );
        }

        log.info( "filterQuery: {}" , filterQueryBuilder.toString( )
                                                        .trim( ) );
        log.info( "collectionQuery: {}" , collectionQueryBuilder.toString( )
                                                                .trim( ) );
        ret = search.w3SetFilterQuery( COLLECTION , filterQueryBuilder.toString( )
                                                                      .trim( ) );
        ret = search.w3SetCollectionQuery( COLLECTION , collectionQueryBuilder.toString( )
                                                                              .trim( ) );

        // request
        ret = search.w3ConnectServer( server_ip , server_port , server_timeout );
        ret = search.w3ReceiveSearchQueryResult( 3 );

        // check error
        if ( search.w3GetError( ) != 0 ) {
            log.info( "검색 오류 로그 : {}" , search.w3GetErrorInfo( ) );
            return null;
        }

        // 전체건수, 결과건수 출력
        int totalCount = search.w3GetResultTotalCount( COLLECTION );
        int resultCount = search.w3GetResultCount( COLLECTION );

        log.info( "검색 결과 : " + resultCount + "건 / 전체 건수 : " + totalCount + "건" );

        for ( int i = 0 ; i < resultCount ; i++ ) {

            // 기본 검색결과 객체 생성
            String oid = search.w3GetField( COLLECTION , "OID" , i );
            String foldername = search.w3GetField( COLLECTION , "NAME" , i );
            String description = search.w3GetField( COLLECTION , "DESCRIPTION" , i );
            String creatoroid = search.w3GetField( COLLECTION , "CREATOROID" , i );
            String creatorgroupname = search.w3GetField( COLLECTION , "CREATORGROUPNAME" , i );
            String createdat = search.w3GetField( COLLECTION , "CREATEDAT" , i );
            String lastmodifiedat = search.w3GetField( COLLECTION , "LASTMODIFIEDAT" , i );
            String folderfullpathname = search.w3GetField( COLLECTION , "FOLDERFULLPATHNAME" , i );
            String folderfullpathoid = search.w3GetField( COLLECTION , "FOLDERFULLPATHOID" , i );
            String managergroupoid = search.w3GetField( COLLECTION , "MANAGERGROUPOID" , i );
            String managergroupfullpathoid = search.w3GetField( COLLECTION , "MANAGERGROUPFULLPATHOID" , i );
            String fullpathindex = search.w3GetField( COLLECTION , "FULLPATHINDEX" , i );
            String knowledgefolderlist = search.w3GetField( COLLECTION , "KNOWLEDGEFOLDERLIST" , i );
            String doctypefolder = search.w3GetField( COLLECTION , "DOCTYPEFOLDER" , i );
            String aclKeyCode = search.w3GetField( COLLECTION , "ACLKEYCODE" , i );

            FolderSearchVo vo = FolderSearchVo.builder( )
                                              .oid( oid )
                                              .foldername( foldername )
                                              .description( description )
                                              .creatoroid( creatoroid )
                                              .creatorgroupname( creatorgroupname )
                                              .createdat( createdat )
                                              .lastmodifiedat( lastmodifiedat )
                                              .folderfullpathname( folderfullpathname )
                                              .folderfullpathoid( folderfullpathoid )
                                              .managergroupoid( managergroupoid )
                                              .managergroupfullpathoid( managergroupfullpathoid )
                                              .fullpathindex( fullpathindex )
                                              .knowledgefolderlist( knowledgefolderlist )
                                              .doctypefolder( doctypefolder )
                                              .aclkeycode( aclKeyCode )
                                              .build( );

            list.add( vo );

        }

        log.info( "list  : " + list );

        return FolderSearch.builder( )
                           .Collection( COLLECTION )
                           .TotalCount( totalCount )
                           .Count( resultCount )
                           .Result( list )
                           .build( );

    }

    /**
     * 개인정보 검색
     *
     * @throws MissingArgumentException
     * @author 안선정
     */
    public SearchPersonalDTO searchPersonalDataTotalListByCategory( Map< String, String > params ) throws MissingArgumentException {

        log.info( "**********************************" );
        params.keySet( )
              .stream( )
              .forEach( key -> log.info( " - " + key + ": {}" , params.get( key ) ) );
        log.info( "**********************************" );

        String query = "";
        if ( params.containsKey( "query" ) )
            query = params.get( "query" );
        /*} else {
            throw new MissingArgumentException("query는 '필수'값 입니다.");
        }*/

        List< FileSearchVo > list = new ArrayList<>( );

        // collection, 검색필드, 출력필드 정의
        String COLLECTION = "fileinfo";
        if ( params.containsKey( "searchTargetOID" ) )
            COLLECTION = params.get( "searchTargetOID" );
        /* } else {
            throw new MissingArgumentException("searchTargetOID는 '필수'값 입니다.");
        }*/

        int QUERY_LOG = 1;
        int EXTEND_OR = 0;
        String startDate = "1970/01/01";

        LocalDate currentDate = LocalDate.now( );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy/MM/dd" );
        String formattedDate = currentDate.format( formatter );
        String endDate = formattedDate;

        int RESULT_COUNT = params.containsKey( "count" ) ? Integer.parseInt( params.get( "count" ) ) : 10; // 한번에 출력되는 검색 건수
        int PAGE_START = params.containsKey( "pageStart" ) ? Integer.parseInt( params.get( "pageStart" ) ) * RESULT_COUNT : 0; // 검색 결과를 받아오는 시작 위치
        String SORT_FIELD = "RANK/DESC"; // 정렬필드
        String SEARCH_FIELD = "FILENAME,DOCUMENTNAME,TAGLIST,CREATOROID,CREATORNAME,FILETYPE,FILESIZE,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPFULLPATHOID,DOCTYPEOID,CONTENT"; // 검색필드
        String DOCUMENT_FIELD = "DOCID,DATE,TARGETOID,OID,STORAGEFILEID,FILENAME,DOCUMENTNAME,TAGLIST,CREATOROID,CREATORNAME,CREATORGROUPNAME,LASTMODIFIEROID,LASTMODIFIEDAT,LASTMODIFIEDATN,FILETYPE,FILESIZE,FILESIZEM,FOLDEROID,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPOID,MANAGERGROUPFULLPATHOID,DOCTYPEOID,CHECKOUT,ACLKEYCODE,NO_ACLKEYCODE,CONTENT,CUSTOM_CATEGORY,ALIAS"; // 출력필드
        String CATEGORY_FILED = "CUSTOM_CATEGORY";

        // create object
        QueryAPI530.Search search = new QueryAPI530.Search( );
        int ret = 0;

        // common query 설정
        ret = search.w3SetCodePage( "UTF-8" );
        ret = search.w3SetQueryLog( QUERY_LOG );
        ret = search.w3SetCommonQuery( query , EXTEND_OR );
        log.info( "query : " + query );

        // collection, 검색 필드, 출력 필드 설정
        ret = search.w3AddCollection( COLLECTION );
        ret = search.w3SetPageInfo( COLLECTION , PAGE_START , RESULT_COUNT );
        ret = search.w3SetSortField( COLLECTION , SORT_FIELD );
        ret = search.w3SetSearchField( COLLECTION , SEARCH_FIELD );
        ret = search.w3SetDocumentField( COLLECTION , DOCUMENT_FIELD );

        ret = search.w3SetRanking( COLLECTION, "basic", "prkmfo",1000);

        // category
        ret = search.w3AddCategoryGroupBy( COLLECTION, CATEGORY_FILED, "1/SC/10");

        // category 필드 설정(개인정보 추출 위함)
        int groupCount = search.w3GetCategoryCount( COLLECTION , CATEGORY_FILED , 1 );
        System.out.println( "groupcount: " + groupCount );
//        if ( groupCount <= 0 ) return SearchPersonalDTO.builder( )
//                                                       .groups( groupNameService.getGroupNames( ) )
//                                                       .build( );
        String categoryName = "";
        int categoryCount = 0;
        Map< String, Integer > tagCountMap = new HashMap< String, Integer >( );

        for ( int i = 0 ; i < groupCount ; i++ ) {
        //for ( int i = 0 ; i < 10000 ; i++ ) {
            categoryName = search.w3GetCategoryName( COLLECTION , CATEGORY_FILED , 1 , i );
            categoryCount = search.w3GetDocumentCountInCategory( COLLECTION , CATEGORY_FILED , 1 , i );
            tagCountMap.put( categoryName , categoryCount );
        }

        ret = search.w3SetDateRange( COLLECTION , startDate , endDate );

       /* String aclFilterInfos = "";
        String aclfilterInfoOidType = "";
        // String aclfilterInfoAccessGrade = "";

        if (params.containsKey("aclFilterInfos")) {
            aclFilterInfos = params.get("aclFilterInfos");
            StringBuilder sb = new StringBuilder();

            for (String item : aclFilterInfos.split(",")) {

                String[] aclfilterInfoDetails = item.trim()
                                                    .split("\\|");
                aclfilterInfoOidType = aclfilterInfoDetails[0];
                //aclfilterInfoAccessGrade = aclfilterInfoDetails[1];

                sb.append("<ACLKEYCODE:substring:")
                  .append(aclfilterInfoOidType)
                  .append(">");
                sb.append("|");


                 for(char c = aclfilterInfoAccessGrade.charAt(0); c <= 'z'; c++) {
                    sb.append("<ACLKEYCODE:substring:").append(aclfilterInfoOidType).append("|").
                    append(c).append(">"); sb.append("|"); }

            }
            ret = search.w3SetFilterQuery(COLLECTION, sb
                    .substring(0, sb.toString()
                                    .length() - 1));
        } else {
            throw new MissingArgumentException("aclFilterInfos는 '필수'값 입니다.");
        } */

        // request
        ret = search.w3ConnectServer( server_ip , server_port , server_timeout );
        ret = search.w3ReceiveSearchQueryResult( 3 );

        // check error
        if ( search.w3GetError( ) != 0 ) {
            log.info( "검색 오류 로그 : {}" , search.w3GetErrorInfo( ) );
            return null;
        }

        // 전체건수, 결과건수 출력
        int totalCount = search.w3GetResultTotalCount( COLLECTION );
        int resultCount = search.w3GetResultCount( COLLECTION );

        log.info( "검색 결과 : " + resultCount + "건 / 전체 건수 : " + totalCount + "건" );

        for ( int i = 0 ; i < resultCount ; i++ ) {

            // 기본 검색결과 객체 생성
            String oid = search.w3GetField( COLLECTION , "OID" , i );
            String targetoid = search.w3GetField( COLLECTION , "TARGETOID" , i );
            String storagefileid = search.w3GetField( COLLECTION , "STORAGEFILEID" , i );
            String filename = search.w3GetField( COLLECTION , "FILENAME" , i );
            String documentname = search.w3GetField( COLLECTION , "DOCUMENTNAME" , i );
            String taglist = search.w3GetField( COLLECTION , "TAGLIST" , i );
            String creatoroid = search.w3GetField( COLLECTION , "CREATOROID" , i );
            String creatorname = search.w3GetField( COLLECTION , "CREATORNAME" , i );
            String creatorgroupname = search.w3GetField( COLLECTION , "CREATORGROUPNAME" , i );
            String lastmodifiedat = search.w3GetField( COLLECTION , "LASTMODIFIEDAT" , i );
            String filetype = search.w3GetField( COLLECTION , "FILETYPE" , i );
            String filesize = search.w3GetField( COLLECTION , "FILESIZE" , i );
            String filesizem = search.w3GetField( COLLECTION , "FILESIZEM" , i );
            String folderoid = search.w3GetField( COLLECTION , "FOLDEROID" , i );
            String folderfullpathname = search.w3GetField( COLLECTION , "FOLDERFULLPATHNAME" , i );
            String managergroupoid = search.w3GetField( COLLECTION , "MANAGERGROUPOID" , i );
            String managergroupfullpathoid = search.w3GetField( COLLECTION , "MANAGERGROUPFULLPATHOID" , i );
            String doctypeoid = search.w3GetField( COLLECTION , "DOCTYPEOID" , i );
            String checkout = search.w3GetField( COLLECTION , "CHECKOUT" , i );
            String content = search.w3GetField( COLLECTION , "CONTENT" , i );
            String aclKeyCode = search.w3GetField( COLLECTION , "ACLKEYCODE" , i );
            //String customcategory = search.w3GetField( COLLECTION, "CUSTOM_CATEGORY", i );
            String alias = search.w3GetField( COLLECTION , "ALIAS" , i );

            // List<String> filterList = Arrays.asList(securityFilter.split("\\|"));
            List< SecurityVo > security = new ArrayList<>( );
            if ( !alias.trim( )
                       .contentEquals( "" ) ) {
                String[] aliasArr = alias.split( "\\|" );

                for ( String item : aliasArr ) {
                    String securityInfo = item.split( "/" )[ 0 ];
                    String securityCount = "(" + item.split( "/" )[ 1 ] + ")";

                    switch ( securityInfo ) {
                        case "JUMIN":
                            securityInfo = "주민등록번호";
                            break;
                        case "FOREIGN":
                            securityInfo = "외국인등록번호";
                            break;
                        case "DRIVE":
                            securityInfo = "운전면허번호";
                            break;
                        case "PASS_PORT":
                            securityInfo = "여권번호";
                            break;
                    }

                    security.add( SecurityVo.builder( )
                                            .securityInfo( securityInfo )
                                            .securityCount( securityCount )
                                            .build( ) );
                }
            }

            FileSearchVo vo = FileSearchVo.builder( )
                                          .oid( oid )
                                          .targetoid( targetoid )
                                          .storagefileid( storagefileid )
                                          .filename( filename )
                                          .documentname( documentname )
                                          .taglist( taglist )
                                          .creatoroid( creatoroid )
                                          .creatorname( creatorname )
                                          .creatorgroupname( creatorgroupname )
                                          .lastmodifiedat( lastmodifiedat )
                                          .filetype( filetype )
                                          .filesize( filesize )
                                          .filesizem( filesizem )
                                          .folderoid( folderoid )
                                          .folderfullpathname( folderfullpathname )
                                          .managergroupoid( managergroupoid )
                                          .managergroupfullpathoid( managergroupfullpathoid )
                                          .doctypeoid( doctypeoid )
                                          .checkout( checkout )
                                          .content( content )
                                          .aclkeycode( aclKeyCode )
                                          .security( security )
                                          .build( );

            list.add( vo );

        }

        log.info( "list  : " + list );

        FileSearch file = FileSearch.builder( )
                                    .Collection( COLLECTION )
                                    .TotalCount( totalCount )
                                    .Count( resultCount )
                                    .Result( list )
                                    .build( );

        SearchPersonalDTOBuilder dtoBuilder = SearchPersonalDTO.builder( );
        List< Object > data = new ArrayList<>( );
        data.add( file );
        SearchPersonalDTO dto = dtoBuilder.data( data )
                                          .groups( groupNameService.getGroupNames( ) )
                                          .customCategoryMap( tagCountMap )
                                          .build( );

        return dto;
    }

    public FileSearch searchSensitiveFileTotalListByCategory( Map< String, String > params ) throws MissingArgumentException {

        log.info( "**********************************" );
        params.keySet( )
              .stream( )
              .forEach( key -> log.info( " - " + key + ": {}" , params.get( key ) ) );
        log.info( "**********************************" );

        String query = "";
        if ( params.containsKey( "query" ) ) {
            query = params.get( "query" );
        } else {
            throw new MissingArgumentException( "query는 '필수'값 입니다." );
        }

        List< FileSearchVo > list = new ArrayList<>( );

        // collection, 검색필드, 출력필드 정의
        String COLLECTION = "fileinfo";
        if ( params.containsKey( "searchTargetOID" ) ) {
            COLLECTION = params.get( "searchTargetOID" );
            if ( COLLECTION.contentEquals( "ALL" ) )
                COLLECTION = "fileinfo";
        } else {
            throw new MissingArgumentException( "searchTargetOID는 '필수'값 입니다." );
        }

        int QUERY_LOG = 1;
        int EXTEND_OR = 1; // and 검색결과가 없을 시 or로 확장검색
        int RESULT_COUNT = params.containsKey( "count" ) ? Integer.parseInt( params.get( "count" ) ) : 10; // 한번에 출력되는 검색 건수
        int PAGE_START = params.containsKey( "pageStart" ) ? Integer.parseInt( params.get( "pageStart" ) ) * RESULT_COUNT : 0; // 검색 결과를 받아오는 시작 위치
        String SORT_FIELD = "DATE/DESC"; // 정렬필드
        String SEARCH_FIELD = "FILENAME,DOCUMENTNAME,TAGLIST,CREATOROID,CREATORNAME,FILETYPE,FILESIZE,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPFULLPATHOID,DOCTYPEOID,CONTENT"; // 검색필드
        String DOCUMENT_FIELD = "DOCID,DATE,TARGETOID,OID,STORAGEFILEID,FILENAME,DOCUMENTNAME,TAGLIST,CREATOROID,CREATORNAME,CREATORGROUPNAME,LASTMODIFIEROID,LASTMODIFIEDAT,LASTMODIFIEDATN,FILETYPE,FILESIZE,FILESIZEM,FOLDEROID,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPOID,MANAGERGROUPFULLPATHOID,DOCTYPEOID,CHECKOUT,ACLKEYCODE,NO_ACLKEYCODE,CONTENT,CUSTOM_CATEGORY,ALIAS"; // 출력필드

        // create object
        QueryAPI530.Search search = new QueryAPI530.Search( );
        int ret = 0;

        // common query 설정
        ret = search.w3SetCodePage( "UTF-8" );
        ret = search.w3SetQueryLog( QUERY_LOG );
        ret = search.w3SetCommonQuery( query , EXTEND_OR );
        log.info( "query : " + query );

        // collection, 검색 필드, 출력 필드 설정
        ret = search.w3AddCollection( COLLECTION );
        ret = search.w3SetPageInfo( COLLECTION , PAGE_START , RESULT_COUNT );
        ret = search.w3SetSortField( COLLECTION , SORT_FIELD );
        ret = search.w3SetSearchField( COLLECTION , SEARCH_FIELD );
        ret = search.w3SetDocumentField( COLLECTION , DOCUMENT_FIELD );

		/*String aclFilterInfos = "";
		String aclfilterInfoOidType = "";
		// String aclfilterInfoAccessGrade = "";

		if (params.containsKey("aclFilterInfos")) {
			aclFilterInfos = params.get("aclFilterInfos");
			StringBuilder sb = new StringBuilder();

			for (String item : aclFilterInfos.split(",")) {

				String[] aclfilterInfoDetails = item.trim().split("\\|");
				aclfilterInfoOidType = aclfilterInfoDetails[0];
				// aclfilterInfoAccessGrade = aclfilterInfoDetails[1];

				sb.append("<ACLKEYCODE:substring:").append(aclfilterInfoOidType).append(">");
				sb.append("|");


				 for(char c = aclfilterInfoAccessGrade.charAt(0); c <= 'z'; c++) {
				  sb.append("<ACLKEYCODE:substring:").append(aclfilterInfoOidType).append("|").
				 append(c).append(">"); sb.append("|"); }

			}
			ret = search.w3SetFilterQuery(COLLECTION, sb.toString().substring(0, sb.toString().length() - 1));
		} else {
			throw new MissingArgumentException("aclFilterInfos는 \'필수\'값 입니다.");
		}*/


        // request
        ret = search.w3ConnectServer( server_ip , server_port , server_timeout );
        ret = search.w3ReceiveSearchQueryResult( 3 );

        // check error
        if ( search.w3GetError( ) != 0 ) {
            log.info( "검색 오류 로그 : {}" , search.w3GetErrorInfo( ) );
            return null;
        }

        // 전체건수, 결과건수 출력
        int totalCount = search.w3GetResultTotalCount( COLLECTION );
        int resultCount = search.w3GetResultCount( COLLECTION );

        log.info( "검색 결과 : " + resultCount + "건 / 전체 건수 : " + totalCount + "건" );

        for ( int i = 0 ; i < resultCount ; i++ ) {

            // 기본 검색결과 객체 생성
            String oid = search.w3GetField( COLLECTION , "OID" , i );
            String targetoid = search.w3GetField( COLLECTION , "TARGETOID" , i );
            String storagefileid = search.w3GetField( COLLECTION , "STORAGEFILEID" , i );
            String filename = search.w3GetField( COLLECTION , "FILENAME" , i );
            String documentname = search.w3GetField( COLLECTION , "DOCUMENTNAME" , i );
            String taglist = search.w3GetField( COLLECTION , "TAGLIST" , i );
            String creatoroid = search.w3GetField( COLLECTION , "CREATOROID" , i );
            String creatorname = search.w3GetField( COLLECTION , "CREATORNAME" , i );
            String creatorgroupname = search.w3GetField( COLLECTION , "CREATORGROUPNAME" , i );
            String lastmodifieroid = search.w3GetField( COLLECTION , "LASTMODIFIEROID" , i );
            String lastmodifiedat = search.w3GetField( COLLECTION , "LASTMODIFIEDAT" , i );
            String filetype = search.w3GetField( COLLECTION , "FILETYPE" , i );
            String filesize = search.w3GetField( COLLECTION , "FILESIZE" , i );
            String filesizem = search.w3GetField( COLLECTION , "FILESIZEM" , i );
            String folderoid = search.w3GetField( COLLECTION , "FOLDEROID" , i );
            String folderfullpathname = search.w3GetField( COLLECTION , "FOLDERFULLPATHNAME" , i );
            String folderfullpathoid = search.w3GetField( COLLECTION , "FOLDERFULLPATHOID" , i );
            String managergroupoid = search.w3GetField( COLLECTION , "MANAGERGROUPOID" , i );
            String managergroupfullpathoid = search.w3GetField( COLLECTION , "MANAGERGROUPFULLPATHOID" , i );
            String doctypeoid = search.w3GetField( COLLECTION , "DOCTYPEOID" , i );
            String checkout = search.w3GetField( COLLECTION , "CHECKOUT" , i );
            String content = search.w3GetField( COLLECTION , "CONTENT" , i );
            String aclKeyCode = search.w3GetField( COLLECTION , "ACLKEYCODE" , i );

            // String managergroupfullpathoid = search.w3GetField(COLLECTION,
            // "MANAGERGROUPFULLPATHOID", i);
            // String doctypeoid = search.w3GetField(COLLECTION, "DOCTYPEOID", i);

            FileSearchVo vo = FileSearchVo.builder( )
                                          .oid( oid )
                                          .targetoid( targetoid )
                                          .storagefileid( storagefileid )
                                          .filename( filename )
                                          .documentname( documentname )
                                          .taglist( taglist )
                                          .creatoroid( creatoroid )
                                          .creatorname( creatorname )
                                          .creatorgroupname( creatorgroupname )
                                          .lastmodifieroid( lastmodifieroid )
                                          .lastmodifiedat( lastmodifiedat )
                                          .filetype( filetype )
                                          .filesize( filesize )
                                          .filesizem( filesizem )
                                          .folderoid( folderoid )
                                          .folderfullpathname( folderfullpathname )
                                          .folderfullpathoid( folderfullpathoid )
                                          .managergroupoid( managergroupoid )
                                          .managergroupfullpathoid( managergroupfullpathoid )
                                          .doctypeoid( doctypeoid )
                                          .checkout( checkout )
                                          .content( content )
                                          .aclkeycode( aclKeyCode )
                                          .build( );

            list.add( vo );


        }

        log.info( "list  : " + list );

        return FileSearch.builder( )
                         .Collection( COLLECTION )
                         .TotalCount( totalCount )
                         .Count( resultCount )
                         .Result( list )
                         .build( );
    }
}