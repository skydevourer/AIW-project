/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getExamInfo;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Administrator
 */
@WebService(serviceName = "getInfo")
public class getInfo {
    
    /**
     * Web service operation
     * @param userID
     * @param name
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    @WebMethod(operationName="getInfo")
    public void getInfo(@WebParam(name = "userID") String userID, @WebParam(name = "name") String name) throws SQLException, ClassNotFoundException {
        
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(getInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
            Document doc = builder.newDocument();
            Element results = doc.createElement("Results");
            doc.appendChild(results);
            
            String url = "jdbc:mysql://localhost:8080/hanu";
            String dbClass = "com.mysql.jdbc.Driver";
            String query = "SELECT * FROM `2014_ketquathi` WHERE `so_bao_danh` =" +userID+ "AND `ho_va_ten`= +"+name+"";
            String userName = "root", password = "";
            
            Class.forName("com.mysql.jdbc.Driver");
            ResultSet rs;
            Connection con = DriverManager.getConnection (url, userName, password); 
                Statement stmt = con.createStatement();
                rs = stmt.executeQuery(query);
                ResultSetMetaData rsmd = rs.getMetaData();
                int colCount = rsmd.getColumnCount();
                if (rs.next()==false)
                    {
                        System.out.println("No data found");
                    }
                else {
                while (rs.next()) {
                    
                    Element row = doc.createElement("Row");
                    results.appendChild(row);
                    for (int i = 1; i <= colCount; i++) {
                        String columnName = rsmd.getColumnName(i);
                        Object value = rs.getObject(i);
                        Element node = doc.createElement(columnName);
                        node.appendChild(doc.createTextNode(value.toString()));
                        row.appendChild(node);
                    }
                }
                DOMSource domSource = new DOMSource(doc);
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = null;
                try {
                    transformer = tf.newTransformer();
                } catch (TransformerConfigurationException ex) {
                    Logger.getLogger(getInfo.class.getName()).log(Level.SEVERE, null, ex);
                }
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
                StringWriter sw = new StringWriter();
                StreamResult sr = new StreamResult(sw);
                try {
                    transformer.transform(domSource, sr);
                } catch (TransformerException ex) {
                    Logger.getLogger(getInfo.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(sw.toString());
            }
            rs.close();
                   
        }
  }



