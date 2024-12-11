package com.wisenut.ebk.spring.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeprecatedService {

//    /**
//     * 문서 통합 검색
//     *
//     * @throws MissingArgumentException
//     * @author 안선정
//     */
//    public DocumentSearch searchDocumentTotalListByCategory( Map< String, String > params ) throws MissingArgumentException {
//
//        log.info( "**********************************" );
//        params.keySet( )
//              .stream( )
//              .forEach( key -> log.info( " - " + key + ": {}" , params.get( key ) ) );
//        log.info( "**********************************" );
//
//        String query = "";
//        if ( params.containsKey( "query" ) ) {
//            query = params.get( "query" );
//        } else {
//            throw new MissingArgumentException( "query는 '필수'값 입니다." );
//        }
//
//        List< DocumentSearchVo > list = new ArrayList<>( );
//
//        // collection, 검색필드, 출력필드 정의
//        String COLLECTION = "";
//        if ( params.containsKey( "searchTargetOID" ) ) {
//            COLLECTION = params.get( "searchTargetOID" );
//            if ( COLLECTION.contentEquals( "ALL" ) )
//                COLLECTION = "documentinfo";
//        } else {
//            throw new MissingArgumentException( "searchTargetOID는 '필수'값 입니다." );
//        }
//
//        int QUERY_LOG = 1;
//        int EXTEND_OR = 0;
//        int RESULT_COUNT = params.containsKey( "count" ) ? Integer.parseInt( params.get( "count" ) ) : 10; // 한번에 출력되는 검색 건수
//        int PAGE_START = params.containsKey( "pageStart" ) ? Integer.parseInt( params.get( "pageStart" ) ) * RESULT_COUNT : 0; // 검색 결과를 받아오는 시작 위치
//        String SORT_FIELD = "RANK/DESC"; // 정렬필드
//        String SEARCH_FIELD = "NAME,DESCRIPTION,TAGLIST,CREATOROID,CREATORNAME,DIRECTOR,AUTHOR,COAUTHOR,REVIEWER,APPROVER,RECEIVER,CREATEDATN,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPFULLPATHOID,DOCTYPEOID"; // 검색필드
//        String DOCUMENT_FIELD = "DOCID,DATE,OID,NAME,DESCRIPTION,TAGLIST,CREATOROID,CREATORNAME,CREATORGROUPNAME,DIRECTOR,AUTHOR,COAUTHOR,REVIEWER,APPROVER,RECEIVER,CREATEDAT,CREATEDATN,LASTMODIFIEDAT,LASTMODIFIEDATN,REVIEWEDAT,REVIEWEDATN,APPROVEDAT,APPROVEDATN,FOLDEROID,FOLDERFULLPATHOID,FOLDERFULLPATHNAME,MANAGERGROUPOID,MANAGERGROUPFULLPATHOID,DOCTYPEOID,CHECKOUT,ACLKEYCODE,NO_ACLKEYCODE"; // 출력필드
//
//        // create object
//        QueryAPI530.Search search = new QueryAPI530.Search( );
//        int ret = 0;
//
//        // common query 설정
//        ret = search.w3SetCodePage( "UTF-8" );
//        ret = search.w3SetQueryLog( QUERY_LOG );
//        ret = search.w3SetCommonQuery( query , EXTEND_OR );
//        log.info( "query : " + query );
//
//        // collection, 검색 필드, 출력 필드 설정
//        ret = search.w3AddCollection( COLLECTION );
//        ret = search.w3SetPageInfo( COLLECTION , PAGE_START , RESULT_COUNT );
//        ret = search.w3SetSortField( COLLECTION , SORT_FIELD );
//        ret = search.w3SetSearchField( COLLECTION , SEARCH_FIELD );
//        ret = search.w3SetDocumentField( COLLECTION , DOCUMENT_FIELD );
//
//        ret = search.w3SetRanking( COLLECTION , "basic" , "prkmfo" , 1000 );
//
//        String aclFilterInfos = "";
//        String aclfilterInfoOidType = "";
//        // String aclfilterInfoAccessGrade = "";
//
//        if ( params.containsKey( "aclFilterInfos" ) ) {
//            aclFilterInfos = params.get( "aclFilterInfos" );
//            StringBuilder sb = new StringBuilder( );
//
//            for ( String item : aclFilterInfos.split( "," ) ) {
//
//                String[] aclfilterInfoDetails = item.trim( )
//                                                    .split( "\\|" );
//                aclfilterInfoOidType = aclfilterInfoDetails[ 0 ];
//                // aclfilterInfoAccessGrade = aclfilterInfoDetails[1];
//
//                sb.append( "<ACLKEYCODE:substring:" )
//                  .append( aclfilterInfoOidType )
//                  .append( ">" );
//                sb.append( "|" );
//
//                /*
//                 * for(char c = aclfilterInfoAccessGrade.charAt(0); c <= 'z'; c++) {
//                 * sb.append("<ACLKEYCODE:substring:").append(aclfilterInfoOidType).append("|").
//                 * append(c).append(">"); sb.append("|"); }
//                 */
//            }
//            ret = search.w3SetFilterQuery( COLLECTION , sb
//                    .substring( 0 , sb.toString( )
//                                      .length( ) - 1 ) );
//        } else {
//            throw new MissingArgumentException( "aclFilterInfos는 '필수'값 입니다." );
//        }
//
//
//        // request
//        ret = search.w3ConnectServer( server_ip , server_port , server_timeout );
//        ret = search.w3ReceiveSearchQueryResult( 3 );
//
//        // check error
//        if ( search.w3GetError( ) != 0 ) {
//            log.info( "검색 오류 로그 : {}" , search.w3GetErrorInfo( ) );
//            return null;
//        }
//
//        // 전체건수, 결과건수 출력
//        int totalCount = search.w3GetResultTotalCount( COLLECTION );
//        int resultCount = search.w3GetResultCount( COLLECTION );
//
//        log.info( "검색 결과 : " + resultCount + "건 / 전체 건수 : " + totalCount + "건" );
//
//        for ( int i = 0 ; i < resultCount ; i++ ) {
//
//            // 기본 검색결과 객체 생성
//            String name = search.w3GetField( COLLECTION , "NAME" , i );
//            String description = search.w3GetField( COLLECTION , "DESCRIPTION" , i );
//            String taglist = search.w3GetField( COLLECTION , "TAGLIST" , i );
//            String creatoroid = search.w3GetField( COLLECTION , "CREATOROID" , i );
//            String creatorname = search.w3GetField( COLLECTION , "CREATORNAME" , i );
//            String creatorgroupname = search.w3GetField( COLLECTION , "CREATORGROUPNAME" , i );
//            String createdat = search.w3GetField( COLLECTION , "CREATEDAT" , i );
//            String lastmodifiedat = search.w3GetField( COLLECTION , "LASTMODIFIEDAT" , i );
//            String folderfullpathname = search.w3GetField( COLLECTION , "FOLDERFULLPATHNAME" , i );
//            String doctypeoid = search.w3GetField( COLLECTION , "DOCTYPEOID" , i );
//            String aclKeyCode = search.w3GetField( COLLECTION , "ACLKEYCODE" , i );
//
//            DocumentSearchVo vo = DocumentSearchVo.builder( )
//                                                  .name( name )
//                                                  .description( description )
//                                                  .taglist( taglist )
//                                                  .creatoroid( creatoroid )
//                                                  .creatorname( creatorname )
//                                                  .creatorgroupname( creatorgroupname )
//                                                  .createdat( createdat )
//                                                  .lastmodifiedat( lastmodifiedat )
//                                                  .folderfullpathname( folderfullpathname )
//                                                  .doctypeoid( doctypeoid )
//                                                  .aclkeycode( aclKeyCode )
//                                                  .build( );
//
//            list.add( vo );
//
//            log.info( "list  : " + list );
//
//        }
//
//        return DocumentSearch.builder( )
//                             .Collection( COLLECTION )
//                             .TotalCount( totalCount )
//                             .Count( resultCount )
//                             .Result( list )
//                             .build( );
//    }
}
