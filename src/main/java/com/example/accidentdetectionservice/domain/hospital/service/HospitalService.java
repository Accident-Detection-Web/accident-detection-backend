package com.example.accidentdetectionservice.domain.hospital.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class HospitalService {
    public static void main(String[] args) throws Exception {
        String hospitalInfo = getHospitalInfo();
        System.out.println(hospitalInfo);
    }

    public static String getHospitalInfo() throws Exception {
        StringBuilder hospitalInfoBuilder = new StringBuilder();

        String Si = "경기도";
        String Gun = "수원시";
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552657/ErmctInfoInqireService/getEmrrmRltmUsefulSckbdInfoInqire"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=351qYfh59jJHQGLGCTf2af0is6PVkCNFKEfj2%2FdXVQKfBWFGg1%2BiSHbG6D6edWitwcgQ%2FKV6P82xpCPpM%2FD4sg%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("STAGE1","UTF-8") + "=" + URLEncoder.encode(Si, "UTF-8")); /*주소(시도)*/
        urlBuilder.append("&" + URLEncoder.encode("STAGE2","UTF-8") + "=" + URLEncoder.encode(Gun, "UTF-8")); /*주소(시군구)*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*목록 건수*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/xml");

        // Read the XML response
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(conn.getInputStream()));

        NodeList itemList = doc.getElementsByTagName("item");
        for (int i = 0; i < itemList.getLength(); i++) {
            Node itemNode = itemList.item(i);
            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                Element itemElement = (Element) itemNode;
                String dutyName = itemElement.getElementsByTagName("dutyName").item(0).getTextContent();
                String dutyTel3 = itemElement.getElementsByTagName("dutyTel3").item(0).getTextContent();
                hospitalInfoBuilder.append("Hospital Name: ").append(dutyName).append(", Telephone: ").append(dutyTel3).append("\n");
            }
        }

        conn.disconnect();
        
        return hospitalInfoBuilder.toString();
    }
}