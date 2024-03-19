package com.wisenut.ebk.spring.service;


import com.wisenut.ebk.spring.dto.TotalSearchDTO;
import com.wisenut.ebk.spring.vo.FileSearch;
import com.wisenut.ebk.spring.vo.FileSearchVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Service
@Slf4j

public class ExcelService {

    @Value( "${limit.excel.rows}" )
    int rowsLimit;

    public void getPersonalDataExcel( HttpServletResponse res ) throws Exception {

        /**
         * excel sheet 생성
         */
        Workbook workbook = new XSSFWorkbook( );
        Sheet sheet = workbook.createSheet( "sheet1" ); // 엑셀 sheet 이름
        sheet.setDefaultColumnWidth( 28 ); // 디폴트 너비 설정

        /**
         * header font style
         */
        XSSFFont headerXSSFFont = ( XSSFFont ) workbook.createFont( );
//        headerXSSFFont.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}));

        /**
         * header cell style
         */
        XSSFCellStyle headerXssfCellStyle = ( XSSFCellStyle ) workbook.createCellStyle( );

        // 테두리 설정
        headerXssfCellStyle.setBorderLeft( BorderStyle.THIN );
        headerXssfCellStyle.setBorderRight( BorderStyle.THIN );
        headerXssfCellStyle.setBorderTop( BorderStyle.THIN );
        headerXssfCellStyle.setBorderBottom( BorderStyle.THIN );
        headerXssfCellStyle.setAlignment( HorizontalAlignment.CENTER );


        // 배경 설정
//        headerXssfCellStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 34, (byte) 37, (byte) 41}));
//        headerXssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        headerXssfCellStyle.setFont(headerXSSFFont);

        /**
         * body cell style
         */
        XSSFCellStyle bodyXssfCellStyle = ( XSSFCellStyle ) workbook.createCellStyle( );

        // 테두리 설정
//        bodyXssfCellStyle.setBorderLeft(BorderStyle.THIN);
//        bodyXssfCellStyle.setBorderRight(BorderStyle.THIN);
//        bodyXssfCellStyle.setBorderTop(BorderStyle.THIN);
//        bodyXssfCellStyle.setBorderBottom(BorderStyle.THIN);

        /**
         * header data
         */
        int rowCount = 0; // 데이터가 저장될 행
        String[] headerNames = new String[]{ "파일이름" , "작성자" , "최종 수정일" };

        Row headerRow = null;
        Cell headerCell = null;

        headerRow = sheet.createRow( rowCount++ );
        for ( int i = 0 ; i < headerNames.length ; i++ ) {
            headerCell = headerRow.createCell( i );
            headerCell.setCellValue( headerNames[ i ] ); // 데이터 추가
            headerCell.setCellStyle( headerXssfCellStyle ); // 스타일 추가
        }

        /**
         * body data
         */
        String[][] bodyData = new String[][]{
                { "A2" , "B2" , "C2" } ,
                { "A3" , "B3" , "C3" } ,
                { "A4" , "B4" , "C4" } ,
                { "A5" , "B5" , "C5" }
        };

        Row bodyRow = null;
        Cell bodyCell = null;

        for ( String[] bodyRowData : bodyData ) {
            bodyRow = sheet.createRow( rowCount++ );

            for ( int i = 0 ; i < bodyRowData.length ; i++ ) {
                bodyCell = bodyRow.createCell( i );
                bodyCell.setCellValue( bodyRowData[ i ] ); // 데이터 추가
                bodyCell.setCellStyle( bodyXssfCellStyle ); // 스타일 추가
            }
        }

        /**
         * download
         */
        String fileName = "excel_personalData";

        res.setContentType( "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
        res.setHeader( "Content-Disposition" , "attachment;filename=" + fileName + ".xlsx" );
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
        sheet.setColumnWidth( 0, 60 * 250);
        sheet.setColumnWidth( 1, 20 * 250);
        sheet.setColumnWidth( 2, 25 * 250);

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
        XSSFCellStyle bodyXssfCellStyleLeft = ( XSSFCellStyle ) workbook.createCellStyle( );
        bodyXssfCellStyleLeft.setAlignment( HorizontalAlignment.LEFT );

        XSSFCellStyle bodyXssfCellStyleCenter = ( XSSFCellStyle ) workbook.createCellStyle( );
        bodyXssfCellStyleCenter.setAlignment( HorizontalAlignment.CENTER );


        /**
         * header data
         */
        int rowCount = 0; // 데이터가 저장될 행
        String[] headerNames = new String[]{ "파일이름" , "작성자" , "최종 수정일" };

        Row headerRow = sheet.createRow( rowCount++ );
        Cell headerCell = null;

        for ( int i = 0 ; i < headerNames.length ; i++ ) {
            headerCell = headerRow.createCell( i );
            headerCell.setCellValue( headerNames[ i ] ); // 데이터 추가
            headerCell.setCellStyle( bodyXssfCellStyleCenter ); // 스타일 추가
        }

        FileSearch data = ( FileSearch ) dto.getData( )
                                            .get( 0 );
        List< FileSearchVo > result = data
                .getResult( );

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

        }
        String query = dto.getQuery( )
                          .replaceAll( " " , "_" );
        String time = DateTimeFormatter.ofPattern( "yyyyMMddHHmmss" )
                                       .format( LocalDateTime.now( ) );
        /**
         * download
         */
        String fileName = URLEncoder.encode(  "민감정보_" + query + "_" + time , "UTF-8" ).replaceAll( "\\+", "%20" );

        res.setContentType( "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
        res.setHeader( "Content-Disposition" , "attachment; filename=" + fileName + ".xlsx" );
        ServletOutputStream servletOutputStream = res.getOutputStream( );

        workbook.write( servletOutputStream );
        workbook.close( );
        servletOutputStream.flush( );
        servletOutputStream.close( );
    }
}