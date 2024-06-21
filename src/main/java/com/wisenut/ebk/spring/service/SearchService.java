package com.wisenut.ebk.spring.service;

import QueryAPI530.Search;
import com.wisenut.ebk.spring.MissingArgumentException;
import com.wisenut.ebk.spring.dto.SearchPersonalDTO;
import com.wisenut.ebk.spring.dto.SearchPersonalDTO.SearchPersonalDTOBuilder;
import com.wisenut.ebk.spring.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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


    final int QUERY_LOG = 1;

    final String ENCODE_VALUE = "UTF-8";

    final int SERVER_PORT = 7000;

    final int SERVER_TIMEOUT = 10 * 1000;

    private final GroupNameService groupNameService;
    // 검색기 server 설정
    @Value( "${engine.server.ip}" )
    String SERVER_IP;

    /**
     * 파일 통합 검색
     *
     * @throws MissingArgumentException
     * @author 안선정
     */
    public FileSearch searchFileTotalListByCategory( Map< String, String > params ) throws MissingArgumentException {

        List< FileSearchVo > list = new ArrayList<>( );

        String query = params.getOrDefault( "query" , "" );
        query = query.replaceAll("[^\\uAC00-\\uD7A3a-zA-Z0-9\\s\\|\\-]", "");

        log.debug( "real query : " + query );

        String COLLECTION = "";
        if ( params.containsKey( "searchTargetOID" ) ) {
            COLLECTION = params.get( "searchTargetOID" );
            if ( COLLECTION.contentEquals( "ALL" ) )
                COLLECTION = "fileinfo";
        } else {
            throw new MissingArgumentException( "searchTargetOID는 '필수'값 입니다." );
        }

        // collection, 검색필드, 출력필드 정의

        int EXTEND_OR = 1; // and 검색결과가 없을 시 or로 확장검색
        int RESULT_COUNT = Integer.parseInt( params.getOrDefault( "count" , String.valueOf( 10 ) ) ); // 한번에 출력되는 검색 건수
        int PAGE_START = Integer.parseInt( params.getOrDefault( "pageStart" , String.valueOf( 0 ) ) ); // 검색결과를 받아오는 시작 위치
//        String SEARCH_FIELD = "FILENAME,DOCUMENTNAME,TAGLIST,CREATOROID,CREATORNAME,LASTMODIFIEROID,LASTMODIFIEDAT,LASTMODIFIEDATN,FILETYPE,FILESIZE,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPOID,MANAGERGROUPFULLPATHOID,DOCTYPEOID,CHECKOUT,CONTENT,ACLKEYCODE"; // 검색필드
//        String DOCUMENT_FIELD = "DOCID,DATE,TARGETOID,OID,STORAGEFILEID,FILENAME,DOCUMENTNAME,TAGLIST,CREATOROID,CREATORNAME,CREATORGROUPNAME,CREATEDAT,LASTMODIFIEROID,LASTMODIFIEDAT,LASTMODIFIEDATN,FILETYPE,FILESIZE,FILESIZEM,FOLDEROID,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPOID,MANAGERGROUPFULLPATHOID,DOCTYPEOID,CHECKOUT,ACLKEYCODE,NO_ACLKEYCODE,CONTENT/300,CUSTOM_CATEGORY,CATEGORY_YN,ALIAS"; // 출력필드

        List< String > SEARCH_FIELD_LIST = new ArrayList<>( );
        SEARCH_FIELD_LIST.add( "FILENAME" );
        SEARCH_FIELD_LIST.add( "DOCUMENTNAME" );
        SEARCH_FIELD_LIST.add( "TAGLIST" );
        SEARCH_FIELD_LIST.add( "CREATOROID" );
        SEARCH_FIELD_LIST.add( "CREATORNAME" );
        SEARCH_FIELD_LIST.add( "LASTMODIFIEROID" );
        SEARCH_FIELD_LIST.add( "LASTMODIFIEDAT" );
        SEARCH_FIELD_LIST.add( "LASTMODIFIEDATN" );
        SEARCH_FIELD_LIST.add( "FILETYPE" );
        SEARCH_FIELD_LIST.add( "FILESIZE" );
        SEARCH_FIELD_LIST.add( "FOLDERFULLPATHOID" );
        SEARCH_FIELD_LIST.add( "FOLDERFULLPATHNAME" );
        SEARCH_FIELD_LIST.add( "MANAGERGROUPOID" );
        SEARCH_FIELD_LIST.add( "MANAGERGROUPFULLPATHOID" );
        SEARCH_FIELD_LIST.add( "DOCTYPEOID" );
        SEARCH_FIELD_LIST.add( "CHECKOUT" );
        SEARCH_FIELD_LIST.add( "CONTENT" );
        // SEARCH_FIELD_LIST.add( "ACLKEYCODE" );
        final String SEARCH_FIELD = String.join( "," , SEARCH_FIELD_LIST );

        List< String > DOCUMENT_FIELD_LIST = new ArrayList<>( );
        DOCUMENT_FIELD_LIST.add( "DOCID" );
        DOCUMENT_FIELD_LIST.add( "DATE" );
        DOCUMENT_FIELD_LIST.add( "TARGETOID" );
        DOCUMENT_FIELD_LIST.add( "OID" );
        DOCUMENT_FIELD_LIST.add( "STORAGEFILEID" );
        DOCUMENT_FIELD_LIST.add( "FILENAME" );
        DOCUMENT_FIELD_LIST.add( "DOCUMENTNAME" );
        DOCUMENT_FIELD_LIST.add( "TAGLIST" );
        DOCUMENT_FIELD_LIST.add( "CREATOROID" );
        DOCUMENT_FIELD_LIST.add( "CREATORNAME" );
        DOCUMENT_FIELD_LIST.add( "CREATORGROUPNAME" );
        DOCUMENT_FIELD_LIST.add( "CREATEDAT" );
        DOCUMENT_FIELD_LIST.add( "LASTMODIFIEROID" );
        DOCUMENT_FIELD_LIST.add( "LASTMODIFIEDAT" );
        DOCUMENT_FIELD_LIST.add( "LASTMODIFIEDATN" );
        DOCUMENT_FIELD_LIST.add( "FILETYPE" );
        DOCUMENT_FIELD_LIST.add( "FILESIZE" );
        DOCUMENT_FIELD_LIST.add( "FILESIZEM" );
        DOCUMENT_FIELD_LIST.add( "FOLDEROID" );
        DOCUMENT_FIELD_LIST.add( "FOLDERFULLPATHOID" );
        DOCUMENT_FIELD_LIST.add( "FOLDERFULLPATHNAME" );
        DOCUMENT_FIELD_LIST.add( "MANAGERGROUPOID" );
        DOCUMENT_FIELD_LIST.add( "MANAGERGROUPFULLPATHOID" );
        DOCUMENT_FIELD_LIST.add( "DOCTYPEOID" );
        DOCUMENT_FIELD_LIST.add( "CHECKOUT" );
        DOCUMENT_FIELD_LIST.add( "ACLKEYCODE" );
        DOCUMENT_FIELD_LIST.add( "NO_ACLKEYCODE" );
        DOCUMENT_FIELD_LIST.add( "CONTENT/300" );
        DOCUMENT_FIELD_LIST.add( "CUSTOM_CATEGORY" );
        DOCUMENT_FIELD_LIST.add( "CATEGORY_YN" );
        DOCUMENT_FIELD_LIST.add( "ALIAS" );

        final String DOCUMENT_FIELD = String.join( "," , DOCUMENT_FIELD_LIST );

        String SORT_FIELD = ""; // 정렬필드
        if ( params.containsKey( "sortColumnIndex" ) && params.containsKey( "sortDirection" ) )
            SORT_FIELD = params.get( "sortColumnIndex" ) + "/" + params.get( "sortDirection" );
        else
            SORT_FIELD = "RANK/DESC";

        // create object
        Search search = new Search( );
        int ret = 0;

        // common query 설정
        ret = search.w3SetCodePage( ENCODE_VALUE );
        ret = search.w3SetQueryLog( QUERY_LOG );
        ret = search.w3SetCommonQuery( query , EXTEND_OR );

        // collection, 검색 필드, 출력 필드 설정
        ret = search.w3AddCollection( COLLECTION );
        ret = search.w3SetPageInfo( COLLECTION , PAGE_START , RESULT_COUNT );
        ret = search.w3SetSortField( COLLECTION , SORT_FIELD );
        ret = search.w3SetSearchField( COLLECTION , SEARCH_FIELD );
        ret = search.w3SetDocumentField( COLLECTION , DOCUMENT_FIELD );
        ret = search.w3SetHighlight( COLLECTION , 1 , 1 );
        ret = search.w3SetRanking( COLLECTION , "basic" , "prkmfo" , 1000 );
        ret = search.w3SetQueryAnalyzer( COLLECTION, 1, 1, 1, 0 );

        // ret = search.w3SetTraceLog(0);

        StringBuilder filterQueryBuilder = new StringBuilder( );
        StringBuilder collectionQueryBuilder = new StringBuilder( );

        String doctype = "";
        if ( params.containsKey( "doctype" ) ) {
            doctype = params.get( "doctype" );
            collectionQueryBuilder.append( "<DOCTYPEOID:contains:" )
                              .append( doctype )
                              .append( ">" )
                              .append( " " );
        }

        String name = "";
        if ( params.containsKey( "name" ) ) {
            name = params.get( "name" );
            filterQueryBuilder.append( "<FILENAME:substring:" )
                              .append( name )
                              .append( ">" )
                              .append( " " );
        }

        String contents = "";
        if ( params.containsKey( "contents" ) ) {
            contents = params.get( "contents" );
            collectionQueryBuilder.append( "<CONTENT:contains:" )
                                  .append( contents )
                                  .append( ">" )
                                  .append( " " );
        }

        String creatorOID = "";
        if ( params.containsKey( "creatorOID" ) ) {
            creatorOID = params.get( "creatorOID" );
            collectionQueryBuilder.append( "<CREATOROID:contains:" )
                                  .append( creatorOID )
                                  .append( ">" )
                                  .append( " " );
        }

        String managerGroupOID = "";
        String includeFolderChildren = "";
        if ( params.containsKey( "managerGroupOID" ) ) {
            managerGroupOID = params.get( "managerGroupOID" );

            if ( params.containsKey( "includeFolderChildren" ) ) {
                includeFolderChildren = params.get( "includeFolderChildren" );

                if ( includeFolderChildren.contentEquals( "true" ) ) {
                    filterQueryBuilder.append( "<MANAGERGROUPFULLPATHOID:substring:" )
                                      .append( managerGroupOID )
                                      .append( ">" )
                                      .append( " " );
                } else {
                    collectionQueryBuilder.append( "<MANAGERGROUPOID:contains:" )
                                          .append( managerGroupOID )
                                          .append( ">" )
                                          .append( " " );
                }
            }
        }

        String fileType = "";
        if ( params.containsKey( "fileType" ) ) {
            fileType = params.get( "fileType" );
            collectionQueryBuilder.append( "<FILETYPE:contains:" )
                                  .append( fileType )
                                  .append( ">" )
                                  .append( " " );
        }

        String aclFilterInfos = "";
        String aclfilterInfoOidType = "";

//        if ( params.containsKey( "aclFilterInfos" ) ) {
//            aclFilterInfos = params.get( "aclFilterInfos" );
//
//            StringBuilder sb = new StringBuilder( );
//            sb.append( "(" );
//            for ( String item : aclFilterInfos.split( "," ) ) {
//
//                String[] aclfilterInfoDetails = item.trim( )
//                                                    .split( "\\|" );
//                aclfilterInfoOidType = aclfilterInfoDetails[ 0 ];
//                sb.append( "<ACLKEYCODE:contains:" )
//                  .append( aclfilterInfoOidType )
//                  .append( ">" )
//                  .append( "|" );
//
//            }
//            collectionQueryBuilder.append( sb.toString( )
//                                         .subSequence( 0 , sb.toString( )
//                                                             .length( ) - 1 ) )
//                              .append( ") " );
//        } else {
//            throw new MissingArgumentException( "aclFilterInfos는 '필수'값 입니다." );
//        }

        if ( params.containsKey( "aclFilterInfos" ) ) {
            aclFilterInfos = params.get( "aclFilterInfos" );

            StringBuilder sb = new StringBuilder( );

            for ( String item : aclFilterInfos.split( "," ) ) {

                String[] aclfilterInfoDetails = item.trim( )
                                                    .split( "\\|" );
                aclfilterInfoOidType = aclfilterInfoDetails[ 0 ];
                sb.append( "<ACLKEYCODE:contains:" )
                  .append( aclfilterInfoOidType )
                  .append( ">" )
                  .append( "|" );

            }

            ret = search.w3SetPrefixQuery( COLLECTION , sb.toString( ).trim( )
                                                          .subSequence( 0 , sb.toString( )
                                                                              .length( ) - 1 ).toString( )
                                                          , 1 );
        }else {
            throw new MissingArgumentException( "aclFilterInfos는 '필수'값 입니다." );
        }














            if ( params.containsKey( "modifyFrom" ) && params.containsKey( "modifyTo" ) ) {
            ret = search.w3SetDateRange(COLLECTION,
                    params.get( "modifyFrom" ).substring(0, 4) + "/" + params.get( "modifyFrom" ).substring(4, 6) + "/" + params.get( "modifyFrom" ).substring(6, 8),
                    params.get( "modifyTo" ).substring(0, 4) + "/" + params.get( "modifyTo" ).substring(4, 6) + "/" + params.get( "modifyTo" ).substring(6, 8) );
//            filterQueryBuilder.append( "<DATE:gte:" )
//                              .append( params.get( "modifyFrom" ) )
//                              .append( "> <DATE:lte:" )
//                              .append( params.get( "modifyTo" ) )
//                              .append( "> " );
        }

        if ( params.containsKey( "fileSizeFrom" ) && params.containsKey( "fileSizeTo" ) ) {
            filterQueryBuilder.append( "<FILESIZEM:gt:" )
                              .append( params.get( "fileSizeFrom" ) )
                              .append( "> <FILESIZEM:lt:" )
                              .append( params.get( "fileSizeTo" ) )
                              .append( "> " );
        }

        String folderOids = "";
        if ( params.containsKey( "folderOid" ) ) {
            folderOids = params.get( "folderOid" );

            StringBuilder sb = new StringBuilder( );
            sb.append( "(" );

            for ( String folderOid : folderOids.split( "," ) ) {
                folderOid = folderOid.trim( );
                sb.append( "<FOLDERFULLPATHOID:substring:" )
                  .append( folderOid )
                  .append( ">" )
                  .append( "|" );
            }
            filterQueryBuilder.append( sb.toString( )
                                         .subSequence( 0 , sb.toString( )
                                                             .length( ) - 1 ) );
            filterQueryBuilder.append( ") " );
        }

        String lastmodifierOids = "";
        if ( params.containsKey( "lastmodifieroid" ) ) {
            lastmodifierOids = params.get( "lastmodifieroid" );

            StringBuilder sb = new StringBuilder( );
            sb.append( "(" );

            for ( String lastmodifieroid : lastmodifierOids.split( "," ) ) {
                lastmodifieroid = lastmodifieroid.trim( );
                sb.append( "<LASTMODIFIEROID:substring:" )
                  .append( lastmodifieroid )
                  .append( ">" )
                  .append( "|" );
            }
            filterQueryBuilder.append( sb.toString( )
                                         .subSequence( 0 , sb.toString( )
                                                             .length( ) - 1 ) )
                              .append( ") " );
        }

        String filterQuery = filterQueryBuilder.toString( )
                                               .trim( );
        log.debug( "[file filterQuery]: {}" , filterQuery );
        ret = search.w3SetFilterQuery( COLLECTION , filterQuery );


        String collectionQuery = collectionQueryBuilder.toString( )
                                                       .trim( );
        log.debug( "[file collectionQuery]: {}" , collectionQuery );
        ret = search.w3SetCollectionQuery( COLLECTION , collectionQuery );

        // request
        ret = search.w3ConnectServer( SERVER_IP , SERVER_PORT , SERVER_TIMEOUT );
        ret = search.w3ReceiveSearchQueryResult( 3 );

        // check error
        if ( search.w3GetError( ) != 0 ) {
            log.debug( "검색 오류 로그 : {}" , search.w3GetErrorInfo( ) );
            return null;
        }

        // 전체건수, 결과건수 출력
        int totalCount = search.w3GetResultTotalCount( COLLECTION );
        int resultCount = search.w3GetResultCount( COLLECTION );

        log.debug( "검색 결과 : {}건 / 전체 건수 : {}건" , resultCount , totalCount );

        for ( int i = 0 ; i < resultCount ; i++ ) {

            // 기본 검색결과 객체 생성
            String oid = search.w3GetField( COLLECTION , "OID" , i );
            String targetoid = search.w3GetField( COLLECTION , "TARGETOID" , i );
            String storagefileid = search.w3GetField( COLLECTION , "STORAGEFILEID" , i );
            String filename = search.w3GetField( COLLECTION , "FILENAME" , i )
                                    .replaceAll( "<!HS>" , "<b>" )
                                    .replaceAll( "<!HE>" , "</b>" );
            String documentname = search.w3GetField( COLLECTION , "DOCUMENTNAME" , i );
            String taglist = search.w3GetField( COLLECTION , "TAGLIST" , i );
            String creatoroid = search.w3GetField( COLLECTION , "CREATOROID" , i );
            String creatorname = search.w3GetField( COLLECTION , "CREATORNAME" , i )
                                       .replaceAll( "<!HS>" , "<b>" )
                                       .replaceAll( "<!HE>" , "</b>" );
            String creatorgroupname = search.w3GetField( COLLECTION , "CREATORGROUPNAME" , i );
            String createdat = search.w3GetField( COLLECTION , "CREATEDAT" , i );
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
            String content = search.w3GetField( COLLECTION , "CONTENT" , i )
                                   .replaceAll( "<!HS>" , "<b>" )
                                   .replaceAll( "<!HE>" , "</b>" );
            String aclKeyCode = search.w3GetField( COLLECTION , "ACLKEYCODE" , i );

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
                                          .createdat( createdat )
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

            log.debug( vo.toString( ) );

        }

        return FileSearch.builder( )
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

        List< FolderSearchVo > list = new ArrayList<>( );

        String query = params.getOrDefault( "query" , "" );
        query = query.replaceAll("[^\\uAC00-\\uD7A3a-zA-Z0-9\\s\\|\\-]", "");

        // collection, 검색필드, 출력필드 정의
        String COLLECTION = "";
        if ( params.containsKey( "searchTargetOID" ) ) {
            COLLECTION = params.get( "searchTargetOID" );
            if ( COLLECTION.contentEquals( "ALL" ) )
                COLLECTION = "folderinfo";
        } else {
            throw new MissingArgumentException( "searchTargetOID는 '필수'값 입니다." );
        }

        int EXTEND_OR = query.contains(" ") ? 1 : 0; // and 검색결과가 없을 시 or로 확장검색
        int RESULT_COUNT = params.containsKey( "count" ) ? Integer.parseInt( params.get( "count" ) ) : 10; // 한번에 출력되는 검색 건수
        int PAGE_START = params.containsKey( "pageStart" ) ? Integer.parseInt( params.get( "pageStart" ) ) * RESULT_COUNT : 0; // 검색	결과 받아오는 시작위치
        String SORT_FIELD = ""; // 정렬필드
        if ( params.containsKey( "sortColumnIndex" ) && params.containsKey( "sortDirection" ) )
            SORT_FIELD = params.get( "sortColumnIndex" ) + "/" + params.get( "sortDirection" );
        else
            SORT_FIELD = "RANK/DESC";
//        String SEARCH_FIELD = "NAME,DESCRIPTION,CREATOROID,CREATEDATE,CREATEDATN,LASTMODIFIEDAT,LASTMODIFIEDATN,FULLPATHINDEX,MANAGERGROUPOID,MANAGERGROUPFULLPATHOID,FOLDERFULLPATHOID,KNOWLEDGEFOLDERLIST,DOCTYPEFOLDER,ACLKEYCODE"; // 검색필드
//        String DOCUMENT_FIELD = "DOCID,DATE,OID,NAME,DESCRIPTION,CODE,CREATOROID,CREATORGROUPNAME,CREATEDAT,CREATEDATE,CREATEDATN,LASTMODIFIEDAT,LASTMODIFIEDATN,MANAGERGROUPOID,FULLPATHINDEX,MANAGERGROUPFULLPATHOID,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,KNOWLEDGEFOLDERLIST,DOCTYPEFOLDER,ACLKEYCODE,NO_ACLKEYCODE"; // 출력필드

        List< String > SEARCH_FIELD_LIST = new ArrayList<>( );
        SEARCH_FIELD_LIST.add( "NAME" );
        SEARCH_FIELD_LIST.add( "DESCRIPTION" );
        SEARCH_FIELD_LIST.add( "CODE" );
        SEARCH_FIELD_LIST.add( "CREATOROID" );
        SEARCH_FIELD_LIST.add( "CREATEDATE" );
        SEARCH_FIELD_LIST.add( "CREATEDATN" );
        SEARCH_FIELD_LIST.add( "LASTMODIFIEDAT" );
        SEARCH_FIELD_LIST.add( "LASTMODIFIEDATN" );
        SEARCH_FIELD_LIST.add( "FULLPATHINDEX" );
        SEARCH_FIELD_LIST.add( "MANAGERGROUPOID" );
        SEARCH_FIELD_LIST.add( "MANAGERGROUPFULLPATHOID" );
        SEARCH_FIELD_LIST.add( "FOLDERFULLPATHOID" );
        SEARCH_FIELD_LIST.add( "FOLDERFULLPATHNAME" );
        SEARCH_FIELD_LIST.add( "KNOWLEDGEFOLDERLIST" );
        SEARCH_FIELD_LIST.add( "DOCTYPEFOLDER" );
        //SEARCH_FIELD_LIST.add( "ACLKEYCODE" );
        final String SEARCH_FIELD = String.join( "," , SEARCH_FIELD_LIST );

        List< String > DOCUMENT_FIELD_LIST = new ArrayList<>( );
        DOCUMENT_FIELD_LIST.add( "DOCID" );
        DOCUMENT_FIELD_LIST.add( "DATE" );
        DOCUMENT_FIELD_LIST.add( "OID" );
        DOCUMENT_FIELD_LIST.add( "NAME" );
        DOCUMENT_FIELD_LIST.add( "DESCRIPTION" );
        DOCUMENT_FIELD_LIST.add( "CODE" );
        DOCUMENT_FIELD_LIST.add( "CREATOROID" );
        DOCUMENT_FIELD_LIST.add( "CREATORGROUPNAME" );
        DOCUMENT_FIELD_LIST.add( "CREATEDAT" );
        DOCUMENT_FIELD_LIST.add( "CREATEDATE" );
        DOCUMENT_FIELD_LIST.add( "CREATEDATN" );
        DOCUMENT_FIELD_LIST.add( "LASTMODIFIEDAT" );
        DOCUMENT_FIELD_LIST.add( "LASTMODIFIEDATN" );
        DOCUMENT_FIELD_LIST.add( "MANAGERGROUPOID" );
        DOCUMENT_FIELD_LIST.add( "FULLPATHINDEX" );
        DOCUMENT_FIELD_LIST.add( "MANAGERGROUPFULLPATHOID" );
        DOCUMENT_FIELD_LIST.add( "FOLDERFULLPATHOID" );
        DOCUMENT_FIELD_LIST.add( "FOLDERFULLPATHNAME" );
        DOCUMENT_FIELD_LIST.add( "KNOWLEDGEFOLDERLIST" );
        DOCUMENT_FIELD_LIST.add( "DOCTYPEFOLDER" );
        DOCUMENT_FIELD_LIST.add( "ACLKEYCODE" );
        DOCUMENT_FIELD_LIST.add( "NO_ACLKEYCODE" );
        final String DOCUMENT_FIELD = String.join( "," , DOCUMENT_FIELD_LIST );

        // create object
        Search search = new Search( );
        int ret = 0;

        // common query 설정
        ret = search.w3SetCodePage( ENCODE_VALUE );
        ret = search.w3SetQueryLog( QUERY_LOG );
        ret = search.w3SetCommonQuery( query , EXTEND_OR );

        // collection, 검색 필드, 출력 필드 설정
        ret = search.w3AddCollection( COLLECTION );
        ret = search.w3SetPageInfo( COLLECTION , PAGE_START , RESULT_COUNT );
        ret = search.w3SetSortField( COLLECTION , SORT_FIELD );
        ret = search.w3SetSearchField( COLLECTION , SEARCH_FIELD );
        ret = search.w3SetDocumentField( COLLECTION , DOCUMENT_FIELD );
        ret = search.w3SetHighlight( COLLECTION , 1 , 1 );

        ret = search.w3SetRanking( COLLECTION , "basic" , "prkmfo" , 1000 );

        ret = search.w3SetQueryAnalyzer( COLLECTION, 1, 1, 1, 0 );

        StringBuilder filterQueryBuilder = new StringBuilder( );
        StringBuilder collectionQueryBuilder = new StringBuilder( );

        String doctype = "";
        if ( params.containsKey( "doctype" ) ) {
            doctype = params.get( "doctype" );
            collectionQueryBuilder.append( "<DOCTYPEFOLDER:contains:" )
                                  .append( doctype )
                                  .append( "> " );
        }

        String name = "";
        if ( params.containsKey( "name" ) ) {
            name = params.get( "name" );
            filterQueryBuilder.append( "<NAME:substring:" )
                              .append( name )
                              .append( "> " );
        }

        String creatorOID = "";
        if ( params.containsKey( "creatorOID" ) ) {
            creatorOID = params.get( "creatorOID" );
            collectionQueryBuilder.append( "<CREATOROID:contains:" )
                                  .append( creatorOID )
                                  .append( "> " );
        }

        String managerGroupOID = "";
        String includeFolderChildren = "";
        if ( params.containsKey( "managerGroupOID" ) ) {
            if ( params.containsKey( "includeFolderChildren" ) ) {
                managerGroupOID = params.get( "managerGroupOID" );
                includeFolderChildren = params.get( "includeFolderChildren" );
                if ( includeFolderChildren == "true" ) {
                    filterQueryBuilder.append( "<MANAGERGROUPFULLPATHOID:substring:" )
                                      .append( managerGroupOID )
                                      .append( "> " );
                } else {
                    collectionQueryBuilder.append( "<MANAGERGROUPOID:contains:" )
                                          .append( managerGroupOID )
                                          .append( "> " );
                }
            }
        }

        String aclFilterInfos = "";
        String aclfilterInfoOidType = "";

//        if ( params.containsKey( "aclFilterInfos" ) ) {
//            aclFilterInfos = params.get( "aclFilterInfos" );
//            StringBuilder sb = new StringBuilder( );
//            sb.append( "(" );
//            for ( String item : aclFilterInfos.split( "," ) ) {
//
//                String[] aclfilterInfoDetails = item.trim( )
//                                                    .split( "\\|" );
//                aclfilterInfoOidType = aclfilterInfoDetails[ 0 ];
//                sb.append( "<ACLKEYCODE:contains:" )
//                  .append( aclfilterInfoOidType )
//                  .append( ">" )
//                  .append( "|" );
//            }
//            collectionQueryBuilder.append( sb , 0 , sb.toString( )
//                                                  .length( ) - 1 )
//                              .append( ")" );
//        } else {
//            throw new MissingArgumentException( "aclFilterInfos는 '필수'값 입니다." );
//        }

        if ( params.containsKey( "aclFilterInfos" ) ) {
            aclFilterInfos = params.get( "aclFilterInfos" );

            StringBuilder sb = new StringBuilder( );

            for ( String item : aclFilterInfos.split( "," ) ) {

                String[] aclfilterInfoDetails = item.trim( )
                                                    .split( "\\|" );
                aclfilterInfoOidType = aclfilterInfoDetails[ 0 ];
                sb.append( "<ACLKEYCODE:contains:" )
                  .append( aclfilterInfoOidType )
                  .append( ">" )
                  .append( "|" );

            }

            ret = search.w3SetPrefixQuery( COLLECTION , sb.toString( ).trim( )
                                                          .subSequence( 0 , sb.toString( )
                                                                              .length( ) - 1 ).toString( )
                    , 1 );
        }else {
            throw new MissingArgumentException( "aclFilterInfos는 '필수'값 입니다." );
        }


        if ( params.containsKey( "createFrom" ) && params.containsKey( "createTo" ) ) {
            filterQueryBuilder.append( "<CREATEDATE:gte:" )
                              .append( params.get( "createFrom" ) )
                              .append( "> <CREATEDATE:lte:" )
                              .append( params.get( "createTo" ) )
                              .append( "> " );
        }

        if ( params.containsKey( "modifyFrom" ) && params.containsKey( "modifyTo" ) ) {
            ret = search.w3SetDateRange(COLLECTION,
                    params.get( "modifyFrom" ).substring(0, 4) + "/" + params.get( "modifyFrom" ).substring(4, 6) + "/" + params.get( "modifyFrom" ).substring(6, 8),
                    params.get( "modifyTo" ).substring(0, 4) + "/" + params.get( "modifyTo" ).substring(4, 6) + "/" + params.get( "modifyTo" ).substring(6, 8) );
//            filterQueryBuilder.append( "<DATE:gte:" )
//                              .append( params.get( "modifyFrom" ) )
//                              .append( "> <DATE:lte:" )
//                              .append( params.get( "modifyTo" ) )
//                              .append( "> " );
        }

        String folderOid = "";
        if ( params.containsKey( "folderOid" ) ) {
            folderOid = params.get( "folderOid" );
            filterQueryBuilder.append( "<FOLDERFULLPATHOID:substring:" )
                              .append( folderOid )
                              .append( "> " );
        }

        String filterQuery = filterQueryBuilder.toString( )
                                               .trim( );
        log.debug( "[folder filterQuery]: {}" , filterQuery );
       ret = search.w3SetFilterQuery( COLLECTION , filterQuery );


        String collectionQuery = collectionQueryBuilder.toString( )
                                                       .trim( );
        log.debug( "[folder collectionQuery]: {}" , collectionQuery );
        ret = search.w3SetCollectionQuery( COLLECTION , collectionQuery );

        // request
        ret = search.w3ConnectServer( SERVER_IP , SERVER_PORT , SERVER_TIMEOUT );
        ret = search.w3ReceiveSearchQueryResult( 3 );

        // check error
        if ( search.w3GetError( ) != 0 ) {
            log.debug( "검색 오류 로그 : {}" , search.w3GetErrorInfo( ) );
            return null;
        }

        // 전체건수, 결과건수 출력
        int totalCount = search.w3GetResultTotalCount( COLLECTION );
        int resultCount = search.w3GetResultCount( COLLECTION );

        log.debug( "검색 결과 : " + resultCount + "건 / 전체 건수 : " + totalCount + "건" );

        for ( int i = 0 ; i < resultCount ; i++ ) {

            // 기본 검색결과 객체 생성
            String oid = search.w3GetField( COLLECTION , "OID" , i );
            String foldername = search.w3GetField( COLLECTION , "NAME" , i );
            foldername = foldername.replaceAll( "<!HS>" , "<b>" );
            foldername = foldername.replaceAll( "<!HE>" , "</b>" );
            String description = search.w3GetField( COLLECTION , "DESCRIPTION" , i );
            String code = search.w3GetField( COLLECTION , "CODE" , i );
            String creatoroid = search.w3GetField( COLLECTION , "CREATOROID" , i );
            String creatorgroupname = search.w3GetField( COLLECTION , "CREATORGROUPNAME" , i );
            String createdat = search.w3GetField( COLLECTION , "CREATEDAT" , i );
            String lastmodifiedat = search.w3GetField( COLLECTION , "LASTMODIFIEDAT" , i );
            String folderfullpathname = search.w3GetField( COLLECTION , "FOLDERFULLPATHNAME" , i);
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
                                              .code( code )
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
            log.debug( vo.toString( ) );
        }


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

        String query = params.getOrDefault( "query" , "" );
        query = query.replaceAll("[^\\uAC00-\\uD7A3a-zA-Z0-9\\s\\|\\-]", "");

        List< FileSearchVo > list = new ArrayList<>( );

        // collection, 검색필드, 출력필드 정의
        String COLLECTION = "fileinfo";
        if ( params.containsKey( "searchTargetOID" ) )
            COLLECTION = params.get( "searchTargetOID" );

        int EXTEND_OR = query.contains(" ") ? 1 : 0; // and 검색결과가 없을 시 or로 확장검색

        final String startDate = "1970/01/01";

        LocalDate currentDate = LocalDate.now( );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy/MM/dd" );
        String endDate = currentDate.format( formatter );

        int RESULT_COUNT = params.containsKey( "count" ) ? Integer.parseInt( params.get( "count" ) ) : 10; // 한번에 출력되는 검색 건수
        int PAGE_START = params.containsKey( "pageStart" ) ? Integer.parseInt( params.get( "pageStart" ) ) * RESULT_COUNT : 0; // 검색 결과를 받아오는 시작 위치
        String SEARCH_FIELD = "FILENAME,DOCUMENTNAME,TAGLIST,CREATOROID,CREATORNAME,LASTMODIFIEROID,LASTMODIFIEDAT,LASTMODIFIEDATN,FILETYPE,FILESIZE,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPOID,MANAGERGROUPFULLPATHOID,DOCTYPEOID,CHECKOUT,CONTENT"; // 검색필드
        String DOCUMENT_FIELD = "DOCID,DATE,TARGETOID,OID,STORAGEFILEID,FILENAME,DOCUMENTNAME,TAGLIST,CREATOROID,CREATORNAME,CREATORGROUPNAME,CREATEDAT,LASTMODIFIEROID,LASTMODIFIEDAT,LASTMODIFIEDATN,FILETYPE,FILESIZE,FILESIZEM,FOLDEROID,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPOID,MANAGERGROUPFULLPATHOID,DOCTYPEOID,CHECKOUT,ACLKEYCODE,NO_ACLKEYCODE,CONTENT/300,CUSTOM_CATEGORY,CATEGORY_YN,ALIAS"; // 출력필드
        String SORT_FIELD = ""; // 정렬필드
        if ( params.containsKey( "sortColumnIndex" ) && params.containsKey( "sortDirection" ) )
            SORT_FIELD = params.get( "sortColumnIndex" ) + "/" + params.get( "sortDirection" );
        else
            SORT_FIELD = "DATE/DESC";


        // create object
        Search search = new Search( );
        int ret = 0;

        // common query 설정
        ret = search.w3SetCodePage( ENCODE_VALUE );
        ret = search.w3SetQueryLog( QUERY_LOG );
        ret = search.w3SetCommonQuery( query , EXTEND_OR );

        // collection, 검색 필드, 출력 필드 설정
        ret = search.w3AddCollection( COLLECTION );
        ret = search.w3SetPageInfo( COLLECTION , PAGE_START , RESULT_COUNT );
        ret = search.w3SetSortField( COLLECTION , SORT_FIELD );
        ret = search.w3SetSearchField( COLLECTION , SEARCH_FIELD );
        ret = search.w3SetDocumentField( COLLECTION , DOCUMENT_FIELD );
        ret = search.w3SetHighlight( COLLECTION , 1 , 1 );
        if ( params.containsKey( "query") ) {
            ret = search.w3SetRanking( COLLECTION , "basic" , "prkmfo" , 1000 );
        }
        ret = search.w3SetQueryAnalyzer( COLLECTION, 1, 1, 1, 0 );


        // category
        ret = search.w3AddCategoryGroupBy( COLLECTION , "CUSTOM_CATEGORY" , "1/SC" );
        ret = search.w3SetDateRange( COLLECTION , startDate , endDate );


        StringBuilder collectionQueryBuilder = new StringBuilder( );
        StringBuilder prefixQueryBuilder = new StringBuilder( );

        String securityfilter = "";
        if ( params.containsKey( "alias" ) ) {
            securityfilter = params.get( "alias" );

            prefixQueryBuilder.append( "<ALIAS:contains:" )
                              .append( securityfilter )
                              .append( "> " );

        }

        prefixQueryBuilder.append( "<CATEGORY_YN:contains:Y>" );
        final String prefixQuery = prefixQueryBuilder.toString( )
                                                     .trim( );
        log.debug( "[prefixQuery]: {}" , prefixQuery );
        ret = search.w3SetPrefixQuery( COLLECTION , prefixQuery , 1 );

        if ( params.containsKey( "modifyFrom" ) && params.containsKey( "modifyTo" ) ) {
            ret = search.w3SetFilterQuery(COLLECTION, "<DATE:gte:" + params.get( "modifyFrom" ) + "> <DATE:lte:" + params.get( "modifyTo" ) + "> " );
        }

        //dodtype
        String doctype = "";
        if ( params.containsKey( "doctype" ) ) {
            doctype = params.get( "doctype" );

            collectionQueryBuilder.append( "<DOCTYPEOID:contains:" )
                              .append( doctype )
                              .append( "> " );
        }

        //group
        String groupNames = "";
        if ( params.containsKey( "groupName" ) ) {
            groupNames = params.get( "groupName" );

            StringBuilder sb = new StringBuilder( );
            sb.append( "(" );

            String[] groupArray = groupNames.split( "," );

            for ( String group : groupArray ) {
                group = group.trim( );
                sb.append( "<CREATORGROUPNAME:contains:" )
                  .append( group )
                  .append( ">" )
                  .append( "|" );
            }
            collectionQueryBuilder.append( sb.toString( )
                                             .subSequence( 0 , sb.toString( )
                                                                 .length( ) - 1 ) )
                                  .append( ")" );
        }

        final String collectionQuery = collectionQueryBuilder.toString( )
                                                             .trim( );
        log.debug( "[collectionQuery]: {}" , collectionQuery );
        ret = search.w3SetCollectionQuery( COLLECTION , collectionQuery );

        // request
        ret = search.w3ConnectServer( SERVER_IP , SERVER_PORT , SERVER_TIMEOUT );
        ret = search.w3ReceiveSearchQueryResult( 3 );

        // category 필드 설정(개인정보 추출 위함)
        int groupCount = search.w3GetCategoryCount( COLLECTION , "CUSTOM_CATEGORY" , 1 );
        log.debug( "groupCount: " + groupCount );

        String categoryName = "";
        int categoryCount = 0;
        HashMap< String, Integer > tagCountMap = new HashMap< String, Integer >( );

        for ( int i = 0 ; i < groupCount ; i++ ) {
            categoryName = search.w3GetCategoryName( COLLECTION , "CUSTOM_CATEGORY" , 1 , i );
            if ( categoryName.isEmpty( ) || categoryName.contentEquals( "null" ) ) continue;

            categoryCount = search.w3GetDocumentCountInCategory( COLLECTION , "CUSTOM_CATEGORY" , 1 , i );
            tagCountMap.put( categoryName , categoryCount );
        }

        // check error
        if ( search.w3GetError( ) != 0 ) {
            log.debug( "검색 오류 로그 : {}" , search.w3GetErrorInfo( ) );
            return null;
        }

        // 전체건수, 결과건수 출력
        int totalCount = search.w3GetResultTotalCount( COLLECTION );
        int resultCount = search.w3GetResultCount( COLLECTION );

        for ( int i = 0 ; i < resultCount ; i++ ) {

            // 기본 검색결과 객체 생성
            String oid = search.w3GetField( COLLECTION , "OID" , i );
            String targetoid = search.w3GetField( COLLECTION , "TARGETOID" , i );
            String storagefileid = search.w3GetField( COLLECTION , "STORAGEFILEID" , i );
            String filename = search.w3GetField( COLLECTION , "FILENAME" , i )
                                    .replaceAll( "<!HS>" , "<b>" )
                                    .replaceAll( "<!HE>" , "</b>" );
            String documentname = search.w3GetField( COLLECTION , "DOCUMENTNAME" , i );
            String taglist = search.w3GetField( COLLECTION , "TAGLIST" , i );
            String creatoroid = search.w3GetField( COLLECTION , "CREATOROID" , i );
            String creatorname = search.w3GetField( COLLECTION , "CREATORNAME" , i )
                                       .replaceAll( "<!HS>" , "<b>" )
                                       .replaceAll( "<!HE>" , "</b>" );
            String creatorgroupname = search.w3GetField( COLLECTION , "CREATORGROUPNAME" , i );
            String createdat = search.w3GetField( COLLECTION , "CREATEDAT" , i );
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
            String content = search.w3GetField( COLLECTION , "CONTENT" , i )
                                   .replaceAll( "<!HS>" , "<b>" )
                                   .replaceAll( "<!HE>" , "</b>" );
            String aclKeyCode = search.w3GetField( COLLECTION , "ACLKEYCODE" , i );
            String customcategory = search.w3GetField( COLLECTION , "CUSTOM_CATEGORY" , i );
            String alias = search.w3GetField( COLLECTION , "ALIAS" , i );

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
                                          .createdat( createdat )
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
                                          .customcategory( customcategory )
                                          .security( security )
                                          .build( );
            list.add( vo );
            log.debug( vo.toString( ) );

        }

        FileSearch file = FileSearch.builder( )
                                    .Collection( COLLECTION )
                                    .TotalCount( totalCount )
                                    .Count( resultCount )
                                    .Result( list )
                                    .build( );

        SearchPersonalDTOBuilder dtoBuilder = SearchPersonalDTO.builder( );
        List< Object > data = new ArrayList<>( );
        data.add( file );

        return dtoBuilder.data( data )
                         .customCategoryMap( tagCountMap )
                         .build( );
    }

    public FileSearch searchSensitiveFileTotalListByCategory( Map< String, String > params ) throws MissingArgumentException {

        String query = "";
        if ( params.containsKey( "query" ) ) {
            query = params.get( "query" );
            query = query.replaceAll("[^\\uAC00-\\uD7A3a-zA-Z0-9\\s\\|\\-]", "");
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

        int EXTEND_OR = 1; // and 검색결과가 없을 시 or로 확장검색
        int RESULT_COUNT = params.containsKey( "count" ) ? Integer.parseInt( params.get( "count" ) ) : 10; // 한번에 출력되는 검색 건수
        int PAGE_START = params.containsKey( "pageStart" ) ? Integer.parseInt( params.get( "pageStart" ) ) * RESULT_COUNT : 0; // 검색 결과를 받아오는 시작 위치
        String SEARCH_FIELD = "FILENAME,DOCUMENTNAME,TAGLIST,CREATOROID,CREATORNAME,CREATORGROUPNAME,LASTMODIFIEROID,LASTMODIFIEDAT,LASTMODIFIEDATN,FILETYPE,FILESIZE,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPOID,MANAGERGROUPFULLPATHOID,DOCTYPEOID,CHECKOUT,CONTENT"; // 검색필드
        String DOCUMENT_FIELD = "DOCID,DATE,TARGETOID,OID,STORAGEFILEID,FILENAME,DOCUMENTNAME,TAGLIST,CREATOROID,CREATORNAME,CREATORGROUPNAME,CREATEDAT,LASTMODIFIEROID,LASTMODIFIEDAT,LASTMODIFIEDATN,FILETYPE,FILESIZE,FILESIZEM,FOLDEROID,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPOID,MANAGERGROUPFULLPATHOID,DOCTYPEOID,CHECKOUT,ACLKEYCODE,NO_ACLKEYCODE,CONTENT/300,CUSTOM_CATEGORY,ALIAS"; // 출력필드
//        String SORT_FIELD = ""; // 정렬필드
//        if ( params.containsKey( "sortColumnIndex" ) && params.containsKey( "sortDirection" ) )
//            SORT_FIELD = params.get( "sortColumnIndex" ) + "/" + params.get( "sortDirection" );
//        else
//            SORT_FIELD = "RANK/DESC";
        String SORT_FIELD = params.containsKey( "sortColumnIndex" ) && params.containsKey( "sortDirection" ) ?
                params.get( "sortColumnIndex" ) + "/" + params.get( "sortDirection" ) :
                "RANK/DESC";

        // create object
        Search search = new Search( );
        int ret = 0;

        // common query 설정
        ret = search.w3SetCodePage( ENCODE_VALUE );
        ret = search.w3SetQueryLog( QUERY_LOG );
        ret = search.w3SetCommonQuery( query , EXTEND_OR );


        // collection, 검색 필드, 출력 필드 설정
        ret = search.w3AddCollection( COLLECTION );
        ret = search.w3SetPageInfo( COLLECTION , PAGE_START , RESULT_COUNT );
        ret = search.w3SetSortField( COLLECTION , SORT_FIELD );
        ret = search.w3SetSearchField( COLLECTION , SEARCH_FIELD );
        ret = search.w3SetDocumentField( COLLECTION , DOCUMENT_FIELD );
        ret = search.w3SetHighlight( COLLECTION , 1 , 1 );
        ret = search.w3SetRanking( COLLECTION , "basic" , "prkmfo" , 1000 );
        ret = search.w3SetQueryAnalyzer( COLLECTION, 1, 1, 1, 0 );


        StringBuilder collectionQueryBuilder = new StringBuilder( );

        if ( params.containsKey( "modifyFrom" ) && params.containsKey( "modifyTo" ) ) {
            ret = search.w3SetDateRange(COLLECTION,
                    params.get( "modifyFrom" ).substring(0, 4) + "/" + params.get( "modifyFrom" ).substring(4, 6) + "/" + params.get( "modifyFrom" ).substring(6, 8),
                    params.get( "modifyTo" ).substring(0, 4) + "/" + params.get( "modifyTo" ).substring(4, 6) + "/" + params.get( "modifyTo" ).substring(6, 8) );
//            ret = search.w3SetFilterQuery(COLLECTION, "<DATE:gte:" + params.get( "modifyFrom" ) + "> <DATE:lte:" + params.get( "modifyTo" ) + "> " );
        }

        //dodtype
        String doctype = "";
        if ( params.containsKey( "doctype" ) ) {
            doctype = params.get( "doctype" );
            collectionQueryBuilder.append( "<DOCTYPEOID:contains:" )
                              .append( doctype )
                              .append( ">" )
                              .append( " " );
        }

        //group
        String groupNames = "";
        if ( params.containsKey( "groupName" ) ) {
            groupNames = params.get( "groupName" );

            StringBuilder sb = new StringBuilder( );
            sb.append( "(" );

            String[] groupNameArray = groupNames.split( "," );

            for ( String group : groupNameArray ) {
                group = group.trim( );
                sb.append( "<CREATORGROUPNAME:contains:" )
                  .append( group )
                  .append( ">" )
                  .append( "|" );
            }
            collectionQueryBuilder.append( sb.toString( )
                                         .subSequence( 0 , sb.toString( )
                                                             .length( ) - 1 ) )
                              .append( ") " );
        }

        final String collectionQuery = collectionQueryBuilder.toString( )
                                                     .trim( );
        log.debug( "[collectionQuery]: {}" , collectionQuery );
        ret = search.w3SetCollectionQuery( COLLECTION , collectionQuery );

        // request
        ret = search.w3ConnectServer( SERVER_IP , SERVER_PORT , SERVER_TIMEOUT );
        ret = search.w3ReceiveSearchQueryResult( 3 );

        // check error
        if ( search.w3GetError( ) != 0 ) {
            log.debug( "검색 오류 로그 : {}" , search.w3GetErrorInfo( ) );
            return null;
        }

        // 전체건수, 결과건수 출력
        int totalCount = search.w3GetResultTotalCount( COLLECTION );
        int resultCount = search.w3GetResultCount( COLLECTION );

        log.debug( "검색 결과 : " + resultCount + "건 / 전체 건수 : " + totalCount + "건" );

        for ( int i = 0 ; i < resultCount ; i++ ) {

            // 기본 검색결과 객체 생성
            String oid = search.w3GetField( COLLECTION , "OID" , i );
            String targetoid = search.w3GetField( COLLECTION , "TARGETOID" , i );
            String storagefileid = search.w3GetField( COLLECTION , "STORAGEFILEID" , i );
            String filename = search.w3GetField( COLLECTION , "FILENAME" , i )
                                    .replaceAll( "<!HS>" , "<b>" )
                                    .replaceAll( "<!HE>" , "</b>" );
            String documentname = search.w3GetField( COLLECTION , "DOCUMENTNAME" , i );
            String taglist = search.w3GetField( COLLECTION , "TAGLIST" , i );
            String creatoroid = search.w3GetField( COLLECTION , "CREATOROID" , i );
            String creatorname = search.w3GetField( COLLECTION , "CREATORNAME" , i )
                                       .replaceAll( "<!HS>" , "<b>" )
                                       .replaceAll( "<!HE>" , "</b>" );
            String creatorgroupname = search.w3GetField( COLLECTION , "CREATORGROUPNAME" , i );
            String createdat = search.w3GetField( COLLECTION , "CREATEDAT" , i );
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
            String content = search.w3GetField( COLLECTION , "CONTENT" , i )
                                   .replaceAll( "<!HS>" , "<b>" )
                                   .replaceAll( "<!HE>" , "</b>" );
            String aclKeyCode = search.w3GetField( COLLECTION , "ACLKEYCODE" , i );

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
                                          .createdat( createdat )
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
            log.debug( vo.toString( ) );

        }


        return FileSearch.builder( )
                         .Collection( COLLECTION )
                         .TotalCount( totalCount )
                         .Count( resultCount )
                         .Result( list )
                         .build( );
    }
}