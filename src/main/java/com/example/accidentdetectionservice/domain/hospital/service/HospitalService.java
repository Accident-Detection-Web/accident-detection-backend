package com.example.accidentdetectionservice.domain.hospital.service;

import com.example.accidentdetectionservice.domain.hospital.dto.AllDataResponseDto;
import com.example.accidentdetectionservice.domain.hospital.dto.HospitalResponseDto;
import com.example.accidentdetectionservice.domain.hospital.entity.Accident;
import com.example.accidentdetectionservice.domain.hospital.entity.Hospital;
import com.example.accidentdetectionservice.domain.hospital.repository.AccidentRepository;
import com.example.accidentdetectionservice.domain.hospital.repository.HospitalRepository;
import com.example.accidentdetectionservice.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "국립중앙의료원_전국 응급의료기관 정보 조회 서비스")
public class HospitalService {

    private final AccidentRepository accidentRepository;
    private final HospitalRepository hospitalRepository;

    public List<HospitalResponseDto> getHospitalData(User receiver) throws Exception{

        // 해당 유저에 해당하는 마지막으로 저장된 사고 객체 가져오기
        Accident accident = accidentRepository.findTopByReceiverIdOrderByIdDesc(receiver.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 사고 정보가 없습니다."));

        String[] address = accident.getAddress().split(" ");


        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552657/ErmctInfoInqireService/getEmrrmRltmUsefulSckbdInfoInqire"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=351qYfh59jJHQGLGCTf2af0is6PVkCNFKEfj2%2FdXVQKfBWFGg1%2BiSHbG6D6edWitwcgQ%2FKV6P82xpCPpM%2FD4sg%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("STAGE1","UTF-8") + "=" + URLEncoder.encode(address[0], "UTF-8")); /*주소(시도)*/
        urlBuilder.append("&" + URLEncoder.encode("STAGE2","UTF-8") + "=" + URLEncoder.encode(address[1], "UTF-8")); /*주소(시군구)*/
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

                // 양방향 관계 
                // 병원 객체 생성 및 사고 객체에 해당 병워 정보 추가
                Hospital hospital = new Hospital(dutyName, dutyTel3, accident);
                accident.getList().add(hospital);
                hospitalRepository.save(hospital);
            }
        }
        conn.disconnect();

        if (accident.getList().isEmpty()) {
           throw new IllegalArgumentException("현재 가용 병원이 없습니다.");
        }

        return hospitalRepository.findAllByAccident(accident).stream()
                .map(hospital -> new HospitalResponseDto(hospital.getName(), hospital.getTel()))
                .toList();
    }

    public AllDataResponseDto getAllData(User user) {
        List<Accident> accidentList = accidentRepository.findAllByReceiver(user);

        return new AllDataResponseDto(accidentList.stream()
                .map(accident -> new AllDataResponseDto.AllData(
                        accident.getId(),
                        accident.getDate(),
                        accident.getList().stream().collect(Collectors.toMap(Hospital::getName, Hospital::getTel)),
                        accident.getSeverityLevel(),
                        accident.getSeverity()))
                .collect(Collectors.toList()));

    }




//    public static HospitalResponseDto getHospitalInfo() throws Exception {
//        StringBuilder hospitalInfoBuilder = new StringBuilder();
//
//        String Si = "경기도";
//        String Gun = "수원시";
//        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552657/ErmctInfoInqireService/getEmrrmRltmUsefulSckbdInfoInqire"); /*URL*/
//        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=351qYfh59jJHQGLGCTf2af0is6PVkCNFKEfj2%2FdXVQKfBWFGg1%2BiSHbG6D6edWitwcgQ%2FKV6P82xpCPpM%2FD4sg%3D%3D"); /*Service Key*/
//        urlBuilder.append("&" + URLEncoder.encode("STAGE1","UTF-8") + "=" + URLEncoder.encode(Si, "UTF-8")); /*주소(시도)*/
//        urlBuilder.append("&" + URLEncoder.encode("STAGE2","UTF-8") + "=" + URLEncoder.encode(Gun, "UTF-8")); /*주소(시군구)*/
//        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
//        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*목록 건수*/
//        URL url = new URL(urlBuilder.toString());
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("GET");
//        conn.setRequestProperty("Content-type", "application/xml");
//
//        // Read the XML response
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        Document doc = builder.parse(new InputSource(conn.getInputStream()));
//
//        List<String> availableHospitalName = new ArrayList<>();
//        List<String> availableHospitalTel = new ArrayList<>();
//        NodeList itemList = doc.getElementsByTagName("item");
//        for (int i = 0; i < itemList.getLength(); i++) {
//            Node itemNode = itemList.item(i);
//            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
//                Element itemElement = (Element) itemNode;
//                String dutyName = itemElement.getElementsByTagName("dutyName").item(0).getTextContent();
//                String dutyTel3 = itemElement.getElementsByTagName("dutyTel3").item(0).getTextContent();
//                availableHospitalName.add(dutyName);
//                availableHospitalTel.add(dutyTel3);
//                hospitalInfoBuilder.append("Hospital Name: ").append(dutyName).append(", Telephone: ").append(dutyTel3).append("\n");
//            }
//        }
//
//        conn.disconnect();
//        return new HospitalResponseDto(availableHospitalName, availableHospitalTel);
////        return hospitalInfoBuilder.toString();
//    }

//    public static void main(String[] args) throws Exception {
//        String hospitalInfo = getHospitalInfo();
//        System.out.println(hospitalInfo);
//    }
}