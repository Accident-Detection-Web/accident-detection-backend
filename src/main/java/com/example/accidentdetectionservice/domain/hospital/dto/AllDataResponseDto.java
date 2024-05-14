package com.example.accidentdetectionservice.domain.hospital.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class AllDataResponseDto {

    private Long id;
    private String date;
    private Map<String, String> availableHospital;
    private String sorting;
    private String accuracy;

    public AllDataResponseDto(Long id, String date, Map<String, String> availableHospital, String sorting, String accuracy) {
        this.id = id;
        this.date = date;
        this.availableHospital = availableHospital;
        this.sorting = sorting;
        this.accuracy = accuracy;
    }

    //    private List<AllData> allDataList = new ArrayList<>();
//
//    public void addAllData(Long id, String date, Map<String, String> availableHospital, String sorting,
//                           String accuracy) {
//        AllData allData = new AllData(id, date, availableHospital, sorting, accuracy);
//        allDataList.add(allData);
//    }
//    @AllArgsConstructor
//    @Getter
//    public static class AllData{ // 외부 접근 가능 (static)
//        private Long id;
//        private String date;
//        private Map<String, String> availableHospital;
//        private String sorting;
//        private String accuracy;
//    }
}