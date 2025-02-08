package com.example.eventifyeventmanagment.loaders;

import com.example.eventifyeventmanagment.entity.PaymentStatus;
import com.example.eventifyeventmanagment.entity.Status;
import com.example.eventifyeventmanagment.repository.PaymentStatusRepository;
import com.example.eventifyeventmanagment.repository.StatusRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class PaymentStatusLoader {
    private final PaymentStatusRepository statusRepository;
    private Map<Integer,String> staticSatatusDataMap ;
    private Map<String,Integer> statusNameDataMap;
    @Autowired
    public PaymentStatusLoader(PaymentStatusRepository statusRepository){
        this.statusRepository =statusRepository;

    }

    @PostConstruct
// this annoatation tells to run this method wheenver spring boot application runs. before calling it and  load the data from database
    public void  loadPaymentStatusData() {
        List<PaymentStatus> statusData = statusRepository.findAll();
        staticSatatusDataMap = new HashMap<>();
        statusNameDataMap = new HashMap<>();
        for (PaymentStatus status : statusData) {
            staticSatatusDataMap.put(status.getId(), status.getPaymentStatus());
            statusNameDataMap.put(status.getPaymentStatus(),status.getId());
        }


    }
    public Integer getPaymentStatusIdByUsingStatusName( String statusName){
        if(statusNameDataMap == null) {
            loadPaymentStatusData();
        }
        Integer statusId = statusNameDataMap.get(statusName);
        return statusId;
    }

    public String getPaymentStatusNameByUsingStatusId( Integer statusId){
        if (staticSatatusDataMap == null) {
            loadPaymentStatusData();
        }
        String statusName = staticSatatusDataMap.get(statusId);
        return statusName;
    }

}



