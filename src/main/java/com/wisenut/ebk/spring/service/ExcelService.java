package com.wisenut.ebk.spring.service;


import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.wisenut.ebk.spring.dto.SearchPersonalDTO;
import com.wisenut.ebk.spring.dto.TotalSearchDTO;
import com.wisenut.ebk.spring.vo.FileSearch;
import com.wisenut.ebk.spring.vo.FileSearchVo;

import lombok.extern.slf4j.Slf4j;

@RestController
@Service
@Slf4j

public class ExcelService {

    @Value( "${limit.excel.rows}" )
    int rowsLimit;

    public void getPersonalDataExcel( HttpServletResponse res , SearchPersonalDTO dto) throws Exception {

        /**
         * excel sheet 생성
         */
        Workbook workbook = new XSSFWorkbook( );
        Sheet sheet = workbook.createSheet( "sheet1" ); // 엑셀 sheet 이름
        sheet.setDefaultColumnWidth( 28 ); // 디폴트 너비 설정
        sheet.setColumnWidth( 0 , 60 * 250 );
        sheet.setColumnWidth( 1 , 20 * 250 );
        sheet.setColumnWidth( 2 , 25 * 250 );
        sheet.setColumnWidth( 3 , 100 * 250 );

        /**
         * header font style
         */
        XSSFFont headerXSSFFont = ( XSSFFont ) workbook.createFont( );

        /**
         * header cell style
         */
        XSSFCellStyle headerXssfCellStyle = ( XSSFCellStyle ) workbook.createCellStyle( );

        /**
         * body cell style
         */
        XSSFCellStyle headerXssfCellStyleCenter = ( XSSFCellStyle ) workbook.createCellStyle( );
        headerXssfCellStyleCenter.setAlignment( HorizontalAlignment.CENTER );
        headerXssfCellStyleCenter.setFillForegroundColor( IndexedColors.GREY_25_PERCENT.getIndex( ) );
        headerXssfCellStyleCenter.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle bodyXssfCellStyleLeft = ( XSSFCellStyle ) workbook.createCellStyle( );
        bodyXssfCellStyleLeft.setAlignment( HorizontalAlignment.LEFT );

        XSSFCellStyle bodyXssfCellStyleCenter = ( XSSFCellStyle ) workbook.createCellStyle( );
        bodyXssfCellStyleCenter.setAlignment( HorizontalAlignment.CENTER );



        /**
         * header data
         */
        int rowCount = 0; // 데이터가 저장될 행
        String[] headerNames = new String[]{ "파일이름" , "등록자" , "최종 수정일" , "폴더경로"};

        Row headerRow = sheet.createRow( rowCount++ );
        Cell headerCell = null;

        for ( int i = 0 ; i < headerNames.length ; i++ ) {
            headerCell = headerRow.createCell( i );
            headerCell.setCellValue( headerNames[ i ] ); // 데이터 추가
            headerCell.setCellStyle( headerXssfCellStyleCenter ); // 스타일 추가
        }

        FileSearch data = ( FileSearch ) dto.getData( )
                                            .get( 0 );
        List< FileSearchVo > result = data
                .getResult( );
        if ( result.isEmpty( ) ) throw new RuntimeException( "검색결과가 0입니다." );

        Row bodyRow = null;
        Cell bodyCell = null;

        for ( int i = 0 ; i < result.size( ) || i == rowsLimit ; i++ ) {
            bodyRow = sheet.createRow( rowCount++ );
            FileSearchVo vo = result.get( i );

            bodyCell = bodyRow.createCell( 0 );
            bodyCell.setCellValue( vo.getFilename( ) ); // 데이터 추가
            bodyCell.setCellStyle( bodyXssfCellStyleLeft ); // 스타일 추가


            bodyCell = bodyRow.createCell( 1 );
            bodyCell.setCellValue( vo.getCreatorname( ) ); // 데이터 추가
            bodyCell.setCellStyle( bodyXssfCellStyleCenter ); // 스타일 추가

            bodyCell = bodyRow.createCell( 2 );
            bodyCell.setCellValue( vo.getLastmodifiedat( ) ); // 데이터 추가
            bodyCell.setCellStyle( bodyXssfCellStyleCenter ); // 스타일 추가


            bodyCell = bodyRow.createCell( 3 );
            bodyCell.setCellValue( "S:\\" + vo.getFolderfullpathname().replaceAll( ">" , "\\\\" ) ); // 데이터 추가
            bodyCell.setCellStyle( bodyXssfCellStyleLeft ); // 스타일 추가

        }
//        String query = dto.getQuery( )
//                          .replaceAll( " " , "_" );
        String time = DateTimeFormatter.ofPattern( "yyyyMMddHHmmss" )
                                       .format( LocalDateTime.now( ) );
        /**
         * download
         */
        String fileName = URLEncoder.encode( "개인정보_" + /*query + "_" +*/ time , "UTF-8" )
                                    .replaceAll( "\\+" , "%20" );

        res.setContentType( "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
        res.setHeader( "Content-Disposition" , "attachment; filename=" + fileName + ".xlsx" );
        ServletOutputStream servletOutputStream = res.getOutputStream( );

        workbook.write( servletOutputStream );
        workbook.close( );
        servletOutputStream.flush( );
        servletOutputStream.close( );
    }

    public void getSensitiveDataExcel( HttpServletResponse res , TotalSearchDTO dto ) throws Exception {

        /**
         * excel sheet 생성
         */
        Workbook workbook = new XSSFWorkbook( );
        Sheet sheet = workbook.createSheet( "sheet1" ); // 엑셀 sheet 이름
        sheet.setDefaultColumnWidth( 28 ); // 디폴트 너비 설정
        sheet.setColumnWidth( 0 , 60 * 250 );
        sheet.setColumnWidth( 1 , 20 * 250 );
        sheet.setColumnWidth( 2 , 25 * 250 );
        sheet.setColumnWidth( 3 , 100 * 250 );

        /**
         * header font style
         */
        XSSFFont headerXSSFFont = ( XSSFFont ) workbook.createFont( );

        /**
         * header cell style
         */
        XSSFCellStyle headerXssfCellStyle = ( XSSFCellStyle ) workbook.createCellStyle( );

        /**
         * body cell style
         */
        XSSFCellStyle headerXssfCellStyleCenter = ( XSSFCellStyle ) workbook.createCellStyle( );
        headerXssfCellStyleCenter.setAlignment( HorizontalAlignment.CENTER );
        headerXssfCellStyleCenter.setFillForegroundColor( IndexedColors.GREY_25_PERCENT.getIndex( ) );
        headerXssfCellStyleCenter.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle bodyXssfCellStyleLeft = ( XSSFCellStyle ) workbook.createCellStyle( );
        bodyXssfCellStyleLeft.setAlignment( HorizontalAlignment.LEFT );

        XSSFCellStyle bodyXssfCellStyleCenter = ( XSSFCellStyle ) workbook.createCellStyle( );
        bodyXssfCellStyleCenter.setAlignment( HorizontalAlignment.CENTER );



        /**
         * header data
         */
        int rowCount = 0; // 데이터가 저장될 행
        String[] headerNames = new String[]{ "파일이름" , "등록자" , "최종 수정일" , "폴더경로"};

        Row headerRow = sheet.createRow( rowCount++ );
        Cell headerCell = null;

        for ( int i = 0 ; i < headerNames.length ; i++ ) {
            headerCell = headerRow.createCell( i );
            headerCell.setCellValue( headerNames[ i ] ); // 데이터 추가
            headerCell.setCellStyle( headerXssfCellStyleCenter ); // 스타일 추가
        }

        FileSearch data = ( FileSearch ) dto.getData( )
                                            .get( 0 );
        List< FileSearchVo > result = data
                .getResult( );
        if ( result.isEmpty( ) ) throw new RuntimeException( "검색결과가 0입니다." );

        Row bodyRow = null;
        Cell bodyCell = null;

        for ( int i = 0 ; i < result.size( ) || i == rowsLimit ; i++ ) {
            bodyRow = sheet.createRow( rowCount++ );
            FileSearchVo vo = result.get( i );

            bodyCell = bodyRow.createCell( 0 );
            bodyCell.setCellValue( vo.getFilename( ) ); // 데이터 추가
            bodyCell.setCellStyle( bodyXssfCellStyleLeft ); // 스타일 추가


            bodyCell = bodyRow.createCell( 1 );
            bodyCell.setCellValue( vo.getCreatorname( ) ); // 데이터 추가
            bodyCell.setCellStyle( bodyXssfCellStyleCenter ); // 스타일 추가

            bodyCell = bodyRow.createCell( 2 );
            bodyCell.setCellValue( vo.getLastmodifiedat( ) ); // 데이터 추가
            bodyCell.setCellStyle( bodyXssfCellStyleCenter ); // 스타일 추가


            bodyCell = bodyRow.createCell( 3 );
            bodyCell.setCellValue( "S:\\" + vo.getFolderfullpathname().replaceAll( ">" , "\\\\" ) ); // 데이터 추가
            bodyCell.setCellStyle( bodyXssfCellStyleLeft ); // 스타일 추가

        }
        String query = dto.getQuery( )
                          .replaceAll( " " , "_" );
        String time = DateTimeFormatter.ofPattern( "yyyyMMddHHmmss" )
                                       .format( LocalDateTime.now( ) );
        /**
         * download
         */
        String fileName = URLEncoder.encode( "민감정보_" + query + "_" + time , "UTF-8" )
                                    .replaceAll( "\\+" , "%20" );

        res.setContentType( "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
        res.setHeader( "Content-Disposition" , "attachment; filename=" + fileName + ".xlsx" );
        ServletOutputStream servletOutputStream = res.getOutputStream( );

        workbook.write( servletOutputStream );
        workbook.close( );
        servletOutputStream.flush( );
        servletOutputStream.close( );
    }
}